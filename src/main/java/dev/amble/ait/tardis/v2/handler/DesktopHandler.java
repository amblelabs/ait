package dev.amble.ait.tardis.v2.handler;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.v2.handler.THandler;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.tardis.TardisDesktop;
import dev.amble.ait.tardis.v2.Tardis;
import dev.amble.ait.tardis.v2.data.AlarmData;
import dev.amble.ait.tardis.v2.data.DesktopData;
import dev.amble.ait.tardis.v2.data.RepairData;
import dev.amble.ait.tardis.v2.event.ServerEvents;
import dev.amble.ait.tardis.v2.event.TardisEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.TypeFilter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class DesktopHandler implements THandler, ServerEvents, TardisEvents {

    // TODO: use entity tags
    static TypeFilter<Entity, ? extends Entity> HOSTILE_FILTER = new TypeFilter<Entity, Entity>() {
        @Override
        public @Nullable Entity downcast(Entity obj) {
            return obj instanceof TntEntity || (obj instanceof HostileEntity hostile && !hostile.hasCustomName())
                    || obj instanceof ServerPlayerEntity ? obj : null;
        }

        @Override
        public Class<? extends Entity> getBaseClass() {
            return Entity.class;
        }
    };

    @Override
    public void event$tardisTick(Tardis tardis, MinecraftServer server) {
        if (server.getTicks() % 20 != 0)
            return;

        if (!tardis.has(AlarmData.ID))
            return;

        AlarmData alarm = tardis.get(AlarmData.ID);

        if (alarm.enabled().get() || !alarm.hostilePresence().get())
            return;

        DesktopData desktop = tardis.get(DesktopData.ID);

        List<? extends Entity> entities = desktop.getServerWorld().getEntitiesByType(HOSTILE_FILTER, entity -> {
            if (!(entity instanceof ServerPlayerEntity player)) {
                return true;
            }

            return tardis.loyalty().get(player).level() == Loyalty.Type.REJECT.level;
        });

        if (!entities.isEmpty())
            alarm.enabled().set(true);
    }

    @Override
    public void event$repairTick(Tardis tardis, MinecraftServer server, RepairData repair) {
        // every 2 seconds
        if (server.getTicks() % 40 != 0)
            return;

        if (repair.state().get() != RepairData.State.TOXIC)
            return;

        DesktopData desktop = tardis.resolve(DesktopData.ID);

        ServerWorld world = desktop.getServerWorld();
        List<ServerPlayerEntity> players = world.getPlayers();

        if (players.isEmpty())
            return;

        int loyaltySubAmount = AITMod.RANDOM.nextInt(10, 25);

        for (ServerPlayerEntity player : players) {
            tardis.loyalty().get(player).subtract(loyaltySubAmount);

            ItemStack stack = player.getEquippedStack(EquipmentSlot.HEAD);

            if (stack.isIn(AITTags.Items.FULL_RESPIRATORS) || stack.isIn(AITTags.Items.HALF_RESPIRATORS))
                continue;

            player.damage(world.getDamageSources().magic(), 3f);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 100, 3, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 100, 5, true, false, false));
        }
    }

    @Override
    public void event$disablePower(Tardis tardis) {
        this.playSoundAtEveryConsole(AITSounds.SHUTDOWN, SoundCategory.AMBIENT, 10f, 1f);
    }

    @Override
    public void event$enablePower(Tardis tardis) {
        this.playSoundAtEveryConsole(AITSounds.POWERUP, SoundCategory.AMBIENT, 10f, 1f);
        this.playSoundAtEveryConsole(AITSounds.CONSOLE_BOOTUP, SoundCategory.AMBIENT, 0.15f, 1f);
    }

    @Override
    public void event$crash(Tardis tardis, int power) {
        DesktopData desktop = tardis.resolve(DesktopData.ID);
        ServerWorld world = desktop.getServerWorld();

        desktop.consolePos().forEach(console -> {
            TardisDesktop.playSoundAtConsole(world, console, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 3f, 1f);

            if (!world.getGameRules().getBoolean(AITMod.TARDIS_FIRE_GRIEFING))
                return;

            world.playSound(null, console, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 3.0f, 1.0f);
            world.playSound(null, console, SoundEvents.ENTITY_WITHER_HURT, SoundCategory.BLOCKS, 3.0f, 1.0f);
        });

        Random random = AITMod.RANDOM;

        for (ServerPlayerEntity player : world.getPlayers()) {
            float xVel = random.nextFloat(-2f, 3f);
            float yVel = random.nextFloat(-1f, 2f);
            float zVel = random.nextFloat(-2f, 3f);

            player.setVelocity(xVel * power, yVel * power, zVel * power);
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 40 * power, 1, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 40 * power, 1, true, false, false));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 40 * power, 2, true, false, false));

            int damage = (int) Math.round(0.5 * power);
            player.damage(world.getDamageSources().generic(), damage);
        }
    }

    @Override
    public void event$alarmToll(Tardis tardis, @Nullable AlarmHandler.Alarm alarm) {
        if (alarm == null)
            return;

        DesktopData desktop = tardis.resolve(DesktopData.ID);

        for (ServerPlayerEntity player : desktop.getServerWorld().getPlayers()) {
            alarm.sendMessage(player);
        }
    }
}
