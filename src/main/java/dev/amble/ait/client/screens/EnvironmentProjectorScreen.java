package dev.amble.ait.client.screens;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.blocks.EnvironmentProjectorBlock;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.util.WorldUtil;
import dev.amble.ait.core.world.TardisServerWorld;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

import java.util.Iterator;

import static dev.amble.ait.core.blocks.EnvironmentProjectorBlock.ENABLED;

public class EnvironmentProjectorScreen extends Screen {
    public EnvironmentProjectorScreen(Tardis tardis, BlockState state, PlayerEntity player) {
        // The parameter is the title of the screen,
        // which will be narrated when you enter the screen.
        super(Text.literal("Testing Screen"));
    }

    public ButtonWidget button1;
    public ButtonWidget button2;

    Tardis tardis;
    BlockState state;
    PlayerEntity player;

    public void init() {
        state = state.cycle(ENABLED);
        BlockState finalState = state;
        button1 = ButtonWidget.builder(Text.literal("Change Skybox"), button -> {
                    System.out.println("Skybox Changed!");
                    this.switchSkybox(tardis, finalState, player);
                })
                .dimensions(width / 2 - 205, 20, 200, 20)
                .build();
        button2 = ButtonWidget.builder(Text.literal("Button 2"), button -> {
                    System.out.println("You clicked button2!");
                })
                .dimensions(width / 2 + 5, 20, 200, 20)
                .build();

        addDrawableChild(button1);
        addDrawableChild(button2);
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



}
