package dev.amble.ait.client.config;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import dev.amble.ait.config.AITServerConfig;

public class AITConfigScreen {

    public static Screen create(Screen parent) {
        return YetAnotherConfigLib.create(AITServerConfig.INSTANCE, (defaults, config, builder) -> {
            builder.title(Text.translatable("text.ait.config.title"));

            // Add all client categories
            AITClientConfig.INSTANCE.generateGui()
                    .categories()
                    .forEach(builder::category);

            // Add all server categories
            AITServerConfig.INSTANCE.generateGui()
                    .categories()
                    .forEach(builder::category);

            return builder;
        }).generateScreen(parent);
    }
}
