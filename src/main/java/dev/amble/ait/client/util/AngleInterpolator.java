package dev.amble.ait.client.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class AngleInterpolator {
    private double value;
    private double speed;
    private long lastUpdateTime;

    public AngleInterpolator() {
    }

    public double value() {
        return value;
    }

    public boolean shouldUpdate(long time) {
        return this.lastUpdateTime != time;
    }

    public void update(long time, double target) {
        this.lastUpdateTime = time;
        double d = target - this.value;
        d = MathHelper.floorMod(d + 0.5, 1.0) - 0.5;
        this.speed += d * 0.1;
        this.speed *= 0.8;
        this.value = MathHelper.floorMod(this.value + this.speed, 1.0);
    }
}