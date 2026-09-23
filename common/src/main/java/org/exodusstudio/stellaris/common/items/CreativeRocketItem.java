package org.exodusstudio.stellaris.common.items;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;

import java.util.List;

public class CreativeRocketItem extends RocketItem {
    public CreativeRocketItem(Properties properties) {
        super(properties.delayedComponent(DataComponentsRegistry.FLUID_LIST.get(), provider ->
                new FluidAmountMapDataComponent(List.of(FluidsRegistry.FUEL_STILL.get()), List.of(FUEL_CAPACITY))));
    }
}
