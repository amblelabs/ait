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
    public static final String TEMPERAMENT_CATEGORY = "tardis_temperament";

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

    @AutoGen(category = CATEGORY)
    @MasterTickBox({
            "temperamentHomeRadius", "temperamentApproachRadius", "temperamentApproachTrackingRadius",
            "temperamentCompanionSafetyRadius", "temperamentKeyRadius", "temperamentKeyPickupDelayTicks",
            "temperamentHopDistance", "temperamentHopAngleSpreadDegrees", "temperamentMaxApproachStep",
            "temperamentHopChance", "temperamentDoorEntryTriggerRadius", "temperamentCamouflageChance",
            "temperamentCloakChance", "temperamentLeftBehindChance", "temperamentUnsafeLandingChance",
            "temperamentFailedEventGravityChance", "temperamentKeyBurnChance", "temperamentKeyFireSeconds",
            "temperamentDoorCloseChance", "temperamentGravityDurationSeconds",
            "temperamentBrokenKeyLoyaltyPenalty", "temperamentHammerCooldownTicks",
            "temperamentUnsafeLandingHorizontalRadius", "temperamentUnsafeLandingVerticalRadius",
            "temperamentUnsafeLandingColumnAttempts", "temperamentUnsafeLandingLocalColumnAttempts",
            "temperamentUnsafeLandingHostileAttempts", "temperamentUnsafeLandingHostileCaptureRadius",
            "temperamentUnsafeLandingVoidHeight", "temperamentUnsafeLandingBuildLimitMargin",
            "temperamentPassiveLoyaltyLossChance", "temperamentPassiveLoyaltyLoss",
            "temperamentJealousyAbsenceMinutes", "temperamentJealousySameOrLowerPenalty",
            "temperamentJealousyHigherPenalty", "temperamentHomeRelocationLoyaltyPenalty",
            "temperamentHomeOverlapLoyaltyPenalty", "temperamentHomeOverlapPenaltyIntervalSeconds",
            "temperamentConsoleRejectionPushHorizontal", "temperamentConsoleRejectionPushVertical",
            "handlesRejectWarningChance", "handlesZeroLoyaltyRejectWarningChance",
            "timelineErasureEnabled"
    })
    @SerialEntry public boolean tardisTemperament = true;

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


    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 512)
    @SerialEntry public int temperamentHomeRadius = 100;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 256)
    @SerialEntry public int temperamentApproachRadius = 30;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 512)
    @SerialEntry public int temperamentApproachTrackingRadius = 60;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 512)
    @SerialEntry public int temperamentCompanionSafetyRadius = 50;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 256)
    @SerialEntry public int temperamentKeyRadius = 10;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 1200)
    @SerialEntry public int temperamentKeyPickupDelayTicks = 20;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 256)
    @SerialEntry public int temperamentHopDistance = 35;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 360)
    @SerialEntry public int temperamentHopAngleSpreadDegrees = 120;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @FloatField(min = 0, max = 64)
    @SerialEntry public float temperamentMaxApproachStep = 8f;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int temperamentHopChance = 25;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int handlesRejectWarningChance = 10;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int handlesZeroLoyaltyRejectWarningChance = 100;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int temperamentCamouflageChance = 5;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int temperamentCloakChance = 10;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int temperamentLeftBehindChance = 5;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int temperamentUnsafeLandingChance = 50;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int temperamentFailedEventGravityChance = 5;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int temperamentKeyBurnChance = 75;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 300)
    @SerialEntry public int temperamentKeyFireSeconds = 2;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int temperamentDoorCloseChance = 10;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 32)
    @SerialEntry public int temperamentDoorEntryTriggerRadius = 4;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 3600)
    @SerialEntry public int temperamentGravityDurationSeconds = 20;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 500)
    @SerialEntry public int temperamentBrokenKeyLoyaltyPenalty = 10;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 1200)
    @SerialEntry public int temperamentHammerCooldownTicks = 20;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 256)
    @SerialEntry public int temperamentUnsafeLandingHorizontalRadius = 64;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 256)
    @SerialEntry public int temperamentUnsafeLandingVerticalRadius = 48;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 128)
    @SerialEntry public int temperamentUnsafeLandingColumnAttempts = 24;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 128)
    @SerialEntry public int temperamentUnsafeLandingLocalColumnAttempts = 8;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 128)
    @SerialEntry public int temperamentUnsafeLandingHostileAttempts = 16;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @FloatField(min = 0.5f, max = 32)
    @SerialEntry public float temperamentUnsafeLandingHostileCaptureRadius = 2f;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 512)
    @SerialEntry public int temperamentUnsafeLandingVoidHeight = 32;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 64)
    @SerialEntry public int temperamentUnsafeLandingBuildLimitMargin = 4;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 100)
    @SerialEntry public int temperamentPassiveLoyaltyLossChance = 5;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 500)
    @SerialEntry public int temperamentPassiveLoyaltyLoss = 1;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 525_600)
    @SerialEntry public int temperamentJealousyAbsenceMinutes = 300;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 500)
    @SerialEntry public int temperamentJealousySameOrLowerPenalty = 100;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 500)
    @SerialEntry public int temperamentJealousyHigherPenalty = 200;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 500)
    @SerialEntry public int temperamentHomeRelocationLoyaltyPenalty = 50;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 0, max = 500)
    @SerialEntry public int temperamentHomeOverlapLoyaltyPenalty = 1;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @IntField(min = 1, max = 3600)
    @SerialEntry public int temperamentHomeOverlapPenaltyIntervalSeconds = 1;


    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @FloatField(min = 0, max = 16)
    @SerialEntry public float temperamentConsoleRejectionPushHorizontal = 1.35f;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @FloatField(min = 0, max = 16)
    @SerialEntry public float temperamentConsoleRejectionPushVertical = 0.6f;

    @AutoGen(category = TEMPERAMENT_CATEGORY)
    @Boolean(formatter = Boolean.Formatter.YES_NO, colored = true)
    @SerialEntry public boolean timelineErasureEnabled = true;

    @AutoGen(category = CATEGORY)
    @IntSlider(min = 1, max = 128, step = 1)
    @SerialEntry public int maxStabilizedSpeed = 4;

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
