package org.exodusstudio.stellaris.common.blocks.entities.machines;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.FluidOutputManager;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.FluidOutputable;
import org.exodusstudio.stellaris.common.data.recipes.FuelRefineryRecipe;
import org.exodusstudio.stellaris.common.data.recipes.input.FluidInput;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.menus.FuelRefineryMenu;
import org.exodusstudio.stellaris.common.network.packets.SyncFluidPacket;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.registries.RecipesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class FuelRefineryBlockEntity extends BaseEnergyContainerBlockEntity implements FluidOutputable {

    private final SingleFluidStorage inputTank;
    private final SingleFluidStorage outputFuelTank;
    private final SingleFluidStorage outputDieselTank;
    public final FluidOutputManager outputManager;
    private final RecipeManager.CachedCheck<FluidInput, FuelRefineryRecipe> cachedCheck = RecipeManager.createCheck(RecipesRegistry.FUEL_REFINERY_TYPE.get());

    public FuelRefineryBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.FUEL_REFINERY.get(), pos, state);
        this.inputTank = new SingleFluidStorage(10000) {
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
                return stack.getFluid().isSame(FluidsRegistry.OIL_STILL.get());
            }
        };


        this.outputFuelTank = new SingleFluidStorage(10000) {
            @Override
            protected void onChange() {
                setChanged();
                if (level != null && level.getServer() != null && !level.getServer().getPlayerList().getPlayers().isEmpty()) {
                    NetworkManager.sendToPlayers(level.getServer().getPlayerList().getPlayers(),
                            new SyncFluidPacket(new FluidAmountMapDataComponent(List.of(getFluidInTank(0).getFluid()), List.of(getFluidValueInTank())), 0, getBlockPos(), Direction.NORTH));
                }
            }
        };
        this.outputDieselTank = new SingleFluidStorage(10000) {
            @Override
            protected void onChange() {
                setChanged();
                if (level != null && level.getServer() != null && !level.getServer().getPlayerList().getPlayers().isEmpty()) {
                    NetworkManager.sendToPlayers(level.getServer().getPlayerList().getPlayers(),
                            new SyncFluidPacket(new FluidAmountMapDataComponent(List.of(getFluidInTank(0).getFluid()), List.of(getFluidValueInTank())), 0, getBlockPos(), Direction.SOUTH));
                }
            }
        };

        this.outputManager = new FluidOutputManager(this);

        this.outputManager.setDefault(
                new FluidOutputManager.FluidOutputEntry(Direction.NORTH, FluidStack.create(FluidsRegistry.FUEL_STILL.get(), 1)),
                new FluidOutputManager.FluidOutputEntry(Direction.SOUTH, FluidStack.create(FluidsRegistry.DIESEL_STILL.get(), 1))
        );
    }

    @Override
    public void tick(Level level, BlockState state) {

        FluidUtil.moveFluidFromItem(0, 0, 1, items, inputTank, 1000);
        FluidUtil.moveFluidToItem(0, inputTank, 0, 1, items, 1000);
        FluidUtil.moveFluidToItem(0, outputFuelTank, 2, 3, items, 1000);
        FluidUtil.moveFluidToItem(0, outputDieselTank, 4, 5, items, 1000);


        if (level == null) {
            return;
        }

        Optional<RecipeHolder<FuelRefineryRecipe>> recipeHolder = cachedCheck.getRecipeFor(new FluidInput(level.getBlockEntity(getBlockPos())), (ServerLevel) level);
        if (recipeHolder.isPresent()) {
            FuelRefineryRecipe recipe = recipeHolder.get().value();

            if (energyContainer.getEnergy() >= recipe.energy()) {

                if (inputTank.getFluidValueInTank() >= recipe.ingredientStack().getAmount()) {
                    if ((outputFuelTank.getFluidInTank(0).isEmpty() || outputFuelTank.getFluidInTank(0).isFluidEqual(recipe.fuelStack())) &&
                            (outputDieselTank.getFluidInTank(0).isEmpty() || outputDieselTank.getFluidInTank(0).isFluidEqual(recipe.dieselStack()))) {
                        boolean shouldUseEnergyAndDrainOil = false;
                        if (outputFuelTank.getFluidValueInTank() + recipe.fuelStack().getAmount() < outputFuelTank.getTankCapacity(0)) {
                            outputFuelTank.fill(recipe.fuelStack().copy(), false);
                            shouldUseEnergyAndDrainOil = true;
                        }
                        if (outputDieselTank.getFluidValueInTank() + recipe.dieselStack().getAmount() < outputDieselTank.getTankCapacity(0)) {
                            outputDieselTank.fill(recipe.dieselStack().copy(), false);
                            shouldUseEnergyAndDrainOil = true;
                        }
                        if (shouldUseEnergyAndDrainOil) {
                            inputTank.drain(recipe.ingredientStack().copy(), false);
                            energyContainer.extract(recipe.energy(), false);
                            setChanged();
                        }
                    }
                }
            }
        }

        getFluidOutputManager().distributeFluids(level, getBlockPos());
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("item.stellaris.fuel_refinery");
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        if (inventory.player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, new SyncFluidPacket(
                    new com.fej1fun.potentials.components.FluidAmountMapDataComponent(List.of(inputTank.getFluidInTank(0).getFluid()), List.of(inputTank.getFluidValueInTank())),
                    0, getBlockPos(), Direction.UP));
            NetworkManager.sendToPlayer(serverPlayer, new SyncFluidPacket(
                    new com.fej1fun.potentials.components.FluidAmountMapDataComponent(List.of(outputFuelTank.getFluidInTank(0).getFluid()), List.of(outputFuelTank.getFluidValueInTank())),
                    0, getBlockPos(), Direction.NORTH));
            NetworkManager.sendToPlayer(serverPlayer, new SyncFluidPacket(
                    new com.fej1fun.potentials.components.FluidAmountMapDataComponent(List.of(outputDieselTank.getFluidInTank(0).getFluid()), List.of(outputDieselTank.getFluidValueInTank())),
                    0, getBlockPos(), Direction.SOUTH));
        }
        return new FuelRefineryMenu(containerId, inventory, this, this);
    }

    @Override
    public int getContainerSize() {
        return 6;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        inputTank.load(input, "input");
        outputFuelTank.load(input, "fuel");
        outputDieselTank.load(input, "diesel");
        outputManager.load(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        inputTank.save(output, "input");
        outputFuelTank.save(output, "fuel");
        outputDieselTank.save(output, "diesel");
        outputManager.save(output);

    }

    public SingleFluidStorage getIngredientTank() {
        return inputTank;
    }
    public SingleFluidStorage getOutputFuelTank() {
        return outputFuelTank;
    }
    public SingleFluidStorage getOutputDieselTank() {
        return outputDieselTank;
    }


    @Override
    public List<Fluid> getFluidsOutput() {
        return List.of(FluidsRegistry.DIESEL_STILL.get(), FluidsRegistry.FUEL_STILL.get());
    }

    @Override
    public List<UniversalFluidStorage> getOutputFluidsTank() {
        return List.of(outputFuelTank, outputDieselTank);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public FluidOutputManager getFluidOutputManager() {
        return outputManager;
    }

    @Override
    public @Nullable SingleFluidStorage getFluidTank(@Nullable Direction direction) {
        if (direction == null) {
            return inputTank;
        }

        return switch (direction) {
            case UP, DOWN -> inputTank;
            case EAST, NORTH -> outputFuelTank;
            case WEST, SOUTH -> outputDieselTank;
        };
    }
}
