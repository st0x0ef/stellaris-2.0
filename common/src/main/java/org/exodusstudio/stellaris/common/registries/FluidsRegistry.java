package org.exodusstudio.stellaris.common.registries;

import dev.architectury.core.fluid.ArchitecturyFlowingFluid;
import dev.architectury.core.fluid.ArchitecturyFluidAttributes;
import dev.architectury.core.fluid.SimpleArchitecturyFluidAttributes;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FluidsRegistry {

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(Stellaris.MOD_ID, Registries.FLUID);

    public static final List<ArchitecturyFluidAttributes> FLUIDS_INFOS = new ArrayList<>();

    /** HYDROGEN FLUIDS */
    public static final ArchitecturyFluidAttributes HYDROGEN_ATTRIBUTES = SimpleArchitecturyFluidAttributes.ofSupplier(() -> FluidsRegistry.HYDROGEN_FLOWING, () -> FluidsRegistry.HYDROGEN_STILL)
            .blockSupplier(() -> BlocksRegistry.HYDROGEN)
            .bucketItem(() -> Optional.of(ItemsRegistry.HYDROGEN_BUCKET.get()))
            .slopeFindDistance(4)
            .dropOff(1)
            .tickDelay(8)
            .explosionResistance(100.0F)
            .lighterThanAir(true)
            .convertToSource(false)

            .overlayTexture(IdentifierUtils.id("block/fluids/hydrogen_overlay"))
            .sourceTexture(IdentifierUtils.id("block/fluids/hydrogen_still"))
            .flowingTexture(IdentifierUtils.id("block/fluids/hydrogen_flow"));

    public static final RegistrySupplier<FlowingFluid> HYDROGEN_FLOWING = FLUIDS.register("flowing_hydrogen", () -> new ArchitecturyFlowingFluid.Flowing(HYDROGEN_ATTRIBUTES));
    public static final RegistrySupplier<FlowingFluid> HYDROGEN_STILL = FLUIDS.register("hydrogen", () -> new ArchitecturyFlowingFluid.Source(HYDROGEN_ATTRIBUTES));

    /** OXYGEN FLUIDS **/
    public static final ArchitecturyFluidAttributes OXYGEN_ATTRIBUTES = SimpleArchitecturyFluidAttributes.ofSupplier(() -> FluidsRegistry.OXYGEN_FLOWING, () -> FluidsRegistry.OXYGEN_STILL)
            .blockSupplier(() -> BlocksRegistry.OXYGEN)
            .slopeFindDistance(4)
            .dropOff(1)
            .tickDelay(8)
            .explosionResistance(100)
            .lighterThanAir(true)
            .convertToSource(false)
            .overlayTexture(IdentifierUtils.id("block/fluids/oxygen_overlay"))
            .sourceTexture(IdentifierUtils.id("block/fluids/oxygen_still"))
            .flowingTexture(IdentifierUtils.id("block/fluids/oxygen_flow"));

    public static final RegistrySupplier<FlowingFluid> OXYGEN_FLOWING = FLUIDS.register("flowing_oxygen", () -> new ArchitecturyFlowingFluid.Flowing(OXYGEN_ATTRIBUTES));
    public static final RegistrySupplier<FlowingFluid> OXYGEN_STILL = FLUIDS.register("oxygen", () -> new ArchitecturyFlowingFluid.Source(OXYGEN_ATTRIBUTES));


    public static final ArchitecturyFluidAttributes FUEL_ATTRIBUTES = SimpleArchitecturyFluidAttributes.ofSupplier(() -> FluidsRegistry.FUEL_FLOWING, () -> FluidsRegistry.FUEL_STILL)
            .blockSupplier(() -> BlocksRegistry.FUEL)
            .bucketItem(() -> Optional.of(ItemsRegistry.FUEL_BUCKET.get()))
            .slopeFindDistance(4)
            .dropOff(1)
            .tickDelay(8)
            .viscosity(1500)
            .explosionResistance(100)
            .lighterThanAir(false)
            .convertToSource(false)
            .overlayTexture(IdentifierUtils.id("block/fluids/fuel_overlay"))
            .sourceTexture(IdentifierUtils.id("block/fluids/fuel_still"))
            .flowingTexture(IdentifierUtils.id("block/fluids/fuel_flow"));

    public static final RegistrySupplier<FlowingFluid> FUEL_FLOWING = FLUIDS.register("flowing_fuel", () -> new ArchitecturyFlowingFluid.Flowing(FUEL_ATTRIBUTES));
    public static final RegistrySupplier<FlowingFluid> FUEL_STILL = FLUIDS.register("fuel", () -> new ArchitecturyFlowingFluid.Source(FUEL_ATTRIBUTES));

    /** OIL FLUIDS **/
    public static final ArchitecturyFluidAttributes OIL_ATTRIBUTES = SimpleArchitecturyFluidAttributes.ofSupplier(() -> FluidsRegistry.FLOWING_OIL, () -> FluidsRegistry.OIL_STILL)
            .blockSupplier(() -> BlocksRegistry.OIL)
            .bucketItem(() -> Optional.of(ItemsRegistry.OIL_BUCKET.get()))
            .slopeFindDistance(4)
            .dropOff(1)
            .tickDelay(8)
            .explosionResistance(100.0F)
            .convertToSource(true)
            .sourceTexture(IdentifierUtils.id("block/fluids/oil_still"))
            .flowingTexture(IdentifierUtils.id("block/fluids/oil_flow"));

    public static final RegistrySupplier<FlowingFluid> FLOWING_OIL = FLUIDS.register("flowing_oil", () -> new ArchitecturyFlowingFluid.Flowing(OIL_ATTRIBUTES));
    public static final RegistrySupplier<FlowingFluid> OIL_STILL = FLUIDS.register("oil", () -> new ArchitecturyFlowingFluid.Source(OIL_ATTRIBUTES));

    public static final ArchitecturyFluidAttributes DIESEL_ATTRIBUTES = SimpleArchitecturyFluidAttributes.ofSupplier(() -> FluidsRegistry.FLOWING_DIESEL, () -> FluidsRegistry.DIESEL_STILL)
            .blockSupplier(() -> BlocksRegistry.DIESEL)
            .bucketItem(() -> Optional.of(ItemsRegistry.DIESEL_BUCKET.get()))
            .slopeFindDistance(4)
            .dropOff(1)
            .tickDelay(8)
            .explosionResistance(100.0F)
            .luminosity(3)
            .convertToSource(false)
            .sourceTexture(IdentifierUtils.id("block/fluids/diesel_still"))
            .flowingTexture(IdentifierUtils.id("block/fluids/diesel_flow"));


    public static final RegistrySupplier<FlowingFluid> FLOWING_DIESEL = FLUIDS.register("flowing_diesel", () -> new ArchitecturyFlowingFluid.Flowing(DIESEL_ATTRIBUTES));
    public static final RegistrySupplier<FlowingFluid> DIESEL_STILL = FLUIDS.register("diesel", () -> new ArchitecturyFlowingFluid.Source(DIESEL_ATTRIBUTES));

    public static void init() {
        FLUIDS.register();
        FLUIDS_INFOS.add(OXYGEN_ATTRIBUTES);
        FLUIDS_INFOS.add(HYDROGEN_ATTRIBUTES);
        FLUIDS_INFOS.add(FUEL_ATTRIBUTES);

    }

}

