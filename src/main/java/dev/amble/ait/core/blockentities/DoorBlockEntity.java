package dev.amble.ait.core.blockentities;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedBlockPos;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.event.GameEvent;

import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.compat.DependencyChecker;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.blocks.ExteriorBlock;
import dev.amble.ait.core.blocks.types.HorizontalDirectionalBlock;
import dev.amble.ait.core.item.KeyItem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.SonicHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.world.TardisServerWorld;

public class DoorBlockEntity extends InteriorLinkableBlockEntity {

    private DirectedBlockPos directedPos;

    public DoorBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.DOOR_BLOCK_ENTITY_TYPE, pos, state);
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState blockState, T tDoor) {
        DoorBlockEntity door = (DoorBlockEntity) tDoor;

        if (!(world instanceof ServerWorld serverWorld))
            return;

        if (!door.isLinked())
            return;

        Tardis tardis = door.tardis().get();

        if (tardis.areShieldsActive() || world.getServer().getTicks() % 20 != 0)
            return;

        CachedDirectedGlobalPos globalExteriorPos = tardis.travel().position();

        if (globalExteriorPos == null)
            return;

        BlockPos exteriorPos = globalExteriorPos.getPos();
        World exteriorWorld = globalExteriorPos.getWorld();

        if (exteriorWorld == null)
            return;

        if (blockState.get(Properties.WATERLOGGED) && world.getRandom().nextBoolean()) {
            serverWorld.getPlayers().forEach(player -> tardis.loyalty().subLevel(player, 2));
        }

        if (!tardis.door().isOpen())
            return;

        ChunkPos exteriorChunkPos = new ChunkPos(exteriorPos);
        Chunk exteriorChunk = exteriorWorld.getChunk(exteriorChunkPos.x, exteriorChunkPos.z, ChunkStatus.EMPTY, false);

        if (exteriorChunk == null)
            return;

        BlockState exteriorState = exteriorChunk.getBlockState(exteriorPos);

        if (!(exteriorState.getBlock() instanceof ExteriorBlock))
            return;

        boolean waterlogged = exteriorWorld.getBlockState(exteriorPos).get(Properties.WATERLOGGED);

        world.setBlockState(pos, blockState.with(Properties.WATERLOGGED, waterlogged),
                Block.NOTIFY_ALL | Block.REDRAW_ON_MAIN_THREAD);

        world.emitGameEvent(null, GameEvent.BLOCK_CHANGE, pos);
        world.scheduleFluidTick(pos, blockState.getFluidState().getFluid(),
                blockState.getFluidState().getFluid().getTickRate(world));
    }

    public void useOn(World world, boolean sneaking, PlayerEntity player) {
        if (player == null || this.tardis() == null || this.tardis().isEmpty())
            return;

        Tardis tardis = this.tardis().get();
        ItemStack keyStack = player.getMainHandStack();

        if (tardis.hasGrowthExterior())
            return;

        tardis.getDesktop().setDoorPos(this);

        if (keyStack.getItem() instanceof KeyItem key && !tardis.siege().isActive()) {
            if (keyStack.isOf(AITItems.SKELETON_KEY) || key.isOf(keyStack, tardis)) {
                tardis.door().interactToggleLock((ServerPlayerEntity) player);
            } else {
                world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), SoundCategory.BLOCKS, 1F, 0.2F);
                player.sendMessage(Text.translatable("tardis.key.identity_error"), true); // TARDIS does not identify with key
            }

            return;
        }

        if (tardis.sonic().getExteriorSonic() != null) {
            SonicHandler handler = tardis.sonic();
            if (pos != null) {
                player.getInventory().offerOrDrop(handler.takeExteriorSonic());
                world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.BLOCKS, 1F,
                        0.2F);
            }

            return;
        }

        tardis.door().interact((ServerWorld) world, this.getPos(), (ServerPlayerEntity) player);
    }

    public Direction getFacing() {
        return this.getCachedState().get(HorizontalDirectionalBlock.FACING);
    }

    @Nullable @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void onEntityCollision(Entity entity) {
        if (!TardisServerWorld.isTardisDimension((ServerWorld) this.getWorld()))
            return;

        if (!this.isLinked())
            return;

        Tardis tardis = this.tardis().get();

        if (tardis.door().isClosed())
            return;

        if (DependencyChecker.hasPortals() && tardis.getExterior().getVariant().hasPortals())
            return;

        TravelHandler travel = tardis.travel();

        if (!tardis.flight().isFlying() && travel.getState() == TravelHandlerBase.State.FLIGHT && !tardis.areShieldsActive()) {
            TardisUtil.dropOutside(tardis, entity);
            return;
        }

        if (travel.getState() != TravelHandlerBase.State.LANDED)
            return;

        TardisUtil.teleportOutside(tardis, entity);
    }

    @Override
    public void onLinked() {
        this.tardis().ifPresent(tardis -> tardis.getDesktop().setDoorPos(this));
    }

    public void onBreak() {
        if (!this.isLinked())
            return;

        Tardis tardis = this.tardis().get();
        tardis.door().closeDoors();

        tardis.getDesktop().removeDoor(this);
    }

    public DirectedBlockPos getDirectedPos() {
        if (this.directedPos != null)
            return this.directedPos;

        this.directedPos = DirectedBlockPos.create(this.getPos(), (byte)
                RotationPropertyHelper.fromDirection(this.getFacing()));

        return this.directedPos;
    }
}
