package dev.amble.ait.module.planet.core.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.AITMod;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.densityfunction.DensityFunction;

public record CraterField(DensityFunction.Noise seed, int salt, int cellSize, double minRadius, double maxRadius, double chance,
                          double depth, double rim, double peakRadius) implements DensityFunction {

    private static final double RIM_REACH = 2.4;
    private static final double RIM_WIDTH = 0.4;
    private static final double BOWL_EXPONENT = 2.6;
    private static final double PEAK_HEIGHT = 0.45;
    private static final double PEAK_WIDTH = 0.18;

    public static final MapCodec<CraterField> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DensityFunction.Noise.CODEC.fieldOf("seed").forGetter(CraterField::seed),
            Codec.INT.fieldOf("salt").forGetter(CraterField::salt),
            Codec.intRange(8, 4096).fieldOf("cell_size").forGetter(CraterField::cellSize),
            Codec.doubleRange(1, 2048).fieldOf("min_radius").forGetter(CraterField::minRadius),
            Codec.doubleRange(1, 2048).fieldOf("max_radius").forGetter(CraterField::maxRadius),
            Codec.doubleRange(0, 1).fieldOf("chance").forGetter(CraterField::chance),
            Codec.doubleRange(0, 2).fieldOf("depth").forGetter(CraterField::depth),
            Codec.doubleRange(0, 2).fieldOf("rim").forGetter(CraterField::rim),
            Codec.doubleRange(0, 4096).optionalFieldOf("peak_radius", 4096.0).forGetter(CraterField::peakRadius)
    ).apply(instance, CraterField::new));

    public static final CodecHolder<CraterField> CODEC_HOLDER = CodecHolder.of(MAP_CODEC);

    public static void register() {
        Registry.register(Registries.DENSITY_FUNCTION_TYPE, AITMod.id("craters"), MAP_CODEC.codec());
    }

    @Override
    public double sample(NoisePos pos) {
        long worldSeed = Double.doubleToLongBits(this.seed.sample(0.137, 0.271, 0.619));
        int x = pos.blockX();
        int z = pos.blockZ();
        int cellX = Math.floorDiv(x, this.cellSize);
        int cellZ = Math.floorDiv(z, this.cellSize);

        double inside = Double.POSITIVE_INFINITY;
        double rims = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                long hash = mix(worldSeed ^ (this.salt * 0x9E3779B97F4A7C15L)
                        ^ ((cellX + dx) * 0xC2B2AE3D27D4EB4FL) ^ ((cellZ + dz) * 0x165667B19E3779F9L));

                if (unit(hash) >= this.chance)
                    continue;

                hash = mix(hash);
                double centerX = (cellX + dx + unit(hash)) * this.cellSize;
                hash = mix(hash);
                double centerZ = (cellZ + dz + unit(hash)) * this.cellSize;
                hash = mix(hash);
                double t = unit(hash);
                double radius = MathHelper.lerp(t * t, this.minRadius, this.maxRadius);

                double ox = x - centerX;
                double oz = z - centerZ;
                double distance = Math.sqrt(ox * ox + oz * oz) / radius;

                if (distance >= RIM_REACH)
                    continue;

                if (distance < 1) {
                    inside = Math.min(inside, this.interior(distance, radius));
                } else {
                    double edge = (distance - 1) / RIM_WIDTH;
                    rims += this.rim * radius * Math.exp(-edge * edge);
                }
            }
        }

        if (inside == Double.POSITIVE_INFINITY)
            return rims;

        return inside + rims * 0.5;
    }

    private double interior(double distance, double radius) {
        double bowl = this.depth * radius;
        double height = -bowl + (bowl + this.rim * radius) * Math.pow(distance, BOWL_EXPONENT);

        if (radius >= this.peakRadius) {
            double peak = distance / PEAK_WIDTH;
            height += bowl * PEAK_HEIGHT * Math.exp(-peak * peak);
        }

        return height;
    }

    private static double unit(long hash) {
        return (hash >>> 11) * 0x1.0p-53;
    }

    private static long mix(long value) {
        value = (value ^ (value >>> 33)) * 0xFF51AFD7ED558CCDL;
        value = (value ^ (value >>> 33)) * 0xC4CEB9FE1A85EC53L;
        return value ^ (value >>> 33);
    }

    @Override
    public void fill(double[] densities, EachApplier applier) {
        applier.fill(densities, this);
    }

    @Override
    public DensityFunction apply(DensityFunctionVisitor visitor) {
        return visitor.apply(new CraterField(visitor.apply(this.seed), this.salt, this.cellSize, this.minRadius, this.maxRadius,
                this.chance, this.depth, this.rim, this.peakRadius));
    }

    @Override
    public double minValue() {
        return -this.depth * this.maxRadius * 1.5;
    }

    @Override
    public double maxValue() {
        return this.maxRadius * (this.rim * 3 + this.depth);
    }

    @Override
    public CodecHolder<? extends DensityFunction> getCodecHolder() {
        return CODEC_HOLDER;
    }
}
