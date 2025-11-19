package org.exodusstudio.stellaris.common.blocks.entities.machines;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.blocks.ElectrolyzerBlock;
import org.exodusstudio.stellaris.common.blocks.base.BaseMachineBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.data.recipe.ElectrolyzeRecipeData;
import org.exodusstudio.stellaris.common.data.recipe.input.FluidInput;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.fluid.MultipleFluidStorage;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.menus.ElectrolyzerMenu;
import org.exodusstudio.stellaris.common.network.packets.SyncFluidPacket;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ElectrolyzerBlockEntity extends BaseEnergyContainerBlockEntity implements FluidProvider.BLOCK{

    public final SingleFluidStorage ingredientTank = new SingleFluidStorage(3000, 3000, 0) {

        @Override
        protected void onChange() {
            setChanged();
            if (level != null && level.getServer() != null && !level.getServer().getPlayerList().getPlayers().isEmpty()) {
                NetworkManager.sendToPlayers(level.getServer().getPlayerList().getPlayers(),
                        new SyncFluidPacket(new FluidAmountMapDataComponent(List.of(getFluidInTank(0).getFluid()), List.of(getFluidValueInTank())), 0, getBlockPos(), Direction.UP));
            }
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            if (this.isEmpty()) return ElectrolyzeRecipeData.RECIPES.containsKey(stack.getFluid());
            else return this.getFluidInTank(0).getFluid() == stack.getFluid();
        }
    };

    public final MultipleFluidStorage resultTanks = new MultipleFluidStorage(2, 6000, 0, 1000) {

        @Override
        protected void onChange(int tank) {
            setChanged();
            if (level != null && level.getServer() != null && !level.getServer().getPlayerList().getPlayers().isEmpty()) {
                NetworkManager.sendToPlayers(level.getServer().getPlayerList().getPlayers(),
                        new SyncFluidPacket(new FluidAmountMapDataComponent(List.of(getFluidInTank(tank).getFluid()), List.of(getFluidValueInTank(tank))), tank, getBlockPos(), getBlockState().getValue(ElectrolyzerBlock.FACING).getClockWise()));
            }
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            if(ElectrolyzerBlockEntity.this.ingredientTank.isEmpty()) {
                return false;
            } else {
                ElectrolyzeRecipeData.ElectrolyzeRecipe recipe = ElectrolyzeRecipeData.RECIPES.get(ElectrolyzerBlockEntity.this.ingredientTank.getFluidInTank(0).getFluid());
                return recipe.resultStacks().get(tank).getFluid() == stack.getFluid();
            }
        }
    };



    public ElectrolyzerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.ELECTROLYZER.get(), blockPos, blockState);
    }

    @Override
    public void tick() {
        if (level == null) {
            return;
        }


        //Move fluid to item for results tanks
        FluidUtil.moveFluidToItem(0, resultTanks, 3, items, 1000);
        FluidUtil.moveFluidToItem(1, resultTanks, 2, items, 1000);

        //Move fluid to item for ingredient tank
        FluidUtil.moveFluidFromItem(0, 1, items, ingredientTank, 1000);

        Direction facing = getBlockState().getValue(ElectrolyzerBlock.FACING);
        FluidUtil.distributeFluidNearby(level, worldPosition, resultTanks.getFluidInTank(0), List.of(facing.getClockWise()));
        FluidUtil.distributeFluidNearby(level, worldPosition, resultTanks.getFluidInTank(1), List.of(facing.getCounterClockWise()));
        FluidUtil.distributeFluidNearby(level, worldPosition, ingredientTank.getFluidInTank(0), List.of(Direction.UP, Direction.DOWN, facing, facing.getOpposite()));

        if(level instanceof ServerLevel && ElectrolyzeRecipeData.RECIPES.containsKey(this.ingredientTank.getFluidInTank(0).getFluid())) {
                ElectrolyzeRecipeData.ElectrolyzeRecipe recipe = ElectrolyzeRecipeData.RECIPES.get(this.ingredientTank.getFluidInTank(0).getFluid());
                //TODO REMOVE THIS ONLY FOR TEST PURPOSES
                if (energyContainer.getEnergy() >= recipe.energy() || true) {
                    boolean shouldDrainWaterAndEnergy = false;

                    if (resultTanks.getFluidValueInTank(0) < resultTanks.getTankCapacity(0)) {
                        resultTanks.fillWithoutLimits(recipe.resultStacks().getFirst(), false);
                        shouldDrainWaterAndEnergy = true;
                    }
                    if (resultTanks.getFluidValueInTank(1) < resultTanks.getTankCapacity(1)) {
                        resultTanks.fillWithoutLimits(recipe.resultStacks().get(1), false);
                        shouldDrainWaterAndEnergy = true;
                    }

                    if (shouldDrainWaterAndEnergy) {
                        ingredientTank.drainWithoutLimits(recipe.ingredientStack(), false);
                        energyContainer.extract(recipe.energy(), false);
                    }
            }

        }

    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ingredientTank.save(output, "ingredient");
        resultTanks.save(output, "resultTanks");

    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ingredientTank.load(input, "ingredient");
        resultTanks.load(input, "resultTanks");
    }

    @Override
    public @Nullable UniversalFluidStorage getFluidTank(@Nullable Direction direction) {
        Direction facing = getBlockState().getValue(BaseMachineBlock.FACING);
        if (facing.getCounterClockWise() == direction || facing.getClockWise() == direction) {
            return resultTanks;
        }
        return ingredientTank;
    }


    @Override
    protected Component getDefaultName() {
        return Component.empty();
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ElectrolyzerMenu(containerId, inventory, this, this);
    }

    @Override
    public int getContainerSize() {
        return 4;
    }
}
