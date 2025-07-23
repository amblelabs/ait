package dev.amble.ait.data.schema.desktop.textures;

import dev.amble.ait.AITMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import dev.amble.ait.data.schema.desktop.TardisDesktopSchema;

public class StructurePreviewTexture {
    // public static final Codec<DesktopPreviewTexture> CODEC =
    // RecordCodecBuilder.create(
    // instance -> instance.group(
    // Identifier.CODEC.fieldOf("path").forGetter(texture -> texture.path),
    // Codec.INT.fieldOf("width").forGetter(texture -> texture.width),
    // Codec.INT.fieldOf("height").forGetter(texture -> texture.height)
    // )
    // )

    private static final Identifier MISSING_PREVIEW = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/presets/missing_preview.png");

    private final Identifier path;
    public final int width;
    public final int height;

    public StructurePreviewTexture(Identifier path, int width, int height) {
        this.path = path;
        this.width = width;
        this.height = height;
    }

    public StructurePreviewTexture(TardisDesktopSchema schema, int width, int height) {
        this(pathFromDesktopId(schema.id()), width, height);
    }

    public StructurePreviewTexture(Identifier path) {
        this(path, 128, 128);
    }

    public StructurePreviewTexture(TardisDesktopSchema schema) {
        this(schema.id());
    }

    public Identifier texture() {
        return this.path;
    }

    @Environment(EnvType.CLIENT)
    public Identifier textureOrFallback() {
        if (doesTextureExist(this.path)) {
            return this.path;
        }
        return MISSING_PREVIEW;
    }

    @Environment(EnvType.CLIENT)
    public static boolean doesTextureExist(Identifier id) {
        return MinecraftClient.getInstance().getResourceManager().getResource(id).isPresent();
    }

    public static Identifier pathFromDesktopId(Identifier desktopId) {
        return new Identifier(desktopId.getNamespace(), "textures/desktop/" + desktopId.getPath() + ".png");
    }

    public static StructurePreviewTexture textureFromArsId(Identifier arsId) {
        return new StructurePreviewTexture(new Identifier(arsId.getNamespace(), "textures/ars/" + arsId.getPath() + ".png"));
    }
}
