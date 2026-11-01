package ru.white.manager.rotation;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.class_310;
import net.minecraft.class_746;

/**
 * Применяет поворот камеры (ApplyMode.CAMERA) каждый кадр рендера,
 * а не раз в игровой тик (20 Гц). RotationManager.submit() по-прежнему
 * считает цель раз в тик (visualFrom/visualTo); здесь мы только
 * линейно интерполируем между ними по доле прошедшего тика.
 *
 * SILENT-режим эту логику не использует — камера там не трогается вовсе,
 * подмена идёт в SilentRotationMixin вокруг отправки пакета.
 *
 * ПРОВЕРЬ ПЕРЕД СБОРКОЙ: ctx.tickCounter().getTickProgress(true) — сигнатура
 * из fabric-api WorldRenderContext/RenderTickCounter по общей памяти API,
 * НЕ подтверждена разбором байткода (в проекте не было ни одной ссылки на
 * рендер-тик до этого патча). Если у тебя в этой версии fabric-api метод
 * называется иначе — у javac будет точная ошибка компиляции на этой строке,
 * поправь по своим маппингам.
 */
public final class RotationRenderer {
    private RotationRenderer() {}

    public static void register() {
        WorldRenderEvents.START.register(ctx -> {
            RotationManager rm = RotationManager.get();
            if (rm.getMode() != RotationManager.ApplyMode.CAMERA || !rm.isActive()) return;

            class_746 player = class_310.method_1551().field_1724;
            if (player == null) return;

            float partial = ctx.tickCounter().getTickProgress(true);
            Rotation frame = rm.visualFrom().lerp(rm.visualTo(), partial);

            player.method_36456(frame.yaw);
            player.method_36457(frame.pitch);
        });
    }
}
