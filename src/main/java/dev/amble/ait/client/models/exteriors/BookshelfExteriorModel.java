package dev.amble.ait.client.models.exteriors; // Made with Blockbench 4.10.1
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports

import dev.amble.ait.api.tardis.link.v2.Linkable;
import dev.amble.ait.client.AITModClient;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blockentities.ExteriorBlockEntity;
import dev.amble.ait.core.tardis.handler.DoorHandler;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;

public class BookshelfExteriorModel extends ExteriorModel {
    private final ModelPart bookshelf;

    public BookshelfExteriorModel(ModelPart root) {
        this.bookshelf = root.getChild("bookshelf");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData bookshelf = modelPartData.addChild("bookshelf",
                ModelPartBuilder.create().uv(37, 0)
                        .cuboid(-12.5938F, -21.5354F, -6.4615F, 2.0F, 42.0F, 16.0F, new Dilation(0.0F)).uv(0, 0)
                        .cuboid(9.4062F, -21.5354F, -6.4615F, 2.0F, 42.0F, 16.0F, new Dilation(0.0F)).uv(0, 59)
                        .cuboid(-10.5938F, 18.4646F, -6.4615F, 20.0F, 2.0F, 16.0F, new Dilation(0.0F)).uv(0, 78)
                        .cuboid(-10.5938F, -19.5354F, 7.5385F, 20.0F, 38.0F, 0.0F, new Dilation(0.05F)).uv(74, 0)
                        .cuboid(-10.5938F, 4.4646F, -6.4615F, 20.0F, 2.0F, 14.0F, new Dilation(0.0F)).uv(21, 0)
                        .cuboid(-9.8438F, -3.5354F, -5.4615F, 6.0F, 8.0F, 6.0F, new Dilation(0.0F)).uv(59, 64)
                        .cuboid(-10.5938F, -6.5354F, -6.4615F, 20.0F, 2.0F, 14.0F, new Dilation(0.0F)).uv(58, 43)
                        .cuboid(-10.5938F, -21.5354F, -6.4615F, 20.0F, 2.0F, 16.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.5938F, 3.5354F, 1.4615F, 0.0F, 3.1416F, 0.0F));

        ModelPartData cube_r1 = bookshelf.addChild("cube_r1",
                ModelPartBuilder.create().uv(91, 98).cuboid(14.0F, -8.0F, -2.0F, 2.0F, 8.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(-17.5938F, 2.4646F, -3.4615F, 0.0F, 0.0F, 0.1309F));

        ModelPartData cube_r2 = bookshelf.addChild("cube_r2",
                ModelPartBuilder.create().uv(101, 28).cuboid(14.0F, -8.0F, -2.0F, 2.0F, 8.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(-15.5938F, 2.4646F, -3.4615F, 0.0F, 0.0F, 0.1309F));

        ModelPartData cube_r3 = bookshelf
                .addChild("cube_r3",
                        ModelPartBuilder.create().uv(102, 107).cuboid(14.0F, -8.0F, -2.0F, 2.0F, 8.0F, 6.0F,
                                new Dilation(0.0F)),
                        ModelTransform.of(-13.5938F, 2.4646F, -3.4615F, 0.0F, 0.0F, 0.1309F));

        ModelPartData cube_r4 = bookshelf.addChild("cube_r4",
                ModelPartBuilder.create().uv(112, 17).cuboid(14.0F, -8.0F, -2.0F, 2.0F, 8.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(-11.5938F, 2.4646F, -3.4615F, 0.0F, 0.0F, 0.1309F));

        ModelPartData cube_r5 = bookshelf.addChild("cube_r5",
                ModelPartBuilder.create().uv(114, 62).cuboid(14.0F, -8.0F, -2.0F, 2.0F, 8.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(-7.5938F, 2.4646F, -3.4615F, 0.0F, 0.0F, 0.1309F));

        ModelPartData left_door = bookshelf.addChild("left_door", ModelPartBuilder.create().uv(66, 81).cuboid(-9.0F,
                -38.0F, -1.0F, 10.0F, 38.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(8.4062F, 18.4646F, 8.5385F));

        ModelPartData right_door = bookshelf.addChild("right_door", ModelPartBuilder.create().uv(41, 81).cuboid(-1.0F,
                -38.0F, -1.0F, 10.0F, 38.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(-9.5938F, 18.4646F, 8.5385F));

        ModelPartData plant = bookshelf.addChild("plant",
                ModelPartBuilder.create().uv(58, 0).cuboid(-2.5F, 3.0F, -2.5F, 5.0F, 5.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(4.9062F, 10.4646F, 1.0385F, 0.0F, 0.2182F, 0.0F));

        ModelPartData cube_r6 = plant.addChild("cube_r6",
                ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, -3.0F, -2.5F, 0.0F, 6.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData cube_r7 = plant.addChild("cube_r7",
                ModelPartBuilder.create().uv(0, 59).cuboid(0.0F, -3.0F, -2.5F, 0.0F, 6.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData doom_helmet = bookshelf.addChild("doom_helmet", ModelPartBuilder.create(),
                ModelTransform.pivot(-8.5938F, 18.3646F, -1.4615F));

        ModelPartData cube_r8 = doom_helmet.addChild("cube_r8",
                ModelPartBuilder.create().uv(74, 17).cuboid(0.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.25F))
                        .uv(91, 81).cuboid(0.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.3054F, 0.0F));

        ModelPartData door_open_book = bookshelf.addChild("door_open_book", ModelPartBuilder.create(),
                ModelTransform.pivot(5.8F, 4.4567F, 0.5385F));

        ModelPartData cube_r9 = door_open_book.addChild("cube_r9",
                ModelPartBuilder.create().uv(113, 98).cuboid(14.0F, -8.0F, -2.0F, 2.0F, 8.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.of(-15.3938F, -1.9921F, -4.0F, 0.0F, 0.0F, 0.1309F));

        ModelPartData film = bookshelf.addChild("film",
                ModelPartBuilder.create().uv(46, 9)
                        .cuboid(-2.1667F, 0.1667F, -1.1667F, 0.0F, 1.0F, 1.0F, new Dilation(0.05F)).uv(0, 12)
                        .cuboid(-2.1667F, 0.1667F, -1.1667F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)).uv(11, 5)
                        .cuboid(-0.1667F, -0.8333F, 0.8333F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)).uv(45, 14)
                        .cuboid(-0.1667F, -0.8333F, 0.8333F, 1.0F, 0.0F, 1.0F, new Dilation(0.05F)).uv(21, 3)
                        .cuboid(0.8333F, -0.8333F, -1.1667F, 1.0F, 0.0F, 1.0F, new Dilation(0.05F)).uv(0, 0)
                        .cuboid(0.8333F, -0.8333F, -1.1667F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(-0.4271F, -7.7021F, -4.2949F, 0.0F, 0.2182F, 0.0F));

        ModelPartData camera = bookshelf.addChild("camera", ModelPartBuilder.create(),
                ModelTransform.pivot(4.9062F, -7.5354F, -2.4615F));

        ModelPartData cube_r10 = camera.addChild("cube_r10",
                ModelPartBuilder.create().uv(6, 0).cuboid(-1.5F, -1.75F, -1.75F, 3.0F, 3.0F, 1.0F, new Dilation(-0.25F))
                        .uv(40, 0).cuboid(-1.0F, -1.0F, -3.0F, 2.0F, 2.0F, 2.0F, new Dilation(0.0F)).uv(11, 9)
                        .cuboid(-1.0F, -1.0F, -3.1F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(57, 62)
                        .cuboid(-2.5F, -2.0F, -1.0F, 5.0F, 3.0F, 2.0F, new Dilation(0.0F)).uv(7, 12)
                        .cuboid(-1.0F, -2.5F, 0.25F, 2.0F, 1.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.48F, 0.0F));

        ModelPartData candlestick = bookshelf.addChild("candlestick",
                ModelPartBuilder.create().uv(79, 0).cuboid(5.0F, -11.0F, -2.0F, 2.0F, 6.0F, 2.0F, new Dilation(0.0F))
                        .uv(57, 68).cuboid(5.0F, -5.0F, -2.0F, 2.0F, 4.0F, 2.0F, new Dilation(0.25F)).uv(21, 0)
                        .cuboid(7.25F, -4.0F, -1.0F, 2.0F, 2.0F, 0.0F, new Dilation(0.0F)).uv(99, 17)
                        .cuboid(4.0F, -1.0F, -3.0F, 4.0F, 1.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.of(-12.1372F, -6.5354F, -0.6306F, 0.0F, -0.2182F, 0.0F));

        ModelPartData cube_r11 = candlestick.addChild("cube_r11",
                ModelPartBuilder.create().uv(47, 5).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(6.0F, -11.0F, -1.0F, 0.0F, -0.7854F, 0.0F));

        ModelPartData cube_r12 = candlestick.addChild("cube_r12",
                ModelPartBuilder.create().uv(46, 7).cuboid(-0.5F, -1.0F, 0.0F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)),
                ModelTransform.of(6.0F, -11.0F, -1.0F, 0.0F, 0.7854F, 0.0F));

        ModelPartData potion = bookshelf.addChild("potion", ModelPartBuilder.create(),
                ModelTransform.pivot(5.4062F, -6.5354F, 3.5385F));

        ModelPartData cube_r13 = potion
                .addChild(
                        "cube_r13", ModelPartBuilder.create().uv(131, 42).cuboid(-6.0F, -12.75F, -3.0F, 10.0F, 15.0F,
                                0.0F, new Dilation(0.001F)),
                        ModelTransform.of(0.0F, 0.0F, 0.0F, -0.3969F, 0.5372F, 0.0702F));
        return TexturedModelData.of(modelData, 256, 256);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red,
            float green, float blue, float alpha) {
        bookshelf.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    @Override
    public ModelPart getPart() {
        return bookshelf;
    }

    @Override
    public void renderWithAnimations(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices,
                                     VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha) {
        matrices.push();
        matrices.scale(1F, 1F, 1F);
        matrices.translate(0, -1.5f, 0);

        this.renderDoors(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha, false);

        super.renderWithAnimations(tardis, exterior, root, matrices, vertices, light, overlay, red, green, blue, pAlpha);

        matrices.pop();
    }

    @Override
    public <T extends Entity & Linkable> void renderEntity(T falling, ModelPart root, MatrixStack matrices,
                                                           VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        if (!falling.isLinked())
            return;

        matrices.push();
        matrices.scale(1F, 1F, 1F);
        matrices.translate(0, -1.5f, 0);

        if (!AITModClient.CONFIG.animateDoors) {
            DoorHandler door = falling.tardis().get().door();

            this.bookshelf.getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? -4.75F : 0.0F;
            this.bookshelf.getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen()) ? 4.75F : 0.0F;
        } else {
            float maxRot = 90f;
            this.bookshelf.getChild("left_door").yaw = -(float) Math.toRadians(maxRot * falling.tardis().get().door().getLeftRot());
            this.bookshelf.getChild("right_door").yaw = (float) Math.toRadians(maxRot * falling.tardis().get().door().getRightRot());
        }

        super.renderEntity(falling, root, matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
        matrices.pop();
    }

    @Override
    public Animation getAnimationForDoorState(DoorHandler.AnimationDoorState state) {
        return Animation.Builder.create(0).build();
    }

    @Override
    public void renderDoors(ClientTardis tardis, ExteriorBlockEntity exterior, ModelPart root, MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float pAlpha, boolean isBOTI) {
        DoorHandler door = tardis.door();

        if (!AITModClient.CONFIG.animateDoors) {
            this.bookshelf.getChild("left_door").yaw = (door.isLeftOpen() || door.isOpen()) ? -4.75F : 0.0F;
            this.bookshelf.getChild("right_door").yaw = (door.isRightOpen() || door.areBothOpen()) ? 4.75F : 0.0F;
        } else {
            float maxRot = 90f;
            this.bookshelf.getChild("left_door").yaw = -(float) Math.toRadians(maxRot * door.getLeftRot());
            this.bookshelf.getChild("right_door").yaw = (float) Math.toRadians(maxRot * door.getRightRot());
        }

      if (isBOTI) {
          matrices.push();
            matrices.scale(1F, 1F, 1F);
           matrices.translate(0.04, -1.26f, -0.36);
           this.bookshelf.getChild("left_door").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
           this.bookshelf.getChild("right_door").render(matrices, vertices, light, overlay, red, green, blue, pAlpha);
            matrices.pop();
        }
    }
}
