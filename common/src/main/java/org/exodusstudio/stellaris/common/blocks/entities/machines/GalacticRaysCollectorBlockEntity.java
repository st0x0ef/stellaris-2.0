package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseGeneratorBlockEntity;
import org.exodusstudio.stellaris.common.components.RadiationHolderComponent;
import org.exodusstudio.stellaris.common.menus.GalacticRaysCollectorMenu;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.world.attribute.EnvironmentAttributesRegistry;

public class GalacticRaysCollectorBlockEntity extends BaseGeneratorBlockEntity {


    public static int RADIATION_CONTAINER_STOCK_SLOT = 0;
    public static int RADIATION_CONTAINER_SLOT = 1;
    public static int FULL_RADIATION_CONTAINER_SLOT = 2;

    public static int ENERGY_CAPACITY = 100000;
    public static int DEFAULT_ENERGY_OUTPUT = 10;


    public GalacticRaysCollectorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.GALACTIC_RAYS_COLLECTOR.get(), blockPos, blockState, DEFAULT_ENERGY_OUTPUT, ENERGY_CAPACITY);
    }


    @Override
    protected Component getDefaultName() {
        return Component.translatable("stellaris.screen.galactic_rays_collector");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new GalacticRaysCollectorMenu(containerId, inventory, this, this);
    }

    @Override
    public boolean canGenerate() {
        Level level = getLevel();
        if(level == null) return false;

        EnvironmentAttributeSystem environmentAttributeSystem = level.environmentAttributes();
        float solarRadiation = environmentAttributeSystem.getValue(EnvironmentAttributesRegistry.SOLAR_RADIATION.get(), getBlockPos());

        if(solarRadiation <= 0.0f) return false;

        return energyContainer.getEnergy() != energyContainer.getMaxEnergy();
    }

    @Override
    public void tick(Level level, BlockState state) {
        if (level.isClientSide()) return;




        if(canGenerate()) {

            ItemStack radiationContainer = getItem(RADIATION_CONTAINER_SLOT);

            if(radiationContainer.isEmpty() && !getItem(RADIATION_CONTAINER_STOCK_SLOT).isEmpty()) {;
                setItem(RADIATION_CONTAINER_SLOT, removeItem(RADIATION_CONTAINER_STOCK_SLOT, 1));
            }


            if(radiationContainer.isEmpty() || !radiationContainer.has(DataComponentsRegistry.RADIATION_HOLDER.get())) return;

            float solarRadiation = level.environmentAttributes().getValue(EnvironmentAttributesRegistry.SOLAR_RADIATION.get(), getBlockPos());

            RadiationHolderComponent radiationHolderComponent = radiationContainer.get(DataComponentsRegistry.RADIATION_HOLDER.get());

            if (radiationHolderComponent.isFull()) {
                moveFullContainerToOutput();
                return;
            }

            radiationContainer.set(
                    DataComponentsRegistry.RADIATION_HOLDER.get(),
                    radiationHolderComponent.addRadiation(solarRadiation)
            );

            if (radiationContainer.get(DataComponentsRegistry.RADIATION_HOLDER.get()).isFull()) {
                moveFullContainerToOutput();
            }

            int energyToGenerate = (int)(DEFAULT_ENERGY_OUTPUT * solarRadiation);
            energyContainer.insertWithoutLimits(energyToGenerate, false);
        }
    }

    private void moveFullContainerToOutput() {
        ItemStack fullContainer = getItem(RADIATION_CONTAINER_SLOT);
        ItemStack fullContainerOutput = getItem(FULL_RADIATION_CONTAINER_SLOT);

        if (fullContainer.isEmpty() || (!fullContainerOutput.isEmpty()
                && (!ItemStack.isSameItemSameComponents(fullContainer, fullContainerOutput)
                || fullContainerOutput.getCount() >= Math.min(fullContainerOutput.getMaxStackSize(), getMaxStackSize())))) {
            return;
        }

        if (fullContainerOutput.isEmpty()) {
            setItem(FULL_RADIATION_CONTAINER_SLOT, fullContainer.copyWithCount(1));
        }
        else {
            fullContainerOutput.grow(1);
        }

        fullContainer.shrink(1);
        if (fullContainer.isEmpty()) {
            setItem(RADIATION_CONTAINER_SLOT, removeItem(RADIATION_CONTAINER_STOCK_SLOT, 1));
        }
        else {
            setChanged();
        }
    }

    public float getSolarRadiation() {
        Level level = getLevel();
        if(level == null) return 0.0f;

        return level.environmentAttributes().getValue(EnvironmentAttributesRegistry.SOLAR_RADIATION.get(), getBlockPos());
    }

    @Override
    public int getContainerSize() {
        return 3;
    }
}
