package ru.white.module.setting;

public abstract class Setting<T> {
    protected final String name;
    protected final T defaultValue;
    protected T value;

    protected Setting(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getName() { return name; }
    public T getValue() { return value; }
    public void setValue(T v) { this.value = v; }
    public void reset() { this.value = defaultValue; }

    public abstract String displayValue();
}
