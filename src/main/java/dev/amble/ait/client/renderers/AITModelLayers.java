package dev.amble.ait.client.renderers;

import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

import dev.amble.ait.AITMod;

public class AITModelLayers {
    public static final EntityModelLayer NIGHTMARE_CYBERMAN =
            new EntityModelLayer(new Identifier(AITMod.MOD_ID, "nightmare_cyberman"), "main");
}
