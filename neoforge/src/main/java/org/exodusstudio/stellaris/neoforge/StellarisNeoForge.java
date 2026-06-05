package org.exodusstudio.stellaris.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.neoforge.common.registries.DataAttachmentRegistry;
import org.exodusstudio.stellaris.platform.neoforge.RegistryPlatformImpl;

@Mod(Stellaris.MOD_ID)
public final class StellarisNeoForge {
    public StellarisNeoForge(IEventBus bus) {
        Stellaris.init();
        DataAttachmentRegistry.register(bus);

        RegistryPlatformImpl.ENTITY_DATA_SERIALIZERS.register(bus);
    }
}
