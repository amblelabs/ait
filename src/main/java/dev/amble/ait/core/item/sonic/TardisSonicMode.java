package dev.amble.ait.core.item.sonic;

import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.util.TardisUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import dev.amble.ait.data.schema.sonic.SonicSchema;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedGlobalPos;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TardisSonicMode extends SonicMode {

    protected TardisSonicMode(int index) {
        super(index);
    }

    @Override
    public void tick(ItemStack stack, World world, LivingEntity user, int ticks, int ticksLeft) {
        if (!(world instanceof ServerWorld serverWorld) || !(user instanceof PlayerEntity player) || ticks % 10 != 0)
            return;

        this.process(stack, world, player);
    }

    public boolean process(ItemStack stack, World world, PlayerEntity user) {
        if (!(user instanceof ServerPlayerEntity player))
            return false;

        Tardis tardis = SonicItem.getTardisStatic(world, stack);

        if (tardis == null)
            return false;

        boolean isMainHand = user.getMainHandStack().getItem() == stack.getItem();
        if (isMainHand) {
            HitResult hitResult = SonicMode.getHitResult(user, 2);

            // summon to selected block
            return this.interactBlock(stack, world, player, BlockPos.ofFloored(hitResult.getPos()));
        }
        boolean isLookingUp = user.getPitch() < 0;

        if (isLookingUp) {
            // send tardis to flight and disengage handbrake
            tardis.travel().handbrake(false);
            tardis.travel().dematerialize();

            player.sendMessage(Text.translatable("sonic.ait.mode.tardis.flight"), true);

            return true;
        }

        // turn on handbrake and engage refueling
        tardis.travel().handbrake(true);
        tardis.fuel().refueling().set(true);

        player.sendMessage(Text.translatable("sonic.ait.mode.tardis.refuel"), true);

        return true;
    }

    private boolean interactBlock(ItemStack stack, World world, ServerPlayerEntity player, BlockPos pos) {
        // summon tardis to block
        Tardis tardis = SonicItem.getTardisStatic(world, stack);

        if (tardis == null)
            return false;

        // fail silently if in tardis dim
        if (TardisServerWorld.isTardisDimension(world)) return false;

        // get position of player
        CachedDirectedGlobalPos targetPos = CachedDirectedGlobalPos.create(player.getServerWorld().getRegistryKey(), pos, DirectedGlobalPos.getGeneralizedRotation(player.getMovementDirection()));

        if (!tardis.subsystems().get(SubSystem.Id.STABILISERS).isUsable()) {
            player.sendMessage(Text.translatable("sonic.ait.mode.tardis.does_not_have_stabilisers"), true);
            return false;
        }

        // check if player is within range of and in same world as TARDIS
        World tardisWorld = tardis.travel().position().getWorld();
        boolean inSameWorld = player.getWorld().equals(tardisWorld);
        boolean isNearTardis = TardisUtil.isNearTardis(player, tardis, 256);
        if (!inSameWorld || !isNearTardis) {
            player.sendMessage(Text.translatable("sonic.ait.mode.tardis.is_not_in_range"), true);
            return false;
        }

        tardis.travel().destination(targetPos);
        tardis.travel().autopilot(true);
        tardis.travel().dematerialize();

        // inform player
        player.sendMessage(Text.translatable("sonic.ait.mode.tardis.location_summon"), true);

        return true;
    }

    @Override
    public Text text() {
        return Text.translatable("sonic.ait.mode.tardis").formatted(Formatting.BLUE, Formatting.BOLD);
    }

    @Override
    public int maxTime() {
        return 2 * 20;
    }

    @Override
    public Identifier model(SonicSchema.Models models) {
        return models.tardis();
    }
}
