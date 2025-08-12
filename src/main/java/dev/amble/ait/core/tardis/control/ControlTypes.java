package dev.amble.ait.core.tardis.control;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.data.codec.MoreCodec;
import dev.amble.ait.registry.impl.ControlRegistry;
import net.minecraft.util.Identifier;
import org.joml.Vector3f;

import net.minecraft.entity.EntityDimensions;

import dev.amble.ait.core.blocks.ConsoleBlock;
import dev.amble.ait.core.entities.ConsoleControlEntity;
import dev.amble.ait.data.schema.console.ConsoleTypeSchema;

/**
 * Holds a control which will be ran when interacted with, an
 * {@linkplain Vector3f offset} from the centre of the {@link ConsoleBlock} and
 * a {@linkplain EntityDimensions scale} for the entity <br>
 * <br>
 * A list of these is gotten by {@link ConsoleTypeSchema#getControlTypes()} and
 * used in {@link ConsoleControlEntity} to hold its information
 *
 * @author loqor
 * @see ConsoleControlEntity
 */
public class ControlTypes {
    public static final Codec<ControlTypes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(c -> c.getControl().id()),
            Codec.FLOAT.fieldOf("width").forGetter(c -> c.getScale().width),
            Codec.FLOAT.fieldOf("height").forGetter(c -> c.getScale().height),
            MoreCodec.VECTOR3F.fieldOf("offset").forGetter(ControlTypes::getOffset)
    ).apply(instance, ControlTypes::new));

    private final Control control;
    private EntityDimensions scale;
    private Vector3f offset;

    public ControlTypes(Control control, EntityDimensions scaling, Vector3f offset) {
        this.control = control;
        this.scale = scaling;
        this.offset = offset;
    }

    public ControlTypes(Identifier controlId, float width, float height, Vector3f offset) {
        this(ControlRegistry.REGISTRY.get(controlId), EntityDimensions.fixed(width, height), offset);
    }

    @Override
    public String toString() {
        return "ControlTypes{" + "control=" + control + ", scale=" + scale + ", offset=" + offset + '}';
    }

    public Control getControl() {
        return this.control;
    }

    public EntityDimensions getScale() {
        return this.scale;
    }

    public void setScale(EntityDimensions scale) {
        this.scale = scale;
    }

    public Vector3f getOffset() {
        return this.offset;
    }

    public void setOffset(Vector3f offset) {
        this.offset = offset;
    }
}
