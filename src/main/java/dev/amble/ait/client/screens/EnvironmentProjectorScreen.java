package dev.amble.ait.client.screens;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.screens.widget.CompassYawWidget;
import dev.amble.ait.client.screens.widget.PitchLadderWidget;
import dev.amble.ait.client.screens.widget.WorldListWidget;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.blocks.EnvironmentProjectorBlock;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.util.WorldUtil;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class EnvironmentProjectorScreen extends TardisScreen {
    private static final Identifier DEFAULT_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/environment_menu_sky.png");
    private static final Identifier DIRECTION_TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/environment_menu_direction_compass.png");

    private GuiSelection currentGuiSelection = GuiSelection.SKY;
    private final BlockPos projectorPos;
    private WorldListWidget worldList;
    private TextWidget enabledLabel;
    private CheckboxWidget enabledCheckbox;
    private PitchLadderWidget pitchLadder;
    private CompassYawWidget yawCompass;

    int bgHeight = 150;
    int bgWidth = 216;
    int left, top;

    private static final RegistryKey<World> DEFAULT = World.END;
    private RegistryKey<World> current = DEFAULT;
    private float currentYaw = 0f;
    private float currentPitch = 0f;
    private enum GuiSelection { SKY, DIRECTION }

    public EnvironmentProjectorScreen(ClientTardis tardis, BlockPos projectorPos) {
        super(Text.of("screen." + AITMod.MOD_ID + ".environment_projector"), tardis);
        this.client = MinecraftClient.getInstance();
        this.projectorPos = projectorPos;
    }

    public void apply(Tardis tardis, BlockState state) {
        tardis.stats().skybox().set(this.current);
        tardis.stats().skyboxYaw().set(this.currentYaw);
        tardis.stats().skyboxPitch().set(this.currentPitch);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private void switchToDirectionTab() {
        this.currentGuiSelection = GuiSelection.DIRECTION;
        this.clearChildren();
        renderTabButtons();
        directionTab();
    }

    private void switchToSkyTab() {
        this.currentGuiSelection = GuiSelection.SKY;
        this.clearChildren();
        renderTabButtons();
        skyTab();
    }

    public void onWorldSelected(RegistryKey<World> key) {
        BlockState state = this.client.world.getBlockState(projectorPos);
        this.current = key;

        AITMod.sendProjectorSelection(projectorPos, key.getValue());

        if (state.get(EnvironmentProjectorBlock.ENABLED)) {
            this.apply(tardis(), state);
        }

    }

    private void sendAngles() {
        AITMod.sendProjectorAngles(projectorPos, this.currentYaw, this.currentPitch);
    }

    private void snapToDirection(Direction direction) {
        if (direction == Direction.UP) {
            this.currentYaw = 0f;
            this.currentPitch = 90f;
        } else if (direction == Direction.DOWN) {
            this.currentYaw = 0f;
            this.currentPitch = -90f;
        } else {
            this.currentYaw = direction.asRotation();
            this.currentPitch = 0f;
        }
        if (this.pitchLadder != null) this.pitchLadder.setValue(this.currentPitch);
        if (this.yawCompass != null) this.yawCompass.setValue(this.currentYaw);
        sendAngles();
    }

    private void renderTabButtons(){
        this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.literal("Sky")) / 2 - 85), (height / 2 - 71),
                this.textRenderer.getWidth(Text.literal("Sky")), 10, Text.literal("Sky"), button -> switchToSkyTab(), this.textRenderer));

        this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.literal("Direction")) / 2 - 35), (height / 2 - 71),
                this.textRenderer.getWidth(Text.literal("Direction")), 10, Text.literal("Direction"), button -> switchToDirectionTab(), this.textRenderer));
    }

    private void directionTab(){
        //north
        this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.literal("NORTH")) / 2 + 0), (height / 2 - 40),
                this.textRenderer.getWidth(Text.literal("NORTH")), 10, Text.literal("NORTH"),
                button -> snapToDirection(Direction.NORTH), this.textRenderer));

        //south
        this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.literal("SOUTH")) / 2 + 0), (height / 2 + 45),
                this.textRenderer.getWidth(Text.literal("SOUTH")), 10, Text.literal("SOUTH"),
                button -> snapToDirection(Direction.SOUTH), this.textRenderer));

        //west
        this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.literal("WEST")) / 2 - 45), (height / 2 + 2),
                this.textRenderer.getWidth(Text.literal("WEST")), 10, Text.literal("WEST"),
                button -> snapToDirection(Direction.WEST), this.textRenderer));

        //east
        this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.literal("EAST")) / 2 + 47), (height / 2 + 2),
                this.textRenderer.getWidth(Text.literal("EAST")), 10, Text.literal("EAST"),
                button -> snapToDirection(Direction.EAST), this.textRenderer));

        //up
        this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.literal("UP")) / 2 - 90), (height / 2 - 50),
                this.textRenderer.getWidth(Text.literal("UP")), 10, Text.literal("UP"),
                button -> snapToDirection(Direction.UP), this.textRenderer));

        //down
        this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.literal("DOWN")) / 2 + 83), (height / 2 - 50),
                this.textRenderer.getWidth(Text.literal("DOWN")), 10, Text.literal("DOWN"),
                button -> snapToDirection(Direction.DOWN), this.textRenderer));
        int ladderW = 40;
        int ladderH = 80;
        int ladderX = width / 2 - 80 - ladderW / 2;
        int ladderY = height / 2 - ladderH / 2 + 15;

        this.pitchLadder = new PitchLadderWidget(ladderX, ladderY, ladderW, ladderH,
                this.currentPitch, this.textRenderer, v -> {
            this.currentPitch = v;
            sendAngles();
        });
        this.addDrawableChild(this.pitchLadder);
        int compassSize = 70;
        this.yawCompass = new CompassYawWidget(width / 2 - compassSize / 2 - 0, height / 2 - compassSize / 2 + 6,
                compassSize, this.currentYaw, v -> {
            this.currentYaw = v;
            sendAngles();
        });
        this.addDrawableChild(this.yawCompass);
    }

    private void skyTab(){
        currentGuiSelection = GuiSelection.SKY;
        this.top = (this.height - this.bgHeight) / 2;
        this.left = (this.width - this.bgWidth) / 2;

        super.init();

        int listLeft = this.left - 0;
        int listTop = this.top + 50;
        int listWidth = this.bgWidth - 15;
        int listHeight = this.bgHeight - 90;
        int itemHeight = 10;

        this.worldList = new WorldListWidget(this.client, listWidth, listHeight, listTop, listTop + listHeight, itemHeight, listLeft, this::onWorldSelected);

        for (ServerWorld world : WorldUtil.getProjectorWorlds()) {
            Identifier id = world.getRegistryKey().getValue();
            Text label = Text.translatableWithFallback(id.toTranslationKey("dimension"), WorldUtil.fakeTranslate(id.getPath()));
            this.worldList.addWorld(world.getRegistryKey(), label);
        }
        this.addDrawableChild(this.worldList);

        BlockState state = this.client.world.getBlockState(projectorPos);
        boolean enabled = state.get(EnvironmentProjectorBlock.ENABLED);

        int labelW = this.textRenderer.getWidth("OFF");
        int checkboxX = width / 2 + 76;
        this.enabledLabel = new TextWidget(
                checkboxX - labelW - 4,
                (height / 2 - 53) + 6,
                labelW,
                10,
                Text.literal(enabled ? "ON" : "OFF"),
                this.textRenderer
        );
        this.enabledLabel.alignRight();
        this.addDrawable(this.enabledLabel);
        this.enabledCheckbox = this.addDrawableChild(new CheckboxWidget(
                checkboxX,
                (height / 2 - 53),
                20, 20,
                Text.empty(),
                enabled
        ) {@Override
            public void onPress() {
                super.onPress();
                boolean checked = this.isChecked();
                EnvironmentProjectorScreen.this.enabledLabel.setMessage(Text.literal(checked ? "ON" : "OFF"));
                AITMod.sendProjectorToggle(projectorPos, checked);
            }});
        this.addDrawable(new TextWidget(
                (width / 2 - this.textRenderer.getWidth("CURRENT: ") / 2 - 72),
                (height / 2 - 52),
                this.textRenderer.getWidth("CURRENT: "),
                10,
                Text.literal("CURRENT: "), this.textRenderer));
        this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.literal("Sky")) / 2 - 85), (height / 2 - 71),
                this.textRenderer.getWidth(Text.literal("Sky")), 10, Text.literal("Sky"), button -> switchToSkyTab(), this.textRenderer));
        this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.literal("Direction")) / 2 - 35), (height / 2 - 71),
                this.textRenderer.getWidth(Text.literal("Direction")), 10, Text.literal("Direction"), button -> switchToDirectionTab(), this.textRenderer));
    }

    @Override
    protected void init() {
        if (tardis() != null && tardis().stats() != null) {
            if (tardis().stats().skybox() != null) {
                RegistryKey<World> saved = tardis().stats().skybox().get();
                if (saved != null) {
                    this.current = saved;
                }
            }
            if (tardis().stats().skyboxYaw() != null) {
                this.currentYaw = tardis().stats().skyboxYaw().get();
            }
            if (tardis().stats().skyboxPitch() != null) {
                this.currentPitch = tardis().stats().skyboxPitch().get();
            }
        }
        renderTabButtons();
        skyTab();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.client.options.inventoryKey.matchesKey(keyCode, scanCode)) {
            this.close();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.drawBackground(context, currentGuiSelection);
        if (currentGuiSelection.equals(GuiSelection.SKY) && this.current != null){
            Identifier currentId = this.current.getValue();
            Text currentText = Text.translatableWithFallback(currentId.toTranslationKey("dimension"), WorldUtil.fakeTranslate(currentId.getPath()));
            float scale = 0.9f;
            int x = this.left + 58;
            int y = this.top + 24;

            context.getMatrices().push();
            context.getMatrices().translate(x, y, 0);
            context.getMatrices().scale(scale, scale, 1);

            context.drawText(
                    this.textRenderer,
                    currentText,
                    0, 0,
                    0xFFFFFF,
                    false
            );

            context.getMatrices().pop();
        }
        if (this.client != null) {
            super.render(context, mouseX, mouseY, delta);
        }
        super.render(context, mouseX, mouseY, delta);
    }

    private void drawBackground(DrawContext context, GuiSelection current) {
        if (current.equals(GuiSelection.SKY)) {
            context.drawTexture(DEFAULT_TEXTURE, left, top, 0, 0, bgWidth, bgHeight);
        } else {
            context.drawTexture(DIRECTION_TEXTURE, left, top, 0, 0, bgWidth, bgHeight);
        }
    }
}
