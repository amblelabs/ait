package dev.amble.ait.core.tardis.handler.mood.v2;

import dev.amble.ait.AITMod;
import dev.amble.ait.api.tardis.KeyedTardisComponent;
import dev.amble.ait.api.tardis.TardisTickable;
import dev.amble.ait.client.util.ClientTardisUtil;
import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.core.tardis.manager.ServerTardisManager;
import dev.amble.ait.core.world.TardisServerWorld;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.server.MinecraftServer;

public class MoodHandler2 extends KeyedTardisComponent implements TardisTickable {

    static {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            HudRenderCallback.EVENT.register((drawContext, v) -> {
                MinecraftClient client = MinecraftClient.getInstance();
                TextRenderer renderer = client.textRenderer;
                Tardis tardis = ClientTardisUtil.getCurrentTardis();

                if (tardis == null) return;

                tardis = ServerTardisManager.getInstance().demandTardis(null, tardis.getUuid());
                MoodHandler2 mood = tardis.handler(Id.MOOD);

                String text = mood.container.toString();

                String[] list = text.lines().toArray(String[]::new);

                for (int i = 1; i < list.length - 1; i++) {
                    String s = list[i].stripLeading();

                    drawContext.drawText(renderer, s,
                            renderer.getWidth(s),
                            i * 16,
                            0xFFFFFF, true
                    );
                }
            });
        }
    }

    public EmotionContainer container;
    private float costMultiplier = 1f;

    public MoodHandler2() {
        super(Id.MOOD);
    }

    @Override
    public void onCreate() {
        container = EmotionRegistry.createContainer();
        costMultiplier += 1f / (AITMod.RANDOM.nextInt(0, 10) + 5);
    }

    @Override
    public void onLoaded() {
        if (container == null)
            container = EmotionRegistry.createContainer();
    }

    public void add(Emotion.Type type, float amount) {
        container.add(type, amount);
    }

    public void runEvent() {
        float cost = AITMod.RANDOM.nextInt(16) * costMultiplier;

        MoodEventRegistry.MoodEvent event;

        while (true) {
            event = MoodEventRegistry.poll(tardis.asServer(), container, (int) cost);

            if (event == null)
                break;

            cost -= event.cost();
            event.execute(tardis.asServer());

            for (MoodEventRegistry.Requires requires : event.requirements()) {
                container.add(requires.type(), -requires.penalty());
            }
        }
    }

    @Override
    public void tick(MinecraftServer server) {
        if (server.getTicks() % 20 != 0 || !AITMod.CONFIG.moodEnabled)
            return;

        this.runEvent();

        if (server.getOverworld().random.nextBoolean())
            return;

        if (tardis.asServer().hasWorld()) {
            TardisServerWorld world = tardis.asServer().world();
            if (!world.getPlayers().isEmpty()) container.add(Emotion.Type.CONTENT, AITMod.CONFIG.passiveEmotionMultiplier);
            else container.add(Emotion.Type.LONELINESS, AITMod.CONFIG.passiveEmotionMultiplier);
        }
        else container.add(Emotion.Type.LONELINESS, AITMod.CONFIG.passiveEmotionMultiplier);
    }
}
