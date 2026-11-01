package ru.white.keybind;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Files;
import java.nio.file.Path;

public class KeybindManager {
    public static final int DEFAULT_OPEN_KEY = GLFW.GLFW_KEY_SEMICOLON;

    private int openKey = DEFAULT_OPEN_KEY;
    private boolean lastPressed = false;
    private final Path path;

    public KeybindManager() {
        this.path = FabricLoader.getInstance().getConfigDir().resolve("white-key.json");
        load();
    }

    public int getOpenKey() { return openKey; }

    public void setOpenKey(int k) {
        this.openKey = k;
        save();
    }

    public void load() {
        try {
            if (!Files.exists(path)) return;
            JsonObject o = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            if (o.has("open")) openKey = o.get("open").getAsInt();
        } catch (Exception ignored) { }
    }

    public void save() {
        try {
            JsonObject o = new JsonObject();
            o.addProperty("open", openKey);
            Files.createDirectories(path.getParent());
            Files.writeString(path, o.toString());
        } catch (Exception ignored) { }
    }

    public boolean pollEdge(boolean currentlyPressed) {
        boolean edge = currentlyPressed && !lastPressed;
        lastPressed = currentlyPressed;
        return edge;
    }
}
