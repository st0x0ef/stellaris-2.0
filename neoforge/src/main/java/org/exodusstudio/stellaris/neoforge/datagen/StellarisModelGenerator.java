package org.exodusstudio.stellaris.neoforge.datagen;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.jspecify.annotations.NonNull;

import java.util.stream.Stream;

public class StellarisModelGenerator extends ModelProvider {
    private final ResourceManager resourceManager;
    public StellarisModelGenerator(PackOutput output, ResourceManager resourceManager) {
        super(output, Stellaris.MOD_ID);
        this.resourceManager = resourceManager;
    }

    private static ItemModelGenerators itemModels;
    private static BlockModelGenerators blockModels;

    @Override
    protected void registerModels(@NonNull BlockModelGenerators blockModels, @NonNull ItemModelGenerators itemModels) {
        StellarisModelGenerator.itemModels = itemModels;
        StellarisModelGenerator.blockModels = blockModels;

        blockModels.createTrivialCube(BlocksRegistry.MARS_STONE_IRON_ORE.block().get());
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return super.getKnownBlocks().filter(holder -> resourceManager.getResource(holder.getKey().identifier().withPrefix("blockstates/").withSuffix(".json")).isEmpty());
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(itemHolder -> resourceManager.getResource(itemHolder.getKey().identifier().withPrefix("items/").withSuffix(".json")).isEmpty());
    }
}
