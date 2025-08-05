package org.exodusstudio.stellaris.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.StellarisClient;

@EventBusSubscriber(modid = Stellaris.MOD_ID, value = Dist.CLIENT)
public class StellarisNeoforgeClient {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(StellarisClient::initClient);
    }


}
