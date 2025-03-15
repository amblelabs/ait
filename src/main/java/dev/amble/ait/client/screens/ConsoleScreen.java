package dev.amble.ait.client.screens;

import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.client.tardis.ClientTardis;

public abstract class ConsoleScreen extends TardisScreen {

    protected final BlockPos console;

    protected ConsoleScreen(Text title, ClientTardis tardis, BlockPos console) {
        super(title, tardis);

        this.console = console;
    }

    public BlockPos getConsole() {
        return console;
    }
}
