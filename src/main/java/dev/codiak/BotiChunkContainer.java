/* (C) TAMA Studios 2025 */
package dev.codiak;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BotiChunkContainer {
    final World level;
    final BlockState state;
    FluidState fluidState;
    final BlockPos pos;
    final int light;
    boolean IsTile;
    boolean IsFluid;
    NbtCompound entityTag;

    public BotiChunkContainer(World level, BlockState state, FluidState fluidState, BlockPos pos, int light) {
        this.state = state;
        this.fluidState = fluidState;
        this.pos = pos;
        this.light = light;
        this.IsFluid = true;
        this.level = level;
    }

    public BotiChunkContainer(
            World level, BlockState state, BlockPos pos, int light, boolean IsTile, NbtCompound tileTag) {
        this.state = state;
        this.IsTile = IsTile;
        this.entityTag = tileTag;
        this.pos = pos;
        this.light = light;
        this.level = level;
    }

    public BotiChunkContainer(World world, BlockState state, BlockPos pos, int light) {
        this.level = world;
        this.state = state;
        this.pos = pos;
        this.light = light;
    }

    public void encode(@NotNull PacketByteBuf buf) {
        buf.writeBlockPos(pos);

        // Write BlockState as raw ID (includes properties)
        int stateId = Block.STATE_IDS.getRawId(state);
        buf.writeVarInt(stateId);
        buf.writeVarInt(light);
        buf.writeBoolean(IsFluid);
        buf.writeBoolean(IsTile);

        if (IsFluid) {
            int fluidStateId = Fluid.STATE_IDS.getRawId(fluidState);
            buf.writeVarInt(fluidStateId);
        }

        if (IsTile) {
            buf.writeNbt(level.getBlockEntity(pos).createNbtWithIdentifyingData());
        }
    }

    @Contract("_ -> new")
    public static @NotNull BotiChunkContainer decode(@NotNull PacketByteBuf buf) {
        BlockPos pos = buf.readBlockPos();

        // Read BlockState
        BlockState state = Block.STATE_IDS.get(buf.readVarInt());

        int light = buf.readVarInt();
        boolean IsFluid = buf.readBoolean();
        boolean IsTile = buf.readBoolean();
        if (IsFluid) {
            int id = buf.readVarInt();
            FluidState fluid = Fluid.STATE_IDS.get(id);
            return new BotiChunkContainer(MinecraftClient.getInstance().world, state, fluid, pos, light);
        }
        if (IsTile) {
            return new BotiChunkContainer(MinecraftClient.getInstance().world, state, pos, light, true, buf.readNbt());
        }
        return new BotiChunkContainer(MinecraftClient.getInstance().world, state, pos, light);
    }

    public static void encodeList(List<BotiChunkContainer> list, PacketByteBuf buf) {
        buf.writeVarInt(list.size());
        for (BotiChunkContainer container : list) {
            container.encode(buf);
        }
    }

    public static List<BotiChunkContainer> decodeList(PacketByteBuf buf) {
        int size = buf.readVarInt();
        List<BotiChunkContainer> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(BotiChunkContainer.decode(buf));
        }
        return list;
    }
}
