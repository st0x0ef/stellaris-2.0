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
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

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
            .overlayTexture(ResourceLocationUtils.id("block/fluids/hydrogen_overlay"))
            .sourceTexture(ResourceLocationUtils.id("block/fluids/hydrogen_still"))
            .flowingTexture(ResourceLocationUtils.id("block/fluids/hydrogen_flow"));

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
            .overlayTexture(ResourceLocationUtils.id("block/fluids/oxygen_overlay"))
            .sourceTexture(ResourceLocationUtils.id("block/fluids/oxygen_still"))
            .flowingTexture(ResourceLocationUtils.id("block/fluids/oxygen_flow"));

    public static final RegistrySupplier<FlowingFluid> OXYGEN_FLOWING = FLUIDS.register("flowing_oxygen", () -> new ArchitecturyFlowingFluid.Flowing(OXYGEN_ATTRIBUTES));
    public static final RegistrySupplier<FlowingFluid> OXYGEN_STILL = FLUIDS.register("oxygen", () -> new ArchitecturyFlowingFluid.Source(OXYGEN_ATTRIBUTES));


    public static void init() {
        FLUIDS.register();
        FLUIDS_INFOS.add(OXYGEN_ATTRIBUTES);
        FLUIDS_INFOS.add(HYDROGEN_ATTRIBUTES);
    }

}

