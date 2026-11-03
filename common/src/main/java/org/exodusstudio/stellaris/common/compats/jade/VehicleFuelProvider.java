package org.exodusstudio.stellaris.common.compats.jade;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;
import org.exodusstudio.stellaris.common.entities.vehicles.base.FuelledVehicle;
import org.exodusstudio.stellaris.common.fluid.VehicleFuelStorage;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.fluid.JadeFluidObject;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.FluidView;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

import java.util.List;

public enum VehicleFuelProvider implements IServerExtensionProvider<FluidView.Data>, IClientExtensionProvider<FluidView.Data, FluidView> {
    INSTANCE;

    private static final Identifier UID = IdentifierUtils.id("vehicle_fuel");

    @Override
    public @Nullable List<ViewGroup<FluidView.Data>> getGroups(Accessor<?> accessor) {
        if (!(accessor.getTarget() instanceof FuelledVehicle vehicle) || vehicle.getFuelTank() == null) {
            return null;
        }
        VehicleFuelStorage tank = vehicle.getFuelTank();
        long scale = JadeFluidObject.bucketVolume() / 1000;
        Fluid fluid = tank.getFuelFluid();
        long amount = tank.getFuelAmount();
        JadeFluidObject fuel = fluid == null || amount <= 0 ? JadeFluidObject.empty() : JadeFluidObject.of(fluid, amount * scale);
        return List.of(new ViewGroup<>(List.of(new FluidView.Data(fuel, tank.getCapacity() * scale))));
    }

    @Override
    public List<ClientViewGroup<FluidView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<FluidView.Data>> groups) {
        return ClientViewGroup.map(groups, FluidView::readDefault, null);
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
