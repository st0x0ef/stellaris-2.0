package org.exodusstudio.stellaris.fabric.client;

import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.client.fluid.FluidVariantRenderHandler;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
class ArchitecturyFluidRenderingFabric implements FluidVariantRenderHandler, FluidRenderHandler {
    private final ArchitecturyFluidAttributes attributes;
    private TextureAtlas atlas = null;
    private final TextureAtlasSprite[] sprites = new TextureAtlasSprite[2];
    private final TextureAtlasSprite[] spritesOverlaid = new TextureAtlasSprite[3];
    private final TextureAtlasSprite[] spritesOther = new TextureAtlasSprite[2];
    private final TextureAtlasSprite[] spritesOtherOverlaid = new TextureAtlasSprite[3];

    public ArchitecturyFluidRenderingFabric(ArchitecturyFluidAttributes attributes) {
        this.attributes = attributes;
    }

    @Override
    @Nullable
    public TextureAtlasSprite[] getSprites(FluidVariant variant) {
        FluidStack stack = FluidStackHooksFabric.fromFabric(variant, FluidStack.bucketAmount());
        Identifier overlayTexture = attributes.getOverlayTexture(stack);
        TextureAtlasSprite overlaySprite = overlayTexture == null ? null : atlas.getSprite(overlayTexture);
        TextureAtlasSprite[] sprites = overlaySprite == null ? this.sprites : spritesOverlaid;
        sprites[0] = atlas.getSprite(attributes.getSourceTexture(stack));
        sprites[1] = atlas.getSprite(attributes.getFlowingTexture(stack));
        if (overlaySprite != null) sprites[2] = overlaySprite;
        return sprites;
    }

    @Override
    public int getColor(FluidVariant variant, @Nullable BlockAndTintGetter view, @Nullable BlockPos pos) {
        return attributes.getColor(FluidStackHooksFabric.fromFabric(variant, FluidStack.bucketAmount()), view, pos);
    }

    @Override
    public TextureAtlasSprite[] getFluidSprites(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
        Identifier overlayTexture = attributes.getOverlayTexture(state, view, pos);
        TextureAtlasSprite overlaySprite = overlayTexture == null ? null : atlas.getSprite(overlayTexture);
        TextureAtlasSprite[] sprites = overlaySprite == null ? this.spritesOther : spritesOtherOverlaid;
        sprites[0] = atlas.getSprite(attributes.getSourceTexture(state, view, pos));
        sprites[1] = atlas.getSprite(attributes.getFlowingTexture(state, view, pos));
        if (overlaySprite != null) sprites[2] = overlaySprite;
        return sprites;
    }

    @Override
    public int getFluidColor(@Nullable BlockAndTintGetter view, @Nullable BlockPos pos, FluidState state) {
        return attributes.getColor(state, view, pos);
    }

    @Override
    public void reloadTextures(TextureAtlas atlas) {
        this.atlas = atlas;
    }
}