package ru.white.manager.rotation;

import java.util.Objects;
import net.minecraft.class_243;
import net.minecraft.class_3532;

public class Rotation {
    public static final Rotation ZERO = new Rotation(0f, 0f);

    public final float yaw;
    public final float pitch;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    /** Угол от глаз до точки. */
    public static Rotation from(class_243 eye, class_243 target) {
        class_243 d = target.method_1020(eye);
        double flat = Math.hypot(d.field_1352, d.field_1350);
        float yaw = (float) Math.toDegrees(Math.atan2(-d.field_1352, d.field_1350));
        float pitch = (float) -Math.toDegrees(Math.atan2(d.field_1351, flat));
        return new Rotation(yaw, pitch).normalized();
    }

    /** yaw в [-180; 180), pitch в [-90; 90]. */
    public Rotation normalized() {
        return new Rotation(class_3532.method_15393(yaw), class_3532.method_15363(pitch, -90f, 90f));
    }

    /** Кратчайшая разница (this -> other). */
    public Rotation deltaTo(Rotation other) {
        return new Rotation(class_3532.method_15393(other.yaw - yaw), other.pitch - pitch);
    }

    public float distanceTo(Rotation other) {
        Rotation d = deltaTo(other);
        return (float) Math.hypot(d.yaw, d.pitch);
    }

    public Rotation add(float dYaw, float dPitch) {
        return new Rotation(yaw + dYaw, pitch + dPitch);
    }

    /** Интерполяция по кратчайшему пути, t в [0; 1]. */
    public Rotation lerp(Rotation to, float t) {
        Rotation d = deltaTo(to);
        return new Rotation(yaw + d.yaw * t, pitch + d.pitch * t).normalized();
    }

    /** Привязка к шагу мыши относительно предыдущего угла. */
    public Rotation withGcd(Rotation previous) {
        return GcdUtil.quantize(previous, this);
    }

    public boolean isValid() {
        return Float.isFinite(yaw) && Float.isFinite(pitch);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Rotation r
                && Float.compare(yaw, r.yaw) == 0
                && Float.compare(pitch, r.pitch) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(yaw, pitch);
    }

    @Override
    public String toString() {
        return "Rotation[yaw=" + yaw + ", pitch=" + pitch + "]";
    }
}
