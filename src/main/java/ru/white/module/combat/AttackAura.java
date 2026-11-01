package ru.white.module.combat;

import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import ru.white.manager.rotation.RotationManager;
import ru.white.module.Module;
import ru.white.module.setting.BooleanSetting;
import ru.white.module.setting.ModeSetting;
import ru.white.module.setting.NumberSetting;
import ru.white.module.combat.aura.RotationAura;
import ru.white.module.combat.aura.rotation.FunTimeRotation;

public class AttackAura extends Module {

    public final NumberSetting range = add(new NumberSetting("Дальность", 3.1, 1.0, 6.0, 0.1));
    public final NumberSetting fov = add(new NumberSetting("FOV", 90.0, 30.0, 180.0, 5.0));
    public final NumberSetting maxStep = add(new NumberSetting("Макс. шаг", 140.0, 30.0, 360.0, 10.0));
    public final NumberSetting pitchSettle = add(new NumberSetting("Pitch допуск", 5.0, 1.0, 30.0, 0.5));
    public final NumberSetting yawJitterMin = add(new NumberSetting("Yaw jitter мин", 3.0, 0.0, 20.0, 0.5));
    public final NumberSetting yawJitterMax = add(new NumberSetting("Yaw jitter макс", 7.0, 0.0, 30.0, 0.5));
    public final NumberSetting deflectDuration = add(new NumberSetting("Отводка (мс)", 150.0, 0.0, 500.0, 10.0));

    public final BooleanSetting critFlick = add(new BooleanSetting("Крит-флик", true));
    public final BooleanSetting targetPlayers = add(new BooleanSetting("Игроки", true));
    public final BooleanSetting targetMobs = add(new BooleanSetting("Мобы", false));
    public final ModeSetting priority = add(new ModeSetting("Приоритет", "Дистанция", "Дистанция", "Здоровье", "Угол"));

    // --- настройки ротации ---
    public final ModeSetting rotationMode = add(new ModeSetting("Ротация", "Камера", "Камера", "Тихая"));
    public final BooleanSetting gcdFix = add(new BooleanSetting("GCD-фикс", true));
    public final BooleanSetting stickyTarget = add(new BooleanSetting("Удержание цели", true));

    private final RotationAura rotation = new FunTimeRotation();
    private class_1309 currentTarget;

    public AttackAura() {
        super("AttackAura", "Двухслойная аура: Polar + ИИ");
    }

    @Override
    public void onTick() {
        if (!isEnabled()) return;
        class_310 mc = class_310.method_1551();
        if (mc.field_1724 == null || mc.field_1687 == null) return;

        RotationManager rm = RotationManager.get();
        rm.setMode("Тихая".equals(rotationMode.getValue())
                ? RotationManager.ApplyMode.SILENT
                : RotationManager.ApplyMode.CAMERA);
        rm.setGcd(gcdFix.isEnabled());

        class_1309 target = findTarget(mc);
        currentTarget = target;

        float[] ranges = new float[] { range.getFloat() };
        boolean canAttack = mc.field_1724.method_7261(0.5f) >= 1.0f;

        rotation.onRotation(this, target, ranges, canAttack);
    }

    @Override
    protected void onDisable() {
        rotation.reset();
        RotationManager.get().reset();
        currentTarget = null;
    }

    /** Базовые фильтры без учёта дистанции и FOV. */
    private boolean isValid(class_310 mc, class_1309 living) {
        if (living == mc.field_1724 || !living.method_5805() || living.method_31481()) return false;
        if (living.method_7325() || living instanceof class_1531) return false;
        if (living instanceof class_1657) return targetPlayers.isEnabled();
        return targetMobs.isEnabled();
    }

    /** Расстояние от глаз до ближайшей точки хитбокса (согласовано с проверкой досягаемости). */
    private static double reachDistance(class_310 mc, class_1309 living) {
        class_243 eye = mc.field_1724.method_33571();
        class_238 b = living.method_5829();
        class_243 p = new class_243(
                class_3532.method_15350(eye.field_1352, b.field_1323, b.field_1320),
                class_3532.method_15350(eye.field_1351, b.field_1322, b.field_1325),
                class_3532.method_15350(eye.field_1350, b.field_1321, b.field_1324));
        return eye.method_1022(p);
    }

    private class_1309 findTarget(class_310 mc) {
        // Удержание: не переключаемся, пока текущая цель в пределах дальности (+ гистерезис)
        if (stickyTarget.isEnabled() && currentTarget != null
                && isValid(mc, currentTarget)
                && reachDistance(mc, currentTarget) <= range.getValue() + 0.3) {
            return currentTarget;
        }

        class_1309 best = null;
        double bestVal = Double.MAX_VALUE;
        double fovRad = Math.toRadians(fov.getValue());

        for (var e : mc.field_1687.method_18112()) {
            if (!(e instanceof class_1309 living) || !isValid(mc, living)) continue;

            double d = reachDistance(mc, living);
            if (d > range.getValue()) continue;

            var toTarget = living.method_5829().method_1005().method_1020(mc.field_1724.method_33571());
            if (toTarget.method_1027() < 1e-6) continue;
            var look = mc.field_1724.method_5828(1.0f);
            double angle = Math.acos(Math.max(-1, Math.min(1, look.method_1026(toTarget.method_1029()))));
            if (angle > fovRad / 2.0) continue;

            double v;
            switch (priority.getValue()) {
                case "Здоровье" -> v = living.method_6032();
                case "Угол" -> v = angle;
                default -> v = d;
            }
            if (v < bestVal) { bestVal = v; best = living; }
        }
        return best;
    }

    public void attack(class_1309 target) {
        class_310 mc = class_310.method_1551();
        if (mc.field_1761 == null || mc.field_1724 == null) return;
        mc.field_1761.method_2918(mc.field_1724, target);
        mc.field_1724.method_6104(class_1268.field_5808);
    }
}
