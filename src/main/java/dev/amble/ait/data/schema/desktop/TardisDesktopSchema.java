package dev.amble.ait.data.schema.desktop;

import java.lang.reflect.Type;
import java.util.Optional;

import com.google.gson.*;
import dev.amble.ait.core.ars.ArsStructure;
import dev.amble.lib.register.unlockable.Unlockable;

import net.minecraft.util.Identifier;

import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.BasicSchema;
import dev.amble.ait.data.schema.desktop.textures.StructurePreviewTexture;
import dev.amble.ait.registry.impl.DesktopRegistry;

public abstract class TardisDesktopSchema extends BasicSchema implements Unlockable, ArsStructure {

    private final Identifier id;

    private final StructurePreviewTexture preview;
    private final Loyalty loyalty;

    protected TardisDesktopSchema(Identifier id, StructurePreviewTexture texture, Optional<Loyalty> loyalty) {
        super("desktop");
        this.id = id;

        this.preview = texture;
        this.loyalty = loyalty.orElse(null);
    }

    protected TardisDesktopSchema(Identifier id, StructurePreviewTexture texture, Loyalty loyalty) {
        this(id, texture, Optional.of(loyalty));
    }

    protected TardisDesktopSchema(Identifier id, StructurePreviewTexture texture) {
        this(id, texture, Optional.empty());
    }

    @Override
    public Identifier id() {
        return id;
    }

    @Override
    public Optional<Loyalty> requirement() {
        return Optional.ofNullable(loyalty);
    }

    @Override
    public UnlockType unlockType() {
        return UnlockType.DESKTOP;
    }

    @Override
    public StructurePreviewTexture previewTexture() {
        return this.preview;
    }

    @Override
    public Identifier getStructureLocation() {
        Identifier id = this.id();

        return new Identifier(id.getNamespace(), "interiors/" + id.getPath());
    }

    @Override
    public Identifier structureId() {
        return getStructureLocation();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o instanceof TardisDesktopSchema that)
            return id.equals(that.id);

        return false;
    }

    public static Object serializer() {
        return new Serializer();
    }

    private static class Serializer
            implements
                JsonSerializer<TardisDesktopSchema>,
                JsonDeserializer<TardisDesktopSchema> {

        @Override
        public TardisDesktopSchema deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return DesktopRegistry.getInstance().get(new Identifier(json.getAsJsonPrimitive().getAsString()));
        }

        @Override
        public JsonElement serialize(TardisDesktopSchema src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.id().toString());
        }
    }
}
