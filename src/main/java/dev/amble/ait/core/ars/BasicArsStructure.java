package dev.amble.ait.core.ars;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.amble.ait.data.schema.desktop.textures.StructurePreviewTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public record BasicArsStructure(Identifier id, Identifier structureId, StructurePreviewTexture previewTexture, Text text) implements ArsStructure {
	public static final Codec<ArsStructure> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Identifier.CODEC.fieldOf("id").forGetter(ArsStructure::id),
			Identifier.CODEC.optionalFieldOf("structure_id").forGetter(ArsStructure::structureIdOptional)
		).apply(instance, BasicArsStructure::new
	));

	public BasicArsStructure(Identifier id, Optional<Identifier> structureId) {
		this(id, structureId.orElse(id), StructurePreviewTexture.textureFromArsId(id), createNameText(id, "ars"));
	}

	public BasicArsStructure(Identifier id) {
		this(id, Optional.of(id));
	}

	@Override
	public String name() {
		return this.text().getString();
	}

	private static Text createNameText(Identifier id, String prefix) {
		// turn stuff like ait:exterior/police_box into ait:police_box
		String[] parts = id.getPath().split("/");
		String last = parts[parts.length - 1];

		return Text.translatable(prefix + "." + id.getNamespace() + "." + last);
	}
}
