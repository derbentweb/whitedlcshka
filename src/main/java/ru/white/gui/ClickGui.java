package ru.white.gui;

import org.lwjgl.glfw.GLFW;
import ru.white.WhiteMod;
import ru.white.module.Module;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_437;

public class ClickGui extends class_437 {

    private static final int PANEL_BG = 0xF017171F;
    private static final int HEADER_BG = 0xFF1F1F2A;
    private static final int BORDER = 0xFF2A2A38;
    private static final int ACCENT = 0xFF7C5CFF;
    private static final int TEXT = 0xFFEAEAF0;
    private static final int TEXT_DIM = 0xFF8C8CA0;
    private static final int FOOTER_H = 34;

    private final List<ModuleButton> buttons = new ArrayList<>();
    private int px, py, pw, ph;
    private float anim = 0f;
    private long lastMs = 0L;

    // скролл
    private float scroll = 0f;
    private float targetScroll = 0f;
    private int listTop, listBottom;

    private boolean capturingKey = false;
    private int keyButtonX, keyButtonY, keyButtonW, keyButtonH;
    private int resetButtonX, resetButtonY, resetButtonW, resetButtonH;
    private int saveButtonX;

    public ClickGui() {
        super(class_2561.method_43470("White"));
        for (Module m : WhiteMod.MODULES.getModules()) buttons.add(new ModuleButton(m));
    }

    @Override
    protected void method_25426() {
        pw = 340;
        ph = Math.max(120, Math.min(this.field_22790 - 60, 380));
        px = (this.field_22789 - pw) / 2;
        py = (this.field_22790 - ph) / 2;
    }

