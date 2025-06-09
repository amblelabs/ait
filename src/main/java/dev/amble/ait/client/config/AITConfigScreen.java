package dev.amble.ait.client.config;

import dev.amble.ait.config.AITServerConfig;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class AITConfigScreen {

    public static Screen create(Screen parent) {
        return YetAnotherConfigLib.create(
                AITServerConfig.INSTANCE, (defaults, config, builder) -> builder
                        .title(Text.translatable("text.ait.config.title"))
                        .category(ConfigCategory.createBuilder()
                                .name(Text.translatable("text.ait.config.categories"))
                                .option(ButtonOption.createBuilder()
                                        .name(Text.translatable("category.ait.config.client"))
                                        .action((yaclScreen, buttonOption) -> MinecraftClient.getInstance().setScreen(
                                                AITClientConfig.INSTANCE.generateGui().generateScreen(yaclScreen))
                                        ).build())
                                .option(ButtonOption.createBuilder()
                                        .name(Text.translatable("category.ait.config.server"))
                                        .action((yaclScreen, buttonOption) -> MinecraftClient.getInstance().setScreen(
                                                AITServerConfig.INSTANCE.generateGui().generateScreen(yaclScreen))
                                        ).build())
                                .build())
        ).generateScreen(parent);
    }
}
