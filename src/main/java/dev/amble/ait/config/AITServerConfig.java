package dev.amble.ait.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.Lists;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.controller.ControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.ConfigField;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.autogen.*;
import dev.isxander.yacl3.config.v2.api.autogen.Boolean;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.platform.YACLPlatform;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITDimensions;

public class AITServerConfig {

    public static final String CATEGORY = "server";
    public static final String ARTRON_CATEGORY = "artron";

    static {
        OptionFactory.register(ArtronMinimumField.class, new ArtronMinimumFactory());
        OptionFactory.register(ArtronMaximumField.class, new ArtronMaximumFactory());
    }

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

    @AutoGen(category = ARTRON_CATEGORY)
    @ArtronMinimumField
    @SerialEntry public int riftChunkMinArtron = ArtronConfigSettings.DEFAULT_RIFT_CHUNK_MIN_ARTRON;

    @AutoGen(category = ARTRON_CATEGORY)
    @ArtronMaximumField
    @SerialEntry public int riftChunkMaxArtron = ArtronConfigSettings.DEFAULT_RIFT_CHUNK_MAX_ARTRON;

    @AutoGen(category = ARTRON_CATEGORY)
    @DoubleField(min = 0)
    @SerialEntry public double riftChunkArtronRegenPerSecond = ArtronConfigSettings.DEFAULT_RIFT_CHUNK_REGEN_PER_SECOND;

    @AutoGen(category = ARTRON_CATEGORY)
    @DoubleField(min = 0)
    @SerialEntry public double tardisAmbientRefuelPerSecond = ArtronConfigSettings.DEFAULT_TARDIS_AMBIENT_REFUEL_PER_SECOND;

    @AutoGen(category = ARTRON_CATEGORY)
    @DoubleField(min = 0)
    @SerialEntry public double tardisRiftRefuelBonusPerSecond = ArtronConfigSettings.DEFAULT_TARDIS_RIFT_REFUEL_BONUS_PER_SECOND;

    public ArtronConfigSettings.Bounds getRiftChunkArtronBounds() {
        return ArtronConfigSettings.normalizeBounds(this.riftChunkMinArtron, this.riftChunkMaxArtron);
    }

    public double getRiftChunkArtronRegenPerSecond() {
        return ArtronConfigSettings.normalizeRate(this.riftChunkArtronRegenPerSecond,
                ArtronConfigSettings.DEFAULT_RIFT_CHUNK_REGEN_PER_SECOND);
    }

    public double getTardisAmbientRefuelPerSecond() {
        return ArtronConfigSettings.normalizeRate(this.tardisAmbientRefuelPerSecond,
                ArtronConfigSettings.DEFAULT_TARDIS_AMBIENT_REFUEL_PER_SECOND);
    }

    public double getTardisRiftRefuelBonusPerSecond() {
        return ArtronConfigSettings.normalizeRate(this.tardisRiftRefuelBonusPerSecond,
                ArtronConfigSettings.DEFAULT_TARDIS_RIFT_REFUEL_BONUS_PER_SECOND);
    }

    public boolean normalizeArtronSettings() {
        ArtronConfigSettings.Bounds bounds = this.getRiftChunkArtronBounds();
        double regeneration = this.getRiftChunkArtronRegenPerSecond();
        double ambientRefuel = this.getTardisAmbientRefuelPerSecond();
        double riftRefuelBonus = this.getTardisRiftRefuelBonusPerSecond();

        boolean changed = this.riftChunkMinArtron != bounds.minimum()
                || this.riftChunkMaxArtron != bounds.maximum()
                || Double.compare(this.riftChunkArtronRegenPerSecond, regeneration) != 0
                || Double.compare(this.tardisAmbientRefuelPerSecond, ambientRefuel) != 0
                || Double.compare(this.tardisRiftRefuelBonusPerSecond, riftRefuelBonus) != 0;

        this.riftChunkMinArtron = bounds.minimum();
        this.riftChunkMaxArtron = bounds.maximum();
        this.riftChunkArtronRegenPerSecond = regeneration;
        this.tardisAmbientRefuelPerSecond = ambientRefuel;
        this.tardisRiftRefuelBonusPerSecond = riftRefuelBonus;

        return changed;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    private @interface ArtronMinimumField {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    private @interface ArtronMaximumField {
    }

    private static class ArtronMinimumFactory extends SimpleOptionFactory<ArtronMinimumField, Integer> {

        @Override
        protected ControllerBuilder<Integer> createController(ArtronMinimumField annotation, ConfigField<Integer> field,
                                                               OptionAccess storage, Option<Integer> option) {
            return IntegerFieldControllerBuilder.create(option).range(0, Integer.MAX_VALUE);
        }

        @Override
        protected void listener(ArtronMinimumField annotation, ConfigField<Integer> field, OptionAccess storage,
                                Option<Integer> option, Integer value) {
            updateLinkedOption(storage, "riftChunkMaxArtron", linked -> {
                if (linked.pendingValue() < value)
                    linked.requestSet(value);
            });
        }
    }

    private static class ArtronMaximumFactory extends SimpleOptionFactory<ArtronMaximumField, Integer> {

        @Override
        protected ControllerBuilder<Integer> createController(ArtronMaximumField annotation, ConfigField<Integer> field,
                                                               OptionAccess storage, Option<Integer> option) {
            return IntegerFieldControllerBuilder.create(option).range(0, Integer.MAX_VALUE);
        }

        @Override
        protected void listener(ArtronMaximumField annotation, ConfigField<Integer> field, OptionAccess storage,
                                Option<Integer> option, Integer value) {
            updateLinkedOption(storage, "riftChunkMinArtron", linked -> {
                if (linked.pendingValue() > value)
                    linked.requestSet(value);
            });
        }
    }

    @SuppressWarnings("unchecked")
    private static void updateLinkedOption(OptionAccess storage, String fieldName,
                                           Consumer<Option<Integer>> operation) {
        storage.scheduleOptionOperation(fieldName, option -> operation.accept((Option<Integer>) option));
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
