package dev.amble.ait.client.screens;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.link.v2.TardisRef;
import dev.amble.ait.api.tardis.link.v2.block.AbstractLinkableBlockEntity;
import dev.amble.ait.core.blocks.EnvironmentProjectorBlock;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.PressableTextWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Iterator;

public class EnvironmentProjectorScreen extends Screen {
    private static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID,
            "textures/gui/tardis/monitor/security_menu.png");
    int bgHeight = 138;
    int bgWidth = 216;
    int left, top;

    public EnvironmentProjectorScreen() {
        super(Text.of("screen." + AITMod.MOD_ID + ".environment_projector"));

        this.client = MinecraftClient.getInstance();
    }

    public void switchSkybox(Tardis tardis, BlockState state, PlayerEntity player) {
        ServerWorld next = findNext(this.current);

        while (TardisServerWorld.isTardisDimension(next)) {
            next = findNext(next.getRegistryKey());
        }

        player.sendMessage(Text.translatable("message.ait.projector.skybox", next.getRegistryKey().getValue().toString()));
        AITMod.LOGGER.debug("Last: {}, next: {}", this.current, next);

        this.current = next.getRegistryKey();

        if (state.get(EnvironmentProjectorBlock.ENABLED))
            this.apply(tardis, state);
    }


    private static ServerWorld findNext(RegistryKey<World> last) {
        Iterator<ServerWorld> iter = WorldUtil.getProjectorWorlds().iterator();

        ServerWorld first = iter.next();
        ServerWorld found = first;

        while (iter.hasNext()) {
            if (same(found.getRegistryKey(), last)) {
                if (!iter.hasNext())
                    break;

                return iter.next();
            }

            found = iter.next();
        }

        return first;
    }

    private static boolean same(RegistryKey<World> a, RegistryKey<World> b) {
        return a == b || a.getValue().equals(b.getValue());
    }

    public void apply(Tardis tardis, BlockState state) {
        tardis.stats().skybox().set(this.current);
        tardis.stats().skyboxDirection().set(state.get(EnvironmentProjectorBlock.FACING));
    }

    private static final RegistryKey<World> DEFAULT = World.END;
    private RegistryKey<World> current = DEFAULT;

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    protected void init() {
        this.top = (this.height - this.bgHeight) / 2; // this means everythings centered and scaling, same for below
        this.left = (this.width - this.bgWidth) / 2;

        super.init();

        Tardis tardis = this.tardis().get();

        this.addDrawableChild(new PressableTextWidget((width / 2 - this.textRenderer.getWidth(Text.literal("SWITCH")) / 2), (height / 2 + 12),
                this.textRenderer.getWidth(Text.literal("SWITCH")), 10, Text.literal("SWITCH"), button -> AITMod.LOGGER.debug("button"), this.textRenderer));
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
        this.drawBackground(context);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawBackground(DrawContext context) {
        context.drawTexture(TEXTURE, left, top, 0, 0, bgWidth, bgHeight);
    }

    TardisRef ref;

    public TardisRef tardis() {
        return ref;
    }

}
