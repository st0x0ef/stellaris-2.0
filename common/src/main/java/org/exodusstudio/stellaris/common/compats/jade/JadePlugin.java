package org.exodusstudio.stellaris.common.compats.jade;

import net.minecraft.world.level.block.Block;
import org.exodusstudio.stellaris.common.blocks.*;
import org.exodusstudio.stellaris.common.blocks.entities.PipeBlockEntity;
import org.exodusstudio.stellaris.common.blocks.entities.machines.*;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.entities.vehicles.RoverEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.view.HideThingsExtensionProvider;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(EnergyRateProvider.INSTANCE, BaseEnergyContainerBlockEntity.class);
        registration.registerBlockDataProvider(OxygenDistributorProvider.INSTANCE, OxygenDistributorBlockEntity.class);

        registration.registerProgress(MachineProgressProvider.INSTANCE, CoalGeneratorBlockEntity.class);
        registration.registerProgress(MachineProgressProvider.INSTANCE, VacuumatorBlockEntity.class);
        registration.registerProgress(MachineProgressProvider.INSTANCE, BlenderBlockEntity.class);
        registration.registerProgress(MachineProgressProvider.INSTANCE, LaboratoryBlockEntity.class);

        registration.registerFluidStorage(VehicleFuelProvider.INSTANCE, RocketEntity.class);
        registration.registerFluidStorage(VehicleFuelProvider.INSTANCE, RoverEntity.class);

        registration.registerEnergyStorage(HideThingsExtensionProvider.instance(), CableBlockEntity.class);
        registration.registerFluidStorage(HideThingsExtensionProvider.instance(), PipeBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addConfig(ProxyAsMainCallback.UID, true);
        registration.addRayTraceCallback(new ProxyAsMainCallback(registration));

        registration.registerBlockComponent(EnergyRateProvider.Client.INSTANCE, Block.class);
        registration.registerBlockComponent(OxygenDistributorProvider.Client.INSTANCE, OxygenDistributorBlock.class);

        registration.registerProgressClient(MachineProgressProvider.INSTANCE);
        registration.registerFluidStorageClient(VehicleFuelProvider.INSTANCE);

        registration.registerBlockComponent(FlagOwnerProvider.INSTANCE, FlagBlock.class);
        registration.registerBlockComponent(FlagOwnerProvider.INSTANCE, FlagProxyBlock.class);
        registration.registerBlockComponent(SpaceFarmBlockProvider.INSTANCE, SpaceFarmBlock.class);
    }
}
