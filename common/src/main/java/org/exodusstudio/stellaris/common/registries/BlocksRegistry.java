package org.exodusstudio.stellaris.common.registries;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

@SuppressWarnings("all")
public final class BlocksRegistry {

    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registries.BLOCK);

    /**
     * MOON WORLDGEN BLOCKS
     */
    public static final BlockItemRegistrySupplier MOON_ROCK = blockWithItem("moon_rock", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_SAND = blockWithItem("moon_sand", ofFullCopy(Blocks.SAND));
    public static final BlockItemRegistrySupplier MOON_STONE = blockWithItem("moon_stone", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_STONE_IRON_ORE = blockWithItem("moon_stone_iron_ore", ofFullCopy(Blocks.STONE));

    public static final BlockItemRegistrySupplier LUNAR_STONED_WOOD_LOG = blockWithItem("lunar_stoned_wood_log", ofFullCopy(Blocks.STONE), RotatedPillarBlock::new);

    public static final BlockItemRegistrySupplier ICED_MAGMA_BLOCK = blockWithItem("iced_magma_block", ofFullCopy(Blocks.MAGMA_BLOCK), IcedMagmaBlock::new);

    public static final BlockItemRegistrySupplier PACKED_ICE_BRICKS = blockWithItem("packed_ice_bricks", ofFullCopy(Blocks.PACKED_ICE));
    public static final BlockItemRegistrySupplier PACKED_ICE_PILLAR = blockWithItem("packed_ice_pillar", ofFullCopy(Blocks.PACKED_ICE), RotatedPillarBlock::new);
    public static final BlockItemRegistrySupplier POLISHED_PACKED_ICE = blockWithItem("polished_packed_ice", ofFullCopy(Blocks.PACKED_ICE));

    public static final BlockItemRegistrySupplier DESH_BLOCK = blockWithItem("desh_block", ofFullCopy(Blocks.IRON_BLOCK));

    /**
     * MARS WORLDGEN BLOCKS
     */
    public static final BlockItemRegistrySupplier MARS_ROCK = blockWithItem("mars_rock", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MARS_REGOLITH = blockWithItem("mars_regolith", ofFullCopy(Blocks.GRAVEL));
    public static final BlockItemRegistrySupplier MARS_SAND = blockWithItem("mars_sand", ofFullCopy(Blocks.SAND));
    public static final BlockItemRegistrySupplier MARS_ICE = blockWithItem("mars_ice", ofFullCopy(Blocks.PACKED_ICE));
    public static final BlockItemRegistrySupplier RUSTED_IRON = blockWithItem("rusted_iron", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier MARS_COBBLESTONE = blockWithItem("mars_cobblestone", ofFullCopy(Blocks.COBBLESTONE));
    public static final BlockItemRegistrySupplier MARS_STONE = blockWithItem("mars_stone", ofFullCopy(Blocks.STONE));



    /**
     * MACHINES BLOCKS
     */

    // ENERGY GENERATORS
    public static final BlockItemRegistrySupplier SOLAR_PANEL = blockWithItem("solar_panel", BlockBehaviour.Properties.of(), SolarPanelBlock::new);
    public static final BlockItemRegistrySupplier COAL_GENERATOR = blockWithItem("coal_generator", BlockBehaviour.Properties.of(), CoalGeneratorBlock::new);

    // FOOD PROCESSING
    public static final BlockItemRegistrySupplier VACUUMATOR = blockWithItem("vacuumator", BlockBehaviour.Properties.of(), VacuumatorBlock::new);

    // POWER STORAGE
    public static final BlockItemRegistrySupplier POWER_BANK_T1 = blockWithCustomItem("power_bank_t1", BlockBehaviour.Properties.of(), (p) -> new PowerBankBlock(p, (short) 1), new Item.Properties(), PowerBankItem::new);

    // CABLES/PIPES
    public static final BlockItemRegistrySupplier CABLE_T1 = blockWithItem("cable_t1", BlockBehaviour.Properties.of(), (p) -> new CableBlock(p, 20));

    // TECH
    public static final BlockItemRegistrySupplier ELECTROLYZER = blockWithCustomItem("electrolyzer", BlockBehaviour.Properties.of(), ElectrolyzerBlock::new, new Item.Properties(), BlockItem::new);
    public static final BlockItemRegistrySupplier ROCKET_STATION = blockWithCustomItem("rocket_station", BlockBehaviour.Properties.of(), RocketStationBlock::new, new Item.Properties(), BlockItem::new);

    public static final BlockItemRegistrySupplier ROCKET_LAUNCH_PAD = blockWithCustomItem("rocket_launch_pad", BlockBehaviour.Properties.of(), RocketLaunchPadBlock::new, new Item.Properties(), BlockItem::new);
    public static final BlockItemRegistrySupplier GRAVITY_MANIPULATOR = blockWithItem("gravity_manipulator", BlockBehaviour.Properties.of(), GravityManipulatorBlock::new);

    /**
     * Fluids
     */
    public static final RegistrySupplier<ArchitecturyLiquidBlock> HYDROGEN = block("hydrogen_block", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.HYDROGEN_STILL, p));
    public static final RegistrySupplier<ArchitecturyLiquidBlock> OXYGEN = block("oxygen_block", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.OXYGEN_STILL, p));


    /**
     * Decoration
     */
    public static final BlockItemRegistrySupplier FLAG = blockWithItem("flag", BlockBehaviour.Properties.of(), FlagBlock::new);


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

    public static @NotNull BlockItemRegistrySupplier blockWithItem(String name, BlockBehaviour.Properties properties) {
        RegistrySupplier<Block> block = block(name, properties);
        RegistrySupplier<Item> item = ItemsRegistry.item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_BLOCKS), p -> new BlockItem(block.get(), p));
        return new BlockItemRegistrySupplier(block, item);
    }


    public static <B extends Block> @NotNull BlockItemRegistrySupplier blockWithItem(String name, BlockBehaviour.Properties properties,
                                                                                     Function<BlockBehaviour.Properties, B> blockFunc) {
        RegistrySupplier<B> block = block(name, properties, blockFunc);
        RegistrySupplier<Item> item = ItemsRegistry.item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_BLOCKS), p -> new BlockItem(block.get(), p));
        return new BlockItemRegistrySupplier(block, item);
    }

    public static <B extends Block> @NotNull BlockItemRegistrySupplier blockWithItem(String name, BlockBehaviour.Properties properties,
                                                                                     Function<BlockBehaviour.Properties, B> blockFunc,
                                                                                     Item.Properties itemProperties) {
        RegistrySupplier<B> block = block(name, properties, blockFunc);
        RegistrySupplier<Item> item = ItemsRegistry.item(name, itemProperties.arch$tab(CreativeTabsRegistry.STELLARIS_BLOCKS), p -> new BlockItem(block.get(), p));
        return new BlockItemRegistrySupplier(block, item);
    }

    public static <B extends Block, I extends BlockItem> @NotNull BlockItemRegistrySupplier blockWithCustomItem(String name, BlockBehaviour.Properties properties,
                                                                                                                Function<BlockBehaviour.Properties, B> blockFunc, Item.Properties itemProperties,
                                                                                                                BiFunction<B, Item.Properties, I> itemSupplier) {
        RegistrySupplier<B> block = block(name, properties, blockFunc);
        RegistrySupplier<Item> item = ItemsRegistry.item(name, itemProperties.arch$tab(CreativeTabsRegistry.STELLARIS_BLOCKS), p -> itemSupplier.apply(block.get(), p));
        return new BlockItemRegistrySupplier(block, item);
    }
    private BlocksRegistry() {}
}