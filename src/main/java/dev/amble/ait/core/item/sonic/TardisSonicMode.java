package dev.amble.ait.core.item.sonic;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import dev.amble.ait.data.schema.sonic.SonicSchema;

public class TardisSonicMode extends SonicMode {

    protected TardisSonicMode(int index) {
        super(index);
    }

    @Override
    public Text text() {
        return Text.translatable("sonic.ait.mode.tardis").formatted(Formatting.BLUE, Formatting.BOLD);
    }

    @Override
    public int maxTime() {
        return 2 * 20;
    }

    @Override
    public Identifier model(SonicSchema.Models models) {
        return models.tardis();
    }
}
