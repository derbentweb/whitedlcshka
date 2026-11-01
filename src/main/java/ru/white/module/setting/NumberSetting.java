package ru.white.module.setting;

public class NumberSetting extends Setting<Double> {
    private final double min, max, step;

    public NumberSetting(String name, double defaultValue, double min, double max, double step) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
    }

    public double getMin() { return min; }
    public double getMax() { return max; }
    public double getStep() { return step; }
    public float getFloat() { return value.floatValue(); }
    public int getInt() { return (int) Math.round(value); }

    public double fraction() {
        if (max == min) return 0;
        return (value - min) / (max - min);
    }

    public void setFromFraction(double frac) {
        frac = Math.max(0.0, Math.min(1.0, frac));
        double v = min + (max - min) * frac;
        v = Math.round(v / step) * step;
        value = Math.max(min, Math.min(max, v));
    }

    @Override
    public String displayValue() {
        return String.format(java.util.Locale.ROOT, "%.2f", value);
    }
}
