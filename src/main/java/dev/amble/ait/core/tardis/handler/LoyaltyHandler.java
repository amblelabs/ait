package dev.amble.ait.core.tardis.handler;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.Nameable;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.core.likes.ItemOpinion;
import dev.amble.ait.core.likes.ItemOpinionRegistry;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.sonic.SonicSchema;
import dev.amble.ait.registry.impl.DesktopRegistry;
import dev.amble.ait.registry.impl.SonicRegistry;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class LoyaltyHandler extends TardisComponent implements TardisTickable {
    private final Map<UUID, Loyalty> data;
    private boolean messageEnabled = true;

    public boolean isMessageEnabled() {
        return messageEnabled;
    }

    public void setMessageEnabled(boolean messageEnabled) {
        this.messageEnabled = messageEnabled;
    }

    public LoyaltyHandler(HashMap<UUID, Loyalty> data) {
        super(Id.LOYALTY);
        this.data = data;
    }

    public LoyaltyHandler() {
        this(new HashMap<>());
    }

    public Map<UUID, Loyalty> data() {
        return this.data;
    }

    public Loyalty get(PlayerEntity player) {
        return this.data.getOrDefault(player.getUuid(), new Loyalty(Loyalty.Type.NEUTRAL));
    }

    public Loyalty set(ServerPlayerEntity player, Loyalty loyalty) {
        this.data.put(player.getUuid(), loyalty);
        this.unlock(player, loyalty);

        this.sync();
        return loyalty;
    }

    @Override
    public void tick(MinecraftServer server) {
        if (server.getTicks() % 40 != 0)
            return;

        for (ServerPlayerEntity player : TardisUtil.getPlayersInsideInterior((ServerTardis) tardis)) {
            Loyalty loyalty = this.get(player);

            if (!loyalty.isOf(Loyalty.Type.NEUTRAL))
                continue;

            if (ItemOpinionRegistry.getInstance().get(player.getMainHandStack()).isPresent()) {
                ItemOpinion opinion = ItemOpinionRegistry.getInstance().get(player.getMainHandStack()).get();
                tardis.opinions().contains(opinion);
                player.sendMessage(Text.translatable("ait.tardis.likes_item", true));
            }

            if (AITMod.RANDOM.nextInt(0, 20) != 14)
                continue;

            this.addLevel(player, 1);
        }
    }

    public void update(ServerPlayerEntity player, Function<Loyalty, Loyalty> consumer) {
        Loyalty current = this.get(player);
        current = consumer.apply(current);

        this.set(player, current);
    }

    public void unlock(ServerPlayerEntity player, Loyalty loyalty) {

        if (!messageEnabled) {
            return;
        }

        ServerTardis tardis = (ServerTardis) this.tardis;

        boolean playSound = ConsoleVariantRegistry.getInstance().tryUnlock(tardis, loyalty,
                schema -> this.playUnlockEffects(player, schema));
        playSound = DesktopRegistry.getInstance().tryUnlock(tardis, loyalty,
                schema -> this.playUnlockEffects(player, schema)) || playSound;
        playSound = ExteriorVariantRegistry.getInstance().tryUnlock(tardis, loyalty,
                schema -> this.playUnlockEffects(player, schema)) || playSound;
        playSound = SonicRegistry.getInstance().tryUnlock(tardis, loyalty,
                schema -> this.playUnlockEffects(player, schema)) || playSound;

        if (playSound)
            player.getServerWorld().playSound(null, player.getBlockPos(), AITSounds.LOYALTY_UP,
                    SoundCategory.PLAYERS, 0.2F, 1.0F);

        if (loyalty.isOf(Loyalty.Type.PILOT)) {
            TardisCriterions.REACH_PILOT.trigger(player);
        } else if (loyalty.isOf(Loyalty.Type.OWNER)) {
            TardisCriterions.REACH_OWNER.trigger(player);
        }
    }

    private void playUnlockEffects(ServerPlayerEntity player, Nameable nameable) {
        Text nameText = nameable.text().copy().formatted(Formatting.GREEN);

        Text unlockedMessage;
        if (nameable instanceof SonicSchema) {
            unlockedMessage = Text.translatable("message.ait.unlocked_sonic", nameText).formatted(Formatting.WHITE);
        } else if (nameable instanceof ConsoleVariantSchema) {
            unlockedMessage = Text.translatable("message.ait.unlocked_console", nameText).formatted(Formatting.WHITE);
        } else if (nameable instanceof TardisDesktopSchema) {
            unlockedMessage = Text.translatable("message.ait.unlocked_interior", nameText).formatted(Formatting.WHITE);
        } else if (nameable instanceof ExteriorVariantSchema) {
            unlockedMessage = Text.translatable("message.ait.unlocked_exterior", nameText).formatted(Formatting.WHITE);
        } else {
            unlockedMessage = Text.translatable("message.ait.unlocked", nameText).formatted(Formatting.WHITE);
        }

        player.sendMessage(unlockedMessage, false);
    }


    public void addLevel(ServerPlayerEntity player, int level) {
        this.update(player, loyalty -> loyalty.add(level));
    }

    public void subLevel(ServerPlayerEntity player, int level) {
        this.addLevel(player, -level);
    }

    public ServerPlayerEntity getLoyalPlayerInside() {
        if (!(this.tardis instanceof ServerTardis serverTardis))
            return null;

        ServerPlayerEntity highest = null;
        int highestLoyalty = 0;

        for (ServerPlayerEntity player : TardisUtil.getPlayersInsideInterior(serverTardis)) {
            if (highest == null) {
                highest = player;
                highestLoyalty = this.get(highest).level();
                continue;
            }

            int found = this.get(player).level();

            if (found > highestLoyalty) {
                highest = player;
                highestLoyalty = found;
            }
        }

        return highest;
    }

    public void sendMessageToPilot(Text text) {
        ServerPlayerEntity player = this.getLoyalPlayerInside();

        if (player == null)
            return;

        player.sendMessage(text, true);
    }
}
