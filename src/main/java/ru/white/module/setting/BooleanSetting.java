package ru.white.module.setting;

public class BooleanSetting extends Setting<Boolean> {
    public BooleanSetting(String name, boolean defaultValue) {
        super(name, defaultValue);
    }

    public boolean isEnabled() { return value; }
    public void toggle() { value = !value; }

    @Override
    public String displayValue() { return value ? "Вкл" : "Выкл"; }
}
