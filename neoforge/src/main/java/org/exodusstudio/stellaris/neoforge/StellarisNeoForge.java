package org.exodusstudio.stellaris.neoforge;

import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

import net.neoforged.neoforge.registries.RegisterEvent;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.registries.BlocksRegistry;
import org.exodusstudio.stellaris.registries.ModRegistries;

@Mod(Stellaris.MOD_ID)
public final class StellarisNeoForge {

    public StellarisNeoForge(IEventBus bus) {
        ModRegistries.registerAll();
        Stellaris.init();
        bus.addListener(this::registerEvent);
    }

    public void registerEvent(RegisterEvent event) {
        if (event.getRegistry() == BuiltInRegistries.ENTITY_TYPE) { // To register it after blocks and before items
            //BlocksRegistry.registerBlockItems();
        }
    }
}
