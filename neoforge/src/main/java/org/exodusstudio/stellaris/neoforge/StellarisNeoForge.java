package org.exodusstudio.stellaris.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.exodusstudio.stellaris.Stellaris;

@Mod(Stellaris.MOD_ID)
public final class StellarisNeoForge {

    public StellarisNeoForge(IEventBus bus, ModContainer modContainer) {
        Stellaris.init();
    }

}
