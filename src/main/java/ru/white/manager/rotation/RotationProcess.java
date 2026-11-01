package ru.white.manager.rotation;

/**
 * Фасад над RotationManager. Сигнатура update(...) сохранена,
 * поэтому существующие вызовы (FunTimeRotation) компилируются без изменений.
 */
public final class RotationProcess {
    private RotationProcess() {}

    public static Rotation update(Rotation rotation,
                                  float yawSpeed, float pitchSpeed,
                                  float yawAccel, float pitchAccel,
                                  int priority, int ticks, boolean force) {
        return RotationManager.get().submit(
                rotation, yawSpeed, pitchSpeed, yawAccel, pitchAccel, priority, ticks, force);
    }
}
