package org.exodusstudio.stellaris.common.modules.rover;

import org.exodusstudio.stellaris.common.modules.Module;
import org.exodusstudio.stellaris.common.vehicle_upgrade.FuelType;

public interface RoverModule extends Module<RoverModule> {

    RoverFeature getRoverFeature();

    String getDisplayName();

    /**
     * The fuel type this module makes the rover accept.
     * @return the fuel type, or {@code null} if this module does not affect fuel.
     */
    default FuelType.Type getFuelType() {
        return null;
    }

    /**
     * The tank capacity this module grants.
     * @return the tank capacity in units, or {@code 0} if this module does not affect the tank.
     */
    default int getTankCapacity() {
        return 0;
    }

    /**
     * The speed multiplier this module applies to the rover.
     * @return the speed modifier (1 = no change).
     */
    default float getSpeedModifier() {
        return 1f;
    }

    /**
     * Extra inventory rows this module adds to the rover.
     * @return the number of extra rows, or {@code 0} if this module does not affect storage.
     */
    default int getExtraInventoryRows() {
        return 0;
    }

    /**
     * The features a rover module can occupy. Only one module per feature can be installed at a time;
     * installing a new module of a given feature replaces the previous one.
     */
    enum RoverFeature {
        MOTOR, // Fuel Type
        TANK,
        SPEED,
        CARGO,
        OTHER
    }
}
