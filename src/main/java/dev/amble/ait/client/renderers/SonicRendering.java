package dev.amble.ait.client.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.engine.DurableSubSystem;
import dev.amble.ait.core.engine.SubSystem;
import dev.amble.ait.core.engine.block.SubSystemBlockEntity;
import dev.amble.ait.core.engine.impl.EngineSystem;
import dev.amble.ait.core.item.SonicItem;
import dev.amble.ait.core.item.sonic.SonicMode;
import dev.amble.ait.core.world.TardisServerWorld;

public class SonicRendering {
    private static final Identifier SELECTED = AITMod.id("textures/marker/landing.png");

    private final MinecraftClient client;
    private final Profiler profiler;

    public SonicRendering(MinecraftClient client) {
        this.client = client;
        this.profiler = client.getProfiler();
    }
    public SonicRendering() {
        this(MinecraftClient.getInstance());
    }

    public static void renderFloorTexture(BlockPos pos, Identifier texture, @Nullable Identifier previous, boolean spinning) {
        renderFloorTexture(new Vec3d(pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1), texture, previous, spinning);
    }

    public static void renderFloorTexture(Vec3d target, Identifier texture, @Nullable Identifier previous, boolean spinning) {
        Profiler profiler = MinecraftClient.getInstance().world.getProfiler();

        profiler.push("get");
        MinecraftClient client = MinecraftClient.getInstance();
        Camera camera = client.gameRenderer.getCamera();
        MatrixStack matrices = new MatrixStack();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();

        profiler.swap("transform");
        Vec3d transform = target.subtract(camera.getPos());

        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180f));
        matrices.translate(transform.x - 0.5f, transform.y + 0.05f, transform.z - 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90f));

        if (spinning) {
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(client.player.age / 200f * 360f));
        }
        matrices.translate(-0.5f, 0.5f, 0f);

        profiler.swap("vertexes");

        if (!buffer.isBuilding()) buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);

        buffer.vertex(positionMatrix, 0, 0, 0).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
        buffer.vertex(positionMatrix, 0, -1, 0).color(1f, 1f, 1f, 1f).texture(0f, 1f).next();
        buffer.vertex(positionMatrix, 1, -1, 0).color(1f, 1f, 1f, 1f).texture(1f, 1f).next();
        buffer.vertex(positionMatrix, 1, 0, 0).color(1f, 1f, 1f, 1f).texture(1f, 0f).next();

        boolean shouldRender = !texture.equals(previous);

        if (shouldRender) {
            profiler.swap("draw");
            RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.setShaderTexture(0, texture);
            RenderSystem.disableCull();
            RenderSystem.depthFunc(GL11.GL_ALWAYS);

            Tessellator.getInstance().draw();

            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.enableCull();
        }

        profiler.pop();
    }

    public void renderWorld(WorldRenderContext context) {
        Profiler worldProfiler = context.profiler();
        worldProfiler.push("sonic");
        worldProfiler.push("world");

        if (client.player == null)
            return;

        if (isPlayerHoldingSonicOf(SonicMode.Modes.TARDIS) && !TardisServerWorld.isTardisDimension(client.player.getWorld()))
            renderSelectedBlock(context);

        worldProfiler.pop();
        worldProfiler.pop();
    }

    private void renderSelectedBlock(WorldRenderContext context) {
        Profiler worldProfiler = context.profiler();
        worldProfiler.push("target");

        if (!(client.crosshairTarget instanceof BlockHitResult crosshair)) {
            profiler.pop();
            profiler.pop();
            return;
        }
        BlockPos targetPos = crosshair.getBlockPos();
        BlockState state = client.world.getBlockState(targetPos.down());
        if (state.isAir()) {
            profiler.pop();
            return;
        }

        renderFloorTexture(targetPos, SELECTED, null, false);

        worldProfiler.pop();
        worldProfiler.pop();
    }

    public void renderGui(DrawContext context, float delta) {
        if (client.world == null) return;
        if (!isPlayerHoldingScanningSonic()) return;

        profiler.swap("sonic");
        profiler.push("gui");

        profiler.push("target");;
        if (!(client.crosshairTarget instanceof BlockHitResult crosshair)) {
            profiler.pop();
            profiler.pop();
            return;
        }
        BlockPos targetPos = crosshair.getBlockPos();
        BlockState state = client.world.getBlockState(targetPos);

        profiler.swap("redstone");
        renderRedstone(context, state, targetPos);
        profiler.swap("subsystem_info");
        renderSubSystemInfo(context, targetPos);

        profiler.pop();
        profiler.pop();
    }

    private void renderRedstone(DrawContext context, BlockState state, BlockPos pos) {
        profiler.push("power");
        renderPower(context, pos);
        profiler.pop();
    }

    private void renderPower(DrawContext context, BlockPos pos) {
        int power = this.client.world.getReceivedRedstonePower(pos);
        if (power == 0) return;

        context.drawCenteredTextWithShadow(client.textRenderer, "" + power, getCentreX(), (int) (getMaxY() * 0.4), Colors.WHITE);
    }

    private void renderSubSystemInfo(DrawContext context, BlockPos pos) {
        if (!(client.world.getBlockEntity(pos) instanceof SubSystemBlockEntity be)) return;

        SubSystem system = be.system();
        if (system == null) return;

        Text text = Text.empty();

        if (system instanceof DurableSubSystem) {
            text = Text.literal((Math.round(((DurableSubSystem) be.system()).durability())) + " / 1250");
        }
        if (!system.isEnabled() && !(system instanceof EngineSystem)) {
            text = Text.translatable("tardis.message.subsystem.requires_link");
        }

        context.drawCenteredTextWithShadow(client.textRenderer, text, getCentreX(), (int) (getMaxY() * 0.42), Colors.WHITE);

        text = system.name();
        context.drawCenteredTextWithShadow(client.textRenderer, text, getCentreX(), (int) (getMaxY() * 0.46), Colors.WHITE);
    }

    private int getMaxX() {
        return client.getWindow().getScaledWidth();
    }

    private int getMaxY() {
        return client.getWindow().getScaledHeight() ;
    }


    private int getCentreX() {
        return getMaxX() / 2;
    }

    private int getCentreY() {
        return getMaxY() / 2;
    }

    private int getTextWidth(String text) {
        return client.textRenderer.getWidth(text);
    }

    private static SonicRendering INSTANCE;

    public static SonicRendering getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SonicRendering();

        return INSTANCE;
    }

    public static boolean isScanningSonic(ItemStack sonic) {
        return isSonicOf(SonicMode.Modes.SCANNING, sonic);
    }

    public static boolean isSonicOf(SonicMode mode, ItemStack sonic) {
        if (sonic.getItem() instanceof SonicItem)
            return SonicItem.mode(sonic) == mode;

        return false;
    }

    public static boolean isPlayerHoldingScanningSonic() {
        return isPlayerHoldingSonicOf(SonicMode.Modes.SCANNING);
    }

    public static boolean isPlayerHoldingSonicOf(SonicMode mode) {
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (player == null)
            return false;

        ItemStack sonic = getSonicStack(player);

        if (sonic == null)
            return false;

        return isSonicOf(mode, sonic);
    }

    public static ItemStack getSonicStack(PlayerEntity player) {
        if (player.getMainHandStack().getItem() instanceof SonicItem)
            return player.getMainHandStack();

        if (player.getOffHandStack().getItem() instanceof SonicItem)
            return player.getOffHandStack();

        return null;
    }
}
