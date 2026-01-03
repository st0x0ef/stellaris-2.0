package org.exodusstudio.stellaris.platform.neoforge;

import dev.architectury.registry.registries.Registrar;
import net.minecraft.core.Registry;
import org.exodusstudio.stellaris.neoforge.mixin.RegistrarMixin;

public class RegistrarUtilPlatformImpl {
    public static <T> Registry<T> getBaseRegistry(Registrar<T> registrar) {
        return ((RegistrarMixin)registrar).getDelegate();
    }
}
