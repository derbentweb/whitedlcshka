package ru.white.gui;

import ru.white.module.Module;
import ru.white.module.setting.BooleanSetting;
import ru.white.module.setting.ModeSetting;
import ru.white.module.setting.NumberSetting;
import ru.white.module.setting.Setting;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_310;
import net.minecraft.class_332;

public class ModuleButton {

    public static final int ACCENT = 0xFF7C5CFF;
    public static final int TEXT = 0xFFEAEAF0;
    public static final int TEXT_DIM = 0xFF8C8CA0;
    public static final int ROW = 0xFF24242E;
    public static final int ROW_HOVER = 0xFF2E2E3A;
    public static final int SLIDER_BG = 0xFF1A1A24;
    public static final int OFF = 0xFF3A3A48;

    public final Module module;
    public boolean expanded = false;

    private int x, y, width;
    private int rowHeight = 22;
    private final List<SettingRow> rows = new ArrayList<>();
    private boolean draggingSlider = false;
    private SettingRow activeSlider = null;

    private static class SettingRow {
        Setting<?> setting;
        int y, height;
        SettingRow(Setting<?> s, int h) { this.setting = s; this.height = h; }
    }

    public ModuleButton(Module m) {
        this.module = m;
        for (Setting<?> s : m.getSettings()) {
            int h = (s instanceof NumberSetting) ? 26 : 22;
            rows.add(new SettingRow(s, h));
        }
    }

    public int layout(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
        int cy = y + rowHeight + 2;
        if (expanded) {
            for (SettingRow r : rows) {
                r.y = cy;
                cy += r.height;
            }
            cy += 2;
        }
        return cy - y;
    }

    public void render(class_332 ctx, class_310 mc, int mouseX, int mouseY, float anim) {
        var tr = mc.field_1772;

        boolean hover = inside(mouseX, mouseY, x, y, width, rowHeight);
        int bg = hover ? ROW_HOVER : ROW;
        ctx.method_25294(x, y, x + width, y + rowHeight, bg);

        if (module.isEnabled()) {
            ctx.method_25294(x, y, x + 2, y + rowHeight, ACCENT);
        }

        int nameColor = module.isEnabled() ? TEXT : TEXT_DIM;
        ctx.method_51433(tr, module.getName(), x + 8, y + 7, nameColor, false);

        String state = module.isEnabled() ? "ON" : "OFF";
        int stateColor = module.isEnabled() ? ACCENT : OFF;
        ctx.method_51433(tr, state, x + width - 12 - tr.method_1727(state), y + 7, stateColor, false);

        int arrowX = x + width - 10;
        String arrow = expanded ? "▲" : "▼";
        ctx.method_51433(tr, arrow, arrowX, y + 7, TEXT_DIM, false);

        if (expanded) {
            for (SettingRow r : rows) {
                renderRow(ctx, mc, r, mouseX, mouseY);
            }
        }
    }

    private void renderRow(class_332 ctx, class_310 mc, SettingRow r, int mouseX, int mouseY) {
        var tr = mc.field_1772;
        int rx = x + 8;
        int rw = width - 16;
        Setting<?> s = r.setting;

        if (s instanceof BooleanSetting bs) {
            boolean hover = inside(mouseX, mouseY, rx, r.y, rw, r.height - 2);
            ctx.method_25294(rx, r.y, rx + rw, r.y + r.height - 2, hover ? ROW_HOVER : SLIDER_BG);
            ctx.method_51433(tr, s.getName(), rx + 6, r.y + (r.height - 2 - 8) / 2, TEXT, false);

            int tw = 30, th = 12;
            int tx = rx + rw - tw - 6;
            int ty = r.y + (r.height - 2 - th) / 2;
            ctx.method_25294(tx, ty, tx + tw, ty + th, bs.isEnabled() ? ACCENT : OFF);
            int knob = th - 2;
            int kx = bs.isEnabled() ? tx + tw - knob - 1 : tx + 1;
            ctx.method_25294(kx, ty + 1, kx + knob, ty + 1 + knob, 0xFF0B0B0F);
        } else if (s instanceof NumberSetting ns) {
            ctx.method_25294(rx, r.y, rx + rw, r.y + r.height - 2, SLIDER_BG);
            ctx.method_51433(tr, s.getName(), rx + 6, r.y + 4, TEXT, false);
            ctx.method_51433(tr, ns.displayValue(), rx + rw - 6 - tr.method_1727(ns.displayValue()), r.y + 4, TEXT_DIM, false);

            int barY = r.y + r.height - 8;
            int barH = 4;
            ctx.method_25294(rx + 6, barY, rx + rw - 6, barY + barH, OFF);
            int fillW = (int) ((rw - 12) * ns.fraction());
            ctx.method_25294(rx + 6, barY, rx + 6 + fillW, barY + barH, ACCENT);

            if (draggingSlider && activeSlider == r) {
                double frac = (mouseX - (rx + 6)) / (double) (rw - 12);
                ns.setFromFraction(frac);
            }
        } else if (s instanceof ModeSetting ms) {
            boolean hover = inside(mouseX, mouseY, rx, r.y, rw, r.height - 2);
            ctx.method_25294(rx, r.y, rx + rw, r.y + r.height - 2, hover ? ROW_HOVER : SLIDER_BG);
            ctx.method_51433(tr, s.getName(), rx + 6, r.y + (r.height - 2 - 8) / 2, TEXT, false);
            String d = "< " + ms.displayValue() + " >";
            ctx.method_51433(tr, d, rx + rw - 6 - tr.method_1727(d), r.y + (r.height - 2 - 8) / 2, ACCENT, false);
        }
    }

    public boolean mouseClicked(double mx, double my, int button) {
        if (button != 0) return false;
        if (inside((int) mx, (int) my, x, y, width, rowHeight)) {
            if (mx >= x + width - 14) expanded = !expanded;
            else module.toggle();
            return true;
        }
        if (!expanded) return false;
        for (SettingRow r : rows) {
            int rx = x + 8;
            int rw = width - 16;
            if (!inside((int) mx, (int) my, rx, r.y, rw, r.height - 2)) continue;
            Setting<?> s = r.setting;
            if (s instanceof BooleanSetting bs) { bs.toggle(); return true; }
            if (s instanceof ModeSetting ms) { ms.cycle(); return true; }
            if (s instanceof NumberSetting ns) {
                draggingSlider = true;
                activeSlider = r;
                double frac = (mx - (rx + 6)) / (double) (rw - 12);
                ns.setFromFraction(frac);
                return true;
            }
        }
        return false;
    }

    public void mouseReleased(double mx, double my, int button) {
        if (button == 0) { draggingSlider = false; activeSlider = null; }
    }

    private static boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && mx <= x + w && my >= y && my <= y + h;
    }
}
