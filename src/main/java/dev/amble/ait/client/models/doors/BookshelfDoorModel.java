package dev.amble.ait.client.models.doors;

import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.tardis.handler.DoorHandler;

public class BookshelfDoorModel extends DoorModel {
    private final ModelPart bookshelf;

    public BookshelfDoorModel(ModelPart root) {
        this.bookshelf = root.getChild("bookshelf");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bookshelf = modelPartData.addChild("bookshelf",
                ModelPartBuilder.create().uv(51, 14)
                        .cuboid(-12.5938F, -21.5354F, 7.5385F, 2.0F, 42.0F, 2.0F, new Dilation(0.0F)).uv(14, 14)
                        .cuboid(9.4062F, -21.5354F, 7.5385F, 2.0F, 42.0F, 2.0F, new Dilation(0.0F)).uv(14, 73)
                        .cuboid(-10.5938F, 18.4646F, 7.5385F, 20.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(0, 78)
                        .cuboid(-10.5938F, -19.5354F, 7.5385F, 20.0F, 38.0F, 0.0F, new Dilation(0.05F)).uv(72, 57)
                        .cuboid(-10.5938F, -21.5354F, 7.5385F, 20.0F, 2.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.5938F, 3.5354F, 15.4615F, 0.0F, 3.1416F, 0.0F));

        bookshelf.addChild("left_door", ModelPartBuilder.create().uv(66, 81).cuboid(-9.0F, -38.0F, -1.0F, 10.0F, 38.0F,
                2.0F, new Dilation(0.0F)), ModelTransform.pivot(8.4062F, 18.4646F, 8.5385F));

        bookshelf.addChild("right_door", ModelPartBuilder.create().uv(41, 81).cuboid(-1.0F, -38.0F, -1.0F, 10.0F, 38.0F,
                2.0F, new Dilation(0.0F)), ModelTransform.pivot(-9.5938F, 18.4646F, 8.5385F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        bookshelf.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, AbstractLinkableBlockEntity doorEntity, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, float tickDelta) {
        DoorHandler door = tardis.door();

        if (!AITModClient.CONFIG.animateDoors) {
            this.bookshelf.getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? 4.75F : 0.0F;
            this.bookshelf.getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen()) ? -4.75F : 0.0F;
        } else {
            float maxRot = 90f;
            this.bookshelf.getChild("left_door").yaw = (float) Math.toRadians(maxRot*door.getRightRot());
            this.bookshelf.getChild("right_door").yaw = (float) -Math.toRadians(maxRot*door.getLeftRot());
        }

        matrices.push();
        matrices.scale(1F, 1F, 1F);
        matrices.translate(0, -1.5, 0);

        super.renderWithAnimations(tardis, doorEntity, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, tickDelta);
        matrices.pop();
    }

    @Override
    public ModelPart getPart() {
        return bookshelf;
    }
}
