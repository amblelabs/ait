package dev.amble.ait.client.screens;

import com.google.common.collect.Lists;
import dev.amble.ait.AITMod;
import dev.amble.ait.client.models.exteriors.ExteriorModel;
import dev.amble.ait.client.renderers.AITRenderLayers;
import dev.amble.ait.client.screens.interior.InteriorSettingsScreen;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.client.util.ClientLightUtil;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.handler.FuelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandler;
import dev.amble.ait.core.tardis.handler.travel.TravelHandlerBase;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.data.datapack.DatapackConsole;
import dev.amble.ait.data.schema.exterior.ClientExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.ExteriorCategorySchema;
import dev.amble.ait.data.schema.exterior.ExteriorVariantSchema;
import dev.amble.ait.data.schema.exterior.category.ClassicCategory;
import dev.amble.ait.data.schema.exterior.category.PoliceBoxCategory;
import dev.amble.ait.registry.impl.CategoryRegistry;
import dev.amble.ait.registry.impl.exterior.ClientExteriorVariantRegistry;
import dev.amble.ait.registry.impl.exterior.ExteriorVariantRegistry;
import dev.amble.lib.data.CachedDirectedGlobalPos;
import dev.amble.lib.data.DirectedGlobalPos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;

import java.util.List;
import java.util.Objects;

public class MonitorScreen extends ConsoleScreen {
    private static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/monitor_gui.png");
    private final List<ButtonWidget> buttons = Lists.newArrayList();
    private ExteriorCategorySchema category;
    private ClientExteriorVariantSchema currentVariant;
    int backgroundHeight = 166;
    int backgroundWidth = 256;
    private int tickForSpin = 0;
    private final int APPLY_BUTTON_WIDTH = 53;
    private final int APPLY_BUTTON_HEIGHT = 20;
    private final int SMALL_ARROW_BUTTON_WIDTH = 20;
    private final int SMALL_ARROW_BUTTON_HEIGHT = 12;
    private final int BIG_ARROW_BUTTON_WIDTH = 20;
    private final int BIG_ARROW_BUTTON_HEIGHT = 20;
    private final int INTERIOR_SETTINGS_BUTTON_WIDTH = 20;
    private final int INTERIOR_SETTINGS_BUTTON_HEIGHT = 20;

