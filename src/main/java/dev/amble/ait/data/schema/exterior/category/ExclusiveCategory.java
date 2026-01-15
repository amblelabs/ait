package dev.amble.ait.data.schema.exterior.category;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.devteam.DevTeam;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import net.minecraft.util.Identifier;

import java.util.UUID;

/**
 * Used for dev exclusives.
 * @author Loqor, Theo
 * @apiNote This category is not meant to be used by players and is reserved for the dev team.
 * This is NOT meant for multiple variants of the same exterior (cough, Classic). Every dev team member gets one exterior.
 */
public class ExclusiveCategory extends ExteriorCategorySchema {
    public static final Identifier REFERENCE = AITMod.id("exterior/exclusive");

    public ExclusiveCategory() {
	    super(REFERENCE);
    }

    public static boolean isUnlocked(UUID uuid) {
        return DevTeam.isDev(uuid);
    }
}
