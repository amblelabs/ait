package dev.amble.ait.datagen.datagen_providers;

import java.util.function.Consumer;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITBlocks;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.advancement.TardisCriterions;
import dev.amble.ait.module.ModuleRegistry;

public class AITAchievementProvider extends FabricAdvancementProvider {
    public AITAchievementProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {

        ModuleRegistry.instance().iterator().forEachRemaining(module -> module.getDataGenerator().ifPresent(dataGenerator -> {
            dataGenerator.advancements(consumer);
        }));

        Advancement root = Advancement.Builder.create()
                .display(AITItems.CHARGED_ZEITON_CRYSTAL, Text.translatable("achievement.ait.title.root"),
                        Text.translatable("achievement.ait.description.root"), new Identifier("textures/entity/end_portal.png"), AdvancementFrame.TASK, false, false, false)
                .criterion("root", TardisCriterions.ROOT.conditions())
                .build(consumer, AITMod.MOD_ID + "/root");

        Advancement placeEnergizer = Advancement.Builder.create().parent(root)
                .display(AITBlocks.MATRIX_ENERGIZER, Text.translatable("achievement.ait.title.place_energizer"),
                        Text.translatable("achievement.ait.description.place_energizer"),
                        null,
                        AdvancementFrame.TASK, true, true, true)
                .criterion("place_energizer", TardisCriterions.PLACE_ENERGIZER.conditions())
                .build(consumer, AITMod.MOD_ID + "/place_energizer");

        Advancement placeCoral = Advancement.Builder.create().parent(placeEnergizer)
                .display(AITBlocks.CORAL_PLANT, Text.translatable("achievement.ait.title.place_coral"),
                        Text.translatable("achievement.ait.description.place_coral"),
                        null,
                        AdvancementFrame.TASK, true, true, true)
                .criterion("place_coral", TardisCriterions.PLACE_CORAL.conditions())
                .build(consumer, AITMod.MOD_ID + "/place_coral");

        Advancement firstEnter = Advancement.Builder.create().parent(placeCoral)
                .display(AITItems.TARDIS_ITEM, Text.translatable("achievement.ait.title.enter_tardis"),
                        Text.translatable("achievement.ait.description.enter_tardis"), null, AdvancementFrame.CHALLENGE, true,
                        true, false)
                .criterion("enter_tardis", TardisCriterions.ENTER_TARDIS.conditions())
                .build(consumer, AITMod.MOD_ID + "/enter_tardis"); // for now this is the root advancement, meaning
        // its the first
        // one
        // that shows

        Advancement feedPowerConverter = Advancement.Builder.create().parent(firstEnter)
                .display(AITBlocks.POWER_CONVERTER, Text.translatable("achievement.ait.title.feed_power_converter"),
                        Text.translatable("achievement.ait.description.feed_power_converter"),
                        null,
                        AdvancementFrame.TASK, true, true, true)
                .criterion("feed_power_converter", TardisCriterions.FEED_POWER_CONVERTER.conditions())
                .build(consumer, AITMod.MOD_ID + "/feed_power_converter");

        Advancement attackEyebrows = Advancement.Builder.create().parent(firstEnter)
                .display(AITItems.SONIC_SCREWDRIVER, Text.translatable("achievement.ait.title.attack_eyebrows"),
                        Text.translatable("achievement.ait.description.attack_eyebrows"),
                        null,
                        AdvancementFrame.TASK, true, true, true)
                .criterion("attack_eyebrows", TardisCriterions.ATTACK_EYEBROWS.conditions())
                .build(consumer, AITMod.MOD_ID + "/attack_eyebrows");

        Advancement brandNew = Advancement.Builder.create().parent(firstEnter)
                .display(AITItems.MUG, Text.translatable("achievement.ait.title.brand_new"),
                        Text.translatable("achievement.ait.description.brand_new"),
                        null,
                        AdvancementFrame.CHALLENGE, true, true, true)
                .criterion("brand_new", TardisCriterions.BRAND_NEW.conditions())
                .build(consumer, AITMod.MOD_ID + "/brand_new");

        Advancement ironKey = Advancement.Builder.create().parent(firstEnter)
                .display(AITItems.IRON_KEY, Text.translatable("achievement.ait.title.iron_key"),
                        Text.translatable("achievement.ait.description.iron_key"), null, AdvancementFrame.TASK, true, false, true)
                .criterion("iron_key", InventoryChangedCriterion.Conditions.items(AITItems.IRON_KEY))
                .build(consumer, AITMod.MOD_ID + "/iron_key");

        Advancement goldKey = Advancement.Builder.create().parent(ironKey)
                .display(AITItems.GOLD_KEY, Text.translatable("achievement.ait.title.gold_key"), Text.translatable("achievement.ait.description.gold_key"), null,
                        AdvancementFrame.TASK, true, false, true)
                .criterion("gold_key", InventoryChangedCriterion.Conditions.items(AITItems.GOLD_KEY))
                .build(consumer, AITMod.MOD_ID + "/gold_key");

        Advancement netheriteKey = Advancement.Builder.create().parent(goldKey)
                .display(AITItems.NETHERITE_KEY, Text.translatable("achievement.ait.title.netherite_key"), Text.translatable("achievement.ait.description.netherite_key"),
                        null, AdvancementFrame.TASK, true, true, true)
                .criterion("netherite_key", InventoryChangedCriterion.Conditions.items(AITItems.NETHERITE_KEY))
                .build(consumer, AITMod.MOD_ID + "/netherite_key");

        Advancement classicKey = Advancement.Builder.create().parent(netheriteKey)
                .display(AITItems.CLASSIC_KEY, Text.translatable("achievement.ait.title.classic_key"),
                        Text.translatable("achievement.ait.description.classic_key"), null, AdvancementFrame.TASK, true, true, true)
                .criterion("classic_key", InventoryChangedCriterion.Conditions.items(AITItems.CLASSIC_KEY))
                .build(consumer, AITMod.MOD_ID + "/classic_key");

        Advancement firstDemat = Advancement.Builder.create().parent(firstEnter)
                .display(Items.ENDER_EYE, Text.translatable("achievement.ait.title.first_demat"), Text.translatable(
                        "achievement.ait.description.first_demat"),
                        null, AdvancementFrame.CHALLENGE, true, true, true)
                .criterion("first_demat", TardisCriterions.TAKEOFF.conditions())
                .build(consumer, AITMod.MOD_ID + "/first_demat");

        Advancement firstCrash = Advancement.Builder.create().parent(firstDemat)
                .display(Items.TNT, Text.translatable("achievement.ait.title.first_crash"), Text.translatable(
                        "achievement.ait.description.first_crash"),
                        null, AdvancementFrame.CHALLENGE, true, true, true)
                .criterion("first_crash", TardisCriterions.CRASH.conditions())
                .build(consumer, AITMod.MOD_ID + "/first_crash");

        Advancement breakGrowth = Advancement.Builder.create().parent(firstEnter)
                .display(Items.OAK_LEAVES, Text.translatable("achievement.ait.title.break_growth"), Text.translatable(
                        "achievement.ait.description.break_growth"),
                        null, AdvancementFrame.TASK, true, false, true)
                .criterion("break_growth", TardisCriterions.VEGETATION.conditions())
                .build(consumer, AITMod.MOD_ID + "/break_growth");

        Advancement redecoration = Advancement.Builder.create().parent(firstEnter)
                .display(Items.PAINTING , Text.translatable("achievement.ait.title.redecorate"),
                        Text.translatable("achievement.ait.description.redecorate"), null, AdvancementFrame.TASK, true, false, true)
                .criterion("redecorate", TardisCriterions.REDECORATE.conditions())
                .build(consumer, AITMod.MOD_ID + "/redecorate");

        Advancement sonicWood = Advancement.Builder.create().parent(root)
                .display(AITItems.SONIC_SCREWDRIVER, Text.translatable("achievement.ait.title.ultimate_counter"),
                        Text.translatable("achievement.ait.description.ultimate_counter"), null, AdvancementFrame.TASK, true, false, true)
                .criterion("ultimate_counter", TardisCriterions.SONIC_WOOD.conditions())
                .build(consumer, AITMod.MOD_ID + "/ultimate_counter");

        Advancement axeTardis = Advancement.Builder.create().parent(firstEnter)
                .display(Items.IRON_AXE, Text.translatable("achievement.ait.title.forced_entry"),
                        Text.translatable("achievement.ait.description.forced_entry"), null, AdvancementFrame.TASK, true, false, true)
                .criterion("forced_entry", TardisCriterions.FORCED_ENTRY.conditions())
                .build(consumer, AITMod.MOD_ID + "/forced_entry");

        Advancement pilotHigh = Advancement.Builder.create().parent(firstDemat)
                .display(AITItems.ZEITON_DUST, Text.translatable("achievement.ait.title.pui"),
                        Text.translatable("achievement.ait.description.pui"), null, AdvancementFrame.TASK, true, false, true)
                .criterion("pui", TardisCriterions.PILOT_HIGH.conditions())
                .build(consumer, AITMod.MOD_ID + "/pui");

        Advancement reachPilot = Advancement.Builder.create().parent(firstEnter)
                .display(AITBlocks.CORAL_PLANT, Text.translatable("achievement.ait.title.bonding"),
                        Text.translatable("achievement.ait.description.bonding"), null, AdvancementFrame.TASK, true, false, true)
                .criterion("bonding", TardisCriterions.REACH_PILOT.conditions())
                .build(consumer, AITMod.MOD_ID + "/bonding");

        Advancement reachOwner = Advancement.Builder.create().parent(firstEnter)
                .display(AITItems.TARDIS_ITEM, Text.translatable("achievement.ait.title.owner_ship"),
                        Text.translatable("achievement.ait.description.owner_ship"), null, AdvancementFrame.CHALLENGE, true, true, true)
                .criterion("owner_ship", TardisCriterions.REACH_OWNER.conditions())
                .build(consumer, AITMod.MOD_ID + "/owner_ship");

        Advancement enableSubsystem = Advancement.Builder.create().parent(firstEnter)
                .display(AITBlocks.GENERIC_SUBSYSTEM, Text.translatable("achievement.ait.title.enable_subsystem"),
                        Text.translatable("achievement.ait.description.enable_subsystem"), null, AdvancementFrame.CHALLENGE, true, true, true)
                .criterion("enable_subsystem", TardisCriterions.ENABLE_SUBSYSTEM.conditions())
                .build(consumer, AITMod.MOD_ID + "/enable_subsystem");
        Advancement repairSubsystem = Advancement.Builder.create().parent(firstEnter)
                .display(AITItems.HAMMER, Text.translatable("achievement.ait.title.repair_subsystem"),
                        Text.translatable("achievement.ait.description.repair_subsystem"), null, AdvancementFrame.TASK, true, true, true)
                .criterion("repair_subsystem", TardisCriterions.REPAIR_SUBSYSTEM.conditions())
                .build(consumer, AITMod.MOD_ID + "/repair_subsystem");
        Advancement enginesPhase = Advancement.Builder.create().parent(firstDemat)
                .display(AITItems.DEMATERIALIZATION_CIRCUIT, Text.translatable("achievement.ait.title.engines_phase"),
                        Text.translatable("achievement.ait.description.engines_phase"), null, AdvancementFrame.CHALLENGE, true, true, true)
                .criterion("engines_phase", TardisCriterions.ENGINES_PHASE.conditions())
                .build(consumer, AITMod.MOD_ID + "/engines_phase");

        Advancement statRemote = Advancement.Builder.create().parent(ironKey)
                .display(AITItems.REMOTE_ITEM, Text.translatable("achievement.ait.title.remote"),
                        Text.translatable("achievement.ait.description.remote"), null, AdvancementFrame.CHALLENGE, true, false, true)
                .criterion("gain_remote", InventoryChangedCriterion.Conditions.items(AITItems.REMOTE_ITEM))
                .build(consumer, AITMod.MOD_ID + "/gain_remote");
    }
}
