package dev.amble.ait.core.tardis.handler;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.core.AITDamageTypes;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.util.TardisHomeUtil;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;
import dev.amble.ait.data.properties.bool.BoolProperty;
import dev.amble.ait.data.properties.bool.BoolValue;
import dev.amble.lib.data.CachedDirectedGlobalPos;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

/** Persistent state shared by features which only operate at the exact home. */
public final class HomeSystemsHandler extends KeyedTardisComponent implements TardisTickable {
    private static final BoolProperty DEFENSE_ENABLED = new BoolProperty("defense_enabled", false);
    private static final Property<Long> LAST_CORAL_HARVEST = new Property<>(
            Property.LONG, "last_coral_harvest", 0L);

    private final BoolValue defenseEnabled = DEFENSE_ENABLED.create(this);
    private final Value<Long> lastCoralHarvest = LAST_CORAL_HARVEST.create(this);

    public HomeSystemsHandler() {
        super(Id.HOME_SYSTEMS);
    }

    @Override
    public void onLoaded() {
        this.defenseEnabled.of(this, DEFENSE_ENABLED);
        this.lastCoralHarvest.of(this, LAST_CORAL_HARVEST);
    }

    @Override
    public void tick(MinecraftServer server) {
    }

    public void tickDormant(MinecraftServer server) {
        if (Math.floorMod(this.tardis.getUuid().hashCode(), 20) != Math.floorMod(server.getTicks(), 20))
            return;

        if (!AITMod.CONFIG.homeDefenseAvailable) {
            if (this.defenseEnabled.get())
                this.defenseEnabled.set(false);
            return;
        }

        if (!this.canDefend())
            return;

        int fuelCost = Math.max(0, AITMod.CONFIG.homeDefenseFuelPerSecond);
        if (fuelCost > 0)
            this.tardis.fuel().removeFuel(fuelCost);
        if (!this.canDefend())
            return;

        int interval = Math.max(1, AITMod.CONFIG.homeDefenseIntervalSeconds);
        int bucket = Math.floorMod(this.tardis.getUuid().hashCode(), interval);
        if (server.getTicks() / 20 % interval != bucket)
            return;

        CachedDirectedGlobalPos home = this.tardis.stats().getHome();
        if (home == null)
            return;

        home.init(server);
        ServerWorld world = home.getWorld();
        if (world == null)
            return;

        double radius = Math.max(1, AITMod.CONFIG.homeDefenseRadius);
        double radiusSquared = radius * radius;
        BlockPos pos = home.getPos();
        Vec3d center = Vec3d.ofCenter(pos);
        Box bounds = new Box(pos).expand(radius);

        if (world.isChunkLoaded(pos)) {
            this.spawnDefensePulse(world, center, radius);
            for (LivingEntity entity : world.getEntitiesByClass(LivingEntity.class, bounds,
                    candidate -> candidate.isAlive() && candidate.squaredDistanceTo(center) <= radiusSquared
                            && this.isHostile(candidate))) {
                this.damageHostile(world, entity);
                if (!this.canDefend())
                    return;
            }
        }

        ServerTardis tardis = this.tardis.asServer();
        if (!tardis.hasWorld())
            return;

        ServerWorld interior = tardis.world();
        for (LivingEntity entity : interior.getEntitiesByType(TypeFilter.instanceOf(LivingEntity.class),
                candidate -> candidate.isAlive() && this.isHostile(candidate))) {
            this.damageHostile(interior, entity);
            if (!this.canDefend())
                return;
        }
    }

    public boolean needsTick() {
        return false;
    }

    public boolean requiresDormantResidency() {
        return this.canDefend();
    }

    private boolean canDefend() {
        ItemStack handles = this.tardis.butler().getHandles();
        return AITMod.CONFIG.homeDefenseAvailable && this.defenseEnabled.get()
                && this.tardis.fuel().hasPower() && handles != null && !handles.isEmpty()
                && TardisHomeUtil.isParkedAtExactHome(this.tardis);
    }

