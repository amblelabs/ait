package dev.amble.ait.core.item.sonic;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import dev.amble.ait.data.schema.sonic.SonicSchema;

public class InactiveSonicMode extends SonicMode {

    protected InactiveSonicMode() {
        super(-1);
    }

    @Override
    public Text text() {
        return Text.translatable("sonic.ait.mode.inactive").formatted(Formatting.GRAY, Formatting.BOLD);
    }

    @Override
    public int maxTime() {
        return 0;
    }

    @Override
    public Identifier model(SonicSchema.Models models) {
        return models.inactive();
    }

    @Override
    public SonicMode next() {
        return Modes.get(0);
    }

    @Override
    public SonicMode previous() {
        return Modes.get(Modes.VALUES.length - 1);
    }
}
