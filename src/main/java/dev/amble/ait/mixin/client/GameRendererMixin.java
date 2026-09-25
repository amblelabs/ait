package dev.amble.ait.mixin.client;

import dev.amble.ait.core.blocks.DoorBlock;
import dev.amble.ait.core.blocks.ExteriorBlock;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final MinecraftClient client;

    @Inject(method = "updateTargetedEntity", at = @At("TAIL"))
    private void ait$targetUpperHalf(float tickDelta, CallbackInfo ci) {
        Entity camera = this.client.getCameraEntity();
        World world = this.client.world;

        if (camera == null || world == null || this.client.interactionManager == null
                || this.client.crosshairTarget instanceof EntityHitResult)
            return;

        Vec3d start = camera.getCameraPosVec(tickDelta);
        Vec3d end = start.add(camera.getRotationVec(tickDelta).multiply(this.client.interactionManager.getReachDistance()));

        BlockHitResult hit = BlockView.raycast(start, end, null, (ctx, pos) -> {
            BlockPos below = pos.down();
            BlockState state = world.getBlockState(below);

            if (!(state.getBlock() instanceof DoorBlock) && !(state.getBlock() instanceof ExteriorBlock))
                return null;

            return state.getOutlineShape(world, below, ShapeContext.of(camera)).raycast(start, end, below);
        }, ctx -> null);

        if (hit == null)
            return;

        HitResult current = this.client.crosshairTarget;

        if (current != null && current.getType() != HitResult.Type.MISS
                && current.getPos().squaredDistanceTo(start) <= hit.getPos().squaredDistanceTo(start))
            return;

        BlockPos pos = hit.getBlockPos();
        Vec3d at = hit.getPos();

        this.client.crosshairTarget = new BlockHitResult(new Vec3d(at.x, Math.min(at.y, pos.getY() + 1), at.z),
                hit.getSide(), pos, false);
    }
}
