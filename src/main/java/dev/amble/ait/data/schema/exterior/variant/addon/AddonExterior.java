package dev.amble.ait.data.schema.exterior.variant.addon;

import java.util.function.BiFunction;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import dev.amble.ait.client.models.doors.DoorModel;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.models.exteriors.SimpleExteriorModel;
import dev.amble.ait.client.screens.interior.InteriorSettingsScreen;
import dev.amble.ait.data.Loyalty;
import dev.amble.ait.data.datapack.exterior.BiomeOverrides;
import dev.amble.ait.data.schema.door.ClientDoorSchema;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.registry.impl.door.ClientDoorRegistry;
import dev.amble.ait.registry.impl.door.DoorRegistry;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;

/**
 * An all-in-one utility class for creating addon exteriors
 * <br>
 * Example usage: (change values as desired) <br>
 * <code>public static AddonExterior MY_EXTERIOR;</code> ( somewhere in your mod's main class ) <br>
 * and then in the init - <code>MY_EXTERIOR = new AddonExterior(PoliceBoxCategory.REFERENCE, "my_mod", "my_exterior").register();</code> <br>
 * setting the door - <code>MY_EXTERIOR.setDoor(new AddonExterior.Door(MY_EXTERIOR, true, SoundEvents.BLOCK_WOODEN_DOOR_OPEN, SoundEvents.BLOCK_WOODEN_DOOR_CLOSE)).toDoor().register()</code>
 * <br> <br>
 * Now in a client-sided init ( eg your client initializer ), you can set the client versions <br>
 * setting exterior model - <code>MyMod.MY_EXTERIOR.setModel(new MyExteriorModel()).toClient().register()</code> <br>
 * setting door model - <code>MyMod.MY_EXTERIOR.toDoor().setModel(new MyDoorModel()).toClient().register()</code> <br>
 * and then you place your textures in assets/my_mod/textures/blockentities/exteriors/my_exterior/my_exterior.png and assets/my_mod/textures/blockentities/exteriors/my_exterior/my_exterior_emission.png
 *
 * @see SimpleExteriorModel
 * @see DoorModel
 */
public class AddonExterior extends ExteriorVariantSchema {
    protected final String modid;
    protected final String name;

    @Environment(EnvType.CLIENT)
    private ClientExterior client;
    private Door door;
    private float portalWidth = -1f;
    private float portalHeight = -1f;
    @Nullable private BiFunction<Vec3d, Byte, Vec3d> portalTranslations;
    @Environment(EnvType.CLIENT)
    private Vector3f sonicItemTranslations;
    @Environment(EnvType.CLIENT)
    private boolean hasTransparentDoors;
    private Vec3d seatTranslations;

    public AddonExterior(Identifier category, String modid, String name) {
        super(category, new Identifier(modid, "exterior/" + name), Loyalty.fromLevel(Loyalty.Type.OWNER.level));

        this.modid = modid;
        this.name = name;
    }

    /**
     * This registers the exterior variant to the registry
     * Call this once in your mod's initialization
     */
    public AddonExterior register() {
        ExteriorVariantRegistry.getInstance().register(this);

        return this;
    }

    public AddonExterior copy(String modid, String name, boolean register) {
        AddonExterior copy = new AddonExterior(this.categoryId(), modid, name);

        // copy door stuff
        if (this.door != null) {
            copy.setDoor(new Door(copy, this.door.isDouble(), this.door.openSound(), this.door.closeSound()));

            if (register) {
                copy.toDoor().register();
            }
        }

        if (register) {
            copy.register();
        }

        return copy;
    }
    @Environment(EnvType.CLIENT)
    public AddonExterior copyClient(AddonExterior source, boolean register) {
        if (source.client != null) {
            this.setClient(new ClientExterior(this, source.client.model, source.client.sonicItemTranslations,
                    source.client.biomeOverrides, source.client.hasTransparentDoors));

            if (register) {
                this.toClient().register();
            }
        }
        if (this.door != null && source.door != null && source.door.client != null) {
            this.toDoor().setModel(source.door.client.model);

            if (register) {
                this.toDoor().toClient().register();
            }
        }

        return this;
    }

    @Environment(EnvType.CLIENT)
    public AddonExterior setClient(ClientExterior client) {
        this.client = client;

        return this;
    }

    @Override
    public Vec3d seatTranslations() {
        return this.seatTranslations = seatTranslations();
    }

    @Override
    @ApiStatus.Internal
    public DoorSchema door() {
        return this.toDoor();
    }

    @Environment(EnvType.CLIENT)
    public AddonExterior setModel(SimpleExteriorModel model) {
        this.client = new ClientExterior(this, model);

        return this;
    }

    @Environment(EnvType.CLIENT)
    public AddonExterior setHasTransparentDoors(boolean hasTransparentDoors) {
        this.hasTransparentDoors = hasTransparentDoors;

        return this;
    }

    @Environment(EnvType.CLIENT)
    public AddonExterior setSonicItemTranslations(Vector3f translations) {
        this.sonicItemTranslations = translations;

        return this;
    }

    @Environment(EnvType.CLIENT)
    public ClientExterior toClient() {
        if (this.client == null) {
            String message = "Client not created for exterior " + this.id() + ". Did you forget to call setModel?";

            throw new NotImplementedException(message);
        }

        return this.client;
    }

    public AddonExterior setPortalWidth(float width) {
        this.portalWidth = width;

        return this;
    }

    @Override
    public double portalWidth() {
        return this.portalWidth;
    }

    public AddonExterior setPortalHeight(float height) {
        this.portalHeight = height;

        return this;
    }

    @Override
    public double portalHeight() {
        return this.portalHeight;
    }

