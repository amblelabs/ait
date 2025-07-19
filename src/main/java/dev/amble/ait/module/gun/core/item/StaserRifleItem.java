package dev.amble.ait.module.gun.core.item;

public class StaserRifleItem extends BaseGunItem {
    public StaserRifleItem(Settings settings) {
        super(settings);
    }

    @Override
    public double getMaxAmmo() {
        return 64;
    }

    @Override
    public float getAimDeviation(boolean isAds) {
        return !isAds ? 2f : 0.0f;
    }

    @Override
    public int getCooldown() {
        return 30;
    }
}