    private boolean isHostile(LivingEntity entity) {
        if (entity.getType().isIn(AITTags.EntityTypes.BOSS))
            return AITMod.CONFIG.homeDefenseAffectsBosses;

        if (entity instanceof ServerPlayerEntity)
            return false;

        if (entity instanceof HostileEntity)
            return true;

        return entity instanceof MobEntity mob && mob.getTarget() instanceof ServerPlayerEntity target
                && this.tardis.loyalty().get(target).isOf(Loyalty.Type.COMPANION);
    }

    private void damageHostile(ServerWorld world, LivingEntity entity) {
        Vec3d target = new Vec3d(entity.getX(), entity.getBodyY(0.5), entity.getZ());
        world.spawnParticles(AITMod.CORAL_PARTICLE, target.x, target.y, target.z,
                8, 0.35, 0.5, 0.35, 0.04);

        float damage = Math.max(0, AITMod.CONFIG.homeDefenseDamage);
        if (damage <= 0 || !entity.damage(AITDamageTypes.of(world, AITDamageTypes.CHRONAL_DAMAGE), damage)
                || entity.isAlive())
            return;

        if (entity instanceof MobEntity) {
            int engineDamage = Math.max(0, AITMod.CONFIG.homeDefenseEngineDamagePerKill);
            if (engineDamage > 0)
                this.tardis.subsystems().engine().removeDurability(engineDamage);
        }
    }

    private void spawnDefensePulse(ServerWorld world, Vec3d center, double radius) {
        int particles = 40;
        double phase = world.getTime() * 0.08;
        double speed = Math.max(0.25, Math.min(1.25, radius / 100.0));
        double goldenAngle = Math.PI * (3.0 - Math.sqrt(5.0));
        for (int index = 0; index < particles; index++) {
            double y = 1.0 - (index + 0.5) * 2.0 / particles;
            double horizontal = Math.sqrt(Math.max(0.0, 1.0 - y * y));
            double angle = index * goldenAngle + phase;
            double x = Math.cos(angle) * horizontal;
            double z = Math.sin(angle) * horizontal;
            world.spawnParticles(AITMod.CORAL_PARTICLE, center.x, center.y, center.z,
                    0, x, y, z, speed);
        }
    }

    public void rejectHarvest(ServerPlayerEntity player, BlockPos console) {
        if (player == null || console == null)
            return;

        Vec3d away = player.getPos().subtract(Vec3d.ofCenter(console));
        if (away.lengthSquared() < 1.0E-4)
            away = new Vec3d(0, 0, 1);
        away = away.normalize();

        player.addVelocity(
                away.x * Math.max(0, AITMod.CONFIG.consoleRejectionPushHorizontal),
                Math.max(0, AITMod.CONFIG.consoleRejectionPushVertical),
                away.z * Math.max(0, AITMod.CONFIG.consoleRejectionPushHorizontal));
        player.velocityModified = true;
        player.getServerWorld().spawnParticles(
                net.minecraft.particle.ParticleTypes.ANGRY_VILLAGER,
                console.getX() + 0.5, console.getY() + 1.0, console.getZ() + 0.5,
                8, 0.35, 0.4, 0.35, 0.03);
    }

    public boolean defenseEnabled() {
        return this.defenseEnabled.get();
    }

    public void defenseEnabled(boolean enabled) {
        this.defenseEnabled.set(enabled && AITMod.CONFIG.homeDefenseAvailable);
    }

    public boolean markCoralHarvest(MinecraftServer server) {
        if (server == null || !TardisHomeUtil.isParkedAtExactHome(this.tardis))
            return false;

        long now = server.getOverworld().getTime();
        long cooldown = Math.max(0L, AITMod.CONFIG.telepathicCoralCooldownMinutes) * 60L * 20L;
        long previous = this.lastCoralHarvest.get();
        if (cooldown > 0 && previous > 0 && now - previous < cooldown)
            return false;

        this.lastCoralHarvest.set(now);
        return true;
    }
}
