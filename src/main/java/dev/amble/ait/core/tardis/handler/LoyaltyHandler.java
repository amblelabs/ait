package dev.amble.ait.core.tardis.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.Nameable;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.core.likes.ItemOpinion;
import dev.amble.ait.core.likes.ItemOpinionRegistry;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.sounds.flight.FlightSound;
import dev.amble.ait.core.sounds.flight.FlightSoundRegistry;
import dev.amble.ait.core.tardis.animation.v2.TardisAnimation;
import dev.amble.ait.core.tardis.animation.v2.datapack.TardisAnimationRegistry;
import dev.amble.ait.core.tardis.vortex.reference.VortexReference;
import dev.amble.ait.core.tardis.vortex.reference.VortexReferenceRegistry;
import dev.amble.ait.data.Exclude;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.hum.Hum;
import dev.amble.ait.data.schema.console.ConsoleVariantSchema;
import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.sonic.SonicSchema;
import dev.amble.ait.registry.impl.DesktopRegistry;
import dev.amble.ait.registry.impl.HumRegistry;
import dev.amble.ait.registry.impl.SonicRegistry;
import dev.amble.ait.registry.impl.console.variant.ConsoleVariantRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;
import dev.amble.lib.register.unlockable.Unlockable;
import dev.amble.lib.register.unlockable.UnlockableRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class LoyaltyHandler extends TardisComponent implements TardisTickable {
    private static int generation;

    private final Map<UUID, Loyalty> data;
    private boolean messageEnabled = true;

    @Exclude
    private int refreshedGeneration = -1;

    static {
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, manager, success) -> generation++);
    }

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
        if (this.refreshedGeneration != generation) {
            this.refreshedGeneration = generation;
            this.refreshUnlocks();
        }

        if (server.getTicks() % 40 != 0)
            return;

        for (ServerPlayerEntity player : tardis.asServer().world().getPlayers()) {
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

    public void unlock(@Nullable ServerPlayerEntity player, Loyalty loyalty) {
        ServerTardis tardis = (ServerTardis) this.tardis;
        boolean announce = messageEnabled && player != null;
        boolean unlocked = false;

        for (UnlockableRegistry<?> registry : unlockables())
            unlocked = tryUnlock(registry, tardis, loyalty, announce ? player : null) || unlocked;

        if (unlocked && announce)
            player.getServerWorld().playSound(null, player.getBlockPos(), AITSounds.LOYALTY_UP,
                    SoundCategory.PLAYERS, 0.2F, 1.0F);

        if (player == null)
            return;

        if (loyalty.isOf(Loyalty.Type.OWNER))
            TardisCriterions.REACH_OWNER.trigger(player);
        else if (loyalty.isOf(Loyalty.Type.PILOT))
            TardisCriterions.REACH_PILOT.trigger(player);
    }

    private <T extends Unlockable> boolean tryUnlock(UnlockableRegistry<T> registry, ServerTardis tardis, Loyalty loyalty,
            @Nullable ServerPlayerEntity announceTo) {
        return registry.tryUnlock(tardis, loyalty, schema -> {
            if (announceTo != null && schema instanceof Nameable nameable)
                this.playUnlockEffects(announceTo, nameable);
        });
    }

    private void refreshUnlocks() {
        for (Loyalty loyalty : this.data.values())
            this.unlock(null, loyalty);
    }

    public static List<UnlockableRegistry<?>> unlockables() {
        return List.of(ConsoleVariantRegistry.getInstance(), DesktopRegistry.getInstance(),
                ExteriorVariantRegistry.getInstance(), SonicRegistry.getInstance(), HumRegistry.getInstance(),
                VortexReferenceRegistry.getInstance(), FlightSoundRegistry.getInstance(),
                TardisAnimationRegistry.getInstance());
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
        } else if (nameable instanceof Hum) {
            unlockedMessage = Text.translatable("message.ait.unlocked_hum", nameText).formatted(Formatting.WHITE);
        } else if (nameable instanceof VortexReference) {
            unlockedMessage = Text.translatable("message.ait.unlocked_vortex", nameText).formatted(Formatting.WHITE);
        } else if (nameable instanceof FlightSound) {
            unlockedMessage = Text.translatable("message.ait.unlocked_flight_sound", nameText).formatted(Formatting.WHITE);
        } else if (nameable instanceof TardisAnimation) {
            unlockedMessage = Text.translatable("message.ait.unlocked_animation", nameText).formatted(Formatting.WHITE);
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

        for (ServerPlayerEntity player : serverTardis.world().getPlayers()) {
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
