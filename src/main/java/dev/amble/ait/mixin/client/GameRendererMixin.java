package dev.amble.ait.mixin.client;

import dev.amble.ait.core.blocks.DoorBlock;
import dev.amble.ait.core.blocks.ExteriorBlock;
import org.objectweb.asm.Opcodes;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow @Final MinecraftClient client;

    @Inject(method = "updateTargetedEntity", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;crosshairTarget:Lnet/minecraft/util/hit/HitResult;",
            opcode = Opcodes.PUTFIELD, ordinal = 0, shift = At.Shift.AFTER))
    private void ait$targetUpperHalf(float tickDelta, CallbackInfo ci) {
        Entity camera = this.client.getCameraEntity();
        World world = this.client.world;

        Vec3d start = camera.getCameraPosVec(tickDelta);
        Vec3d end = start.add(camera.getRotationVec(tickDelta).multiply(this.client.interactionManager.getReachDistance()));
        BlockPos.Mutable below = new BlockPos.Mutable();

        BlockHitResult hit = BlockView.raycast(start, end, null, (ctx, pos) -> {
            below.set(pos, Direction.DOWN);
            BlockState state = world.getBlockState(below);

            if (!(state.getBlock() instanceof DoorBlock) && !(state.getBlock() instanceof ExteriorBlock))
                return null;

            return state.getOutlineShape(world, below, ShapeContext.of(camera)).raycast(start, end, below);
        }, ctx -> null);

        if (hit == null || this.client.crosshairTarget.getPos().squaredDistanceTo(start) < hit.getPos().squaredDistanceTo(start))
            return;

        BlockPos pos = hit.getBlockPos().toImmutable();
        Vec3d at = hit.getPos();

        this.client.crosshairTarget = new BlockHitResult(new Vec3d(at.x, Math.min(at.y, pos.getY() + 1), at.z),
                hit.getSide(), pos, hit.isInsideBlock());
    }
}
