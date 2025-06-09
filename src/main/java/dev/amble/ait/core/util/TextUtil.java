package dev.amble.ait.core.util;

import dev.amble.ait.core.tardis.Tardis;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

import java.util.UUID;

public class TextUtil {

    public static Text forTardis(Tardis tardis) {
        return forTardis(tardis.getUuid());
    }
    public static Text forTardis(UUID tardis) {
        String id = tardis.toString();

        return Texts.bracketed(Text.literal(id.substring(0, 7))).styled(style -> style.withColor(Formatting.GREEN)
                .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, id))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("message.ait.click_to_copy"))));
    }
}
