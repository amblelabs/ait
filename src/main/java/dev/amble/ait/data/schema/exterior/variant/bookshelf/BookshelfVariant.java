package dev.amble.ait.data.schema.exterior.variant.bookshelf;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.animation.ExteriorAnimation;
import dev.amble.ait.core.tardis.animation.PulsatingAnimation;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.BookshelfDoorVariant;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.BookshelfCategory;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import net.minecraft.util.math.Vec3d;

// a useful class for creating tardim variants as they all have the same filepath you know
public abstract class BookshelfVariant extends ExteriorVariantSchema {
    protected static final String TEXTURE_PATH = "textures/blockentities/exteriors/bookshelf/bookshelf_";

    protected BookshelfVariant(String name) { // idk why i added the modid bit i dont use it later lol
        super(BookshelfCategory.REFERENCE, AITMod.id("exterior/bookshelf/" + name),
                new Loyalty(Loyalty.Type.COMPANION));
    }

    @Override
    public ExteriorAnimation animation(ExteriorBlockEntity exterior) {
        return new PulsatingAnimation(exterior);
    }

    @Override
    public DoorSchema door() {
        return DoorRegistry.REGISTRY.get(BookshelfDoorVariant.REFERENCE);
    }

    @Override
    public boolean hasPortals() {
        return false;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
       return switch (direction) {
            case 0 -> pos.add(0, 0, -0.4); // NORTH
            case 1, 2, 3 -> pos; // NORTH EAST
           case 4 -> pos.add(0.628, 0, 0); // EAST
          case 5, 6, 7 -> pos; // SOUTH EAST
            case 8 -> pos.add(0, 0, 0.4); // SOUTH
           case 9, 10, 11 -> pos; // SOUTH WEST
           case 12 -> pos.add(-0.628, 0, 0); // WEST
            case 13, 14, 15 -> pos; // NORTH WEST
           default -> pos;
      };
  }

    @Override
    public Vec3d seatTranslations() {
        return new Vec3d(0.5, 1, 0.5);
    }

   @Override
   public double portalHeight() {
       return 2.6d;
    }

   @Override
   public double portalWidth() {
       return 1.145d;
   }
}
