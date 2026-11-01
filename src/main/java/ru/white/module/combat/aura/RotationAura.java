package ru.white.module.combat.aura;

import net.minecraft.class_1309;
import ru.white.module.combat.AttackAura;

public interface RotationAura {
    void onRotation(AttackAura aura, class_1309 target, float[] ranges, boolean canAttack);

    /** Сброс внутреннего состояния (вызывается при выключении модуля). */
    default void reset() {}
}
