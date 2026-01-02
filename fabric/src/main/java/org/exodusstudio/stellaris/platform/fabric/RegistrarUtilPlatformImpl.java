package org.exodusstudio.stellaris.platform.fabric;

import dev.architectury.registry.registries.Registrar;
import net.minecraft.core.Registry;
import org.exodusstudio.stellaris.fabric.mixin.RegistrarMixin;

public class RegistrarUtilPlatformImpl {
    public static <T> Registry<T> getBaseRegistry(Registrar<T> registrar) {
        return ((RegistrarMixin)registrar).getDelegate();
    }
}
