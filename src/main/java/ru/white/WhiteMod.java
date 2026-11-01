package ru.white;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;
import ru.white.config.ConfigManager;
import ru.white.gui.ClickGui;
import ru.white.keybind.KeybindManager;
import ru.white.manager.rotation.RotationManager;
import ru.white.manager.rotation.RotationRenderer;
import ru.white.module.Module;
import ru.white.module.ModuleManager;

public class WhiteMod implements ClientModInitializer {

    public static ModuleManager MODULES;
    public static ConfigManager CONFIG;
    public static KeybindManager KEYBINDS;

    @Override
    public void onInitializeClient() {
        // Регистрирует собственный тик менеджера ротации раньше тика модулей
        RotationManager.get().init();
        // Покадровая интерполяция камеры для ApplyMode.CAMERA (не зависит от тик-рейта)
        RotationRenderer.register();

        MODULES = new ModuleManager();
        MODULES.init();

        CONFIG = new ConfigManager(MODULES);
        CONFIG.load();

        KEYBINDS = new KeybindManager();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (Module m : MODULES.getModules()) m.onTick();

            if (client.method_22683() == null) return;
            long handle = client.method_22683().method_4490();
            boolean pressed = GLFW.glfwGetKey(handle, KEYBINDS.getOpenKey()) == GLFW.GLFW_PRESS;
            if (KEYBINDS.pollEdge(pressed) && client.field_1755 == null) {
                client.method_1507(new ClickGui());
            }
        });
    }
}
