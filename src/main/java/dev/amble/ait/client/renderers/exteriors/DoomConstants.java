package dev.amble.ait.client.renderers.exteriors;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.tardis.Tardis;
import net.minecraft.util.Identifier;

public class DoomConstants {

    public static final Identifier DOOM_FRONT_BACK = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_front_back.png");
    public static final Identifier DOOM_LEFT_SIDE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_left_side.png");
    public static final Identifier DOOM_RIGHT_SIDE = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_right_side.png");
    public static final Identifier DOOM_LEFT_DIAGONAL = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_left_diagonal.png");
    public static final Identifier DOOM_RIGHT_DIAGONAL = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_right_diagonal.png");
    public static final Identifier DOOM_BLANK_DIAGONAL = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_blank_diagonal.png");
    public static final Identifier DOOM_TEXTURE_EMISSION = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_emission.png");
    public static final Identifier DOOM_LEFT_SIDE_EMISSION = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_left_side_emission.png");
    public static final Identifier DOOM_RIGHT_SIDE_EMISSION = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_right_side_emission.png");
    public static final Identifier DOOM_DIAGONAL_EMISSION = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_diagonal_emission.png");
    public static final Identifier DOOM_LEFT_DIAGONAL_OPEN = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_left_diagonal_open.png");
    public static final Identifier DOOM_RIGHT_DIAGONAL_OPEN = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_right_diagonal_open.png");
    public static final Identifier DOOM_LEFT_SIDE_OPEN = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_left_side_open.png");
    public static final Identifier DOOM_RIGHT_SIDE_OPEN = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_right_side_open.png");
    public static final Identifier DOOM_FRONT_BACK_OPEN = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_front_back_open.png");
    public static final Identifier DOOM_LEFT_DIAGONAL_OPEN_EMISSION = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_left_diagonal_open_emission.png");
    public static final Identifier DOOM_RIGHT_DIAGONAL_OPEN_EMISSION = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_right_diagonal_open_emission.png");
    public static final Identifier DOOM_FRONT_BACK_OPEN_EMISSION = new Identifier(AITMod.MOD_ID,
            "textures/blockentities/exteriors/doom/doom_front_back_open_emission.png");

    public static Identifier getTextureForRotation(float rotation, Tardis tardis) {
        boolean bl = tardis.door().isOpen();
        if (rotation > 70 && rotation < 110) {
            return bl ? DOOM_RIGHT_SIDE_OPEN : DOOM_RIGHT_SIDE;
        } else if (rotation < -90 && rotation > -110) {
            return bl ? DOOM_LEFT_SIDE_OPEN : DOOM_LEFT_SIDE;
        } else if (rotation > 25 && rotation < 70) {
            return bl ? DOOM_RIGHT_DIAGONAL_OPEN : DOOM_RIGHT_DIAGONAL;
        } else if (rotation < -25 && rotation > -90) {
            return bl ? DOOM_LEFT_DIAGONAL_OPEN : DOOM_LEFT_DIAGONAL;
        } else if (rotation > 110 && rotation < 155 || rotation < -110 && rotation > -155) {
            return DOOM_BLANK_DIAGONAL;
        } else {
            return bl ? DOOM_FRONT_BACK_OPEN : DOOM_FRONT_BACK;
        }
    }

    public static Identifier getEmissionForRotation(Identifier identifier, Tardis tardis) {
        boolean bl = tardis.door().isOpen();
        if (identifier == DOOM_RIGHT_DIAGONAL || identifier == DOOM_RIGHT_DIAGONAL_OPEN) {
            return bl ? DOOM_RIGHT_DIAGONAL_OPEN_EMISSION : DOOM_DIAGONAL_EMISSION;
        } else if (identifier == DOOM_LEFT_DIAGONAL || identifier == DOOM_LEFT_DIAGONAL_OPEN) {
            return bl ? DOOM_LEFT_DIAGONAL_OPEN_EMISSION : DOOM_DIAGONAL_EMISSION;
        } else if (identifier == DOOM_BLANK_DIAGONAL) {
            return DOOM_DIAGONAL_EMISSION;
        } else if (identifier == DOOM_LEFT_SIDE || identifier == DOOM_LEFT_SIDE_OPEN) {
            return DOOM_LEFT_SIDE_EMISSION;
        } else if (identifier == DOOM_RIGHT_SIDE || identifier == DOOM_RIGHT_SIDE_OPEN) {
            return DOOM_RIGHT_SIDE_EMISSION;
        } else {
            return bl ? DOOM_FRONT_BACK_OPEN_EMISSION : DOOM_TEXTURE_EMISSION;
        }
    }
}
