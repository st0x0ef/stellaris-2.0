package org.exodusstudio.stellaris.common.registries;

import dev.architectury.core.block.ArchitecturyLiquidBlock;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.exodusstudio.stellaris.common.blocks.*;
import org.exodusstudio.stellaris.common.blocks.PipeBlock;
import org.exodusstudio.stellaris.common.blocks.SpaceFarmBlock;
import org.exodusstudio.stellaris.common.items.PowerBankItem;
import org.exodusstudio.stellaris.common.items.TooltipBlockItem;
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
     * MISC BLOCKS
     */

    public static final BlockItemRegistrySupplier METEORITE = blockWithItem("meteorite", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier TITANIUM_ORE = blockWithItem("titanium_ore", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier DEEPSLATE_TITANIUM_ORE = blockWithItem("deepslate_titanium_ore", ofFullCopy(Blocks.DEEPSLATE_IRON_ORE));
    public static final BlockItemRegistrySupplier RAW_TITANIUM_BLOCK = blockWithItem("raw_titanium_block", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier TITANIUM_BLOCK = blockWithItem("titanium_block", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier TITANIUM_SLAB = blockWithItem("titanium_slab", ofFullCopy(Blocks.IRON_BLOCK), SlabBlock::new);
    public static final BlockItemRegistrySupplier TITANIUM_STAIRS = blockWithItem("titanium_stairs", ofFullCopy(Blocks.COBBLESTONE_STAIRS), p -> new StairBlock(TITANIUM_BLOCK.block().get().defaultBlockState(), p));
    public static final BlockItemRegistrySupplier TITANIUM_PILLAR = blockWithItem("titanium_pillar", ofFullCopy(Blocks.IRON_BLOCK), RotatedPillarBlock::new);
    public static final BlockItemRegistrySupplier TITANIUM_PLATING_BLOCK = blockWithItem("titanium_plating_block", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier TITANIUM_PLATING_SLAB = blockWithItem("titanium_plating_slab", ofFullCopy(Blocks.IRON_BLOCK), SlabBlock::new);
    public static final BlockItemRegistrySupplier TITANIUM_PLATING_STAIRS = blockWithItem("titanium_plating_stairs", ofFullCopy(Blocks.COBBLESTONE_STAIRS), p -> new StairBlock(TITANIUM_BLOCK.block().get().defaultBlockState(), p));
    public static final BlockItemRegistrySupplier VERTICAL_TITANIUM_SLAB = blockWithItem("vertical_titanium_slab", ofFullCopy(Blocks.IRON_BLOCK), p -> new VerticalSlab(p));
    public static final BlockItemRegistrySupplier VERTICAL_TIANIUM_PLATING_SLAB = blockWithItem("vertical_titanium_plating_slab", ofFullCopy(Blocks.IRON_BLOCK), p -> new VerticalSlab(p));
    public static final BlockItemRegistrySupplier IRON_PLATING_BLOCK = blockWithItem("iron_plating_block", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier IRON_PLATING_SLAB = blockWithItem("iron_plating_slab", ofFullCopy(Blocks.IRON_BLOCK), SlabBlock::new);
    public static final BlockItemRegistrySupplier IRON_PLATING_STAIRS = blockWithItem("iron_plating_stairs", ofFullCopy(Blocks.COBBLESTONE_STAIRS), p -> new StairBlock(TITANIUM_BLOCK.block().get().defaultBlockState(), p));
    public static final BlockItemRegistrySupplier IRON_PILLAR = blockWithItem("iron_pillar", ofFullCopy(Blocks.IRON_BLOCK), RotatedPillarBlock::new);
    public static final BlockItemRegistrySupplier VERTICAL_IRON_PLATING_SLAB = blockWithItem("vertical_iron_plating_slab", ofFullCopy(Blocks.IRON_BLOCK), p -> new VerticalSlab(p));

    /**
     * MOON WORLDGEN BLOCKS
     */
    public static final BlockItemRegistrySupplier MOON_SAND = blockWithItem("moon_sand", ofFullCopy(Blocks.SAND).strength(0.5f));
    public static final BlockItemRegistrySupplier MOON_STONE = blockWithItem("moon_stone", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_COBBLESTONE = blockWithItem("moon_cobblestone", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_DEEPSLATE = blockWithItem("moon_deepslate", ofFullCopy(Blocks.DEEPSLATE));
    public static final BlockItemRegistrySupplier MOON_STONE_DUST = blockWithItem("moon_stone_dust", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_STONE_BRICKS = blockWithItem("moon_stone_bricks", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_STONE_BRICK_SLAB = blockWithItem("moon_stone_brick_slab", ofFullCopy(Blocks.STONE), SlabBlock::new);
    public static final BlockItemRegistrySupplier MOON_STONE_BRICK_STAIRS = blockWithItem("moon_stone_brick_stairs", ofFullCopy(Blocks.COBBLESTONE_STAIRS), p -> new StairBlock(MOON_STONE_BRICKS.block().get().defaultBlockState(), p));
    public static final BlockItemRegistrySupplier MOON_CRACKED_STONE_BRICKS = blockWithItem("moon_cracked_stone_bricks", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_STONE_PILLAR = blockWithItem("moon_stone_pillar", ofFullCopy(Blocks.STONE), RotatedPillarBlock::new);
    public static final BlockItemRegistrySupplier MOON_STONE_SLAB = blockWithItem("moon_stone_slab", ofFullCopy(Blocks.COBBLESTONE_SLAB), SlabBlock::new);
    public static final BlockItemRegistrySupplier MOON_STONE_STAIRS = blockWithItem("moon_stone_stairs", ofFullCopy(Blocks.STONE_STAIRS), p -> new StairBlock(TITANIUM_BLOCK.block().get().defaultBlockState(), p));
    public static final BlockItemRegistrySupplier VERTICAL_MOON_STONE_SLAB = blockWithItem("vertical_moon_stone_slab", ofFullCopy(Blocks.STONE_SLAB), p -> new VerticalSlab(p));
    public static final BlockItemRegistrySupplier POLISHED_MOON_STONE = blockWithItem("polished_moon_stone", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_POLISHED_STONE_BRICK_SLAB = blockWithItem("moon_polished_stone_brick_slab", ofFullCopy(Blocks.STONE), SlabBlock::new);
    public static final BlockItemRegistrySupplier MOON_POLISHED_STONE_BRICK_STAIRS = blockWithItem("moon_polished_stone_brick_stairs", ofFullCopy(Blocks.COBBLESTONE_STAIRS), p -> new StairBlock(POLISHED_MOON_STONE.block().get().defaultBlockState(), p));
    public static final BlockItemRegistrySupplier CHISELED_MOON_STONE = blockWithItem("chiseled_moon_stone", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier MOON_PILLAR = blockWithItem("moon_pillar", ofFullCopy(Blocks.STONE), RotatedPillarBlock::new);
    public static final BlockItemRegistrySupplier MOON_TITANIUM_ORE = blockWithItem("moon_titanium_ore", ofFullCopy(Blocks.IRON_ORE));
    public static final BlockItemRegistrySupplier MOON_DESH_ORE = blockWithItem("moon_desh_ore", ofFullCopy(Blocks.IRON_ORE));
    public static final BlockItemRegistrySupplier MOON_STONE_IRON_ORE = blockWithItem("moon_stone_iron_ore", ofFullCopy(Blocks.IRON_ORE));

    public static final BlockItemRegistrySupplier DESH_BLOCK = blockWithItem("desh_block", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier DESH_SLAB = blockWithItem("desh_slab", ofFullCopy(Blocks.IRON_BLOCK), SlabBlock::new);
    public static final BlockItemRegistrySupplier RAW_DESH_BLOCK = blockWithItem("raw_desh_block", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier DESH_PLATING_BLOCK = blockWithItem("desh_plating_block", ofFullCopy(Blocks.IRON_BLOCK));
    public static final BlockItemRegistrySupplier DESH_PLATING_SLAB = blockWithItem("desh_plating_slab", ofFullCopy(Blocks.IRON_BLOCK), SlabBlock::new);
    public static final BlockItemRegistrySupplier DESH_PLATING_STAIRS = blockWithItem("desh_plating_stairs", ofFullCopy(Blocks.COBBLESTONE_STAIRS), p -> new StairBlock(TITANIUM_BLOCK.block().get().defaultBlockState(), p));
    public static final BlockItemRegistrySupplier DESH_PILLAR = blockWithItem("desh_pillar", ofFullCopy(Blocks.IRON_BLOCK), RotatedPillarBlock::new);
    public static final BlockItemRegistrySupplier VERTICAL_DESH_SLAB = blockWithItem("vertical_desh_slab", ofFullCopy(Blocks.IRON_BLOCK), p -> new VerticalSlab(p));
    public static final BlockItemRegistrySupplier VERTICAL_DESH_PLATING_SLAB = blockWithItem("vertical_desh_plating_slab", ofFullCopy(Blocks.IRON_BLOCK), p -> new VerticalSlab(p));

    public static final BlockItemRegistrySupplier LUNAR_STONED_WOOD_LOG = blockWithItem("lunar_stoned_wood_log", ofFullCopy(Blocks.STONE), RotatedPillarBlock::new);

    public static final RegistrySupplier<Block> MOON_VINES = block("moon_vines", BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).randomTicks().noCollision().lightLevel(CaveVines.emission(14)).instabreak().sound(SoundType.CAVE_VINES).pushReaction(PushReaction.DESTROY), MoonVine::new);
    public static final RegistrySupplier<Block> MOON_VINES_PLANT = block("moon_vines_plant", BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).noCollision().lightLevel(CaveVines.emission(14)).instabreak().sound(SoundType.CAVE_VINES).pushReaction(PushReaction.DESTROY), MoonVinesPlant::new);


    // LUNAR FOREST
    public static final BlockItemRegistrySupplier LUNAR_DIRT = blockWithItem("lunar_dirt", ofFullCopy(Blocks.DIRT));
    public static final BlockItemRegistrySupplier LUNAR_GRASS = blockWithItem("lunar_grass", ofFullCopy(Blocks.GRASS_BLOCK).lightLevel((s) -> 9));
    public static final BlockItemRegistrySupplier LUNAR_ROCK = blockWithItem("lunar_rock", ofFullCopy(Blocks.STONE));
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

    public static final BlockItemRegistrySupplier LUNAR_LEAVES = blockWithItem("lunar_leaves", ofFullCopy(Blocks.OAK_LEAVES), p -> new LunarLeavesBlock(p.mapColor(MapColor.COLOR_BLUE).strength(0.2F).randomTicks().sound(SoundType.GRASS).noOcclusion().isValidSpawn(BlocksRegistry::ocelotOrParrot).isSuffocating(BlocksRegistry::never).isViewBlocking(BlocksRegistry::never).ignitedByLava().pushReaction(PushReaction.DESTROY).isRedstoneConductor(BlocksRegistry::never).lightLevel((s) -> 9)));
    public static final BlockItemRegistrySupplier LUNAR_SAPLING = blockWithItem("lunar_sapling", ofFullCopy(Blocks.OAK_SAPLING), p -> new LunarSaplingBlock(new TreeGrower("lunar", Optional.empty(), Optional.of(ModConfiguredFeatures.LUNAR_TREE), Optional.empty()), p));
    public static final BlockItemRegistrySupplier LUNAR_VINES = blockWithItem("lunar_vines", ofFullCopy(Blocks.TWISTING_VINES), p -> new LunarVinesBlock(p.lightLevel(s -> 2)));
    public static final RegistrySupplier<Block> LUNAR_VINES_PLANT = block("lunar_vines_plant", ofFullCopy(Blocks.TWISTING_VINES_PLANT), p -> new LunarVinesPlantBlock(p));

    public static final RegistrySupplier<Block> LUNAR_SIGN = block("lunar_sign", ofFullCopy(Blocks.OAK_SIGN), p -> new ModStandingSignBlock(WoodTypesRegister.LUNAR_WOOD_TYPE, p));
    public static final RegistrySupplier<Block> LUNAR_WALL_SIGN = block("lunar_wall_sign", ofFullCopy(Blocks.OAK_WALL_SIGN), p -> new ModWallSignBlock(WoodTypesRegister.LUNAR_WOOD_TYPE, p));
    public static final RegistrySupplier<Block> LUNAR_HANGING_SIGN = block("lunar_hanging_sign", ofFullCopy(Blocks.OAK_HANGING_SIGN), p -> new ModCeilingHangingSignBlock(WoodTypesRegister.LUNAR_WOOD_TYPE, p));
    public static final RegistrySupplier<Block> LUNAR_WALL_HANGING_SIGN = block("lunar_wall_hanging_sign", ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN), p -> new ModWallHangingSignBlock(WoodTypesRegister.LUNAR_WOOD_TYPE, p));

    /** Coal Torch & Lantern */
    public static final BlockItemRegistrySupplier COAL_TORCH_BLOCK = blockWithItem("coal_torch", BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noCollision().instabreak().sound(SoundType.WOOD), CoalTorchBlock::new);
    public static final RegistrySupplier<Block> WALL_COAL_TORCH_BLOCK = block("wall_coal_torch", BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noCollision().instabreak().sound(SoundType.WOOD), WallCoalTorchBlock::new);
    public static final BlockItemRegistrySupplier COAL_LANTERN_BLOCK = blockWithItem("coal_lantern", BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).noCollision().instabreak().sound(SoundType.WOOD), CoalLanternBlock::new);


    /**
     * Burnt Forest
     **/
    public static final BlockItemRegistrySupplier ASH_STONE = blockWithItem("ash_stone", BlockBehaviour.Properties.ofFullCopy(Blocks.STONE).mapColor(MapColor.COLOR_GRAY).strength(1.5f, 6.0f).sound(SoundType.STONE), Block::new);
    public static final BlockItemRegistrySupplier ASH_LAYER = blockWithItem("ash_layer", BlockBehaviour.Properties.ofFullCopy(Blocks.SNOW).mapColor(MapColor.COLOR_GRAY).strength(0.1f).sound(SoundType.SAND).noOcclusion(), AshLayerBlock::new);



    public static final BlockItemRegistrySupplier ICED_MAGMA_BLOCK = blockWithItem("iced_magma_block", ofFullCopy(Blocks.MAGMA_BLOCK), IcedMagmaBlock::new);

    public static final BlockItemRegistrySupplier PACKED_ICE_BRICKS = blockWithItem("packed_ice_bricks", ofFullCopy(Blocks.PACKED_ICE));
    public static final BlockItemRegistrySupplier PACKED_ICE_PILLAR = blockWithItem("packed_ice_pillar", ofFullCopy(Blocks.PACKED_ICE), RotatedPillarBlock::new);
    public static final BlockItemRegistrySupplier POLISHED_PACKED_ICE = blockWithItem("polished_packed_ice", ofFullCopy(Blocks.PACKED_ICE));


    // ASTRUM BLOCKS
    public static final BlockItemRegistrySupplier CASUS_ASTRUM_STONE = blockWithItem("casus_astrum_stone", ofFullCopy(Blocks.STONE));
    public static final BlockItemRegistrySupplier CASUS_ASTRUM_STONE_SLAB = blockWithItem("casus_astrum_stone_slab", ofFullCopy(Blocks.STONE_SLAB), SlabBlock::new);
    public static final BlockItemRegistrySupplier CASUS_ASTRUM_STONE_STAIRS = blockWithItem("casus_astrum_stone_stairs", ofFullCopy(Blocks.STONE_STAIRS), p -> new StairBlock(CASUS_ASTRUM_STONE.block().get().defaultBlockState(), p));
    public static final BlockItemRegistrySupplier ASTRUM_VITREUS_BLOCK = blockWithItem("astrum_vitreus_block", ofFullCopy(Blocks.AMETHYST_BLOCK));
    public static final BlockItemRegistrySupplier ASTRUM_VITREUS_CLUSTER = blockWithItem("astrum_vitreus_cluster", ofFullCopy(Blocks.AMETHYST_CLUSTER), p -> new AmethystClusterBlock(7.0F, 10.0F, p));
    public static final BlockItemRegistrySupplier CASUS_ASTRUM_DIRT = blockWithItem("casus_astrum_dirt", ofFullCopy(Blocks.DIRT));
    public static final BlockItemRegistrySupplier CASUS_ASTRUM_GRASS = blockWithItem("casus_astrum_grass", ofFullCopy(Blocks.GRASS_BLOCK));


    /**
     * MACHINES BLOCKS
     */

    // ENERGY GENERATORS
    public static final BlockItemRegistrySupplier SOLAR_PANEL = blockWithItem("solar_panel", BlockBehaviour.Properties.of().strength(3.0F).noOcclusion(), SolarPanelBlock::new);
    public static final BlockItemRegistrySupplier COAL_GENERATOR = blockWithItem("coal_generator", BlockBehaviour.Properties.of().strength(3.0F), CoalGeneratorBlock::new);
    public static final BlockItemRegistrySupplier DIESEL_GENERATOR = blockWithItem("diesel_generator", BlockBehaviour.Properties.of().strength(3.0F), DieselGeneratorBlock::new);

    // FOOD PROCESSING
    public static final BlockItemRegistrySupplier VACUUMATOR = blockWithItem("vacuumator", BlockBehaviour.Properties.of().strength(3.0F), VacuumatorBlock::new);

    // STORAGE
    public static final BlockItemRegistrySupplier POWER_BANK_T1 = blockWithCustomItem("power_bank_t1", BlockBehaviour.Properties.of().strength(3.0F), (p) -> new PowerBankBlock(p, (short) 1), new Item.Properties(), PowerBankItem::new);
    public static final BlockItemRegistrySupplier FLUID_TANK_T1 = blockWithItem("fluid_tank_t1", BlockBehaviour.Properties.of().strength(3.0F), (p) -> new FluidTankBlock(p, 5000), new Item.Properties()); // TODO : item should keep it fluid inside

    // CABLES/PIPES
    public static final BlockItemRegistrySupplier CABLE_T1 = blockWithItem("cable_t1", BlockBehaviour.Properties.of().strength(1.0F).noOcclusion(), (p) -> new CableBlock(p, 20));
    public static final BlockItemRegistrySupplier PIPE_T1 = blockWithItem("pipe_t1", BlockBehaviour.Properties.of().strength(1.0F).noOcclusion(), (p) -> new PipeBlock(p, 1000, 250, 250));

    // TECH
    public static final BlockItemRegistrySupplier ELECTROLYZER = blockWithItem("electrolyzer", BlockBehaviour.Properties.of().strength(3.0F), ElectrolyzerBlock::new, new Item.Properties());
    public static final BlockItemRegistrySupplier GRAVITY_MANIPULATOR = blockWithItem("gravity_manipulator", BlockBehaviour.Properties.of().strength(3.0F).noOcclusion(), GravityManipulatorBlock::new);
    public static final BlockItemRegistrySupplier PUMPJACK = blockWithItem(
            "pumpjack",
            BlockBehaviour.Properties.of().strength(3.0F).noOcclusion().isSuffocating(BlocksRegistry::never).isViewBlocking(BlocksRegistry::never),
            PumpjackBlock::new,
            new Item.Properties()
    );
    public static final RegistrySupplier<Block> PUMPJACK_PROXY = block(
            "pumpjack_proxy",
            BlockBehaviour.Properties.of().strength(3.0F).noOcclusion().isSuffocating(BlocksRegistry::never).isViewBlocking(BlocksRegistry::never),
            PumpjackProxyBlock::new
    );
    public static final BlockItemRegistrySupplier FUEL_REFINERY = blockWithItem("fuel_refinery", BlockBehaviour.Properties.of().strength(3.0F), FuelRefineryBlock::new, new Item.Properties());
    public static final BlockItemRegistrySupplier WATER_PUMP = blockWithItem("water_pump", BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(4.5F, 3F).sound(SoundType.METAL).noOcclusion(), WaterPumpBlock::new);

    // OXYGEN
    public static final BlockItemRegistrySupplier OXYGEN_DISTRIBUTOR = blockWithItem("oxygen_distributor", BlockBehaviour.Properties.of().strength(3.0F), OxygenDistributorBlock::new);
    public static final BlockItemRegistrySupplier OXYGEN_PROPAGATOR = blockWithItem("oxygen_propagator", BlockBehaviour.Properties.of().strength(3.0F), OxygenPropagatorBlock::new);

    // ROCKET
    public static final BlockItemRegistrySupplier ENGINEERING_STATION = blockWithCustomItem("engineering_station", BlockBehaviour.Properties.of().strength(3.0F).noOcclusion(), EngineeringStationBlock::new, new Item.Properties(), BlockItem::new);
    public static final BlockItemRegistrySupplier ROCKET_LAUNCH_PAD = blockWithCustomItem("rocket_launch_pad", BlockBehaviour.Properties.of().strength(3.0F).noOcclusion(), RocketLaunchPadBlock::new, new Item.Properties(), BlockItem::new);
    public static final RegistrySupplier<Block> ROCKET_LAUNCH_PAD_PROXY = block(
            "rocket_launch_pad_proxy",
            BlockBehaviour.Properties.of().strength(3.0F)
                    .noOcclusion()
                    .isSuffocating(BlocksRegistry::never)
                    .isViewBlocking(BlocksRegistry::never),
            RocketLaunchPadProxyBlock::new
    );
    public static final BlockItemRegistrySupplier ANTENNA = blockWithCustomItem("antenna", BlockBehaviour.Properties.of().strength(3.0F), AntennaBlock::new, new Item.Properties(),
            (b, p) -> new TooltipBlockItem(b, p).addTooltip(AntennaBlock.TOOLTIP));
    public static final BlockItemRegistrySupplier CARGO_UNLOADER = blockWithItem("cargo_unloader", BlockBehaviour.Properties.of().strength(3.0F).noOcclusion(), CargoUnloaderBlock::new);


    // LORE
    public static final BlockItemRegistrySupplier LABORATORY = blockWithItem("laboratory", BlockBehaviour.Properties.of().strength(3.0F).noOcclusion(), LaboratoryBlock::new, new Item.Properties());

    public static final BlockItemRegistrySupplier SPACE_FARM = blockWithItem("space_farm", BlockBehaviour.Properties.of().strength(3.0F).noOcclusion(), SpaceFarmBlock::new, new Item.Properties());


    /**
     * Fluids
     */
    public static final RegistrySupplier<ArchitecturyLiquidBlock> HYDROGEN = block("hydrogen_block", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.HYDROGEN_STILL, p.liquid()));
    public static final RegistrySupplier<ArchitecturyLiquidBlock> OIL = block("oil", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.OIL_STILL, p.liquid()));
    public static final RegistrySupplier<ArchitecturyLiquidBlock> OXYGEN = block("oxygen_block", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.OXYGEN_STILL, p.liquid()));
    public static final RegistrySupplier<ArchitecturyLiquidBlock> FUEL = block("fuel_block", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.FUEL_STILL, p.liquid()));
    public static final RegistrySupplier<ArchitecturyLiquidBlock> DIESEL = block("diesel", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.DIESEL_STILL, p.liquid()));
    public static final RegistrySupplier<ArchitecturyLiquidBlock> BLUE_LIQUID = block("blue_liquid", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.BLUE_LIQUID_STILL, p.liquid().lightLevel((e) -> 8)));
    public static final RegistrySupplier<ArchitecturyLiquidBlock> ASTRUM_LIQUIDUS = block("astrum_liquidus", BlockBehaviour.Properties.ofFullCopy(Blocks.WATER).lightLevel((blockStatex) -> 15), (p) -> new ArchitecturyLiquidBlock(FluidsRegistry.ASTRUM_LIQUIDUS_STILL, p.liquid().lightLevel((e) -> 15)));


    /**
     * Decoration
     */
    public static final BlockItemRegistrySupplier FLAG = blockWithItem(
            "flag",
            BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .isSuffocating(BlocksRegistry::never)
                    .isViewBlocking(BlocksRegistry::never)
                    .strength(2.5F),
            FlagBlock::new
    );

    public static final RegistrySupplier<Block> FLAG_PROXY = block(
            "flag_proxy",
            BlockBehaviour.Properties.of()
                    .noOcclusion()
                    .isSuffocating(BlocksRegistry::never)
                    .isViewBlocking(BlocksRegistry::never)
                    .strength(2.5F),
            FlagProxyBlock::new
    );

    public static final BlockItemRegistrySupplier EARTH_GLOBE = blockWithItem("earth_globe",
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2.5F).sound(SoundType.STONE).noOcclusion(),
            GlobeBlock::new);
    public static final BlockItemRegistrySupplier MOON_GLOBE = blockWithItem("moon_globe",
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2.5F).sound(SoundType.STONE).noOcclusion(),
            GlobeBlock::new);


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
