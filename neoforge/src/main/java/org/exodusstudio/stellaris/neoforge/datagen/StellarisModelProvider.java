package org.exodusstudio.stellaris.neoforge.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.*;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.registries.utils.BlockItemRegistrySupplier;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StellarisModelProvider extends ModelProvider {
    public StellarisModelProvider(PackOutput output) {
        super(output, Stellaris.MOD_ID);
    }

    private BlockModelGenerators blockModels;
    private ItemModelGenerators itemModels;

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        this.blockModels = blockModels;
        this.itemModels = itemModels;
        // ITEMS
        itemModels.generateFlatItem(ItemsRegistry.RAW_DESH.get(), ModelTemplates.FLAT_ITEM);

        //BLOCKS
        simpleRotatedVariantBlock(BlocksRegistry.MOON_SAND);
        simpleRotatedVariantBlock(BlocksRegistry.MOON_STONE);

    }

    protected void simpleRotatedVariantBlock(BlockItemRegistrySupplier blockItem) {
        blockModels.createRotatedVariantBlock(blockItem.block().get());
        blockModels.registerSimpleFlatItemModel(blockItem.block().get());
    }
}