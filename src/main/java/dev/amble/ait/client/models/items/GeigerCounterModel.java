package dev.amble.ait.client.models.items;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.util.AngleInterpolator;
import dev.amble.ait.core.item.RiftScannerItem;
import dev.amble.ait.core.world.TardisServerWorld;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class GeigerCounterModel extends Model {

    public static final Identifier TEXTURE = AITMod.id("textures/blockentities/items/geiger_counter.png");
    public static final Identifier EMISSION = AITMod.id("textures/blockentities/items/geiger_counter_emission.png");

    private static final float MULTIPLIER = (float) (360 * Math.PI / 180);

    private final AngleInterpolator aimedInterpolator = new AngleInterpolator();
    private final AngleInterpolator aimlessInterpolator = new AngleInterpolator();

    private final ModelPart root;

    public GeigerCounterModel(ModelPart root) {
        super(RenderLayer::getEntityCutout);
        this.root = root.getChild("root");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData root = modelPartData.addChild("root", ModelPartBuilder.create()
                .uv(29, 1).cuboid(-10.5F, -11.0F, 7.0F, 5.0F, 10.0F, 2.0F, new Dilation(0.0F))
                .uv(18, 10).cuboid(-7.0F, -6.0F, 6.9F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(19, 10).cuboid(-6.7F, -5.7F, 6.8F, 0.4F, 0.4F, 0.0F, new Dilation(0.0F))
                .uv(11, 9).cuboid(-10.0F, -2.5F, 6.9F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(20, 6).cuboid(-11.5F, -8.0F, 7.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(16, 6).cuboid(-11.5F, -10.1F, 7.5F, 1.0F, 2.0F, 1.0F, new Dilation(0.0F))
                .uv(0, 12).cuboid(-11.25F, -8.0F, 8.0F, 4.0F, 9.0F, 0.0F, new Dilation(0.0F))
                .uv(20, 7).cuboid(-11.5F, -11.4F, 7.5F, 1.0F, 1.0F, 1.0F, new Dilation(0.0F))
                .uv(16, 0).cuboid(-11.3F, -12.4F, 7.7F, 0.6F, 5.0F, 0.6F, new Dilation(0.0F))
                .uv(0, 3).cuboid(-11.1F, -17.4F, 8.0F, 0.2F, 5.0F, 0.0F, new Dilation(0.0F))
                .uv(0, 0).cuboid(-9.5F, -14.5F, 6.5F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F))
                .uv(7, 3).cuboid(-9.5F, -14.5F, 9.5F, 3.0F, 3.0F, 0.0F, new Dilation(0.0F))
                .uv(0, -3).cuboid(-9.5F, -14.5F, 6.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(-2, 3).cuboid(-9.5F, -11.5F, 6.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(3, 0).cuboid(-9.5F, -14.5F, 6.5F, 3.0F, 0.0F, 3.0F, new Dilation(0.0F))
                .uv(7, 0).cuboid(-6.5F, -14.5F, 6.5F, 0.0F, 3.0F, 3.0F, new Dilation(0.0F))
                .uv(19, 0).cuboid(-9.0F, -11.5F, 7.0F, 2.0F, 1.0F, 2.0F, new Dilation(-0.001F))
                .uv(53, 0).cuboid(-8.5F, -6.75F, 5.0F, 1.0F, 5.0F, 2.0F, new Dilation(0.0F))
                .uv(44, 0).cuboid(-5.25F, -10.2F, 7.25F, 1.5F, 9.0F, 1.5F, new Dilation(0.0F))
                .uv(0, 20).cuboid(-5.5F, -9.7F, 7.75F, 0.3F, 8.0F, 0.5F, new Dilation(0.0F))
                .uv(18, 9).cuboid(-5.25F, -9.7F, 7.25F, 1.5F, 1.0F, 1.5F, new Dilation(0.1F))
                .uv(18, 9).cuboid(-5.25F, -2.7F, 7.25F, 1.5F, 1.0F, 1.5F, new Dilation(0.1F))
                .uv(8, 7).cuboid(-10.0F, -10.5F, 6.5F, 0.5F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(8, 7).cuboid(-6.5F, -10.5F, 6.5F, 0.5F, 4.0F, 1.0F, new Dilation(0.0F))
                .uv(7, 9).cuboid(-9.5F, -10.5F, 6.5F, 3.0F, 0.5F, 1.0F, new Dilation(0.0F))
                .uv(8, 9).cuboid(-9.5F, -7.0F, 6.5F, 3.0F, 0.5F, 1.0F, new Dilation(0.0F))
                .uv(0, 8).cuboid(-9.9F, -10.4F, 6.85F, 3.8F, 3.8F, 0.0F, new Dilation(0.0F)), ModelTransform.pivot(8.0F, 24.0F, -8.0F));

        root.addChild("arrow", ModelPartBuilder.create().uv(1, 6).cuboid(-0.2F, -1.4F, 1.75F, 0.4F, 1.9F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-8.0F, -8.5F, 5.0F, 0.0F, 0.0F, 0F));

        root.addChild("cube_r1", ModelPartBuilder.create().uv(18, 11).cuboid(-1.45F, 6.9F, 0.0F, 2.9F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(18, 8).cuboid(0.0F, 6.9F, -1.45F, 0.0F, 1.0F, 2.9F, new Dilation(0.0F))
                .uv(18, 11).cuboid(-1.55F, -0.1F, 0.0F, 3.0F, 1.0F, 0.0F, new Dilation(0.0F))
                .uv(18, 8).cuboid(0.0F, -0.1F, -1.45F, 0.0F, 1.0F, 3.0F, new Dilation(0.0F)), ModelTransform.of(-4.5F, -9.5F, 8.0F, 0.0F, 0.7854F, 0.0F));

        root.addChild("cube_r2", ModelPartBuilder.create().uv(10, 9).cuboid(-0.1F, -0.5F, -1.4F, 0.2F, 1.0F, 0.3F, new Dilation(0.0F))
                .uv(10, 11).cuboid(-0.5F, -0.5F, -1.1F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-9.5F, -5.5F, 8.0F, 0.0F, 0.0F, 0.7854F));

        root.addChild("cube_r3", ModelPartBuilder.create().uv(19, 10).cuboid(-0.5F, -0.5F, -1.1F, 1.0F, 1.0F, 0.0F, new Dilation(0.0F)), ModelTransform.of(-6.5F, -2.0F, 8.0F, 0.0F, 0.0F, 0.3927F));

        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        root.render(matrices, vertexConsumer, light, overlay, red, green, blue, alpha);
    }

    public void setAngles(MatrixStack matrices, ModelTransformationMode renderMode, boolean left) {
        matrices.translate(0.5, -1.75, -0.5);

        if (renderMode == ModelTransformationMode.GUI)
            matrices.translate(0, 0.2, 0);
    }

    public void render(@Nullable ClientWorld world, @Nullable LivingEntity entity, ItemStack stack, MatrixStack matrices, VertexConsumerProvider provider, int light, int overlay, int seed) {
        this.root.getChild("arrow").roll = this.unclampedCall(stack, world, entity, seed) * MULTIPLIER;
        this.render(matrices, provider.getBuffer(RenderLayer.getEntityCutout(TEXTURE)), light, overlay, 1, 1, 1, 1);
        this.render(matrices, provider.getBuffer(RenderLayer.getEntityCutout(EMISSION)), 0xf000f0, overlay, 1, 1, 1, 1);
    }

    public float unclampedCall(ItemStack stack, @Nullable ClientWorld clientWorld, @Nullable LivingEntity livingEntity, int i) {
        Entity entity = livingEntity != null ? livingEntity : stack.getHolder();

        if (entity == null)
            return 0;

        clientWorld = this.getClientWorld(entity, clientWorld);

        return clientWorld == null ? 0.0F : this.getAngle(RiftScannerItem.getTarget(stack)
                .getCenterAtY(75), clientWorld, i, entity);
    }

    private float getAngle(BlockPos target, ClientWorld world, int seed, Entity entity) {
        long l = world.getTime();
        return !this.canPointTo(entity, target, world)
                ? this.getAimlessAngle(seed, l)
                : this.getAngleTo(entity, l, target);
    }

    private float getAimlessAngle(int seed, long time) {
        if (this.aimlessInterpolator.shouldUpdate(time))
            this.aimlessInterpolator.update(time, Math.random());

        double d = this.aimlessInterpolator.value() + (double) ((float) this.scatter(seed) / 2.14748365E9F);
        return MathHelper.floorMod((float) d, 1.0F);
    }

    private int scatter(int seed) {
        return seed * 1327217883;
    }

    private boolean canPointTo(Entity entity, @Nullable BlockPos pos, @Nullable ClientWorld world) {
        return world != null && !TardisServerWorld.isTardisDimension(world) &&
                pos != null && !(pos.getSquaredDistance(entity.getPos()) < 9.999999747378752E-6);
    }

    private @Nullable ClientWorld getClientWorld(Entity entity, @Nullable ClientWorld world) {
        return world == null && entity.getWorld() instanceof ClientWorld ? (ClientWorld) entity.getWorld() : world;
    }

    private double getAngleTo(Entity entity, BlockPos pos) {
        Vec3d vec3d = Vec3d.ofCenter(pos);
        return Math.atan2(vec3d.getZ() - entity.getZ(), vec3d.getX() - entity.getX()) / 6.2831854820251465;
    }

    private float getAngleTo(Entity entity, long time, BlockPos pos) {
        double d = this.getAngleTo(entity, pos);
        double e = this.getBodyYaw(entity);
        double f;
        if (entity instanceof PlayerEntity playerEntity) {
            if (playerEntity.isMainPlayer()) {
                if (this.aimedInterpolator.shouldUpdate(time)) {
                    this.aimedInterpolator.update(time, 0.5 - (e - 0.25));
                }

                f = d + this.aimedInterpolator.value();
                return MathHelper.floorMod((float) f, 1.0F);
            }
        }

        f = 0.5 - (e - 0.25 - d);
        return MathHelper.floorMod((float) f, 1.0F);
    }

    private double getBodyYaw(Entity entity) {
        return MathHelper.floorMod(entity.getBodyYaw() / 360.0F, 1.0);
    }
}