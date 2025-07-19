package dev.amble.ait.core.blockentities;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.event.BlockPositionSource;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.PositionSource;
import net.minecraft.world.event.Vibrations;
import net.minecraft.world.event.listener.GameEventListener;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITTags;
import dev.amble.ait.core.blocks.MatrixEnergizerBlock;

public class MatrixEnergizerBlockEntity
        extends BlockEntity
        implements GameEventListener.Holder<Vibrations.VibrationListener>,
        Vibrations {
    private static final Logger LOGGER = LogUtils.getLogger();
    private Vibrations.ListenerData listenerData;
    private final Vibrations.VibrationListener listener;
    private final Vibrations.Callback callback = this.createCallback();
    private int lastVibrationFrequency;

    protected MatrixEnergizerBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
        this.listenerData = new Vibrations.ListenerData();
        this.listener = new Vibrations.VibrationListener(this);
    }

    public MatrixEnergizerBlockEntity(BlockPos pos, BlockState state) {
        this(AITBlockEntityTypes.MATRIX_ENERGIZER_BLOCK_ENTITY_TYPE, pos, state);
    }

    public Vibrations.Callback createCallback() {
        return new VibrationCallback(this.getPos());
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.lastVibrationFrequency = nbt.getInt("last_vibration_frequency");
        if (nbt.contains("listener", NbtElement.COMPOUND_TYPE)) {
            Vibrations.ListenerData.CODEC.parse(new Dynamic<>(
                    NbtOps.INSTANCE, nbt.getCompound("listener")))
                    .resultOrPartial(LOGGER::error).ifPresent(listener -> {
                this.listenerData = listener;
            });
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putInt("last_vibration_frequency", this.lastVibrationFrequency);
        Vibrations.ListenerData.CODEC.encodeStart(NbtOps.INSTANCE, this.listenerData)
                .resultOrPartial(LOGGER::error).ifPresent(listenerNbt -> nbt.put("listener", listenerNbt));
    }

    @Override
    public Vibrations.ListenerData getVibrationListenerData() {
        return this.listenerData;
    }

    @Override
    public Vibrations.Callback getVibrationCallback() {
        return this.callback;
    }

    public int getLastVibrationFrequency() {
        return this.lastVibrationFrequency;
    }

    public void setLastVibrationFrequency(int lastVibrationFrequency) {
        this.lastVibrationFrequency = lastVibrationFrequency;
    }

    @Override
    public Vibrations.VibrationListener getEventListener() {
        return this.listener;
    }

    protected class VibrationCallback
            implements Vibrations.Callback {
        protected final BlockPos pos;
        private final PositionSource positionSource;

        public VibrationCallback(BlockPos pos) {
            this.pos = pos;
            this.positionSource = new BlockPositionSource(pos);
        }

        @Override
        public int getRange() {
            return 1;
        }

        @Override
        public PositionSource getPositionSource() {
            return this.positionSource;
        }

        @Override
        public boolean triggersAvoidCriterion() {
            return true;
        }

        @Override
        public boolean accepts(ServerWorld world, BlockPos pos, GameEvent event, @Nullable GameEvent.Emitter emitter) {
            if (pos.equals(this.pos) && (event == GameEvent.BLOCK_DESTROY || event == GameEvent.BLOCK_PLACE)) {
                return false;
            }
            return MatrixEnergizerBlock.isInactive(MatrixEnergizerBlockEntity.this.getCachedState());
        }

        @Override
        public TagKey<GameEvent> getTag() {
            return AITTags.GameEvents.MATRIX_CAN_LISTEN;
        }

        @Override
        public void accept(ServerWorld world, BlockPos pos, GameEvent event, @Nullable Entity sourceEntity, @Nullable Entity entity, float distance) {
            BlockState blockState = MatrixEnergizerBlockEntity.this.getCachedState();
            if (MatrixEnergizerBlock.isInactive(blockState)) {
                MatrixEnergizerBlockEntity.this.setLastVibrationFrequency(Vibrations.getFrequency(event));
                Block block = blockState.getBlock();
                if (event.equals(GameEvent.SHRIEK) && block instanceof MatrixEnergizerBlock matrixEnergizerBlock) {
                    matrixEnergizerBlock.setActive(world, this.pos, blockState,
                            MatrixEnergizerBlockEntity.this.getLastVibrationFrequency());
                }
            }
        }

        @Override
        public void onListen() {
            MatrixEnergizerBlockEntity.this.markDirty();
        }

        @Override
        public boolean requiresTickingChunksAround() {
            return true;
        }
    }
}
