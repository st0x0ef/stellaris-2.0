package org.exodusstudio.stellaris.neoforge.datagen;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.exodusstudio.stellaris.Stellaris;

@EventBusSubscriber(modid = Stellaris.MOD_ID)
public class StellarisDatagen {
    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {

        ResourceManager resourceManager = event.getResourceManager(PackType.CLIENT_RESOURCES);
        event.createProvider(packOutput -> new StellarisModelGenerator(packOutput, resourceManager));
        event.createProvider(StellarisDatapackProvide::new);
    }
}
