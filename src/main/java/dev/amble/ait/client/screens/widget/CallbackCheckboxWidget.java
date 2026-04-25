package dev.amble.ait.client.screens.widget;

import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

/**
 * Variant of the Checkbox that allows for a callback when pressed
 */
public class CallbackCheckboxWidget extends CheckboxWidget {

    private final PressAction onPress;

    public CallbackCheckboxWidget(int x, int y, int width, int height, Text message, boolean checked, PressAction onPress) {
        super(x, y, width, height, message, checked);
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        super.onPress();
        this.onPress.onPress(this);
    }

    @FunctionalInterface
    public interface PressAction {
        void onPress(CallbackCheckboxWidget checkbox);
    }

}