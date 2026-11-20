package dev.amble.ait.core.item;

import java.util.UUID;

import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.tardis.comms.CommsMessages;
import dev.amble.ait.core.tardis.comms.TardisComms;
import dev.amble.ait.core.tardis.comms.TardisPhones;
import org.jetbrains.annotations.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PhoneItem extends Item {

    private static final String SESSION = "Session";
    private static final String TARDIS = "Tardis";
    private static final String ANCHOR_X = "AnchorX";
    private static final String ANCHOR_Y = "AnchorY";
    private static final String ANCHOR_Z = "AnchorZ";
    private static final int USE_COOLDOWN = 10;

    public PhoneItem(Settings settings) {
        super(settings);
    }

    public static ItemStack create(TardisPhones.Handset handset) {
        ItemStack stack = new ItemStack(AITItems.PHONE);
        NbtCompound nbt = stack.getOrCreateNbt();

        nbt.putUuid(SESSION, handset.session());
        nbt.putUuid(TARDIS, handset.line());
        nbt.putDouble(ANCHOR_X, handset.anchor().x);
        nbt.putDouble(ANCHOR_Y, handset.anchor().y);
        nbt.putDouble(ANCHOR_Z, handset.anchor().z);

        return stack;
    }

    @Nullable public static UUID session(ItemStack stack) {
        if (!(stack.getItem() instanceof PhoneItem))
            return null;

        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.containsUuid(SESSION) ? nbt.getUuid(SESSION) : null;
    }

    @Nullable public static UUID tardis(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        return nbt != null && nbt.containsUuid(TARDIS) ? nbt.getUuid(TARDIS) : null;
    }

    @Nullable public static Vec3d anchor(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();

        if (nbt == null || !nbt.contains(ANCHOR_X))
            return null;

        return new Vec3d(nbt.getDouble(ANCHOR_X), nbt.getDouble(ANCHOR_Y), nbt.getDouble(ANCHOR_Z));
    }

    public static boolean isFor(ItemStack stack, UUID tardis) {
        return tardis.equals(tardis(stack));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (world.isClient() || !(user instanceof ServerPlayerEntity player))
            return TypedActionResult.success(stack, world.isClient());

        UUID session = session(stack);
        TardisPhones.Handset handset = session == null ? null : TardisPhones.get(session);

        if (handset == null) {
            stack.setCount(0);
            return TypedActionResult.consume(stack);
        }

        TardisPhones.claim(session, player);

        UUID line = handset.line();
        player.getItemCooldownManager().set(this, USE_COOLDOWN);

        switch (TardisComms.status(line)) {
            case INCOMING -> TardisComms.answer(player.getServer(), player, line);
            case OUTGOING, CONNECTED, RECORDING -> TardisComms.endFor(player.getServer(), line);
            case IDLE -> CommsMessages.actionBar(player, Text.translatable("tardis.message.comms.no_call").formatted(Formatting.GRAY));
        }

        return TypedActionResult.consume(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (world.isClient())
            return;

        UUID session = session(stack);

        if (session == null || TardisPhones.get(session) == null) {
            stack.setCount(0);
            return;
        }

        if (entity instanceof ServerPlayerEntity player)
            TardisPhones.claim(session, player);
        else
            stack.setCount(0);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (!(entity instanceof ServerPlayerEntity target) || !(user instanceof ServerPlayerEntity player))
            return entity instanceof PlayerEntity ? ActionResult.SUCCESS : ActionResult.PASS;

        UUID session = session(stack);

        if (session == null || !TardisPhones.isActive(session, player.getUuid()))
            return ActionResult.PASS;

        ItemStack handed = stack.copy();

        if (!target.getInventory().insertStack(handed))
            return ActionResult.FAIL;

        player.setStackInHand(hand, ItemStack.EMPTY);
        TardisPhones.claim(session, target);

        CommsMessages.actionBar(player, Text.translatable("tardis.message.comms.handed", target.getDisplayName()).formatted(Formatting.GRAY));
        CommsMessages.actionBar(target, Text.translatable("tardis.message.comms.handed_you", player.getDisplayName()).formatted(Formatting.GOLD));
        return ActionResult.CONSUME;
    }
}
