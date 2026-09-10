package org.exodusstudio.stellaris.common.entities.vehicles.base;

import org.exodusstudio.stellaris.common.fluid.VehicleFuelStorage;
import org.jetbrains.annotations.Nullable;

public interface FuelledVehicle {

    @Nullable VehicleFuelStorage getFuelTank();
}
