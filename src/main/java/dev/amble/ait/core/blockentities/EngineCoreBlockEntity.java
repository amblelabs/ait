package dev.amble.ait.core.blockentities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import dev.amble.ait.api.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.world.TardisServerWorld;

public class EngineCoreBlockEntity extends InteriorLinkableBlockEntity {

    private final List<BlockPos> activatingBlocks = new ArrayList<>();

    public int ticks;
    private float ticksActive;

    private long nextAmbientSoundTime;
    private boolean active;

    public EngineCoreBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ENGINE_CORE_BLOCK_ENTITY_TYPE, pos, state);
    }

    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public static void clientTick(World world, BlockPos pos, BlockState state, EngineCoreBlockEntity blockEntity) {
        if (!TardisServerWorld.isTardisDimension(world) || !blockEntity.isLinked())
            return;

        blockEntity.ticks++;
        long l = world.getTime();

        List<BlockPos> list = blockEntity.activatingBlocks;

        if (l % 40L == 0L)
            blockEntity.active = updateActivatingBlocks(world, pos, list);

        spawnNautilusParticles(world, pos, list, blockEntity.ticks);

        if (blockEntity.isActive())
            ++blockEntity.ticksActive;
    }

    public static void serverTick(World world, BlockPos pos, BlockState state, EngineCoreBlockEntity blockEntity) {
        if (!TardisServerWorld.isTardisDimension((ServerWorld) world) || !blockEntity.isLinked())
            return;

        blockEntity.ticks++;
        long l = world.getTime();

        List<BlockPos> list = blockEntity.activatingBlocks;

        if (l % 40L == 0L) {
            boolean bl = updateActivatingBlocks(world, pos, list);

            if (bl != blockEntity.active) {
                SoundEvent soundEvent = bl ? SoundEvents.BLOCK_CONDUIT_ACTIVATE : SoundEvents.BLOCK_CONDUIT_DEACTIVATE;
                world.playSound(null, pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);

                // blockEntity.tardis().get().engine().linkEngine(pos.getX(), pos.getZ());
            }

            blockEntity.active = bl;

            if (bl)
                givePlayersEffects(world, pos, list);
        }

        if (blockEntity.isActive()) {
            if (l % 80L == 0L) {
                world.playSound(null, pos, SoundEvents.BLOCK_CONDUIT_AMBIENT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }

            if (l > blockEntity.nextAmbientSoundTime) {
                blockEntity.nextAmbientSoundTime = l + 60L + (long) world.getRandom().nextInt(40);
                world.playSound(null, pos, SoundEvents.BLOCK_CONDUIT_AMBIENT_SHORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        }
    }

    private static boolean updateActivatingBlocks(World world, BlockPos pos, List<BlockPos> activatingBlocks) {
        activatingBlocks.clear();

        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                for (int k = -1; k <= 1; ++k) {
                    BlockPos blockPos = pos.add(i, j, k);
                    /*
                     * if (!world.isWater(blockPos)) { return false; }
                     */
                    activatingBlocks.add(blockPos);
                    return true;
                }
            }
        }

        return true;
    }

    private static void givePlayersEffects(World world, BlockPos pos, List<BlockPos> activatingBlocks) {
        int i = activatingBlocks.size();
        int j = i / 7 * 8;
        int k = pos.getX();
        int l = pos.getY();
        int m = pos.getZ();
        Box box = (new Box(k, l, m, k + 1, l + 1, m + 1)).expand(j).stretch(0.0, world.getHeight(), 0.0);
        List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);
        if (!list.isEmpty()) {
            for (PlayerEntity playerEntity : list) {
                if (pos.isWithinDistance(playerEntity.getBlockPos(), j) && playerEntity.isTouchingWaterOrRain()
                        && !playerEntity.getInventory().player.hasStackEquipped(EquipmentSlot.CHEST)) {
                    playerEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 130, 0, true, true));
                }
            }
        }
    }

    private static void spawnNautilusParticles(World world, BlockPos pos, List<BlockPos> activatingBlocks, int ticks) {

        Random random = world.random;
        double d = MathHelper.sin((float) (ticks + 35) * 0.1F) / 2.0F + 0.5F;
        d = (d * d + d) * 0.30000001192092896;
        Vec3d vec3d = new Vec3d((double) pos.getX() + 0.5, (double) pos.getY() + 1.5 + d, (double) pos.getZ() + 0.5);
        Iterator<BlockPos> var9 = activatingBlocks.iterator();

        float f;
        while (var9.hasNext()) {
            BlockPos blockPos = var9.next();
            // if (random.nextInt(1) == 0) {
            BlockPos blockPos2 = blockPos.subtract(pos);
            f = -0.5F + random.nextFloat() + (float) blockPos2.getX();
            float g = -2.0F + random.nextFloat() + (float) blockPos2.getY();
            float h = -0.5F + random.nextFloat() + (float) blockPos2.getZ();
            world.addParticle(ParticleTypes.NAUTILUS, vec3d.x, vec3d.y, vec3d.z, f, g, h);
            // }
        }
    }

    public boolean isActive() {
        return this.active;
    }

    public float getRotation(float tickDelta) {
        return (this.ticksActive + tickDelta) * -0.0375F;
    }

    public void onBreak(WorldAccess world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (world.isClient())
            return;

        if (!this.active)
            return;

//        if (this.isLinked())
//            this.tardis().get().engine().unlinkEngine();
    }

    public enum VortexEyeState {
        // These three stages simply define from frozen (it's in your inventory),
        // dormant (it's dormant, not producing power though - this is the state it goes
        // into when
        // there
        // is no fuel in
        // the TARDIS),
        // and activated (it's producing power)
        FROZEN, DORMANT, ACTIVATED,
        // Aggravated is used when the player does something that pisses off the eye
        // itself, since
        // it's
        // the
        // semi-sentient part
        // It's also used when trying to open the eye and etc.
        AGGRAVATED,
        // These states are used for opening the eye, not related to the states above
        // The eye needs to be open in order to refuel, do repairs, and change the
        // interior.
        // At first, you will need to manually open it, which leads to >> aggravated,
        // meaning you
        // have
        // to get past it's
        // attacks to force it open,
        // where it becomes semi-dormant but open (the state dormant has nothing to do
        // with being
        // open)
        OPENING_EYE_1, OPENING_EYE_2, EYE_OPEN
    }
}
