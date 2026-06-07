package org.exodusstudio.stellaris.common.vehicle_upgrade;

import net.minecraft.resources.Identifier;

public class MotorUpgrade extends VehicleUpgrade {

    private final FuelType.Type type;

    public MotorUpgrade(FuelType.Type type) {
        this.type = type;
    }

    public MotorUpgrade(FuelType.Type type, Identifier fluidTexture) {
        this(type);
    }

    public FuelType.Type getFuelType() {
        return this.type;
    }

    public Identifier getFluidTexture() {
        return this.getFuelType().getFuelTexture();
    }

    public static MotorUpgrade getBasic(boolean isRocket) {
        return new MotorUpgrade(isRocket ? FuelType.Type.FUEL : FuelType.Type.DIESEL);
    }
}
