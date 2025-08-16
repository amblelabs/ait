package dev.amble.ait.core.tardis.handler;

import dev.amble.lib.data.CachedDirectedGlobalPos;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBiomeTags;
import org.apache.commons.lang3.StringUtils;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisEvents;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;
import dev.amble.ait.data.enummap.Ordered;
import dev.amble.ait.data.properties.Property;
import dev.amble.ait.data.properties.Value;

/**
 * @author Loqor
 * TODO reminder to work on this more, making it so you have to
 *         brush off different biomes if you don't just demat/remat + having to
 *         land on the respective blocks / has to snow for it to take effect.
 */
public class BiomeHandler extends KeyedTardisComponent {

    private static final Property<BiomeType> TYPE = Property.forEnum("type", BiomeType.class, BiomeType.DEFAULT);
    private final Value<BiomeType> type = TYPE.create(this);

    static {
        TardisEvents.DEMAT.register(tardis -> {
            tardis.<BiomeHandler>handler(Id.BIOME).forceTypeDefault();
            return TardisEvents.Interaction.PASS;
        });
        TardisEvents.LANDED.register(tardis -> tardis.<BiomeHandler>handler(Id.BIOME).update());
        TardisEvents.ENTER_FLIGHT.register(tardis -> tardis.<BiomeHandler>handler(Id.BIOME).forceTypeDefault());
    }

    public BiomeHandler() {
        super(Id.BIOME);
    }

    @Override
    public void onLoaded() {
        type.of(this, TYPE);
    }

    public void update() {
        this.update(this.tardis.travel().position());
    }

    public void update(CachedDirectedGlobalPos globalPos) {
        if (globalPos == null)
            return;

        RegistryEntry<Biome> entry = globalPos.getWorld().getBiome(globalPos.getPos());
        BiomeType biome = getTagForBiome(entry);

        this.type.set(biome);
        this.sync();
    }

    public void forceTypeDefault() {
        this.type.set(BiomeType.DEFAULT);
        this.sync();
    }

    public BiomeType getBiomeKey() {
        return this.type.get();
    }

    // FIXME(PERFORMANCE)
    private static BiomeType getTagForBiome(RegistryEntry<Biome> biome) {
        if (biome.isIn(ConventionalBiomeTags.SNOWY) || biome.isIn(ConventionalBiomeTags.SNOWY_PLAINS)
                || biome.isIn(ConventionalBiomeTags.ICY))
            return BiomeType.SNOWY;

        if (biome.isIn(ConventionalBiomeTags.DESERT) || biome.isIn(ConventionalBiomeTags.BEACH)
                || biome.isIn(ConventionalBiomeTags.DEAD))
            return BiomeType.SANDY;

        if (biome.isIn(ConventionalBiomeTags.BADLANDS))
            return BiomeType.RED_SANDY;

        if (biome.isIn(ConventionalBiomeTags.SWAMP))
            return BiomeType.MUDDY;

        if (biome.isIn(ConventionalBiomeTags.IN_THE_END))
            return BiomeType.CHORUS;

        if (biome.isIn(ConventionalBiomeTags.FLORAL))
            return BiomeType.CHERRY;

        RegistryKey<Biome> biomeKey = biome.getKey().orElse(null);

        if (biomeKey == BiomeKeys.DEEP_DARK)
            return BiomeType.SCULK;

        if (biomeKey == BiomeKeys.CHERRY_GROVE)
            return BiomeType.CHERRY;

        return BiomeType.DEFAULT;
    }

    public enum BiomeType implements StringIdentifiable, Ordered {
        DEFAULT, SNOWY("_snowy"),
        SCULK("_sculk"),
        SANDY("_sand"),
        RED_SANDY("_red_sand"),
        MUDDY("_mud"),
        CHORUS("_chorus"),
        CHERRY("_cherry");

        public static final BiomeType[] VALUES = BiomeType.values();
        public static final Codec<BiomeType> CODEC = StringIdentifiable.createCodec(() -> VALUES);

        private final String suffix;

        BiomeType(String suffix) {
            this.suffix = suffix;
        }

        BiomeType() {
            this(null);
        }

        @Override
        public String asString() {
            return StringUtils.capitalize(this.toString().replace("_", " "));
        }

        public Identifier getTexture(Identifier texture) {
            if (this.suffix == null)
                return texture;

            String path = texture.getPath();
            return AITMod.id(path.substring(0, path.length() - 4) + this.suffix + ".png");
        };

        public Identifier get(BiomeOverrides overrides) {
            if (overrides == null)
                return null;

            return overrides.get(this);
        }

        @Override
        public int index() {
            return ordinal();
        }
    }
}