    @Override
    public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) {
        long now = System.currentTimeMillis();
        float dt = lastMs == 0L ? 0f : Math.min(0.1f, (now - lastMs) / 1000f);
        lastMs = now;
        anim = Math.min(1f, anim + dt * 4.5f);

        int bgAlpha = (int) (0x88 * anim);
        ctx.method_25294(0, 0, this.field_22789, this.field_22790, (bgAlpha << 24));

        int offsetY = (int) ((1f - anim) * 12f);
        int panelTop = py + offsetY;
        int panelBottom = panelTop + ph;

        ctx.method_25294(px - 3, panelTop - 3, px + pw + 3, panelBottom + 3, 0x66000000);
        ctx.method_25294(px, panelTop, px + pw, panelBottom, PANEL_BG);

        ctx.method_25294(px, panelTop, px + pw, panelTop + 1, BORDER);
        ctx.method_25294(px, panelBottom - 1, px + pw, panelBottom, BORDER);
        ctx.method_25294(px, panelTop, px + 1, panelBottom, BORDER);
        ctx.method_25294(px + pw - 1, panelTop, px + pw, panelBottom, BORDER);

        // --- шапка ---
        int headerH = 34;
        ctx.method_25294(px + 1, panelTop + 1, px + pw - 1, panelTop + headerH, HEADER_BG);
        ctx.method_25294(px + 1, panelTop + headerH, px + pw - 1, panelTop + headerH + 1, BORDER);
        ctx.method_51433(field_22793, "WHITE", px + 12, panelTop + 8, ACCENT, false);
        ctx.method_51433(field_22793, "client", px + 12 + field_22793.method_1727("WHITE") + 6, panelTop + 8, TEXT_DIM, false);

        int keyW = 92, keyH = 18;
        keyButtonW = keyW; keyButtonH = keyH;
        keyButtonX = px + pw - keyW - 10;
        keyButtonY = panelTop + (headerH - keyH) / 2;
        boolean keyHover = inside(mouseX, mouseY, keyButtonX, keyButtonY, keyW, keyH);
        ctx.method_25294(keyButtonX, keyButtonY, keyButtonX + keyW, keyButtonY + keyH,
                keyHover ? 0xFF2E2E3A : 0xFF23232E);
        String keyLabel = capturingKey ? "Нажми..." : ("Меню: " + keyName(WhiteMod.KEYBINDS.getOpenKey()));
        ctx.method_51433(field_22793, keyLabel, keyButtonX + 8, keyButtonY + 5, TEXT, false);

        // --- список модулей со скроллом ---
        listTop = panelTop + headerH + 8;
        listBottom = panelBottom - FOOTER_H;
        int listX = px + 8;
        int listW = pw - 16 - 6; // место под полосу прокрутки
        int viewH = Math.max(0, listBottom - listTop);

        int total = 0;
        int cy = listTop - Math.round(scroll);
        for (ModuleButton b : buttons) {
            int h = b.layout(listX, cy, listW);
            cy += h;
            total += h;
        }

        float maxScroll = Math.max(0, total - viewH);
        targetScroll = class_3532.method_15363(targetScroll, 0f, maxScroll);
        scroll += (targetScroll - scroll) * Math.min(1f, dt * 14f);
        scroll = class_3532.method_15363(scroll, 0f, maxScroll);

        ctx.method_44379(px + 1, listTop, px + pw - 1, listBottom);
        for (ModuleButton b : buttons) {
            b.render(ctx, class_310.method_1551(), mouseX, mouseY, anim);
        }
        ctx.method_44380();

        if (maxScroll > 0f && total > 0) {
            int trackX = px + pw - 7;
            ctx.method_25294(trackX, listTop, trackX + 3, listBottom, 0xFF1A1A24);
            int thumbH = Math.max(16, (int) (viewH * (viewH / (float) total)));
            int thumbY = listTop + (int) ((viewH - thumbH) * (scroll / maxScroll));
            ctx.method_25294(trackX, thumbY, trackX + 3, thumbY + thumbH, ACCENT);
        }

        // --- подвал ---
        resetButtonW = 80; resetButtonH = 18;
        resetButtonX = px + 10;
        resetButtonY = panelBottom - resetButtonH - 8;
        boolean resetHover = inside(mouseX, mouseY, resetButtonX, resetButtonY, resetButtonW, resetButtonH);
        ctx.method_25294(resetButtonX, resetButtonY, resetButtonX + resetButtonW, resetButtonY + resetButtonH,
                resetHover ? 0xFF45202A : 0xFF2A1A1F);
        ctx.method_51433(field_22793, "Сброс", resetButtonX + 22, resetButtonY + 5, 0xFFFF7A8A, false);

        saveButtonX = px + pw - 90;
        boolean saveHover = inside(mouseX, mouseY, saveButtonX, resetButtonY, resetButtonW, resetButtonH);
        ctx.method_25294(saveButtonX, resetButtonY, saveButtonX + resetButtonW, resetButtonY + resetButtonH,
                saveHover ? 0xFF23352A : 0xFF1A2A20);
        ctx.method_51433(field_22793, "Сохранить", saveButtonX + 12, resetButtonY + 5, 0xFF7AE8A0, false);
    }

    @Override
    public boolean method_25402(class_11909 click, boolean doubleClick) {
        double mouseX = click.comp_4798();
        double mouseY = click.comp_4799();
        int button = click.method_74245();
        if (button == 0) {
            int mx = (int) mouseX, my = (int) mouseY;
            if (inside(mx, my, keyButtonX, keyButtonY, keyButtonW, keyButtonH)) {
                capturingKey = true;
                return true;
            }
            if (inside(mx, my, resetButtonX, resetButtonY, resetButtonW, resetButtonH)) {
                WhiteMod.CONFIG.reset();
                WhiteMod.CONFIG.save();
                return true;
            }
            if (inside(mx, my, saveButtonX, resetButtonY, resetButtonW, resetButtonH)) {
                WhiteMod.CONFIG.save();
                return true;
            }
            // клики по модулям и настройкам только внутри видимой области списка
            if (inside(mx, my, px, listTop, pw, listBottom - listTop)) {
                for (ModuleButton b : buttons) {
                    if (b.mouseClicked(mouseX, mouseY, button)) return true;
                }
            }
        }
        return super.method_25402(click, doubleClick);
    }

    @Override
    public boolean method_25406(class_11909 click) {
        double mouseX = click.comp_4798();
        double mouseY = click.comp_4799();
        int button = click.method_74245();
        for (ModuleButton b : buttons) b.mouseReleased(mouseX, mouseY, button);
        return super.method_25406(click);
    }

    @Override
    public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        targetScroll -= (float) verticalAmount * 24f;
        return true;
    }

    @Override
    public boolean method_25404(class_11908 input) {
        int keyCode = input.comp_4795();
        int scanCode = input.comp_4796();
        int modifiers = input.comp_4797();
        if (capturingKey) {
            if (keyCode != GLFW.GLFW_KEY_ESCAPE) {
                WhiteMod.KEYBINDS.setOpenKey(keyCode);
            }
            capturingKey = false;
            return true;
        }
        if (keyCode == WhiteMod.KEYBINDS.getOpenKey() || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            method_25419();
            return true;
        }
        return super.method_25404(input);
    }

    @Override
    public void method_25432() {
        WhiteMod.CONFIG.save();
        super.method_25432();
    }

    @Override
    public boolean method_25421() {
        return false;
    }

    private static boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }

    private static String keyName(int key) {
        if (key == GLFW.GLFW_KEY_SEMICOLON) return ";";
        if (key == GLFW.GLFW_KEY_ESCAPE) return "ESC";
        String n = GLFW.glfwGetKeyName(key, 0);
        return n != null ? n.toUpperCase() : ("#" + key);
    }
}
