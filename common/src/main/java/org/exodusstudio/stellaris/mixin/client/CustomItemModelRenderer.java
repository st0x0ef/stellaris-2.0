package org.exodusstudio.stellaris.mixin.client;

import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import org.exodusstudio.stellaris.client.renderer.rockets.RocketItemRenderer;
import org.exodusstudio.stellaris.client.renderers.blocks.gravity_manipulator.GravityManipulatorItemRenderer;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpecialModelRenderers.class)
public class CustomItemModelRenderer {
    @Shadow
    @Final
    private static ExtraCodecs.LateBoundIdMapper<ResourceLocation, MapCodec<? extends SpecialModelRenderer.Unbaked>> ID_MAPPER;

    @Inject(at = @At(value = "HEAD"), method = "bootstrap")
    private static void addCustomItemRenderer(CallbackInfo ci) {
        ID_MAPPER.put(ResourceLocationUtils.id("gravity_manipulator"), GravityManipulatorItemRenderer.Unbaked.MAP_CODEC);
        ID_MAPPER.put(ResourceLocationUtils.id("rocket"), RocketItemRenderer.Unbaked.MAP_CODEC);

    }
}
