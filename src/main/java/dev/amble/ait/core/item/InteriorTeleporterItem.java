package dev.amble.ait.core.item;

import dev.amble.ait.api.tardis.link.LinkableItem;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.Loyalty;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class InteriorTeleporterItem extends LinkableItem { // todo - new model + texture?
    private static final ParticleEffect PARTICLE_SUCCESS = ParticleTypes.GLOW;
    private static final ParticleEffect PARTICLE_FAIL = ParticleTypes.ELECTRIC_SPARK;

    public InteriorTeleporterItem(Settings settings) {
        super(settings.maxCount(1).maxDamageIfAbsent(16), true);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        Tardis tardis = getTardis(world, stack);

        if (world.isClient()) {
            boolean success = (tardis != null);

            if (success) {
                MinecraftClient.getInstance().gameRenderer.showFloatingItem(stack);
            }

            return success ? TypedActionResult.success(stack, true) : TypedActionResult.fail(stack);
        }
        // server-side

        if (tardis == null)
            return TypedActionResult.fail(stack);

        Loyalty loyalty = tardis.loyalty().get(user);
        Loyalty.Type type = loyalty.type();

        boolean success = switch (type) {
            case REJECT, NEUTRAL -> false;
            case PILOT, OWNER -> true;

            case COMPANION -> tardis.travel().isLanded();
        };

        if (!success) {
            createTeleportEffect((ServerPlayerEntity) user, PARTICLE_FAIL);
            world.playSound(null, user.getBlockPos(), AITSounds.UNSTABLE_FLIGHT_LOOP, SoundCategory.PLAYERS, 1f, 1f);
            user.getItemCooldownManager().set(this, 4 * 20);

            return TypedActionResult.fail(stack);
        }

        createTeleportEffect((ServerPlayerEntity) user, PARTICLE_SUCCESS);
        world.playSound(null, user.getBlockPos(), AITSounds.BWEEP, SoundCategory.PLAYERS, 1f, 1f);

        TardisUtil.teleportInside(tardis.asServer(), user);

        stack.setCount(stack.getCount() - 1);
        user.getItemCooldownManager().set(this, 16 * 20);

        BlockPos door = tardis.getDesktop().getDoorPos().getPos();
        createTeleportEffect(tardis.asServer().world(), door.toCenterPos().subtract(0, 0.5, 0), PARTICLE_SUCCESS);
        world.playSound(null, door, AITSounds.DING, SoundCategory.PLAYERS, 1f, 1f);
        world.playSound(null, door, AITSounds.LAND_THUD, SoundCategory.PLAYERS, 1f, 1f);

        return TypedActionResult.success(stack, true);
    }

    /**
     * Creates a spiral of particles around the player
     * <br>
     * from <a href="https://github.com/Duzos/vortex-manipulator/blob/trunk/src/main/java/mc/duzo/vortex/util/VortexUtil.java">this mod</a>
     */
    private static void createTeleportEffect(ServerWorld world, Vec3d source, ParticleEffect particle) {
        double b = Math.PI / 8;

        Vec3d pos;
        double x;
        double y;
        double z;

        for(double t = 0.0D; t <= Math.PI * 2; t += Math.PI / 16) {
            for (int i = 0; i <= 1; i++) {
                x = 0.4D * (Math.PI * 2 - t) * 0.5D * Math.cos(t + b + i * Math.PI);
                y = 0.5D * t;
                z = 0.4D * (Math.PI * 2 - t) * 0.5D * Math.sin(t + b + i * Math.PI);
                pos = source.add(x, y, z);

                world.spawnParticles(particle, pos.getX(), pos.getY(), pos.getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
            }
        }
    }
    private static void createTeleportEffect(ServerPlayerEntity player, ParticleEffect particle) {
        Vec3d dest = player.getBlockPos().toCenterPos().subtract(0, 0.5, 0);
        createTeleportEffect(player.getServerWorld(), dest, particle);
    }
}
