package dev.amble.ait.client.util;

import java.util.Locale;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import dev.amble.ait.core.tardis.Tardis;
import dev.amble.ait.data.Loyalty;
import dev.amble.lib.register.unlockable.Unlockable;

@Environment(EnvType.CLIENT)
public final class LockedDisplay {

    public static final String LOCK = "🔒";

    private LockedDisplay() {
    }

    public static boolean isLocked(Tardis tardis, Object value) {
        return value instanceof Unlockable unlockable && !tardis.isUnlocked(unlockable);
    }

    public static Text requirement(Unlockable unlockable) {
        Loyalty.Type type = unlockable.requirement().map(Loyalty::type).orElse(Loyalty.Type.NEUTRAL);
        Text loyalty = Text.translatable("screen.ait.loyalty." + type.name().toLowerCase(Locale.ROOT));

        return Text.translatable("screen.ait.locked.requires", LOCK, loyalty).formatted(Formatting.RED);
    }

    public static void drawRequirement(DrawContext context, TextRenderer textRenderer, Unlockable unlockable, int x, int y) {
        context.drawCenteredTextWithShadow(textRenderer, requirement(unlockable), x, y, 0xFFFFFF);
    }
}
