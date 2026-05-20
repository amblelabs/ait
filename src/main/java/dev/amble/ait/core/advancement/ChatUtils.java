package dev.amble.ait.core.advancement;

import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.server.network.ServerPlayerEntity;

public class ChatUtils {
    public static void sendLinkMessage(ServerPlayerEntity player) {
        MutableText linkText = Text.translatable("ait.text.chat.clicked")
                .formatted(Formatting.BLUE, Formatting.UNDERLINE)
                .styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://amblelabs.dev/wiki"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("ait.text.chat.hover")))
                        .withColor(Formatting.BLUE)
                        .withUnderline(true)
                );
        MutableText fullmessage = Text.translatable("ait.text.chat.readwiki", linkText);

        fullmessage.append(linkText);
        player.sendMessage(fullmessage, false);
    }
}
