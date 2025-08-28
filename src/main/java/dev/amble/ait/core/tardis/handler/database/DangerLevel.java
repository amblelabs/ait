package dev.amble.ait.core.tardis.handler.database;

public enum DangerLevel {
    NONE(0, "danger_level.none"),
    LOW(1, "danger_level.low"),
    MEDIUM(2, "danger_level.medium"),
    HIGH(3, "danger_level.high"),
    EXTREME(4, "danger_level.extreme");

    private final int level;
    private final String translationKey;

    DangerLevel(int level, String translationKey) {
        this.level = level;
        this.translationKey = translationKey;
    }

    public int getLevel() {
        return level;
    }

    public String getTranslationKey() {
        return translationKey;
    }
}
