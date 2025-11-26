package org.exodusstudio.stellaris.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import org.exodusstudio.stellaris.Stellaris;

@Mod(Stellaris.MOD_ID)
public final class StellarisNeoForge {
    public StellarisNeoForge(IEventBus bus) {
        Stellaris.init();

        NeoForge.EVENT_BUS.addListener(StellarisNeoForge::onAddServerReloadListenersEvent);
    }

    public static void onAddServerReloadListenersEvent(AddServerReloadListenersEvent event) {
        Stellaris.onAddReloadListenerEvent(event::addListener);
    }

}
