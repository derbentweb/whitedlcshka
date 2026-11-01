package ru.white.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import ru.white.module.Module;
import ru.white.module.ModuleManager;
import ru.white.module.setting.BooleanSetting;
import ru.white.module.setting.ModeSetting;
import ru.white.module.setting.NumberSetting;
import ru.white.module.setting.Setting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final ModuleManager modules;
    private final Path path;

    public ConfigManager(ModuleManager modules) {
        this.modules = modules;
        this.path = FabricLoader.getInstance().getConfigDir().resolve("white.json");
    }

    public void load() {
        if (!Files.exists(path)) return;
        try {
            String json = Files.readString(path);
            JsonObject root = GSON.fromJson(json, JsonObject.class);
            if (root == null || !root.has("modules")) return;
            JsonObject mods = root.getAsJsonObject("modules");
            for (Module m : modules.getModules()) {
                if (!mods.has(m.getName())) continue;
                JsonObject mo = mods.getAsJsonObject(m.getName());
                if (mo.has("settings") && mo.get("settings").isJsonObject()) {
                    JsonObject so = mo.getAsJsonObject("settings");
                    for (Setting<?> s : m.getSettings()) {
                        if (!so.has(s.getName())) continue;
                        apply(s, so.get(s.getName()));
                    }
                }
                // enabled применяем после настроек, чтобы onEnable видел итоговые значения
                if (mo.has("enabled")) m.setEnabled(mo.get("enabled").getAsBoolean());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void apply(Setting<?> s, JsonElement el) {
        try {
            if (s instanceof BooleanSetting b) {
                b.setValue(el.getAsBoolean());
            } else if (s instanceof NumberSetting n) {
                n.setValue(Math.max(n.getMin(), Math.min(n.getMax(), el.getAsDouble())));
            } else if (s instanceof ModeSetting m) {
                String v = el.getAsString();
                if (Arrays.asList(m.getModes()).contains(v)) m.setValue(v);
            }
        } catch (Exception ignored) { }
    }

    public void save() {
        try {
            JsonObject root = new JsonObject();
            JsonObject mods = new JsonObject();
            for (Module m : modules.getModules()) {
                JsonObject mo = new JsonObject();
                mo.addProperty("enabled", m.isEnabled());
                JsonObject so = new JsonObject();
                for (Setting<?> s : m.getSettings()) {
                    if (s instanceof BooleanSetting b) so.addProperty(s.getName(), b.getValue());
                    else if (s instanceof NumberSetting n) so.addProperty(s.getName(), n.getValue());
                    else if (s instanceof ModeSetting ms) so.addProperty(s.getName(), ms.getValue());
                }
                mo.add("settings", so);
                mods.add(m.getName(), mo);
            }
            root.add("modules", mods);
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        for (Module m : modules.getModules()) {
            m.setEnabled(false);
            for (Setting<?> s : m.getSettings()) s.reset();
        }
    }
}
