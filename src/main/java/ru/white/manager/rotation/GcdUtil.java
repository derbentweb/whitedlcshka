package ru.white.manager.rotation;

import net.minecraft.class_310;
import net.minecraft.class_3532;

/** Привязка углов к минимальному шагу мыши (зависит от чувствительности). */
public final class GcdUtil {
    private GcdUtil() {}

    /** Минимальный шаг в градусах: f^3 * 8 * 0.15 (0.15 - множитель в Entity.changeLookDirection). */
    public static float step() {
        double sens = 0.5;
        class_310 mc = class_310.method_1551();
        if (mc != null && mc.field_1690 != null) {
            // Yarn 1.21: SimpleOption<Double>. При Mojang-маппингах: options.sensitivity().get()
            sens = mc.field_1690.method_42495().method_41753();
        }
        sens = class_3532.method_15350(sens, 0.0, 1.0);
        double f = sens * 0.6 + 0.2;
        double step = f * f * f * 8.0 * 0.15;
        return step > 1.0E-4 ? (float) step : 0.01F;
    }

    /** Квантует РАЗНИЦУ (to - from) к сетке шага мыши. */
    public static Rotation quantize(Rotation from, Rotation to) {
        float step = step();
        float dYaw = class_3532.method_15393(to.yaw - from.yaw);
        float dPitch = to.pitch - from.pitch;
        float qYaw = Math.round(dYaw / step) * step;
        float qPitch = Math.round(dPitch / step) * step;
        return new Rotation(from.yaw + qYaw, class_3532.method_15363(from.pitch + qPitch, -90F, 90F));
    }
}
