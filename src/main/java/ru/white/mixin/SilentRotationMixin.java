package ru.white.mixin;

import net.minecraft.class_746;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.white.manager.rotation.Rotation;
import ru.white.manager.rotation.RotationManager;

/**
 * Подменяет yaw/pitch только на время отправки пакетов движения.
 * Нужен только для ApplyMode.SILENT. Добавьте в mixins.json в секцию "client".
 * Проверьте имя метода sendMovementPackets для 1.21.11 (Yarn).
 */
@Mixin(class_746.class)
public abstract class SilentRotationMixin {
    @Unique private float white$realYaw;
    @Unique private float white$realPitch;
    @Unique private boolean white$spoofed;

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void white$pre(CallbackInfo ci) {
        Rotation r = RotationManager.get().silentRotation();
        if (r == null) return;
        class_746 self = (class_746) (Object) this;
        white$realYaw = self.method_36454();
        white$realPitch = self.method_36455();
        self.method_36456(r.yaw);
        self.method_36457(r.pitch);
        white$spoofed = true;
    }

    @Inject(method = "sendMovementPackets", at = @At("RETURN"))
    private void white$post(CallbackInfo ci) {
        if (!white$spoofed) return;
        class_746 self = (class_746) (Object) this;
        self.method_36456(white$realYaw);
        self.method_36457(white$realPitch);
        white$spoofed = false;
    }
}
