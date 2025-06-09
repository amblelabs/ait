package dev.amble.ait.core.blockentities;

import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.AITBlockEntityTypes;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.AITSounds;
import dev.amble.ait.core.blocks.WaypointBankBlock;
import dev.amble.ait.core.item.WaypointItem;
import dev.amble.ait.core.util.StackUtil;
import dev.amble.ait.data.Waypoint;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WaypointBankBlockEntity extends InteriorLinkableBlockEntity {

    private final WaypointData[] waypoints = new WaypointData[WaypointBankBlock.MAX_COUNT];
    private int selected = -1;

    public WaypointBankBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.WAYPOINT_BANK_BLOCK_ENTITY_TYPE, pos, state);
    }

    public void unselect() {
        this.selected = -1;
    }

    public void dropItems() {
        List<ItemStack> stacks = new ArrayList<>();

        for (WaypointData data : this.waypoints) {
            if (data == null)
                continue;

            stacks.add(data.toStack());
        }

        StackUtil.scatter(this.world, this.pos, stacks);
    }

    public ActionResult onUse(World world, BlockState state, PlayerEntity player, Hand hand, int slot) {
        if (!this.isLinked())
            return ActionResult.FAIL;

        ItemStack stack = player.getStackInHand(hand);

        if (world.isClient())
            return ActionResult.SUCCESS;

        if (stack.getItem() instanceof WaypointItem)
            return this.insert(state, stack, slot);

        if (player.isSneaking())
            return this.take(state, player, slot);

        return this.select(state, slot);
    }

    private ActionResult take(BlockState state, PlayerEntity player, int slot) {
        if (this.selected != slot)
            return ActionResult.FAIL;

        WaypointData waypoint = this.waypoints[slot];

        if (waypoint == null)
            return ActionResult.FAIL;

        this.waypoints[slot] = null;
        this.sync(state);

        player.giveItemStack(waypoint.toStack());
        return ActionResult.SUCCESS;
    }

    private ActionResult insert(BlockState state, ItemStack stack, int slot) {
        WaypointData inserted = WaypointData.fromStack(stack);

        if (inserted == null)
            return ActionResult.FAIL;

        stack.decrement(1);

        this.waypoints[slot] = inserted;
        this.sync(state);

        return ActionResult.SUCCESS;
    }

    private ActionResult activate(int slot) {
        if (!this.isLinked())
            return ActionResult.FAIL;

        WaypointData data = this.waypoints[slot];

        if (data == null)
            return ActionResult.FAIL;

        this.tardis().get().travel().forceDestination(data.pos);

        this.world.playSound(null, this.getPos(), AITSounds.WAYPOINT_ACTIVATE, SoundCategory.BLOCKS);
        return ActionResult.SUCCESS;
    }

    private ActionResult select(BlockState state, int slot) {
        if (this.selected == slot && this.isLinked())
            return this.activate(slot);

        this.selected = slot;
        this.sync(state);

        return ActionResult.SUCCESS;
    }

    protected void sync(BlockState state) {
        this.markDirty();
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        NbtList waypoints = nbt.getList("waypoints", NbtElement.COMPOUND_TYPE);

        for (int i = 0; i < this.waypoints.length; i++) {
            this.waypoints[i] = WaypointData.fromNbt(waypoints.getCompound(i));
        }

        this.selected = nbt.getShort("selected");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        NbtList waypoints = new NbtList();

        for (int i = 0; i < this.waypoints.length; i++) {
            WaypointData data = this.waypoints[i];
            NbtCompound compound = new NbtCompound();

            if (data != null)
                data.toNbt(compound);

            waypoints.add(i, compound);
        }

        nbt.put("waypoints", waypoints);
        nbt.putShort("selected", (short) selected);
    }

    @Nullable @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    public DoubleBlockHalf getHalf() {
        return null;
    }

    public WaypointData[] getWaypoints() {
        return waypoints;
    }

    public int getSelected() {
        return selected;
    }

    public record WaypointData(int color, String name, CachedDirectedGlobalPos pos) {

        private WaypointData(int color, Waypoint waypoint) {
            this(color, waypoint.name(), waypoint.getPos());
        }

        public static WaypointData fromStack(ItemStack stack) {
            if (!stack.getOrCreateNbt().contains(WaypointItem.POS_KEY))
                return null;

            int color = ((DyeableItem) AITItems.WAYPOINT_CARTRIDGE).getColor(stack);
            Waypoint waypoint = Waypoint.fromStack(stack);

            return new WaypointData(color, waypoint);
        }

        public ItemStack toStack() {
            ItemStack result = new ItemStack(AITItems.WAYPOINT_CARTRIDGE);

            WaypointItem.setPos(result, this.pos);
            result.setCustomName(Text.literal(this.name));

            if (this.color != WaypointItem.DEFAULT_COLOR)
                ((DyeableItem) AITItems.WAYPOINT_CARTRIDGE).setColor(result, this.color);

            return result;
        }

        public void toNbt(NbtCompound nbt) {
            nbt.putInt("color", this.color);
            nbt.putString("name", this.name);

            if (this.pos != null)
                nbt.put("pos", this.pos.toNbt());
        }

        public static WaypointData fromNbt(NbtCompound nbt) {
            if (nbt.isEmpty())
                return null;

            int color = nbt.getInt("color");
            String name = nbt.getString("name");

            CachedDirectedGlobalPos pos = CachedDirectedGlobalPos.fromNbt(nbt.getCompound("pos"));

            return new WaypointData(color, name, pos);
        }
    }
}
