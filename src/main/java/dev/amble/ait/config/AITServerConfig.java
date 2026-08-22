package dev.amble.ait.config;

import java.util.List;

import com.google.common.collect.Lists;
import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITDimensions;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;

public class AITServerConfig {

    public static final String CATEGORY = "server";
    public static final String HOME_SYSTEMS_CATEGORY = "home_systems";

    public static final ConfigClassHandler<AITServerConfig> INSTANCE = ConfigClassHandler.createBuilder(AITServerConfig.class)
            .id(YACLPlatform.rl(AITMod.MOD_ID, "server"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(YACLPlatform.getConfigDir().resolve("ait-server.json5"))
                    .setJson5(true)
                    .build())
            .build();

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean minifyJson = false;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean ghostMonument = true;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 1, max = 256)
    @SerialEntry public int homeRadius = 100;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 1, max = 100)
    @SerialEntry public int exactHomeLoyaltyMultiplier = 2;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 1, max = 255)
    @SerialEntry public int exactHomeSaturationLevel = 2;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 1, max = 255)
    @SerialEntry public int exactHomeOwnerResistanceLevel = 5;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 25_000)
    @SerialEntry public int beaconEmanationFuelPerSecond = 40;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @MasterTickBox({
            "biodataRestorationRejectInsertionChance", "biodataRestorationNeutralInsertionChance",
            "biodataRestorationCompanionInsertionChance", "biodataRestorationPilotInsertionChance",
            "biodataRestorationOwnerInsertionChance", "biodataRestorationRejectFireSeconds",
            "biodataRestorationInsertionLoyaltyCost", "biodataRestorationRescueFuelCost",
            "biodataRestorationRescueLoyaltyCost", "biodataRestorationSubsystemDamageMin",
            "biodataRestorationSubsystemDamageMax", "biodataRestorationJealousyPenalty",
            "biodataRestorationHailMaryTeleportFuelCost", "preferTotemsOverBiodataRestoration"
    })
    @SerialEntry public boolean biodataRestorationAvailable = true;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int biodataRestorationRejectInsertionChance = 100;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int biodataRestorationNeutralInsertionChance = 100;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int biodataRestorationCompanionInsertionChance = 50;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int biodataRestorationPilotInsertionChance = 35;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int biodataRestorationOwnerInsertionChance = 0;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 3600)
    @SerialEntry public int biodataRestorationRejectFireSeconds = 15;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 500)
    @SerialEntry public int biodataRestorationInsertionLoyaltyCost = 20;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 1_000_000)
    @SerialEntry public int biodataRestorationRescueFuelCost = 1000;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 500)
    @SerialEntry public int biodataRestorationRescueLoyaltyCost = 100;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 1250)
    @SerialEntry public int biodataRestorationSubsystemDamageMin = 50;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 1250)
    @SerialEntry public int biodataRestorationSubsystemDamageMax = 300;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 500)
    @SerialEntry public int biodataRestorationJealousyPenalty = 300;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 1_000_000)
    @SerialEntry public int biodataRestorationHailMaryTeleportFuelCost = 200;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean preferTotemsOverBiodataRestoration = true;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @MasterTickBox({
            "homeDefenseAffectsBosses", "homeDefenseRadius", "homeDefenseDamage",
            "homeDefenseIntervalSeconds", "homeDefenseEngineDamagePerKill", "homeDefenseFuelPerSecond"
    })
    @SerialEntry public boolean homeDefenseAvailable = true;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean homeDefenseAffectsBosses = true;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 1, max = 256)
    @SerialEntry public int homeDefenseRadius = 100;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @FloatField(min = 0, max = 2048)
    @SerialEntry public float homeDefenseDamage = 8f;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 1, max = 300)
    @SerialEntry public int homeDefenseIntervalSeconds = 2;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 1250)
    @SerialEntry public int homeDefenseEngineDamagePerKill = 5;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 25_000)
    @SerialEntry public int homeDefenseFuelPerSecond = 300;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @DoubleField(min = 1, max = 100)
    @SerialEntry public double homeRefuelMultiplier = 2.0;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 25_000)
    @SerialEntry public int sculkCatalystFuelPerSecond = 50;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 1, max = 100_000)
    @SerialEntry public int sculkCatalystExperiencePerArtron = 1;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 1, max = 100_000)
    @SerialEntry public int sculkCatalystExperiencePerDurability = 1;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 25_000)
    @SerialEntry public int enderChestFuelPerSecond = 30;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 525_600)
    @SerialEntry public int telepathicCoralCooldownMinutes = 30;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 64)
    @SerialEntry public int telepathicCoralBonemealCount = 1;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = -1, max = 64)
    @SerialEntry public int telepathicCoralShearsMin = 2;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = -1, max = 64)
    @SerialEntry public int telepathicCoralShearsMax = 7;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @IntField(min = 0, max = 500)
    @SerialEntry public int telepathicCoralShearsLoyaltyPenalty = 50;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @FloatField(min = 0, max = 8)
    @SerialEntry public float consoleRejectionPushHorizontal = 1.25f;

    @AutoGen(category = HOME_SYSTEMS_CATEGORY)
    @FloatField(min = 0, max = 8)
    @SerialEntry public float consoleRejectionPushVertical = 0.35f;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean lockDimensions = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean rwfEnabled = false;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean allowPortalsBoti = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean tntCanTeleportThroughDoors = true;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean hypercubesEnabled = true;

    @AutoGen(category = CATEGORY)
    @IntField(min = 0)
    @CustomDescription(value = "The levenshtein distance allows for typos when using handles. Distances lower to 0 are stricter while higher values like 5 are more lenient.")
    @CustomImage(value = "textures/yacl3/server/levenshtein.webp", width = 1909, height = 349)
    @SerialEntry public int handlesLevenshteinDistance = 2;

    @AutoGen(category = CATEGORY)
    @CustomDescription(value = "Dimensions listed here will be excluded. Ignored when the whitelist has entries.")
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @SerialEntry public List<String> projectorBlacklist = Lists.newArrayList(
            "ait-tardis");

    @AutoGen(category = CATEGORY)
    @CustomDescription(value = "When populated, only these dimensions will be allowed and the blacklist is ignored.")
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @SerialEntry public List<String> projectorWhitelist = Lists.newArrayList();

    @AutoGen(category = CATEGORY)
    @CustomDescription(value = "Dimensions listed here will be excluded. Ignored when the whitelist has entries.")
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @SerialEntry public List<String> travelBlacklist = Lists.newArrayList(
            "ait-tardis", "ait:tardis_dimension_type", AITDimensions.TIME_VORTEX_WORLD.getValue().toString(), "ait:space");

    @AutoGen(category = CATEGORY)
    @CustomDescription(value = "When populated, only these dimensions will be allowed and the blacklist is ignored.")
    @ListGroup(valueFactory = StringListFactory.class, controllerFactory = StringListFactory.class)
    @SerialEntry public List<String> travelWhitelist = Lists.newArrayList();

    @AutoGen(category = CATEGORY)
    @IntField(min = 1)
    @SerialEntry public int travelPerTick = 2;

    @AutoGen(category = CATEGORY)
    @IntField(min = 256, max = 65536)
    @SerialEntry public int astralMapBiomeLocatorRange = 6400;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean sendBulk = true;

    @AutoGen(category = CATEGORY)
    @IntField(min = -1)
    @SerialEntry public int maxTardises = -1;

    @AutoGen(category = CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean disableSafeguards = false;

    @AutoGen(category = CATEGORY)
    @FloatSlider(min = 0, max = 16, step = 0.1f)
    @SerialEntry public float crashSoundVolume = 1f;

    @AutoGen(category = CATEGORY)
    @FloatSlider(min = 0, max = 16, step = 0.1f)
    @SerialEntry public float flightSoundVolume = 2f;

    @AutoGen(category = CATEGORY)
    @IntSlider(min = 1, max = 128, step = 1)
    @SerialEntry public int maxStabilizedSpeed = 4;

    public void normalizeLinkedRanges() {
        int minimum = Math.max(-1, Math.min(64, this.telepathicCoralShearsMin));
        int maximum = Math.max(-1, Math.min(64, this.telepathicCoralShearsMax));
        if (minimum >= 0 && maximum >= 0 && minimum > maximum) {
            int swap = minimum;
            minimum = maximum;
            maximum = swap;
        }

        this.telepathicCoralShearsMin = minimum;
        this.telepathicCoralShearsMax = maximum;

        int minimumDamage = Math.max(0, Math.min(1250, this.biodataRestorationSubsystemDamageMin));
        int maximumDamage = Math.max(0, Math.min(1250, this.biodataRestorationSubsystemDamageMax));
        if (minimumDamage > maximumDamage) {
            int swap = minimumDamage;
            minimumDamage = maximumDamage;
            maximumDamage = swap;
        }
        this.biodataRestorationSubsystemDamageMin = minimumDamage;
        this.biodataRestorationSubsystemDamageMax = maximumDamage;
    }

    public static class StringListFactory implements ListGroup.ValueFactory<String>, ListGroup.ControllerFactory<String> {

        // used by the reflections
        public StringListFactory() { }

        @Override
        public ControllerBuilder<String> createController(ListGroup annotation, ConfigField<List<String>> field, OptionAccess storage, Option<String> option) {
            return StringControllerBuilder.create(option);
        }

        @Override
        public String provideNewValue() {
            return "";
        }
    }
}
