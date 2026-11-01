package ru.white.module;

import ru.white.module.setting.Setting;
import java.util.ArrayList;
import java.util.List;

public abstract class Module {
    private final String name;
    private final String description;
    private boolean enabled = false;
    protected final List<Setting<?>> settings = new ArrayList<>();

    protected Module(String name, String description) {
        this.name = name;
        this.description = description;
    }

    protected <T extends Setting<?>> T add(T setting) {
        settings.add(setting);
        return setting;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean v) {
        if (enabled == v) return;
        enabled = v;
        if (v) onEnable(); else onDisable();
    }

    public void toggle() { setEnabled(!enabled); }
    public List<Setting<?>> getSettings() { return settings; }

    /** Вызывается при включении (в том числе при загрузке конфига, когда player может быть null). */
    protected void onEnable() {}
    /** Вызывается при выключении. */
    protected void onDisable() {}

    public abstract void onTick();
}
