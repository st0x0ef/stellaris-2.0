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
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.exodusstudio.stellaris.common.blocks.*;
import org.exodusstudio.stellaris.common.items.PowerBankItem;
import org.exodusstudio.stellaris.common.registries.utils.BlockItemRegistrySupplier;
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
    public static final BlockItemRegistrySupplier MOON_SAND = blockWithItem("moon_sand", ofFullCopy(Blocks.SAND));
    public static final BlockItemRegistrySupplier MOON_STONE = blockWithItem("moon_stone", ofFullCopy(Blocks.STONE));

    public static final BlockItemRegistrySupplier ICED_MAGMA_BLOCK = blockWithItem("iced_magma_block", ofFullCopy(Blocks.MAGMA_BLOCK), IcedMagmaBlock::new);

    public static final BlockItemRegistrySupplier PACKED_ICE_BRICKS = blockWithItem("packed_ice_bricks", ofFullCopy(Blocks.PACKED_ICE));
    public static final BlockItemRegistrySupplier PACKED_ICE_PILLAR = blockWithItem("packed_ice_pillar", ofFullCopy(Blocks.PACKED_ICE), RotatedPillarBlock::new);
    public static final BlockItemRegistrySupplier POLISHED_PACKED_ICE = blockWithItem("polished_packed_ice", ofFullCopy(Blocks.PACKED_ICE));


    /**
     * MACHINES BLOCKS
     */

    // ENERGY GENERATORS
    public static final BlockItemRegistrySupplier SOLAR_PANEL = blockWithItem("solar_panel", BlockBehaviour.Properties.of(), SolarPanelBlock::new);
    public static final BlockItemRegistrySupplier COAL_GENERATOR = blockWithItem("coal_generator", BlockBehaviour.Properties.of(), CoalGeneratorBlock::new);

    // POWER STORAGE
    public static final BlockItemRegistrySupplier POWER_BANK_T1 = blockWithCustomItem("power_bank_t1", BlockBehaviour.Properties.of(), (p) -> new PowerBankBlock(p, (short) 1), new Item.Properties().component(DataComponentsRegistry.ENERGY.get(), 0), (b, p) -> new PowerBankItem((PowerBankBlock) b, p));

    // CABLES/PIPES
    public static final BlockItemRegistrySupplier CABLE_T1 = blockWithItem("cable_t1", BlockBehaviour.Properties.of(), (p) -> new CableBlock(p, 20));


    public static <B extends Block> @NotNull RegistrySupplier<Block> block(String name,
                                                               BlockBehaviour.Properties properties,
                                                               Function<BlockBehaviour.Properties, B> blockFunc) {
        ResourceLocation id = ResourceLocationUtils.id(name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        return BLOCKS.register(id, () -> blockFunc.apply(properties.setId(key)));
    }

    public static @NotNull RegistrySupplier<Block> block(String name, BlockBehaviour.Properties properties) {
        return block(name, properties, Block::new);
    }

    public static @NotNull BlockItemRegistrySupplier blockWithItem(String name, BlockBehaviour.Properties properties) {
        RegistrySupplier<Block> block = block(name, properties);
        RegistrySupplier<Item> item = ItemsRegistry.item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_BLOCKS), p -> new BlockItem(block.get(), p));
        return new BlockItemRegistrySupplier(block, item);
    }

    public static <B extends Block> @NotNull BlockItemRegistrySupplier blockWithItem(String name, BlockBehaviour.Properties properties,
                                                                               Function<BlockBehaviour.Properties, B> blockFunc) {
        RegistrySupplier<Block> block = block(name, properties, blockFunc);
        RegistrySupplier<Item> item = ItemsRegistry.item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_BLOCKS), p -> new BlockItem(block.get(), p));
        return new BlockItemRegistrySupplier(block, item);
    }

    public static <B extends Block> @NotNull BlockItemRegistrySupplier blockWithItem(String name, BlockBehaviour.Properties properties,
                                                                               Function<BlockBehaviour.Properties, B> blockFunc,
                                                                               Item.Properties itemProperties) {
        RegistrySupplier<Block> block = block(name, properties, blockFunc);
        RegistrySupplier<Item> item = ItemsRegistry.item(name, itemProperties, p -> new BlockItem(block.get(), p));
        return new BlockItemRegistrySupplier(block, item);
    }

    public static <B extends Block, I extends BlockItem> @NotNull BlockItemRegistrySupplier blockWithCustomItem(String name, BlockBehaviour.Properties properties,
                                                                            Function<BlockBehaviour.Properties, B> blockFunc, Item.Properties itemProperties,
                                                                            BiFunction<Block, Item.Properties, I> itemSupplier) {
        RegistrySupplier<Block> block = block(name, properties, blockFunc);
        RegistrySupplier<Item> item = ItemsRegistry.item(name, itemProperties, p -> itemSupplier.apply(block.get(), p));
        return new BlockItemRegistrySupplier(block, item);
    }
}
