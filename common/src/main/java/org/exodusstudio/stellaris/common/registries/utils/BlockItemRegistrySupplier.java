package org.exodusstudio.stellaris.common.registries.utils;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public record BlockItemRegistrySupplier(RegistrySupplier<? extends Block> block,  RegistrySupplier<Item> item) {

}
