package dev.amble.ait.client.screens.interior;

import dev.amble.ait.AITMod;
import dev.amble.ait.client.screens.TardisScreen;
import dev.amble.ait.client.screens.widget.SwitcherManager;
import dev.amble.ait.client.tardis.ClientTardis;
import dev.amble.ait.core.ars.ArsRegistry;
import dev.amble.ait.core.ars.ArsStructure;
import dev.amble.ait.core.blocks.ArsPlacerBlock;
import dev.amble.ait.data.schema.desktop.textures.StructurePreviewTexture;
import dev.amble.lib.api.Identifiable;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class ArsPlacerScreen extends TardisScreen {
	private static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
			"textures/gui/tardis/monitor/security_menu.png");
	int bgHeight = 138;
	int bgWidth = 216;
	int left, top;

	final BlockPos pos;
	final SwitcherManager.IdentifierSwitcher switcher;

	public ArsPlacerScreen(ClientTardis tardis, BlockPos placementPos) {
		super(Text.translatable("screen." + AITMod.MOD_ID + ".ars.title"), tardis);

		this.pos = placementPos;
		this.switcher = createSwitcher();
	}

	private SwitcherManager.IdentifierSwitcher createSwitcher() {
		return new SwitcherManager.IdentifierSwitcher(ArsRegistry.getInstance().toList().stream()
				.map(Identifiable::id)
				.toList(), this::chooseStructure);
	}

	private void chooseStructure(Identifier id) {
		PacketByteBuf buf = PacketByteBufs.create();

		buf.writeIdentifier(id);
		buf.writeBlockPos(this.pos);

		ClientPlayNetworking.send(ArsPlacerBlock.REQUEST_PLACE, buf);
		this.close();
	}

	@Override
	public boolean shouldPause() {
		return false;
	}

	@Override
	protected void init() {
		this.top = (this.height - this.bgHeight) / 2; // this means everythings centered and scaling, same for below
		this.left = (this.width - this.bgWidth) / 2;

		super.init();

		this.addDrawableChild(new PressableTextWidget((left + 10), top + 28,
				this.textRenderer.getWidth("<"), 10, Text.literal("<"), button -> this.switcher.previous(), this.textRenderer));
		this.addDrawableChild(new PressableTextWidget((left + 55), top + 28,
				this.textRenderer.getWidth(">"), 10, Text.literal(">"), button -> this.switcher.next(), this.textRenderer));
		this.addDrawableChild(new PressableTextWidget((left + 35 - this.textRenderer.getWidth(Text.literal("PLACE")) / 2), top + 28,
				this.textRenderer.getWidth(Text.literal("PLACE")), 10, Text.literal("PLACE"), button -> this.switcher.sync(null), this.textRenderer));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (MinecraftClient.getInstance().options.inventoryKey.matchesKey(keyCode, scanCode)) {
			this.close();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.drawBackground(context);
		super.render(context, mouseX, mouseY, delta);
		renderStructure(context);
	}

	private ArsStructure getSelectedStructure() {
		return ArsRegistry.getInstance().getOrFallback(this.switcher.get().id());
	}

	private void renderStructure(DrawContext context) {
		ArsStructure structure = getSelectedStructure();
		StructurePreviewTexture preview = structure.previewTexture();

		context.getMatrices().push();
		context.getMatrices().translate(0, 0, 15f);
		context.drawTextWithShadow(this.textRenderer, structure.name(),
				left + 10, top + 16, 0xffffff);
		context.getMatrices().pop();

		context.getMatrices().push();
		context.drawTexture(
				preview.textureOrFallback(),
				(width / 2), top + 16, 95, 95, 0, 0, preview.width * 2,
				preview.height * 2, preview.width * 2,
				preview.height * 2);

		context.getMatrices().pop();
	}

	private void drawBackground(DrawContext context) {
		context.drawTexture(TEXTURE, left, top, 0, 0, bgWidth, bgHeight);
	}
}
