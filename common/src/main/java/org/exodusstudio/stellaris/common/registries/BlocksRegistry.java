package org.exodusstudio.stellaris.common.registries;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.exodusstudio.stellaris.common.blocks.*;
import org.exodusstudio.stellaris.common.blocks.PipeBlock;
import org.exodusstudio.stellaris.common.items.PowerBankItem;
import org.exodusstudio.stellaris.common.registries.utils.BlockItemRegistrySupplier;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.world.ModConfiguredFeatures;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;
import static org.exodusstudio.stellaris.Stellaris.MOD_ID;

@SuppressWarnings("all")
public final class BlocksRegistry {

    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registries.BLOCK);

    /**
     * MISC Blocks
     */

    public static final BlockItemRegistrySupplier METEORITE = blockWithItem("meteorite", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier TITANIUM_ORE = blockWithItem("titanium_ore", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier DEEPSLATE_TITANIUM_ORE = blockWithItem("deepslate_titanium_ore", ofFullCopy(Blocks.DEEPSLATE_IRON_ORE));
    public static final BlockItemRegistrySupplier RAW_TITANIUM_BLOCK = blockWithItem("raw_titanium_block", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier TITANIUM_BLOCK = blockWithItem("titanium_block", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier TITANIUM_SLAB = blockWithItem("titanium_slab", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier TITANIUM_STAIRS = blockWithItem("titanium_stairs", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier TITANIUM_PILLAR = blockWithItem("titanium_pillar", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier TIANIUM_PLATING_BLOCK = blockWithItem("titanium_plating_block", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier TIANIUM_PLATING_SLAB = blockWithItem("titanium_plating_slab", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier TIANIUM_PLATING_STAIRS = blockWithItem("titanium_plating_stairs", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier VERTICAL_TITANIUM_SLAB = blockWithItem("vertical_titanium_slab", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier VERTICAL_TIANIUM_PLATING_SLAB = blockWithItem("vertical_titanium_plating_slab", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier IRON_PLATING_BLOCK = blockWithItem("iron_plating_block", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier IRON_PLATING_SLAB = blockWithItem("iron_plating_slab", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier IRON_PLATING_STAIRS = blockWithItem("iron_plating_stairs", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier IRON_PILLAR = blockWithItem("iron_pillar", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier VERTICAL_IRON_PLATING_SLAB = blockWithItem("vertical_iron_plating_slab", ofFullCopy(Blocks.IRON_BLOCK));

    /**
     * MOON WORLDGEN BLOCKS
     */
    public static final BlockItemRegistrySupplier MOON_SAND = blockWithItem("moon_sand", ofFullCopy(Blocks.SAND).strength(0.5f));
    public static final BlockItemRegistrySupplier MOON_STONE = blockWithItem("moon_stone", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_COBBLESTONE = blockWithItem("moon_cobblestone", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_DEEPSLATE = blockWithItem("moon_deepslate", ofFullCopy(Blocks.DEEPSLATE));
    public static final BlockItemRegistrySupplier MOON_STONE_DUST = blockWithItem("moon_stone_dust", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_STONE_BRICKS = blockWithItem("moon_stone_bricks", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_STONE_BRICK_SLAB = blockWithItem("moon_stone_brick_slab", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_STONE_BRICK_STAIRS = blockWithItem("moon_stone_brick_stairs", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_CRACKED_STONE_BRICKS = blockWithItem("moon_cracked_stone_bricks", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_STONE_PILLAR = blockWithItem("moon_stone_pillar", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_STONE_SLAB = blockWithItem("moon_stone_slab", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_STONE_STAIRS = blockWithItem("moon_stone_stairs", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier VERTICAL_MOON_STONE_SLAB = blockWithItem("vertical_moon_stone_slab", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier POLISHED_MOON_STONE = blockWithItem("polished_moon_stone", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_POLISHED_STONE_BRICK_SLAB = blockWithItem("moon_polished_stone_brick_slab", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_POLISHED_STONE_BRICK_STAIRS = blockWithItem("moon_polished_stone_brick_stairs", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier CHISELED_MOON_STONE = blockWithItem("chiseled_moon_stone", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_PILLAR = blockWithItem("moon_pillar", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_TITANIUM_ORE = blockWithItem("moon_titanium_ore", ofFullCopy(Blocks.IRON_ORE));
    public static final BlockItemRegistrySupplier MOON_DESH_ORE = blockWithItem("moon_desh_ore", ofFullCopy(Blocks.IRON_ORE));
    public static final BlockItemRegistrySupplier MOON_STONE_IRON_ORE = blockWithItem("moon_stone_iron_ore", ofFullCopy(Blocks.IRON_ORE));

    public static final BlockItemRegistrySupplier DESH_BLOCK = blockWithItem("desh_block", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier RAW_DESH_BLOCK = blockWithItem("raw_desh_block", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier DESH_PLATING_BLOCK = blockWithItem("desh_plating_block", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier DESH_PLATING_SLAB = blockWithItem("desh_plating_slab", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier DESH_PLATING_STAIRS = blockWithItem("desh_plating_stairs", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier DESH_PILLAR = blockWithItem("desh_pillar", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier VERTICAL_DESH_SLAB = blockWithItem("vertical_desh_slab", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier VERTICAL_DESH_PLATING_SLAB = blockWithItem("vertical_desh_plating_slab", ofFullCopy(Blocks.IRON_BLOCK));

    public static final BlockItemRegistrySupplier LUNAR_STONED_WOOD_LOG = blockWithItem("lunar_stoned_wood_log", ofFullCopy(Blocks.STONE), RotatedPillarBlock::new);

    public static final RegistrySupplier<Block> MOON_VINES = block("moon_vines", BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().noCollision().lightLevel(CaveVines.emission(14)).instabreak().sound(SoundType.CAVE_VINES).pushReaction(PushReaction.DESTROY), MoonVine::new);
    public static final RegistrySupplier<Block> MOON_VINES_PLANT = block("moon_vines_plant", BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().lightLevel(CaveVines.emission(14)).instabreak().sound(SoundType.CAVE_VINES).pushReaction(PushReaction.DESTROY), MoonVinesPlant::new);


    // LUNAR FOREST
    public static final BlockItemRegistrySupplier LUNAR_DIRT = blockWithItem("lunar_dirt", ofFullCopy(Blocks.DIRT));
    public static final BlockItemRegistrySupplier LUNAR_GRASS = blockWithItem("lunar_grass", ofFullCopy(Blocks.GRASS_BLOCK).lightLevel((s) -> 8));
    public static final BlockItemRegistrySupplier LUNAR_LOG = blockWithItem("lunar_log", ofFullCopy(Blocks.OAK_LOG), RotatedPillarBlock::new);
    public static final BlockItemRegistrySupplier LUNAR_WOOD = blockWithItem("lunar_wood", ofFullCopy(Blocks.OAK_WOOD), RotatedPillarBlock::new);
    public static final BlockItemRegistrySupplier STRIPPED_LUNAR_LOG = blockWithItem("stripped_lunar_log", ofFullCopy(Blocks.STRIPPED_OAK_LOG), RotatedPillarBlock::new);
    public static final BlockItemRegistrySupplier STRIPPED_LUNAR_WOOD = blockWithItem("stripped_lunar_wood", ofFullCopy(Blocks.STRIPPED_OAK_WOOD), RotatedPillarBlock::new);

    public static final BlockItemRegistrySupplier LUNAR_PLANKS = blockWithItem("lunar_planks", ofFullCopy(Blocks.OAK_PLANKS));
    public static final BlockItemRegistrySupplier LUNAR_STAIRS = blockWithItem("lunar_stairs", ofFullCopy(Blocks.OAK_STAIRS), p -> new StairBlock(LUNAR_PLANKS.block().get().defaultBlockState(), p));
    public static final BlockItemRegistrySupplier LUNAR_SLAB = blockWithItem("lunar_slab", ofFullCopy(Blocks.OAK_SLAB), SlabBlock::new);

    public static final BlockItemRegistrySupplier LUNAR_FENCE = blockWithItem("lunar_fence", ofFullCopy(Blocks.OAK_FENCE), FenceBlock::new);
    public static final BlockItemRegistrySupplier LUNAR_FENCE_GATE = blockWithItem("lunar_fence_gate", ofFullCopy(Blocks.OAK_FENCE_GATE), p -> new FenceGateBlock(WoodTypesRegister.LUNAR_WOOD_TYPE, p));

    public static final BlockItemRegistrySupplier LUNAR_DOOR = blockWithItem("lunar_door", ofFullCopy(Blocks.OAK_DOOR), p -> new DoorBlock(WoodTypesRegister.LUNAR, p));
    public static final BlockItemRegistrySupplier LUNAR_TRAPDOOR = blockWithItem("lunar_trapdoor", ofFullCopy(Blocks.OAK_TRAPDOOR), p -> new TrapDoorBlock(WoodTypesRegister.LUNAR, p));

    public static final BlockItemRegistrySupplier LUNAR_PRESSURE_PLATE = blockWithItem("lunar_pressure_plate", ofFullCopy(Blocks.OAK_PRESSURE_PLATE), p -> new PressurePlateBlock(WoodTypesRegister.LUNAR, p));
    public static final BlockItemRegistrySupplier LUNAR_BUTTON = blockWithItem("lunar_button", ofFullCopy(Blocks.OAK_BUTTON), p -> new ButtonBlock(WoodTypesRegister.LUNAR, 30, p));

    public static final BlockItemRegistrySupplier LUNAR_LEAVES = blockWithItem("lunar_leaves", ofFullCopy(Blocks.OAK_LEAVES), p -> new TintedParticleLeavesBlock(0.01F, p.mapColor(MapColor.PLANT).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(BlocksRegistry::ocelotOrParrot).isSuffocating(BlocksRegistry::never).isViewBlocking(BlocksRegistry::never).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor(BlocksRegistry::never).lightLevel((s) -> 8)));
    public static final BlockItemRegistrySupplier LUNAR_SAPLING = blockWithItem("lunar_sapling", ofFullCopy(Blocks.OAK_SAPLING), p -> new LunarSaplingBlock(new TreeGrower("lunar", Optional.empty(), Optional.of(ModConfiguredFeatures.LUNAR_TREE), Optional.empty()), p));

    public static final RegistrySupplier<Block> LUNAR_SIGN = block("lunar_sign", ofFullCopy(Blocks.OAK_SIGN), p -> new ModStandingSignBlock(WoodTypesRegister.LUNAR_WOOD_TYPE, p));
    public static final RegistrySupplier<Block> LUNAR_WALL_SIGN = block("lunar_wall_sign", ofFullCopy(Blocks.OAK_WALL_SIGN), p -> new ModWallSignBlock(WoodTypesRegister.LUNAR_WOOD_TYPE, p));
    public static final RegistrySupplier<Block> LUNAR_HANGING_SIGN = block("lunar_hanging_sign", ofFullCopy(Blocks.OAK_HANGING_SIGN), p -> new ModCeilingHangingSignBlock(WoodTypesRegister.LUNAR_WOOD_TYPE, p));
    public static final RegistrySupplier<Block> LUNAR_WALL_HANGING_SIGN = block("lunar_wall_hanging_sign", ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN), p -> new ModWallHangingSignBlock(WoodTypesRegister.LUNAR_WOOD_TYPE, p));


    public static final BlockItemRegistrySupplier ICED_MAGMA_BLOCK = blockWithItem("iced_magma_block", ofFullCopy(Blocks.MAGMA_BLOCK), IcedMagmaBlock::new);

    public static final BlockItemRegistrySupplier PACKED_ICE_BRICKS = blockWithItem("packed_ice_bricks", ofFullCopy(Blocks.PACKED_ICE));
    public static final BlockItemRegistrySupplier PACKED_ICE_PILLAR = blockWithItem("packed_ice_pillar", ofFullCopy(Blocks.PACKED_ICE), RotatedPillarBlock::new);
    public static final BlockItemRegistrySupplier POLISHED_PACKED_ICE = blockWithItem("polished_packed_ice", ofFullCopy(Blocks.PACKED_ICE));

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
    public static final BlockItemRegistrySupplier DIESEL_GENERATOR = blockWithItem("diesel_generator", BlockBehaviour.Properties.of(), DieselGeneratorBlock::new);

    // FOOD PROCESSING
    public static final BlockItemRegistrySupplier VACUUMATOR = blockWithItem("vacuumator", BlockBehaviour.Properties.of(), VacuumatorBlock::new);

    // POWER STORAGE
    public static final BlockItemRegistrySupplier POWER_BANK_T1 = blockWithCustomItem("power_bank_t1", BlockBehaviour.Properties.of(), (p) -> new PowerBankBlock(p, (short) 1), new Item.Properties(), PowerBankItem::new);

    // CABLES/PIPES/FLUIDS
    public static final BlockItemRegistrySupplier CABLE_T1 = blockWithItem("cable_t1", BlockBehaviour.Properties.of(), (p) -> new CableBlock(p, 20));
    public static final BlockItemRegistrySupplier T1_PIPE = blockWithItem("pipe_t1", BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_CHAIN), (p) -> new PipeBlock(p, 1000, 250, 250));
    public static final BlockItemRegistrySupplier FLUID_TANK_T1 = blockWithCustomItem("fluid_tank_t1", BlockBehaviour.Properties.of(), (p) -> new FluidTankBlock(p, 5000), new Item.Properties(), BlockItem::new);

    // TECH
    public static final BlockItemRegistrySupplier ELECTROLYZER = blockWithCustomItem("electrolyzer", BlockBehaviour.Properties.of(), ElectrolyzerBlock::new, new Item.Properties(), BlockItem::new);
    public static final BlockItemRegistrySupplier GRAVITY_MANIPULATOR = blockWithItem("gravity_manipulator", BlockBehaviour.Properties.of(), GravityManipulatorBlock::new);
    public static final BlockItemRegistrySupplier PUMPJACK = blockWithCustomItem("pumpjack", BlockBehaviour.Properties.of(), PumpjackBlock::new, new Item.Properties(), BlockItem::new);
    public static final BlockItemRegistrySupplier FUEL_REFINERY = blockWithCustomItem("fuel_refinery", BlockBehaviour.Properties.of(), FuelRefineryBlock::new, new Item.Properties(), BlockItem::new);

    // OXYGEN
    public static final BlockItemRegistrySupplier OXYGEN_DISTRIBUTOR = blockWithItem("oxygen_distributor", BlockBehaviour.Properties.of(), OxygenDistributorBlock::new);
    public static final BlockItemRegistrySupplier OXYGEN_PROPAGATOR = blockWithItem("oxygen_propagator", BlockBehaviour.Properties.of(), OxygenPropagatorBlock::new);

    // ROCKET
    public static final BlockItemRegistrySupplier ENGINEERING_STATION = blockWithCustomItem("engineering_station", BlockBehaviour.Properties.of(), EngineeringStationBlock::new, new Item.Properties(), BlockItem::new);
    public static final BlockItemRegistrySupplier ROCKET_LAUNCH_PAD = blockWithCustomItem("rocket_launch_pad", BlockBehaviour.Properties.of(), RocketLaunchPadBlock::new, new Item.Properties(), BlockItem::new);

    /**
     * Fluids
     */
    public static final RegistrySupplier<ArchitecturyLiquidBlock> HYDROGEN = block("hydrogen_block", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.HYDROGEN_STILL, p));
    public static final RegistrySupplier<ArchitecturyLiquidBlock> OIL = block("oil", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.OIL_STILL, p));
    public static final RegistrySupplier<ArchitecturyLiquidBlock> OXYGEN = block("oxygen_block", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.OXYGEN_STILL, p));
    public static final RegistrySupplier<ArchitecturyLiquidBlock> FUEL = block("fuel_block", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.FUEL_STILL, p));
    public static final RegistrySupplier<ArchitecturyLiquidBlock> DIESEL = block("diesel", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.DIESEL_STILL, p));
    public static final RegistrySupplier<ArchitecturyLiquidBlock> BLUE_LIQUID = block("blue_liquid", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.BLUE_LIQUID_STILL, p.lightLevel((e) -> 8)));


    /**
     * Decoration
     */
    public static final BlockItemRegistrySupplier FLAG = blockWithItem("flag", BlockBehaviour.Properties.of(), FlagBlock::new);


    public static <B extends Block> @NotNull RegistrySupplier<B> block(String name,
                                                                           BlockBehaviour.Properties properties,
                                                                           Function<BlockBehaviour.Properties, B> blockFunc) {
        Identifier id = IdentifierUtils.id(name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        return BLOCKS.register(id, () -> blockFunc.apply(properties.setId(key)));
    }

    public static @NotNull RegistrySupplier<Block> block(String name, BlockBehaviour.Properties properties) {
        return block(name, properties, Block::new);
    }

    public static @NotNull BlockItemRegistrySupplier blockWithItem(String name, BlockBehaviour.Properties properties) {
        RegistrySupplier<Block> block = block(name, properties);
        RegistrySupplier<BlockItem> item = ItemsRegistry.item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_BLOCKS), p -> new BlockItem(block.get(), p));
        return new BlockItemRegistrySupplier(block, item);
    }


    public static <B extends Block> @NotNull BlockItemRegistrySupplier blockWithItem(String name, BlockBehaviour.Properties properties,
                                                                                     Function<BlockBehaviour.Properties, B> blockFunc) {
        RegistrySupplier<B> block = block(name, properties, blockFunc);
        RegistrySupplier<BlockItem> item = ItemsRegistry.item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_BLOCKS), p -> new BlockItem(block.get(), p));
        return new BlockItemRegistrySupplier(block, item);
    }

    public static <B extends Block> @NotNull BlockItemRegistrySupplier blockWithItem(String name, BlockBehaviour.Properties properties,
                                                                                     Function<BlockBehaviour.Properties, B> blockFunc,
                                                                                     Item.Properties itemProperties) {
        RegistrySupplier<B> block = block(name, properties, blockFunc);
        RegistrySupplier<BlockItem> item = ItemsRegistry.item(name, itemProperties.arch$tab(CreativeTabsRegistry.STELLARIS_BLOCKS), p -> new BlockItem(block.get(), p));
        return new BlockItemRegistrySupplier(block, item);
    }

    public static <B extends Block, I extends BlockItem> @NotNull BlockItemRegistrySupplier blockWithCustomItem(String name, BlockBehaviour.Properties properties,
                                                                                                                Function<BlockBehaviour.Properties, B> blockFunc, Item.Properties itemProperties,
                                                                                                                BiFunction<B, Item.Properties, I> itemSupplier) {
        RegistrySupplier<B> block = block(name, properties, blockFunc);
        RegistrySupplier<BlockItem> item = ItemsRegistry.item(name, itemProperties.arch$tab(CreativeTabsRegistry.STELLARIS_BLOCKS), p -> itemSupplier.apply(block.get(), p));
        return new BlockItemRegistrySupplier(block, item);
    }

    // Utils from vanilla blocks class
    private static Boolean ocelotOrParrot(BlockState state, BlockGetter level, BlockPos pos, EntityType<?> entity) {
        return entity == EntityType.OCELOT || entity == EntityType.PARROT;
    }

    private static boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos) {
        return false;
    }
}
