package dev.amble.ait.registry.impl.door;


import dev.amble.ait.client.models.AnimatedModel;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.DoorBlockEntity;
import dev.amble.ait.data.schema.door.AnimatedDoor;
import dev.amble.ait.data.schema.door.ClientDoorSchema;
import dev.amble.ait.data.schema.door.DatapackDoor;
import dev.amble.ait.data.schema.door.DoorSchema;
import dev.amble.ait.data.schema.door.impl.*;
import dev.amble.ait.data.schema.door.impl.exclusive.ClientBlueBoxDoorVariant;
import dev.amble.ait.data.schema.door.impl.exclusive.ClientDoomDoorVariant;
import dev.amble.lib.client.bedrock.BedrockModel;
import dev.amble.lib.client.bedrock.BedrockModelRegistry;
import dev.amble.lib.register.datapack.DatapackRegistry;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

public class ClientDoorRegistry extends DatapackRegistry<ClientDoorSchema> {
    private static final ClientDoorRegistry INSTANCE = new ClientDoorRegistry();

    public static ClientDoorRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Will return the clients version of a servers door variant
     *
     * @param parent
     * @return the first variant found as there should only be one client version
     */
    public static ClientDoorSchema withParent(DoorSchema parent) {
        for (ClientDoorSchema schema : ClientDoorRegistry.getInstance().toList()) {
            if (schema.parent() == null)
                continue;

            if (schema.parent().id().equals(parent.id()))
                return schema;
        }

        return null;
    }

    public static ClientDoorSchema TARDIM;
    public static ClientDoorSchema CLASSIC;
    public static ClientDoorSchema CLASSIC_HUDOLIN;
    public static ClientDoorSchema BOOTH;
    public static ClientDoorSchema CAPSULE;
    public static ClientDoorSchema BOX;
    public static ClientDoorSchema BOX_CORAL;
    public static ClientDoorSchema BOX_RENAISSANCE;
    public static ClientDoorSchema HEAD;
    public static ClientDoorSchema GROWTH;
    public static ClientDoorSchema PLINTH;
    public static ClientDoorSchema RENEGADE;
    public static ClientDoorSchema BOOKSHELF;
    public static ClientDoorSchema GEOMETRIC;
    public static ClientDoorSchema STALLION;
    public static ClientDoorSchema ADAPTIVE;
    public static ClientDoorSchema DALEK_MOD;
    public static ClientDoorSchema PRESENT;
    public static ClientDoorSchema PIPE;

    public static ClientDoorSchema DOOM;
    public static ClientDoorSchema BLUE_BOX;

    @Override
    public void onClientInit() {
        TARDIM = register(new ClientTardimDoorVariant());
        CLASSIC = register(new ClientClassicDoorVariant());
        CLASSIC_HUDOLIN = register(new ClientClassicHudolinDoorVariant());
        BOOTH = register(new ClientBoothDoorVariant());
        CAPSULE = register(new ClientCapsuleDoorVariant());
        BOX = register(new ClientPoliceBoxDoorVariant());
        BOX_CORAL = register(new ClientPoliceBoxCoralDoorVariant());
        BOX_RENAISSANCE = register(new ClientPoliceBoxRenaissanceDoorVariant());
        HEAD = register(new ClientEasterHeadDoorVariant());
        GROWTH = register(new ClientGrowthDoorVariant());
        PLINTH = register(new ClientPlinthDoorVariant());
        RENEGADE = register(new ClientRenegadeDoorVariant());
        BOOKSHELF = register(new ClientBookshelfDoorVariant());
        GEOMETRIC = register(new ClientGeometricDoorVariant());
        STALLION = register(new ClientStallionDoorVariant());
        ADAPTIVE = register(new ClientAdaptiveDoorVariant());
        DALEK_MOD = register(new ClientDalekModDoorVariant());
        PRESENT = register(new ClientPresentDoorVariant());
        PIPE = register(new ClientPipeDoorVariant());

        DOOM = register(new ClientDoomDoorVariant());
        BLUE_BOX = register(new ClientBlueBoxDoorVariant());
    }

    @Override
    public ClientDoorSchema fallback() {
        return CAPSULE;
    }

    @Override
    public void syncToClient(ServerPlayerEntity player) {
        // do not call
    }

    @Override
    public void readFromServer(PacketByteBuf buf) {
        for (DoorSchema schema : DoorRegistry.getInstance().toList()) {
            if (!(schema instanceof DatapackDoor variant)) continue;

            ClientDoorSchema clientSchema = convertDatapack(variant);

            if (clientSchema == null) continue;

            this.register(clientSchema);
        }
    }

    public static ClientDoorSchema convertDatapack(DatapackDoor variant) {
        if (!variant.wasDatapack())
            return convertNonDatapack(variant);

        return new ClientDoorSchema(variant.id()) {
            @Override
            public AnimatedModel<DoorBlockEntity> model() {
                BedrockModel model = BedrockModelRegistry.getInstance().get(variant.getModelId());
                ModelPart root = model.create().createModel();

                return new AnimatedModel<>() {
	                @Override
	                public void renderWithAnimations(ClientTardis tardis, DoorBlockEntity be, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta) {
		                matrices.push();

		                DoorSchema schema = tardis.getExterior().getVariant().door();

		                if (schema instanceof AnimatedDoor animDoor) {
                            this.getPart().traverse().forEach(ModelPart::resetTransform);
							animDoor.runAnimations(root, matrices, tickDelta, tardis);
		                }
		                root.render(matrices, vertices, light, overlay, red, green, blue, pAlpha);

		                matrices.pop();
	                }

	                @Override
	                public ModelPart getPart() {
		                return root;
	                }
                };
            }
        };
    }

    private static ClientDoorSchema convertNonDatapack(DatapackDoor variant) {
        if (variant.wasDatapack())
            return convertDatapack(variant);

        return getInstance().get(variant.id());
    }
}
