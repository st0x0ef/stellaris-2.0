package org.exodusstudio.stellaris.common.modules.rover;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ItemLike;
import org.exodusstudio.stellaris.common.modules.Module;

public interface RoverModule extends Module<RoverModule> {

    RoverFeature getRoverFeature();

    /**
     * The translated name shown for this module. Modules that are items fall back to their item name,
     * so the text always comes from the language file.
     */
    default Component getDisplayName() {
        return this instanceof ItemLike itemLike ? Component.translatable(itemLike.asItem().getDescriptionId()) : Component.empty();
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
     * The features a rover module can occupy. Only one module per feature can be installed at a time;
     * installing a new module of a given feature replaces the previous one.
     */
    enum RoverFeature {
        TANK,
        SPEED,
        OTHER
    }
}
