package dev.amble.ait.core.blockentities;

import static dev.amble.ait.core.tardis.handler.InteriorChangingHandler.MAX_PLASMIC_MATERIAL_AMOUNT;

import java.util.UUID;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.drtheo.scheduler.api.TimeUnit;
import dev.drtheo.scheduler.api.common.Scheduler;
import dev.drtheo.scheduler.api.common.TaskStage;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BrushItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.core.engine.impl.EngineSystem;
import dev.amble.ait.core.item.KeyItem;
import dev.amble.ait.core.item.SiegeTardisItem;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.item.sonic.SonicMode;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.BiomeHandler;
import dev.amble.ait.core.tardis.handler.SonicHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;

public class ExteriorBlockEntity extends AbstractLinkableBlockEntity implements BlockEntityTicker<ExteriorBlockEntity> {
    private UUID seatEntityUUID = null;

    public ExteriorBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.EXTERIOR_BLOCK_ENTITY_TYPE, pos, state);
    }

    public ExteriorBlockEntity(BlockPos pos, BlockState state, Tardis tardis) {
        this(pos, state);
        this.link(tardis);
    }

    public void useOn(ServerWorld world, boolean sneaking, PlayerEntity player) {
        if (this.tardis().isEmpty() || player == null)
            return;

        if (!this.validateExteriorPosition()) return;

        ServerTardis tardis = (ServerTardis) this.tardis().get();
        ItemStack hand = player.getMainHandStack();

        if (tardis.isGrowth()) {
            if (tardis.interiorChangingHandler().hasCage()) {
                if (hand.getItem() == AITItems.PLASMIC_MATERIAL) {
                    int plasmic = tardis.interiorChangingHandler().plasmicMaterialAmount();
                    if (plasmic < MAX_PLASMIC_MATERIAL_AMOUNT) {
                        tardis.interiorChangingHandler().addPlasmicMaterial(1);
                        world.playSound(null, pos, SoundEvents.ENTITY_MAGMA_CUBE_SQUISH, SoundCategory.BLOCKS, 1F, (float) plasmic / 8);
                        hand.decrement(1);
                    }
                }
            } else {
                if (hand.getItem() == AITItems.CORAL_CAGE) {
                    world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.BLOCKS, 1F, 0.7f);
                    tardis.interiorChangingHandler().setHasCage(true);
                    hand.decrement(1);
                    return;
                }
                world.playSound(null, pos, SoundEvents.BLOCK_CORAL_BLOCK_HIT, SoundCategory.BLOCKS, 1F, 0.3f);
                player.sendMessage(Text.translatable("tardis.message.growth.no_cage"), true);
            }
         return;
        }

        SonicHandler handler = tardis.sonic();

        boolean hasSonic = handler.getExteriorSonic() != null;
        boolean shouldEject = player.isSneaking();

        if (hand.getItem() instanceof BrushItem && tardis.<BiomeHandler>handler(TardisComponent.Id.BIOME).getBiomeKey()
                != BiomeHandler.BiomeType.DEFAULT) {
            tardis.<BiomeHandler>handler(TardisComponent.Id.BIOME).forceTypeDefault();
            return;
        }

        if (hand.getItem() instanceof KeyItem key && !tardis.siege().isActive()
                && !tardis.interiorChangingHandler().queued().get()) {
            if (hand.isOf(AITItems.SKELETON_KEY) || key.isOf(hand, tardis)) {
                tardis.door().interactToggleLock((ServerPlayerEntity) player);
            } else {
                world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), SoundCategory.BLOCKS, 1F, 0.2F);
                player.sendMessage(Text.translatable("tardis.key.identity_error"), true); // TARDIS does not identify
                                                                                            // with key
            }

            return;
        }

        if (hasSonic) {
            if (shouldEject) {
                player.getInventory().offerOrDrop(handler.takeExteriorSonic());
                world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.BLOCKS, 1F,
                        0.2F);
                return;
            }

            player.sendMessage(Text.translatable("tardis.exterior.sonic.repairing")
                    .append(Text.literal(": " + tardis.crash().getRepairTicksAsSeconds() + "s")
                            .formatted(Formatting.BOLD, Formatting.GOLD)),
                    true);
            return;
        }

        if (hand.getItem() instanceof SonicItem sonic && sonic.isOf(hand, tardis)) {
            if (!tardis.siege().isActive()
                    && !tardis.interiorChangingHandler().queued().get()
                    && tardis.door().isClosed() && tardis.crash().getRepairTicks() > 0) {
                if (sonic.isOf(hand, tardis)) {
                    handler.insertExteriorSonic(hand);
                    player.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                    tardis.alarm().disable();
                    world.playSound(null, pos, AITSounds.SONIC_ON, SoundCategory.BLOCKS, 1F, 1F);
                    world.playSound(null, pos, AITSounds.SONIC_MENDING, SoundCategory.BLOCKS, 1F, 1F);
                    Scheduler.get().runTaskLater(() -> {
                        world.playSound(null, pos, AITSounds.TARDIS_BLING, SoundCategory.BLOCKS, 1F, 1F);
                    }, TaskStage.END_SERVER_TICK, TimeUnit.SECONDS, 15);

                } else {
                    world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.BLOCKS, 1F, 0.2F);
                    player.sendMessage(Text.translatable("tardis.tool.cannot_repair"), true);
                }
                return;
            }

            // try to stop phasing
            EngineSystem.Phaser phasing = tardis.subsystems().engine().phaser();

            if (phasing.isPhasing() && SonicItem.mode(hand) == SonicMode.Modes.TARDIS) {
                    world.playSound(null, pos, AITSounds.SONIC_USE, SoundCategory.PLAYERS, 1F, 1F);
                    phasing.cancel();
                return;
            }
        }

        if (sneaking && !tardis.isSiegeBeingHeld() && tardis.siege().isActive()) {
            SiegeTardisItem.pickupTardis(tardis, (ServerPlayerEntity) player);
            return;
        }

        if (!tardis.travel().isLanded())
            return;

        tardis.door().interact((ServerWorld) this.getWorld(), this.getPos(), (ServerPlayerEntity) player);
    }

    /**
     * Validates the exterior position of the TARDIS
     * Will delete this block if the exterior is not valid
     * @return true if the exterior is valid
     */
    @Deprecated(since = "1.3.0")
    public boolean validateExteriorPosition() {
        if (!this.isLinked())
            return true;

        ServerTardis tardis = this.tardis().get().asServer();

        CachedDirectedGlobalPos expectedPos = tardis.travel().position();
        BlockPos expectedBlockPos = expectedPos.getPos();

        ServerWorld extWorld = (ServerWorld) this.getWorld();
        BlockPos extPos = this.getPos();

        if (extPos.equals(expectedBlockPos) && expectedPos.getWorld() == extWorld)
            return true;

        AITMod.LOGGER.warn("Invalid exterior at {} {}, expected {} {} for TARDIS {}. Removing..",
                extWorld.getRegistryKey(), extPos, expectedPos.getDimension(), expectedBlockPos, tardis.getUuid());

        extWorld.setBlockState(extPos, Blocks.AIR.getDefaultState());
        return true;
    }

    public void sitOn(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) return;

        ServerTardis tardis = this.tardis().get().asServer();
        ExteriorVariantSchema variant = tardis.getExterior().getVariant();

        float playerPitch = player.getPitch(1.0F);
        if (variant == null) return;

        if (playerPitch > 50.0F) {
            Vec3d seatPos = new Vec3d(
                    variant.seatTranslations().x + pos.getX(),
                    variant.seatTranslations().y + pos.getY(),
                    variant.seatTranslations().z + pos.getZ()
            );


            byte rotation = tardis.travel().position().getRotation();
            float yaw = RotationPropertyHelper.toDegrees(rotation) + 180.0F;
            Vec3d adjustedPos = moveForward(seatPos, yaw, variant.seatForwardTranslation());

            summonSeatEntity(world, adjustedPos, player, yaw);
        }
    }

    // Moves the seat forward based on yaw direction
    private Vec3d moveForward(Vec3d pos, float yaw, double distance) {
        double radians = Math.toRadians(yaw);
        double offsetX = -Math.sin(radians) * distance;
        double offsetZ = Math.cos(radians) * distance;

        return pos.add(offsetX, 0, offsetZ);
    }

    private void summonSeatEntity(World world, Vec3d pos, PlayerEntity player, float yaw) {
        ArmorStandEntity seat = new ArmorStandEntity(EntityType.ARMOR_STAND, world);
        seat.setPosition(pos.x, pos.y, pos.z);
        seat.setInvisible(true);
        seat.setNoGravity(true);
        seat.setInvulnerable(true);
        seat.setYaw(yaw);

        world.spawnEntity(seat);

        player.startRiding(seat, true);

        this.setSeatEntity(seat.getUuid());
    }

    public void setSeatEntity(UUID seatUUID) {
        this.seatEntityUUID = seatUUID;
    }

    public Entity getSeatEntity(World world) {
        if (seatEntityUUID == null) return null;
        return ((ServerWorld) world).getEntity(seatEntityUUID);
    }

    public void onEntityCollision(Entity entity) {
        if (!this.validateExteriorPosition()) return;

        TardisRef ref = this.tardis();

        if (ref == null)
            return;

        if (ref.isEmpty())
            return;

        ServerTardis tardis = ref.get().asServer();
        TravelHandler travel = tardis.travel();

        boolean previouslyLocked = tardis.door().previouslyLocked().get();

        if (tardis.siege().isActive()) return;

        if (travel.getState() == TravelHandlerBase.State.DEMAT) return;

        if (!previouslyLocked && travel.getState() == TravelHandlerBase.State.MAT
                && travel.getAlpha() >= 0.9F)
            TardisUtil.teleportInside(tardis, entity);

        if (!tardis.door().isClosed()
                && (!DependencyChecker.hasPortals() || !tardis.getExterior().getVariant().hasPortals()))
            TardisUtil.teleportInside(tardis, entity);

        if (tardis.door().isClosed()
                && entity instanceof PlayerEntity player
                && tardis.isGrowth()) {
            player.sendMessage(Text.translatable("tardis.message.growth.in_progress").formatted(Formatting.RED), true);
        }
    }

    @Override
    public void tick(World world, BlockPos pos, BlockState blockState, ExteriorBlockEntity blockEntity) {
        TardisRef ref = this.tardis();

        if (ref == null || ref.isEmpty())
            return;

        Tardis tardis = ref.get();

        TravelHandlerBase travel = tardis.travel();
        TravelHandlerBase.State state = travel.getState();

        if (!world.isClient()) {
            if (tardis.travel().isLanded())
                world.scheduleBlockTick(this.getPos(), AITBlocks.EXTERIOR_BLOCK, 2);

            return;
        }

        if (AITModClient.CONFIG.renderDematParticles && !tardis.travel().isLanded() && tardis.travel().isHitboxShown()) {
            for (int ji = 0; ji < 4; ji++) {
                double offsetX = AITMod.RANDOM.nextGaussian() * 0.125f;
                double offsetY = AITMod.RANDOM.nextGaussian() * 0.125f;
                double offsetZ = AITMod.RANDOM.nextGaussian() * 0.125f;
                Vec3d vec = new Vec3d(offsetX, offsetY, offsetZ);
                float offsetMultiplier = -0.1f;
                Vec3d vec3d = Vec3d.ofCenter(pos);
                int i = Direction.UP.getOffsetX();
                int j = Direction.UP.getOffsetY();
                int k = Direction.UP.getOffsetZ();
                double d = vec3d.x + (i == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double) i * offsetMultiplier);
                double e = vec3d.y + (j == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double) j * offsetMultiplier) - 0.35f;
                double f = vec3d.z + (k == 0 ? MathHelper.nextDouble(world.random, -0.5, 0.5) : (double) k * offsetMultiplier);
                double g = i == 0 ? vec.getX() : 0.0;
                double h = j == 0 ? vec.getY() : 0.0;
                double l = k == 0 ? vec.getZ() : 0.0;
                world.addParticle(ParticleTypes.CLOUD, d, e, f, g, h, l);
                world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, world.getBlockState(pos.down())), d, e, f, g, h, l);
            }
        }

        this.exteriorLightBlockState(blockState, pos, state);
    }

    private void exteriorLightBlockState(BlockState blockState, BlockPos pos, TravelHandlerBase.State state) {
        if (!state.animated())
            return;

        if (!blockState.isOf(AITBlocks.EXTERIOR_BLOCK))
            return;

        if (!this.isLinked()) return;

        Tardis tardis = this.tardis().get();

        this.getWorld().setBlockState(pos, blockState.with(ExteriorBlock.LEVEL_4, MathHelper.clamp(Math.round(tardis.travel().getAlpha() * 4), 0, 15)));
    }
}
