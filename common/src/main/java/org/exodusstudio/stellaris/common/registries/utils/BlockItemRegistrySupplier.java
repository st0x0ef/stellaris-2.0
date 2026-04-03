package org.exodusstudio.stellaris.common.registries.utils;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Supplier;

public record BlockItemRegistrySupplier(RegistrySupplier<? extends Block> block,  RegistrySupplier<BlockItem> item) {
    public Supplier<Item> getAsItem() {
        return item::get;
    }
}
