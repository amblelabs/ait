package dev.amble.ait.core.engine.impl;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.engine.block.generic.GenericStructureSystemBlockEntity;
import dev.amble.ait.core.tardis.ServerTardis;
import dev.amble.ait.core.tardis.manager.BiodataRestorationManager;
import dev.amble.ait.core.tardis.util.BiodataRestorationEffects;
import dev.amble.ait.data.Loyalty;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

/**
 * Home-bound system which restores the biodata of trusted players after lethal
 * damage, at a substantial cost to the TARDIS and its relationship with them.
 */
public final class BiodataRestoration extends HomeBoundSubSystem {

    public BiodataRestoration() {
        super(Id.BIODATA_RESTORATION);
    }

    @Override
    public Item asItem() {
        return Items.TOTEM_OF_UNDYING;
    }

    @Override
    public boolean canInstall(GenericStructureSystemBlockEntity core, ServerPlayerEntity player, ItemStack stack) {
        if (!AITMod.CONFIG.biodataRestorationAvailable || !super.canInstall(core, player, stack))
            return false;

        Loyalty.Type loyalty = this.tardis().loyalty().get(player).type();
        int rejectionChance = switch (loyalty) {
            case REJECT -> AITMod.CONFIG.biodataRestorationRejectInsertionChance;
            case NEUTRAL -> AITMod.CONFIG.biodataRestorationNeutralInsertionChance;
            case COMPANION -> AITMod.CONFIG.biodataRestorationCompanionInsertionChance;
            case PILOT -> AITMod.CONFIG.biodataRestorationPilotInsertionChance;
            case OWNER -> AITMod.CONFIG.biodataRestorationOwnerInsertionChance;
        };

        if (player.getRandom().nextInt(100) >= Math.max(0, Math.min(100, rejectionChance)))
            return true;

        if (loyalty == Loyalty.Type.REJECT) {
            int fireSeconds = Math.max(0, AITMod.CONFIG.biodataRestorationRejectFireSeconds);
            if (fireSeconds > 0)
                player.setOnFireFor(fireSeconds);
        }

        this.serverTardis().homeSystems().rejectInteraction(player, core.getPos());
        BiodataRestorationEffects.rejectInteraction(player, core.getPos());
        return false;
    }

    @Override
    public void onInstalled(GenericStructureSystemBlockEntity core, ServerPlayerEntity player, ItemStack stack) {
        super.onInstalled(core, player, stack);

        if (core.getWorld() instanceof ServerWorld world)
            BiodataRestorationEffects.spawnBurst(world, Vec3d.ofCenter(core.getPos()).add(0, 0.5, 0));

        ServerTardis tardis = this.serverTardis();
        boolean insertedByCompanion = tardis.loyalty().get(player).isOf(Loyalty.Type.COMPANION);
        tardis.loyalty().subLevel(player, Math.max(0, AITMod.CONFIG.biodataRestorationInsertionLoyaltyCost));

        MinecraftServer server = player.getServer();
        if (server == null)
            return;

        Text warning = Text.translatable("tardis.message.biodata_restoration.forced")
                .formatted(Formatting.DARK_RED);
        for (ServerPlayerEntity online : server.getPlayerManager().getPlayerList()) {
            if ((online == player && insertedByCompanion)
                    || tardis.loyalty().get(online).isOf(Loyalty.Type.COMPANION))
                online.sendMessage(warning, false);
        }
    }

    @Override
    protected void tickAtHome(MinecraftServer server) {
        BiodataRestorationManager.setActive(this.serverTardis(), true);
    }

    @Override
    protected void onInstallationChanged(boolean installed) {
        if (this.isServer())
            BiodataRestorationManager.setActive(this.serverTardis(), installed);
    }

    public boolean canProtect(ServerPlayerEntity player) {
        if (player == null)
            return false;

        this.validateNow();
        return this.isOperational() && this.tardis().loyalty().get(player).isOf(Loyalty.Type.COMPANION);
    }

    @Override
    protected boolean meetsAdditionalConditions() {
        return AITMod.CONFIG.biodataRestorationAvailable;
    }
}