    public MonitorScreen(ClientTardis tardis, BlockPos console) {
        super(Text.translatable("screen." + AITMod.MOD_ID + ".monitor"), tardis, console);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    private <T extends ClickableWidget> void addButton(T button) {
        this.addDrawableChild(button);
        this.buttons.add((ButtonWidget) button);
    }

    @Override
    protected void init() {
        super.init();
        this.createButtons();
    }

    public ExteriorCategorySchema getCategory() {
        return category == null ? this.tardis().getExterior().getCategory() : category;
    }

    public void setCategory(ExteriorCategorySchema category) {
        this.category = category;

        if (currentVariant == null)
            return;

        if (this.currentVariant.parent().category() != category)
            currentVariant = null;
    }

    public ClientExteriorVariantSchema getCurrentVariant() {
        if (Objects.equals(currentVariant, ClientExteriorVariantRegistry.CORAL_GROWTH))
            changeCategory(true);

        if (currentVariant == null)
            if (!this.tardis().getExterior().getCategory().equals(getCategory())) {
                setCurrentVariant(this.getCategory().getDefaultVariant());
            } else {
                setCurrentVariant(this.tardis().getExterior().getVariant());
            }

        return currentVariant;
    }

    public void setCurrentVariant(ExteriorVariantSchema var) {
        setCurrentVariant(ClientExteriorVariantRegistry.withParent(var));
    }

    public void setCurrentVariant(ClientExteriorVariantSchema currentVariant) {
        this.currentVariant = currentVariant;
    }

    private void createButtons() {
        this.buttons.clear();
        // exterior change text button
        MutableText applyText = Text.translatable("screen.ait.monitor.apply");

        // apply text (exterior change screen)
        this.addDrawable(new TextWidget((width / 2 + 44), (height / 2 + 3),
                APPLY_BUTTON_WIDTH, APPLY_BUTTON_HEIGHT, applyText.formatted(Formatting.BOLD), this.textRenderer));

        // apply button (exterior change screen)
        this.addButton(new PressableTextWidget((width / 2 + 44), (height / 2 + 3),
                APPLY_BUTTON_WIDTH, APPLY_BUTTON_HEIGHT, Text.empty(), button -> {
                    sendExteriorPacket(this.tardis(), this.getCategory(), this.getCurrentVariant());
                }, this.textRenderer));

        // arrow buttons (exterior change screen)
        this.addButton(new PressableTextWidget((width / 2 + 23), (height / 2 + 3),
                BIG_ARROW_BUTTON_WIDTH, BIG_ARROW_BUTTON_HEIGHT, Text.empty(), button -> {
                    changeCategory(false);
                }, this.textRenderer));
        this.addButton(new PressableTextWidget((width / 2 + 98), (height / 2 + 3),
                BIG_ARROW_BUTTON_WIDTH, BIG_ARROW_BUTTON_HEIGHT, Text.empty(), button -> {
                    changeCategory(true);
                }, this.textRenderer));

        // arrow buttons (exterior variant screen)
        this.addButton(new PressableTextWidget((width / 2 + 23), (height / 2 + 61),
                SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT, Text.empty(), button -> {
                    whichDirectionVariant(false);
                }, this.textRenderer));
        this.addButton(new PressableTextWidget((width / 2 + 98), (height / 2 + 61),
                SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT, Text.empty(), button -> {
                    whichDirectionVariant(true);
                }, this.textRenderer));

        // interior settings button
        this.addButton(new PressableTextWidget((width / 2 - 13), (height / 2 + 52),
                INTERIOR_SETTINGS_BUTTON_WIDTH, INTERIOR_SETTINGS_BUTTON_HEIGHT,
                Text.empty(),
                button -> toInteriorSettingsScreen(), this.textRenderer));

        this.buttons.forEach(buttons -> {
            // buttons.visible = false;
            buttons.active = true;
        });
    }

    public static void sendExteriorPacket(ClientTardis tardis, ExteriorCategorySchema category,
            ClientExteriorVariantSchema variant) {
        if (category != tardis.getExterior().getCategory() || variant.parent() != tardis.getExterior().getVariant()) {
            ClientTardisUtil.changeExteriorWithScreen(tardis, variant.id(),
                    variant.parent() != tardis.getExterior().getVariant());
        }
    }

    public void toInteriorSettingsScreen() {
        if (tardis() == null || tardis().isGrowth())
            return;

        MinecraftClient.getInstance().setScreenAndRender(new InteriorSettingsScreen(this.tardis(), this.console, this));
    }

    public void changeCategory(boolean direction) {
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (player == null)
            return;

        if (direction)
            setCategory(nextCategory());
        else
            setCategory(previousCategory());

        if (CategoryRegistry.CORAL_GROWTH.equals(this.category)
                || (!("ad504e7c-22a0-4b3f-94e3-5b6ad5514cb6".equalsIgnoreCase(player.getUuidAsString()))
                        && CategoryRegistry.DOOM.equals(this.category))) {
            changeCategory(direction);
        }
    }

    public ExteriorCategorySchema nextCategory() {
        List<ExteriorCategorySchema> list = CategoryRegistry.getInstance().toList();

        int idx = list.indexOf(getCategory());
        idx = (idx + 1) % list.size();
        return list.get(idx);
    }

    public ExteriorCategorySchema previousCategory() {
        List<ExteriorCategorySchema> list = CategoryRegistry.getInstance().toList();

        int idx = list.indexOf(getCategory());
        idx = (idx - 1 + list.size()) % list.size();
        return list.get(idx);
    }

    public void whichDirectionVariant(boolean direction) {
        if (direction)
            setCurrentVariant(nextVariant());
        else
            setCurrentVariant(previousVariant());
    }

    public ExteriorVariantSchema nextVariant() {
        List<ExteriorVariantSchema> list = ExteriorVariantRegistry.withParent(getCurrentVariant().parent().category())
                .stream().toList();

        int idx = list.indexOf(getCurrentVariant().parent());
        idx = (idx + 1) % list.size();
        return list.get(idx);
    }

    public ExteriorVariantSchema previousVariant() {
        List<ExteriorVariantSchema> list = ExteriorVariantRegistry.withParent(getCurrentVariant().parent().category())
                .stream().toList();

        int idx = list.indexOf(getCurrentVariant().parent());
        idx = (idx - 1 + list.size()) % list.size();
        return list.get(idx);
    }

    final int UV_BASE = 160;
    final int UV_INCREMENT = 19;

    int calculateUvOffsetForRange(int progress) {
        int rangeProgress = progress % 19;
        return (rangeProgress / 5) * UV_INCREMENT;
    }

    protected void drawBackground(DrawContext context) {
        // just this whole thing is for the flight
        if (this.tardis() == null)
            return;

        int i = (this.width - this.backgroundWidth) / 2;
        int j = ((this.height) - this.backgroundHeight) / 2;
        context.drawTexture(TEXTURE, i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);

        // apply button (exterior change screen)
        if (!this.buttons.get(0).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(0).getX(), this.buttons.get(0).getY(), 40, 166,
                    APPLY_BUTTON_WIDTH,APPLY_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(0).getX(), this.buttons.get(0).getY(), 40, 186,
                    APPLY_BUTTON_WIDTH, APPLY_BUTTON_HEIGHT);

        // arrow buttons (exterior change screen)
        if (!this.buttons.get(1).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(1).getX(), this.buttons.get(1).getY(), 0, 166,
                    BIG_ARROW_BUTTON_WIDTH, BIG_ARROW_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(1).getX(), this.buttons.get(1).getY(), 0, 186,
                    BIG_ARROW_BUTTON_WIDTH, BIG_ARROW_BUTTON_HEIGHT);
        if (!this.buttons.get(2).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(2).getX(), this.buttons.get(2).getY(), 20, 166,
                    BIG_ARROW_BUTTON_WIDTH, BIG_ARROW_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(2).getX(), this.buttons.get(2).getY(), 20, 186,
                    BIG_ARROW_BUTTON_WIDTH, BIG_ARROW_BUTTON_HEIGHT);

        // arrow buttons (exterior variant screen)
        if (!this.buttons.get(3).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(3).getX(), this.buttons.get(3).getY(), 93, 166,
                    SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(3).getX(), this.buttons.get(3).getY(), 93, 178,
                    SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);
        if (!this.buttons.get(4).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(4).getX(), this.buttons.get(4).getY(), 113, 166,
                    SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(4).getX(), this.buttons.get(4).getY(), 113, 178,
                    SMALL_ARROW_BUTTON_WIDTH, SMALL_ARROW_BUTTON_HEIGHT);

        // interior settings button
        if (!this.buttons.get(5).isHovered())
            context.drawTexture(TEXTURE, this.buttons.get(5).getX(), this.buttons.get(5).getY(), 186, 166,
                    INTERIOR_SETTINGS_BUTTON_WIDTH, INTERIOR_SETTINGS_BUTTON_HEIGHT);
        else
            context.drawTexture(TEXTURE, this.buttons.get(5).getX(), this.buttons.get(5).getY(), 186, 186,
                    INTERIOR_SETTINGS_BUTTON_WIDTH, INTERIOR_SETTINGS_BUTTON_HEIGHT);

        context.drawTexture(TEXTURE, i + 16, j + 144, 0,
                this.tardis().getFuel() > (FuelHandler.TARDIS_MAX_FUEL / 4) ? 225 : 234,
                (int) (85 * this.tardis().getFuel() / FuelHandler.TARDIS_MAX_FUEL), 9);

        int progress = this.tardis().travel().getDurationAsPercentage();

        for (int index = 0; index < 5; index++) {
            int rangeStart = index * 19;
            int rangeEnd = (index + 1) * 19;

            int uvOffset;
            if (progress >= rangeStart && progress <= rangeEnd) {
                uvOffset = calculateUvOffsetForRange(progress);
            } else if (progress >= rangeEnd) {
                uvOffset = 76;
            } else {
                uvOffset = UV_BASE;
            }

            context.drawTexture(TEXTURE, i + 11 + (index * 19), j + 113,
                    this.tardis().travel().getState() == TravelHandlerBase.State.FLIGHT
                            ? progress >= 100 ? 76 : uvOffset
                            : UV_BASE,
                    206, 19, 19);
        }
    }

    protected void drawTardisExterior(DrawContext context, int x, int y, float scale) {
        this.tickForSpin++;
        Tardis tardis = this.tardis();

        if (tardis == null)
            return;

        MatrixStack stack = context.getMatrices();

        int centerWidth = width / 2;
        int centerHeight = height / 2;

        ExteriorCategorySchema category = this.getCategory();
        ClientExteriorVariantSchema variant = this.getCurrentVariant();

        if (category == null || variant == null)
            return;

        boolean isPoliceBox = category.equals(CategoryRegistry.getInstance().get(PoliceBoxCategory.REFERENCE))
                || category.equals(CategoryRegistry.getInstance().get(ClassicCategory.REFERENCE));

        boolean isExtUnlocked = tardis.isUnlocked(variant.parent());
        boolean hasPower = tardis.fuel().hasPower();
        boolean alarms = tardis.alarm().enabled().get();

        stack.push();
        stack.translate(0, 0, 500f);

        context.drawCenteredTextWithShadow(this.textRenderer, category.text(), (centerWidth + 70), (centerHeight - 68),
                5636095);

        List<ExteriorVariantSchema> list = ExteriorVariantRegistry.withParent(category);

        context.drawCenteredTextWithShadow(this.textRenderer, Text.literal((list.indexOf(variant.parent()) + 1) + "/" + list.size()).formatted(Formatting.BOLD),
                (centerWidth + 70), (centerHeight + 64), 0xffffff);

        context.drawCenteredTextWithShadow(this.textRenderer, variant.parent().text(), (centerWidth + 70),
                (centerHeight + 44), 5636095);

        context.drawCenteredTextWithShadow(this.textRenderer, variant.parent().id().getNamespace().toUpperCase(),
                (centerWidth + 70), (centerHeight + 34), 5636095);

        stack.pop();
        ExteriorModel model = variant.model();

        /*
         * stack.push(); stack.translate(0, 0, -50f);
         * stack.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(((float) tickForSpin /
         * 1400L) * 360.0f), x, y, 0); context.drawTexture(TEXTURE, x - 41, y - 41, 173,
         * 173, 83, 83); stack.pop();
         */

        stack.push();
        stack.translate(x, isPoliceBox ? y + 11 : y, 100f);

        if (isPoliceBox) {
            stack.scale(-12, 12, 12);
        } else {
            stack.scale(-scale, scale, scale);
        }

        stack.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(((float) tickForSpin / 1200L) * 360.0f));

        Identifier texture = variant.texture();
        Identifier emissive = variant.emission();

        float base = isExtUnlocked ? 1f : 0.1f;
        float tinted = alarms && isExtUnlocked ? 0.3f : base;

        model.render(stack, context.getVertexConsumers().getBuffer(AITRenderLayers.getEntityTranslucentCull(texture)),
                LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, base, base, base, 1f);

        if (hasPower && emissive != null && !(emissive.equals(DatapackConsole.EMPTY))) {
            ClientLightUtil.renderEmissive((v, l) -> model.render(
                    stack, v, l, OverlayTexture.DEFAULT_UV, base, tinted, tinted, 1
            ), emissive, context.getVertexConsumers());
        }

        stack.pop();

        stack.push();
        stack.translate(0, 0, 550f);

        context.drawCenteredTextWithShadow(this.textRenderer, isExtUnlocked ? "" : "\uD83D\uDD12", x, y,
                0xFFFFFF);

        //float h = (float) (-textRenderer.getWidth("\uD83D\uDD12") / 2);

        //Matrix4f matrix4f = stack.peek().getPositionMatrix();
        VertexConsumerProvider vertex = context.getVertexConsumers();

        //textRenderer.draw(Text.literal("\uD83D\uDD12"), h + 0.35f, 0.0F, 0xFFFFFFFF, false, matrix4f, vertex,
                //TextRenderer.TextLayerType.SEE_THROUGH, 0x000000, 0xf000f0);

        stack.push();
        stack.translate(0, 0, 50f);

        context.drawCenteredTextWithShadow(this.textRenderer, isExtUnlocked ? "" : "\uD83D\uDD12", x, y, 0xFFFFFF);

        stack.pop();

        stack.pop();
    }

