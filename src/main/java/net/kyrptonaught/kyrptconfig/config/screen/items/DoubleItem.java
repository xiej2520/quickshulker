package net.kyrptonaught.kyrptconfig.config.screen.items;

import net.minecraft.text.Text;


public class DoubleItem extends NumberItem<Double> {
    public DoubleItem(Text name, Double value, Double defaultValue) {
        super(name, value, defaultValue);
        setMinMax(Double.MIN_VALUE, Double.MAX_VALUE);
    }

    @Override
    public void onTyped(String s) {
        System.out.println(s);
        if (s.isBlank() || s.equals("-") || s.charAt(s.length() - 1) == '.') return;
        String fixed = fixInput(s);
        System.out.println(s + " " + fixed);
        if (s.equals(fixed))
            value = Double.parseDouble(s);
        else
            valueEntry.setText(fixed);
    }

    public String fixInput(String input) {
        try {
            return fixInput(Double.parseDouble(input)).toString();
        } catch (NumberFormatException ignored) {
        }
        return value.toString();
    }
}
