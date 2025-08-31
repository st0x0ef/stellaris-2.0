package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.exodusstudio.stellaris.common.blocks.CableBlock;
import org.exodusstudio.stellaris.common.blocks.CoalGeneratorBlock;
import org.exodusstudio.stellaris.common.blocks.SolarPanelBlock;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;
import static org.exodusstudio.stellaris.Stellaris.MOD_ID;

public final class BlocksRegistry {

    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registries.BLOCK);


    /**
     * MOON WORLDGEN BLOCKS
     */
    public static final RegistrySupplier<Block> MOON_SAND = blockWithItem("moon_sand", ofFullCopy(Blocks.SAND));
    public static final RegistrySupplier<Block> MOON_STONE = blockWithItem("moon_stone", ofFullCopy(Blocks.STONE));

    /**
     * MACHINES BLOCKS
     */

    // ENERGY GENERATORS
    public static final RegistrySupplier<SolarPanelBlock> SOLAR_PANEL = blockWithItem("solar_panel", BlockBehaviour.Properties.of(), SolarPanelBlock::new);
    public static final RegistrySupplier<CoalGeneratorBlock> COAL_GENERATOR = blockWithItem("coal_generator", BlockBehaviour.Properties.of(), CoalGeneratorBlock::new);

    // CABLES/PIPES
    public static final RegistrySupplier<CableBlock> CABLE_T1 = blockWithItem("cable_t1", BlockBehaviour.Properties.of(), (p) -> new CableBlock(p, 20));


    public static <B extends Block> @NotNull RegistrySupplier<B> block(String name,
                                                               BlockBehaviour.Properties properties,
                                                               Function<BlockBehaviour.Properties, B> blockFunc) {
        ResourceLocation id = ResourceLocationUtils.id(name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        return BLOCKS.register(id, () -> blockFunc.apply(properties.setId(key)));
    }

    public static @NotNull RegistrySupplier<Block> block(String name, BlockBehaviour.Properties properties) {
        return block(name, properties, Block::new);
    }

    public static @NotNull RegistrySupplier<Block> blockWithItem(String name, BlockBehaviour.Properties properties) {
        RegistrySupplier<Block> toReturn = block(name, properties);
        ItemsRegistry.item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_BLOCKS), p -> new BlockItem(toReturn.get(), p));
        return toReturn;
    }

    public static <B extends Block> @NotNull RegistrySupplier<B> blockWithItem(String name, BlockBehaviour.Properties properties,
                                                                               Function<BlockBehaviour.Properties, B> blockFunc) {
        RegistrySupplier<B> toReturn = block(name, properties, blockFunc);
        ItemsRegistry.item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_BLOCKS), p -> new BlockItem(toReturn.get(), p));
        return toReturn;
    }

    public static <B extends Block> @NotNull RegistrySupplier<B> blockWithItem(String name, BlockBehaviour.Properties properties,
                                                                               Function<BlockBehaviour.Properties, B> blockFunc,
                                                                               Item.Properties itemProperties) {
        RegistrySupplier<B> b = block(name, properties, blockFunc);
        ItemsRegistry.item(name, itemProperties, p -> new BlockItem(b.get(), p));
        return b;
    }

    public static <B extends Block, I extends BlockItem> @NotNull RegistrySupplier<B> blockWithCustomItem(String name, BlockBehaviour.Properties properties,
                                                                            Function<BlockBehaviour.Properties, B> blockFunc, Item.Properties itemProperties,
                                                                            BiFunction<Block, Item.Properties, I> itemSupplier) {
        RegistrySupplier<B> b = block(name, properties, blockFunc);
        ItemsRegistry.item(name, itemProperties, p -> itemSupplier.apply(b.get(), p));
        return b;
    }

    private BlocksRegistry() {}

}
