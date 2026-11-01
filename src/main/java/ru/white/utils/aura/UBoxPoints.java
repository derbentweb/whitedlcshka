package ru.white.utils.aura;

import net.minecraft.class_1309;
import net.minecraft.class_238;
import net.minecraft.class_243;

public final class UBoxPoints {
    private UBoxPoints() {}

    public static class_243 getBestVector3dOnEntityBox(class_238 box) {
        return new class_243(
                (box.field_1323 + box.field_1320) * 0.5,
                (box.field_1322 + box.field_1325) * 0.5,
                (box.field_1321 + box.field_1324) * 0.5
        );
    }

    public static class_243 getBestVector3dOnEntityBox(class_1309 entity) {
        return getBestVector3dOnEntityBox(entity.method_5829());
    }
}
