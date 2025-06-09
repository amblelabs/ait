package dev.amble.ait.core.item;

import dev.amble.ait.api.tardis.TardisComponent;
import dev.amble.ait.core.blockentities.ConsoleBlockEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.control.impl.DirectionControl;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.LoyaltyHandler;
import dev.amble.ait.core.tardis.handler.SubSystemHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.tardis.manager.TardisBuilder;
import dev.amble.ait.core.tardis.util.DefaultThemes;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.registry.impl.DesktopRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.world.World;

public class TardisItemBuilder extends Item {
    private final Identifier exterior;
    private final Identifier desktop;

    public TardisItemBuilder(Settings settings, Identifier exterior, Identifier desktopId) {
        super(settings);

        this.exterior = exterior;
        this.desktop = desktopId;
    }

    public TardisItemBuilder(Settings settings, Identifier exterior) {
        this(settings, exterior, null);
    }

    public TardisItemBuilder(Settings settings) {
        this(settings, null);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();

        if (!(player instanceof ServerPlayerEntity serverPlayer))
            return ActionResult.PASS;

        if (!(world instanceof ServerWorld serverWorld))
            return ActionResult.PASS;

        if (player.getItemCooldownManager().isCoolingDown(this))
            return ActionResult.FAIL;

        if (context.getHand() != Hand.MAIN_HAND)
            return ActionResult.SUCCESS;

        CachedDirectedGlobalPos pos = CachedDirectedGlobalPos.create(serverWorld,
                serverWorld.getBlockState(context.getBlockPos()).isReplaceable()
                        ? context.getBlockPos()
                        : context.getBlockPos().up(),
                DirectionControl.getGeneralizedRotation(RotationPropertyHelper.fromYaw(player.getBodyYaw())));

        BlockEntity entity = world.getBlockEntity(context.getBlockPos());

        if (entity instanceof ConsoleBlockEntity consoleBlock) {
            Tardis tardis = consoleBlock.tardis().get();

            if (tardis == null)
                return ActionResult.FAIL;

            TravelHandlerBase.State state = tardis.travel().getState();

            if (!(state == TravelHandlerBase.State.LANDED || state == TravelHandlerBase.State.FLIGHT))
                return ActionResult.PASS;

            consoleBlock.killControls();
            world.removeBlock(context.getBlockPos(), false);
            world.removeBlockEntity(context.getBlockPos());
            return ActionResult.SUCCESS;
        }

        // ExteriorCategorySchema category = CategoryRegistry.getInstance().get(this.exterior);

        TardisBuilder builder = new TardisBuilder().at(pos)
                .owner(serverPlayer)
                .<FuelHandler>with(TardisComponent.Id.FUEL, fuel -> {
                    fuel.setCurrentFuel(fuel.getMaxFuel());
                    fuel.enablePower();
                })
                .with(TardisComponent.Id.SUBSYSTEM, SubSystemHandler::repairAll)
                .<LoyaltyHandler> with(TardisComponent.Id.LOYALTY,
                loyalty -> {
                    loyalty.setMessageEnabled(false);
                    loyalty.set(serverPlayer, new Loyalty(Loyalty.Type.OWNER));
                    loyalty.setMessageEnabled(true);
                });

        if (this.exterior == null || this.desktop == null) {
            DefaultThemes.getRandom().apply(builder);
        } else {
            builder.exterior(ExteriorVariantRegistry.getInstance().get(this.exterior));
            builder.desktop(DesktopRegistry.getInstance().get(this.desktop));
        }

        ServerTardis created = ServerTardisManager.getInstance()
                .create(builder);

        player.sendMessage(Text.translatable("message.ait.unlocked_all", Text.translatable("message.ait.all_types").formatted(Formatting.GREEN)).formatted(Formatting.WHITE), false);


        if ( created == null ) {
            player.sendMessage(Text.translatable("message.ait.max_tardises"), true);
            return ActionResult.FAIL;
        }

        context.getStack().decrement(1);
        player.getItemCooldownManager().set(this, 20);
        return ActionResult.SUCCESS;
    }
}