    @Override
    public Vec3d adjustPortalPos(Vec3d pos, byte direction) {
        if (this.portalTranslations != null) {
            Vec3d translation = this.portalTranslations.apply(pos, direction);

            if (translation != null) {
                return translation;
            }
        }

        return super.adjustPortalPos(pos, direction);
    }

    /**
     * Sets the translation for the portal
     * @param translations (pos, dir) -> (pos)
     * @return this
     */
    public AddonExterior setPortalTranslations(BiFunction<Vec3d, Byte, Vec3d> translations) {
        this.portalTranslations = translations;

        return this;
    }

    @Override
    public boolean hasPortals() {
        return this.portalHeight() != -1 && this.portalWidth() != -1;
    }

    public AddonExterior setDoor(Door door) {
        this.door = door;

        return this;
    }
    public Door toDoor() {
        if (this.door == null) {
            throw new NotImplementedException("Door not set for exterior " + this.id() + ". Dont forget to call setDoor!");
        }

        return this.door;
    }

    @Environment(EnvType.CLIENT)
    public static class ClientExterior extends ClientExteriorVariantSchema {
        protected final AddonExterior server;
        private boolean hasEmission;
        private boolean checkedEmission = false;
        private final Vector3f sonicItemTranslations;
        private final BiomeOverrides biomeOverrides;
        private final boolean hasTransparentDoors;
        private final ExteriorModel model;

        public ClientExterior(AddonExterior parent, ExteriorModel model, Vector3f sonicItemTranslations, BiomeOverrides biomeOverrides, boolean hasTransparentDoors) {
            super(parent.id());

            this.server = parent;

            this.sonicItemTranslations = sonicItemTranslations;
            this.biomeOverrides = biomeOverrides;
            this.hasTransparentDoors = hasTransparentDoors;
            this.model = model;
        }
        public ClientExterior(AddonExterior parent, ExteriorModel model) {
            this(parent, model, parent.sonicItemTranslations != null ? parent.sonicItemTranslations :
                    new Vector3f(0, 0, 0), BiomeOverrides.builder().build(), parent.hasTransparentDoors);
        }
        @Override
        public Identifier texture() {
            return new Identifier(server.modid, "textures/blockentities/exteriors/" + server.name + "/" + server.name + ".png");
        }

        @Override
        public Identifier emission() {
            Identifier id = new Identifier(server.modid, "textures/blockentities/exteriors/" + server.name + "/" + server.name + "_emission.png");

            if (!checkedEmission && MinecraftClient.getInstance().getResourceManager() != null) {
                this.hasEmission = InteriorSettingsScreen.doesTextureExist(id);
                checkedEmission = true;
            }

            return hasEmission ? id : null;
        }

        @Override
        public ExteriorModel model() {
            return this.model;
        }

        @Override
        public Vector3f sonicItemTranslations() {
            return this.sonicItemTranslations;
        }

        @Override
        public BiomeOverrides overrides() {
            return this.biomeOverrides;
        }

        @Override
        public boolean hasTransparentDoors() {
            return this.hasTransparentDoors;
        }

        public ClientExterior register() {
            ClientExteriorVariantRegistry.getInstance().register(this);
            return this;
        }

        public AddonExterior toServer() {
            return server;
        }
    }

    public static class Door extends DoorSchema {
        protected final AddonExterior doorParent;
        private final boolean isDouble;
        private final SoundEvent open;
        private final SoundEvent close;

        @Nullable private BiFunction<Vec3d, Direction, Vec3d> portalTranslations;

        @Environment(EnvType.CLIENT)
        private ClientDoor client;

        public Door(AddonExterior exterior, boolean isDouble, SoundEvent open, SoundEvent close) {
            super(exterior.id());

            this.doorParent = exterior;
            this.isDouble = isDouble;
            this.open = open;
            this.close = close;
        }

        @Override
        public Vec3d adjustPortalPos(Vec3d pos, Direction direction) {
            if (this.portalTranslations != null) {
                Vec3d translation = this.portalTranslations.apply(pos, direction);

                if (translation != null) {
                    return translation;
                }
            }

            return super.adjustPortalPos(pos, direction);
        }

        /**
         * Sets the translation for the portal
         * @param translations (pos, dir) -> (pos)
         * @return this
         */
        public Door setPortalTranslations(BiFunction<Vec3d, Direction, Vec3d> translations) {
            this.portalTranslations = translations;

            return this;
        }

        @Override
        public boolean isDouble() {
            return isDouble;
        }

        @Override
        public SoundEvent openSound() {
            return open;
        }

        @Override
        public SoundEvent closeSound() {
            return close;
        }

        public Door register() {
            DoorRegistry.getInstance().register(this);

            return this;
        }

        public AddonExterior toExterior() {
            return this.doorParent;
        }

        @Environment(EnvType.CLIENT)
        public Door setModel(DoorModel model) {
            this.client = new ClientDoor(this, model);

            return this;
        }

        @Environment(EnvType.CLIENT)
        public ClientDoor toClient() {
            if (this.client == null) {
                throw new NotImplementedException("Client not created for door " + this.id() + ". Dont forget to call Door#setModel!");
            }

            return this.client;
        }
    }

    @Environment(EnvType.CLIENT)
    public static class ClientDoor extends ClientDoorSchema {
        protected final Door clientDoor;
        private final DoorModel model;

        public ClientDoor(Door parent, DoorModel model) {
            super(parent.id());
            this.clientDoor = parent;
            this.model = model;
        }

        @Override
        public DoorModel model() {
            return model;
        }

        public ClientDoor register() {
            ClientDoorRegistry.getInstance().register(this);

            return this;
        }

        public Door toServer() {
            return this.clientDoor;
        }
    }
}