    @Override
    public void renderBackground(DrawContext context) {
        super.renderBackground(context);
    }

    protected void drawInformationText(DrawContext context) {
        if (this.tardis() == null)
            return;

        TravelHandler travel = this.tardis().travel();
        DirectedGlobalPos abpd = travel.getState() == TravelHandlerBase.State.FLIGHT
                ? travel.getProgress()
                : travel.position();
        CachedDirectedGlobalPos dabpd = travel.destination();

        if (abpd.getDimension() == null)
            return;

        BlockPos abpdPos = abpd.getPos();

        String positionText = abpdPos.getX() + ", " + abpdPos.getY() + ", " + abpdPos.getZ();
        Text dimensionText = WorldUtil.worldText(abpd.getDimension());

        BlockPos dabpdPos = dabpd.getPos();

        String destinationText = dabpdPos.getX() + ", " + dabpdPos.getY() + ", " + dabpdPos.getZ();
        Text dDimensionText = WorldUtil.worldText(dabpd.getDimension(), false);

        // position
        context.drawText(this.textRenderer, Text.literal(positionText), (width / 2 - 119), (height / 2 - 48), 0xFFFFFF,
                true);
        context.drawText(this.textRenderer, dimensionText, (width / 2 - 119), (height / 2 - 38), 0xFFFFFF, true);
        context.drawText(this.textRenderer, WorldUtil.rot2Text(abpd.getRotation()).asOrderedText(), (width / 2 - 119), (height / 2 - 28), 0xFFFFFF,
                true);

        // destination
        context.drawText(this.textRenderer, Text.literal(destinationText), (width / 2 - 119), (height / 2 - 10),
                0xFFFFFF, true);
        context.drawText(this.textRenderer, dDimensionText, (width / 2 - 119), (height / 2), 0xFFFFFF, true);
        context.drawText(this.textRenderer, WorldUtil.rot2Text(dabpd.getRotation()).asOrderedText(), (width / 2 - 119), (height / 2 + 10),
                0xFFFFFF, true);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int i = ((this.height - this.backgroundHeight) / 2); // loqor make sure to use these so it stays consistent on
                                                                // different sized
        // screens
        // (kind of ??)
        int j = ((this.width - this.backgroundWidth) / 2);
        // background behind the tardis and gallifreyan text
        MatrixStack stack = context.getMatrices();
        this.drawTardisExterior(context, (width / 2 + 70), (height / 2 - 30), 19f);
        this.drawBackground(context);
        // todo manually adjusting all these values are annoying me
        this.drawInformationText(context);
        super.render(context, mouseX, mouseY, delta);
    }
}
