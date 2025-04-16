package dev.amble.ait.core.blockentities;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.serialization.DataResult;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blocks.AbstractRoundelBlock;
import dev.amble.ait.core.blocks.RoundelBlock;
import dev.amble.ait.core.roundels.RoundelPattern;
import dev.amble.ait.core.roundels.RoundelPatterns;
import dev.amble.ait.core.roundels.RoundelType;

public class RoundelBlockEntity
        extends InteriorLinkableBlockEntity
        implements Nameable {
    public static final int MAX_PATTERN_COUNT = 6;
    public static final String PATTERNS_KEY = "Patterns";
    public static final String PATTERN_KEY = "Pattern";
    public static final String COLOR_KEY = "Color";
    @Nullable private Text customName;
    private DyeColor baseColor;
    private boolean emissive;
    private BlockState dynamicTex;
    @Nullable private NbtList patternListNbt;
    @Nullable private List<RoundelType> patterns;

    {
        TardisEvents.LOSE_POWER.register(tardis -> {
            ServerWorld world = tardis.asServer().getInteriorWorld();
            if (world == null) return;
            if (world.getBlockState(this.getPos()).getBlock() instanceof AbstractRoundelBlock)
                world.setBlockState(this.getPos(), world.getBlockState(this.getPos()).with(AbstractRoundelBlock.LEVEL_15, 0));
        });
        TardisEvents.REGAIN_POWER.register(tardis -> {
            ServerWorld world = tardis.asServer().getInteriorWorld();
            if (world == null) return;
            if (world.getBlockState(this.getPos()).getBlock() instanceof AbstractRoundelBlock)
                world.setBlockState(this.getPos(), world.getBlockState(this.getPos()).with(AbstractRoundelBlock.LEVEL_15, 11));
        });
    }

    public RoundelBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ROUNDEL_BLOCK_ENTITY_TYPE, pos, state);
        this.baseColor = ((AbstractRoundelBlock)state.getBlock()).getColor();
        this.emissive = false;
        if (this.dynamicTex == null)
            this.dynamicTex = Blocks.WHITE_CONCRETE.getDefaultState();
    }

    @Override
    public @Nullable Object getRenderData() {
        return this;
    }

    public RoundelBlockEntity(BlockPos pos, BlockState state, DyeColor baseColor) {
        this(pos, state);
        this.baseColor = baseColor;
    }

    @Nullable public static NbtList getPatternListNbt(ItemStack stack) {
        NbtList nbtList = null;
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound != null && nbtCompound.contains(PATTERNS_KEY, NbtElement.LIST_TYPE)) {
            nbtList = nbtCompound.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE).copy();
        }
        return nbtList;
    }

    public void readFrom(ItemStack stack, DyeColor baseColor) {
        this.baseColor = baseColor;
        this.readFrom(stack);
    }

    public void readFrom(ItemStack stack) {
        this.patternListNbt = RoundelBlockEntity.getPatternListNbt(stack);
        this.patterns = null;
        this.customName = stack.hasCustomName() ? stack.getName() : null;
    }

    @Override
    public Text getName() {
        if (this.customName != null) {
            return this.customName;
        }
        return Text.translatable("block.ait.roundel");
    }

    @Override
    @Nullable public Text getCustomName() {
        return this.customName;
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        if (this.dynamicTex != null) {
            nbt.put("DynamicTex", NbtHelper.fromBlockState(this.dynamicTex));
        }
        super.writeNbt(nbt);
        if (this.patternListNbt != null) {
            nbt.put(PATTERNS_KEY, this.patternListNbt);
        }
        if (this.customName != null) {
            nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
        }

    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (this.world instanceof ServerWorld world1) {
            for (ServerPlayerEntity player : world1.getChunkManager().threadedAnvilChunkStorage.getPlayersWatchingChunk(new ChunkPos(this.pos))) {
                player.networkHandler.sendPacket(this.toUpdatePacket());
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        DataResult<BlockState> blockStateResult = BlockState.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("DynamicTex"));
        this.setDynamicTex(blockStateResult.result().orElse(null));
        super.readNbt(nbt);
        if (nbt.contains("CustomName", NbtElement.STRING_TYPE)) {
            this.customName = Text.Serializer.fromJson(nbt.getString("CustomName"));
        }
        this.patternListNbt = nbt.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE);
        this.patterns = null;
    }

    public static int getPatternCount(ItemStack stack) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound != null && nbtCompound.contains(PATTERNS_KEY)) {
            return nbtCompound.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE).size();
        }
        return 0;
    }

    public List<RoundelType> getPatterns() {
        if (this.patterns == null) {
            this.patterns = RoundelBlockEntity.getPatternsFromNbt(this.baseColor, this.patternListNbt);
        }
        return this.patterns;
    }

    public static List<RoundelType> getPatternsFromNbt(DyeColor baseColor, @Nullable NbtList patternListNbt) {
        ArrayList<RoundelType> list = Lists.newArrayList();
        list.add(new RoundelType(RoundelPatterns.BASE, baseColor, false));
        if (patternListNbt != null) {
            for (int i = 0; i < patternListNbt.size(); ++i) {
                NbtCompound nbtCompound = patternListNbt.getCompound(i);
                RoundelPattern pattern = RoundelPatterns.getInstance().get(Identifier.tryParse(nbtCompound.getString(PATTERN_KEY)));
                if (pattern == null) continue;
                int j = nbtCompound.getInt(COLOR_KEY);
                boolean b = nbtCompound.getBoolean("Emissive");
                list.add(new RoundelType(pattern, DyeColor.byId(j), b));
            }
        }
        return list;
    }

    public static void loadFromItemStack(ItemStack stack) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound == null || !nbtCompound.contains(PATTERNS_KEY, NbtElement.LIST_TYPE)) {
            return;
        }
        NbtList nbtList = nbtCompound.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE);
        if (nbtList.isEmpty()) {
            return;
        }
        nbtList.remove(nbtList.size() - 1);
        if (nbtList.isEmpty()) {
            nbtCompound.remove(PATTERNS_KEY);
        }
        BlockItem.setBlockEntityNbt(stack, AITBlockEntityTypes.ROUNDEL_BLOCK_ENTITY_TYPE, nbtCompound);
    }

    public ItemStack getPickStack() {
        ItemStack itemStack = new ItemStack(RoundelBlock.getForColor(this.baseColor));
        if (this.patternListNbt != null && !this.patternListNbt.isEmpty()) {
            NbtCompound nbtCompound = new NbtCompound();
            if (this.dynamicTex != null) {
                nbtCompound.put("DynamicTex", NbtHelper.fromBlockState(this.dynamicTex));
            }
            nbtCompound.put(PATTERNS_KEY, this.patternListNbt.copy());
            BlockItem.setBlockEntityNbt(itemStack, this.getType(), nbtCompound);
        }

        if (this.customName != null) {
            itemStack.setCustomName(this.customName);
        }
        return itemStack;
    }

    public DyeColor getColorForState() {
        return this.baseColor;
    }

    @Nullable @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public void setDynamicTex(BlockState state) {
        this.dynamicTex = state;
        this.markDirty();
    }

    public BlockState getDynamicTextureBlockState() {
        return this.dynamicTex;
    }
}
