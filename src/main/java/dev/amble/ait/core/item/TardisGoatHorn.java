package dev.amble.ait.core.item;

import static net.minecraft.block.entity.BeaconBlockEntity.playSound;

import java.util.Iterator;
import java.util.Optional;

import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Instrument;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.data.enummap.EnumSet;
import dev.amble.ait.data.enummap.Ordered;

public class TardisGoatHorn extends LinkableItem {

    private final TagKey<Instrument> instrumentTag;
    private final EnumSet<TardisGoatHorn.Protocols> protocols;

    public TardisGoatHorn(Settings settings, TagKey<Instrument> instrumentTag, TardisGoatHorn.Protocols... abs) {
        super(settings.maxCount(1), true);
        this.instrumentTag = instrumentTag;

        this.protocols = new EnumSet<>(TardisGoatHorn.Protocols::values);
        this.protocols.addAll(abs);
    }

    public enum Protocols implements Ordered {
        SNAP, HAIL, PERCEPTION, SKELETON;

        @Override
        public int index() {
            return ordinal();
        }
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        Entity owner = entity.getOwner();

        if (!(owner instanceof ServerPlayerEntity player))
            return;

        Tardis tardis = KeyItem.getTardisStatic(entity.getWorld(), entity.getStack());

        if (tardis == null)
            return;

        tardis.loyalty().subLevel(player, 35);
        tardis.getDesktop().playSoundAtEveryConsole(AITSounds.CLOISTER);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        Optional<? extends RegistryEntry<Instrument>> optional = this.getInstrument(itemStack);
        if (optional.isPresent()) {
            Instrument instrument = (Instrument)((RegistryEntry<?>) optional.get()).value();
            user.setCurrentHand(hand);
            playSound(world, user.getBlockPos(), instrument.soundEvent().value());
            user.getItemCooldownManager().set(this, instrument.useDuration());
            user.incrementStat(Stats.USED.getOrCreateStat(this));

            Tardis tardis = TardisGoatHorn.getTardisStatic(world, user.getStackInHand(hand));
            if (tardis == null || tardis.travel() == null) return TypedActionResult.fail(itemStack);
            CachedDirectedGlobalPos abpd = tardis.travel().destination();
            BlockPos abpdPos = abpd.getPos();
            Text message = Text.literal("X: " + abpdPos.getX() + " Y: " + abpdPos.getY() + " Z: " + abpdPos.getZ() + " Dim: ")
                    .formatted(Formatting.GRAY)
                    .append(WorldUtil.worldText(abpd.getDimension(), false)).formatted(Formatting.GRAY);

            user.sendMessage(message, true);

            return TypedActionResult.consume(itemStack);
        } else {
            return TypedActionResult.fail(itemStack);
        }
    }

    public int getMaxUseTime(ItemStack stack) {
        Optional<? extends RegistryEntry<Instrument>> optional = this.getInstrument(stack);
        return optional.map((instrument) -> instrument.value().useDuration()).orElse(0);
    }

    private Optional<RegistryEntry<Instrument>> getInstrument(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound != null && nbtCompound.contains("instrument", 8)) {
            Identifier identifier = Identifier.tryParse(nbtCompound.getString("instrument"));
            if (identifier != null) {
                // Cast the reference to the expected type
                return Registries.INSTRUMENT.getEntry(
                        RegistryKey.of(RegistryKeys.INSTRUMENT, identifier)
                ).map(entryRef -> entryRef);
            }
        }

        Iterator<RegistryEntry<Instrument>> iterator = Registries.INSTRUMENT.iterateEntries(this.instrumentTag).iterator();
        return iterator.hasNext() ? Optional.of(iterator.next()) : Optional.empty();
    }


    public UseAction getUseAction(ItemStack stack) {
        return UseAction.TOOT_HORN;
    }
}
