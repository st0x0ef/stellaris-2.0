package org.exodusstudio.stellaris.client.renderers.mobs;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

/**
 * Render state for the Alien. Carries the villager profession path so the renderer can pick the
 * matching per-profession texture.
 */
public class AlienRenderState extends LivingEntityRenderState {
    public String professionPath = "none";
}
