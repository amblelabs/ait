package dev.amble.ait.core.blockentities;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import org.jetbrains.annotations.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.blocks.AbstractRoundelBlock;
import dev.amble.ait.core.blocks.RoundelBlock;
import dev.amble.ait.core.roundels.RoundelPattern;
import dev.amble.ait.core.roundels.RoundelPatterns;

public class RoundelBlockEntity
        extends BlockEntity
        implements Nameable {
    public static final int MAX_PATTERN_COUNT = 6;
    public static final String PATTERNS_KEY = "Patterns";
    public static final String PATTERN_KEY = "Pattern";
    public static final String COLOR_KEY = "Color";
    @Nullable private Text customName;
    private DyeColor baseColor;
    private BlockState dynamicTex;
    @Nullable private NbtList patternListNbt;
    @Nullable private List<Pair<RoundelPattern, DyeColor>> patterns;

    public RoundelBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.ROUNDEL_BLOCK_ENTITY_TYPE, pos, state);
        this.baseColor = ((AbstractRoundelBlock)state.getBlock()).getColor();
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
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.patternListNbt != null) {
            nbt.put(PATTERNS_KEY, this.patternListNbt);
        }
        if (this.customName != null) {
            nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
        }
        if (this.dynamicTex != null) {
            nbt.put("DynamicTex", NbtHelper.fromBlockState(this.dynamicTex));
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("CustomName", NbtElement.STRING_TYPE)) {
            this.customName = Text.Serializer.fromJson(nbt.getString("CustomName"));
        }
        this.patternListNbt = nbt.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE);
        this.patterns = null;

        if (this.getWorld() == null) return;

        if (nbt.contains("DynamicTex", NbtElement.COMPOUND_TYPE)) {
            this.dynamicTex = NbtHelper.toBlockState(this.getWorld()
                    .createCommandRegistryWrapper(RegistryKeys.BLOCK), nbt.getCompound("DynamicTex"));
        }
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    public static int getPatternCount(ItemStack stack) {
        NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
        if (nbtCompound != null && nbtCompound.contains(PATTERNS_KEY)) {
            return nbtCompound.getList(PATTERNS_KEY, NbtElement.COMPOUND_TYPE).size();
        }
        return 0;
    }

    public List<Pair<RoundelPattern, DyeColor>> getPatterns() {
        if (this.patterns == null) {
            this.patterns = RoundelBlockEntity.getPatternsFromNbt(this.baseColor, this.patternListNbt);
        }
        return this.patterns;
    }

    public static List<Pair<RoundelPattern, DyeColor>> getPatternsFromNbt(DyeColor baseColor, @Nullable NbtList patternListNbt) {
        ArrayList<Pair<RoundelPattern, DyeColor>> list = Lists.newArrayList();
        list.add(Pair.of(RoundelPatterns.BASE, baseColor));
        if (patternListNbt != null) {
            for (int i = 0; i < patternListNbt.size(); ++i) {
                NbtCompound nbtCompound = patternListNbt.getCompound(i);
                RoundelPattern pattern = RoundelPatterns.getInstance().get(Identifier.tryParse(nbtCompound.getString(PATTERN_KEY)));
                if (pattern == null) continue;
                int j = nbtCompound.getInt(COLOR_KEY);
                list.add(Pair.of(pattern, DyeColor.byId(j)));
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

    public BlockState getDynamicTextureBlockState() {
        return this.dynamicTex;
    }
}
