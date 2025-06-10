package dev.amble.ait.data.schema.sonic;

import static dev.amble.ait.AITMod.MOD_ID;

import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;

public class BuiltinSonic extends SonicSchema {

    private static final String prefix = "item/sonic/";

    private static final String[] MODES = new String[]{"inactive", "interaction", "overload", "scanning", "tardis"};

    protected BuiltinSonic(Identifier id, Models models) {
        super(id, models, new Rendering());
    }

    public static BuiltinSonic create(String id) {
        Identifier[] identifiers = new Identifier[MODES.length];

        for (int i = 0; i < MODES.length; i++) {
            identifiers[i] = new Identifier(MOD_ID, prefix + id + "/" + MODES[i]);
        }

        Identifier sonicId = AITMod.id(id);

        return new BuiltinSonic(sonicId,
                new Models(identifiers[0], identifiers[1], identifiers[2], identifiers[3], identifiers[4]));
    }
}
