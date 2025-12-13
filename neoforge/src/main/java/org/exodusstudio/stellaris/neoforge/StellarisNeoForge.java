package org.exodusstudio.stellaris.neoforge;

import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.neoforge.common.registries.DataAttachmentRegistry;
import org.exodusstudio.stellaris.common.utils.GravityUtils;

@Mod(Stellaris.MOD_ID)
public final class StellarisNeoForge {
    public StellarisNeoForge(IEventBus bus) {
        Stellaris.init();
        //DataAttachmentRegistry.register(); TODO : fix crash


        NeoForge.EVENT_BUS.addListener(StellarisNeoForge::onAddServerReloadListenersEvent);
        NeoForge.EVENT_BUS.addListener(StellarisNeoForge::onEntityJoinLevelEvent);
    }

    public static void onAddServerReloadListenersEvent(AddServerReloadListenersEvent event) {
        Stellaris.onAddReloadListenerEvent(event::addListener);
    }

    public static void onEntityJoinLevelEvent(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof LivingEntity living) {
            GravityUtils.setGravity(living);
        }
    }

}
