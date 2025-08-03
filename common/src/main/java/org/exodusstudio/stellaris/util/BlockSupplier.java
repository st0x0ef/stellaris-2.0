package org.exodusstudio.stellaris.util;

import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;

public class BlockSupplier<B extends Block, I extends BlockItem> implements DeferredSupplier<B> {

    public final RegistrySupplier<B> block;
    public final RegistrySupplier<I> item;

    public BlockSupplier(RegistrySupplier<B> block, RegistrySupplier<I> item) {
        this.block = block;
        this.item = item;
    }

    @Override
    public ResourceLocation getRegistryId() {
        return block.getRegistryId();
    }

    @Override
    public ResourceLocation getId() {
        return block.getId();
    }

    @Override
    public boolean isPresent() {
        return block.isPresent();
    }

    @Override
    public B get() {
        return block.get();
    }


}