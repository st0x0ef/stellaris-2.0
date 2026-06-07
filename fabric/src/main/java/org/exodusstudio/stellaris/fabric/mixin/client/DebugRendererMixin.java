package org.exodusstudio.stellaris.fabric.mixin.client;

import net.minecraft.client.renderer.debug.DebugRenderer;
import org.exodusstudio.stellaris.client.debug.OxygenDebugRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {

    @Shadow @Final
    private List<DebugRenderer.SimpleDebugRenderer> renderers;

    @Inject(method = "refreshRendererList()V", at = @At("RETURN"))
    private void addOxygenRenderer(CallbackInfo ci) {
        this.renderers.add(OxygenDebugRenderer.INSTANCE);
    }
}
