package net.kyrptonaught.kyrptconfig.config.screen.items;

import net.minecraft.network.chat.Component;


public class IntegerItem extends NumberItem<Integer> {
    public IntegerItem(Component name, int value, int defaultValue) {
        super(name, value, defaultValue);
        setMinMax(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public void onTyped(String s) {
        if (s.trim().isEmpty() || s.equals("-")) return;
        String fixed = fixInput(s);
        if (s.equals(fixed))
            value = Integer.parseInt(fixed);
        else
            valueEntry.setValue(fixed);
    }

    public String fixInput(String input) {
        try {
            return fixInput(Integer.parseInt(input)).toString();
        } catch (NumberFormatException ignored) {
        }

        return value.toString();
    }
}
