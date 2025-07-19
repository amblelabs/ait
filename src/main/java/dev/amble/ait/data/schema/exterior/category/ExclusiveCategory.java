package dev.amble.ait.data.schema.exterior.category;

import java.util.UUID;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.devteam.DevTeam;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;

/**
 * Used for dev exclusives.
 * @author Loqor, Theo
 * @apiNote This category is not meant to be used by players and is reserved for the dev team.
 * This is NOT meant for multiple variants of the same exterior (cough, Classic). Every dev team member gets one exterior.
 */
public class ExclusiveCategory extends ExteriorCategorySchema {
    public static final Identifier REFERENCE = AITMod.id("exterior/exclusive");

    public ExclusiveCategory() {
        super(REFERENCE, "exclusive");
    }

    public static boolean isUnlocked(UUID uuid) {
        return DevTeam.isDev(uuid);
    }
}
