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

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StellarisModelProvider extends ModelProvider {
    public StellarisModelProvider(PackOutput output) {
        super(output, Stellaris.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        // ITEMS
        itemModels.generateFlatItem(ItemsRegistry.RAW_DESH.get(), ModelTemplates.FLAT_ITEM);



        // BLOCKS
        // blockModels.createTrivialCube(BlocksRegistry.VENUS_DIAMOND_ORE.block().get());
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(BlocksRegistry.BLOCKS.iterator(), Spliterator.ORDERED), false)
                .filter(x -> !x.equals(BlocksRegistry.MOON_ROCK))
                .map(RegistrySupplier::getDelegate);
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(ItemsRegistry.ITEMS.iterator(), Spliterator.ORDERED), false)
                .filter(x -> !x.equals(ItemsRegistry.TEST_ITEM) && !x.equals(ItemsRegistry.DESH_INGOT) && !x.equals(ItemsRegistry.RAW_DESH))
                .map(RegistrySupplier::getDelegate);
    }
}