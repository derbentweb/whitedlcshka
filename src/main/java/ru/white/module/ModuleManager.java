package ru.white.module;

import ru.white.module.combat.AttackAura;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<>();

    public void init() {
        modules.add(new AttackAura());
    }

    public List<Module> getModules() { return modules; }
}
