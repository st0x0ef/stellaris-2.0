package org.exodusstudio.stellaris.common.menus.engineering_station;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.entities.machines.EngineeringStationBlockEntity;
import org.exodusstudio.stellaris.common.items.RocketItem;
import org.exodusstudio.stellaris.common.items.RoverItem;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitItem;
import org.exodusstudio.stellaris.common.menus.base.BaseItemCombinerMenu;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModules;
import org.exodusstudio.stellaris.common.modules.rover.RoverModule;
import org.exodusstudio.stellaris.common.modules.rover.RoverModules;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModules;
import org.exodusstudio.stellaris.common.networking.packets.OpenBlockEntityMenusPacket;
import org.exodusstudio.stellaris.common.registries.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class EngineUpgradeMenu extends BaseItemCombinerMenu {

    public final BlockPos engineeringStationPos;
    private final EngineeringStationBlockEntity blockEntity;

    public static EngineUpgradeMenu create(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        EngineeringStationBlockEntity be = (EngineeringStationBlockEntity) playerInventory.player.level().getBlockEntity(pos);
        return new EngineUpgradeMenu(containerId, playerInventory, ContainerLevelAccess.NULL, pos, be);
    }

    public EngineUpgradeMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access, BlockPos pos, EngineeringStationBlockEntity blockEntity) {
        super(MenuTypesRegistry.ENGINE_UPGRADE.get(), containerId, playerInventory, access);
        this.engineeringStationPos = pos;
        this.blockEntity = blockEntity;
        if (blockEntity != null) {
            this.inputSlots.setItem(0, blockEntity.engineUpgradeItems.get(0));
            this.inputSlots.setItem(1, blockEntity.engineUpgradeItems.get(1));
        }
    }

    @Override
    protected boolean mayPickup(Player player, boolean hasStack) {
        return true;
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        inputSlots.getItem(0).shrink(1);
        inputSlots.getItem(1).shrink(1);
        broadcastChanges();
    }

    @Override
    protected boolean isValidBlock(BlockState state) {
        return state.is(BlocksRegistry.ENGINEERING_STATION.block().get());
    }

    @Override
    public void createResult() {
        if (this.player.level().isClientSide()) {
            return;
        }


        ItemStack itemToUpgrade = this.inputSlots.getItem(0).copy();
        ItemStack module = this.inputSlots.getItem(1).copy();

        if(itemToUpgrade.isEmpty() || module.isEmpty()) {
            return;
        }

        /** ROCKET MODULES HANDLING */
        Modules<RocketModule> rocketModule = itemToUpgrade.getOrDefault(DataComponentsRegistry.ROCKET_MODULES.get(), RocketModules.empty());

        if (module.getItem() instanceof RocketModule validModule) {
            if (!itemToUpgrade.isEmpty() && !module.isEmpty()
                    && !rocketModule.contains(validModule)
                    && canUpgradeFuel(module, itemToUpgrade).equals(Error.NONE)
                    //&& rocketModule.contains(validModule.requires())
            ) {

                List<RocketModule> modules = rocketModule.getModules();
                List<RocketModule> newRocketModules = new ArrayList<>();
                for (RocketModule mod : modules) {
                    if (mod.asModule().getRocketFeature() != validModule.asModule().getRocketFeature()) {
                        newRocketModules.add(mod.asModule());
                    }
                }
                newRocketModules.add(validModule.asModule());

                if (module.is(ItemsRegistry.AUTOPILOT_MODULE.get()) && module.has(DataComponentsRegistry.AUTOPILOT.get())) {
                    itemToUpgrade.set(DataComponentsRegistry.AUTOPILOT.get(), module.get(DataComponentsRegistry.AUTOPILOT.get()));
                }

                itemToUpgrade.set(DataComponentsRegistry.ROCKET_MODULES.get(), new RocketModules(newRocketModules));

                this.resultSlots.setItem(0, itemToUpgrade);
                this.broadcastChanges();

            }
            else {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
            }
        }

        /* --------------------------------------------------------------- */

        /** ROVER MODULES HANDLING */
        Modules<RoverModule> roverModules = itemToUpgrade.getOrDefault(DataComponentsRegistry.ROVER_MODULES.get(), RoverModules.empty());

        if (module.getItem() instanceof RoverModule validModule) {
            if (!itemToUpgrade.isEmpty() && !module.isEmpty()
                    && !roverModules.contains(validModule)
            ) {
                List<RoverModule> modules = roverModules.getModules();
                List<RoverModule> newRoverModules = new ArrayList<>();
                for (RoverModule mod : modules) {
                    if (mod.asModule().getRoverFeature() != validModule.asModule().getRoverFeature()) {
                        newRoverModules.add(mod.asModule());
                    }
                }
                newRoverModules.add(validModule.asModule());

                itemToUpgrade.set(DataComponentsRegistry.ROVER_MODULES.get(), new RoverModules(newRoverModules));

                this.resultSlots.setItem(0, itemToUpgrade);
                this.broadcastChanges();
            }
            else {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
            }
        }

        /* --------------------------------------------------------------- */

        /** SPACE SUITS MODULES HANDLING */
        Modules<SpaceSuitModule> spaceSuitModules = itemToUpgrade.getOrDefault(DataComponentsRegistry.SPACE_SUIT_MODULES.get(), SpaceSuitModules.empty());

        if (module.getItem() instanceof SpaceSuitModule validModule) {
            if (!itemToUpgrade.isEmpty() && !module.isEmpty()
                    && !spaceSuitModules.contains(validModule)
                    && canUpgradeFuel(module, itemToUpgrade).equals(Error.NONE)
                    && validModule.canBeAppliedToSpaceSuitPart(itemToUpgrade)
            ) {
                Modules<SpaceSuitModule>.Mutable mutable = spaceSuitModules.toMutable();
                mutable.insert(validModule);
                itemToUpgrade.set(DataComponentsRegistry.SPACE_SUIT_MODULES.get(), mutable.toImmutable());

                if (itemToUpgrade.getItem() instanceof SpaceSuitItem spaceSuitItem) {
                    spaceSuitItem.onAddModule(itemToUpgrade, validModule);
                }

                this.resultSlots.setItem(0, itemToUpgrade);
                this.broadcastChanges();
            }
        }
    }

    /**
     * Check if the fuel can be upgraded.
     * The rocket should be empty if the module is a custom fuel module.
     * @param module the module to be installed
     * @param rocket the rocket to be upgraded
     * @return true if the fuel can be upgraded, false otherwise
     */
    public Error canUpgradeFuel(ItemStack module, ItemStack rocket) {
        FluidAmountMapDataComponent fuelComponent = rocket.get(DataComponentsRegistry.FLUID_LIST.get());

        if(module.getItem() instanceof RocketModule.CustomFuelModule) {
            if(fuelComponent != null && fuelComponent.getAmount(0) > 0) {
                return Error.FUEL_NOT_EMPTY;
            }
        }
        return Error.NONE;
    }

    /**
     * Check if errors exist when upgrading the rocket.
     * @param module the module to be installed
     * @param rocket the rocket to be upgraded
     * @return the error message, or NONE if no errors exist
     */
    public Error getErrorMessage(ItemStack module, ItemStack rocket) {
        Modules<RocketModule> rocketModule = rocket.getOrDefault(DataComponentsRegistry.ROCKET_MODULES.get(), RocketModules.empty());

        if (module.getItem() instanceof RocketModule validModule) {
            if( rocketModule.contains(validModule)) {
                return Error.DUPLICATE_MODULE;
            }
            return canUpgradeFuel(module, rocket);
        }

        Modules<RoverModule> roverModules = rocket.getOrDefault(DataComponentsRegistry.ROVER_MODULES.get(), RoverModules.empty());

        if (module.getItem() instanceof RoverModule validModule) {
            if (roverModules.contains(validModule)) {
                return Error.DUPLICATE_MODULE;
            }
            return Error.NONE;
        }
        return Error.NONE;
    }

    public ItemStack getInputModule() {
        return this.inputSlots.getItem(1);
    }

    public ItemStack getInputStack() {
        return this.inputSlots.getItem(0);
    }


    @Override
    protected @NotNull ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 31, 48, itemStack -> itemStack.getItem() instanceof RocketItem || itemStack.getItem() instanceof RoverItem || itemStack.is(TagsRegistry.ItemTags.SPACE_SUIT))
                .withSlot(1, 75, 48, this::mayPlaceModule)
                .withResultSlot(2, 127, 48)
                .build();
    }

    private boolean mayPlaceModule(ItemStack module) {
        if (this.inputSlots.getItem(0).is(ItemsRegistry.ROCKET.get())) {
            return module.getItem() instanceof RocketModule rocketModule && StellarisRegistries.ROCKET_MODULES.containsValue(rocketModule);
        } else if (this.inputSlots.getItem(0).is(ItemsRegistry.ROVER.get())) {
            return module.getItem() instanceof RoverModule roverModule && StellarisRegistries.ROVER_MODULES.containsValue(roverModule);
        } else if (this.inputSlots.getItem(0).is(TagsRegistry.ItemTags.SPACE_SUIT)) {
            return module.getItem() instanceof SpaceSuitModule spaceSuitModule && StellarisRegistries.SPACE_SUIT_MODULES.containsValue(spaceSuitModule) && spaceSuitModule.canBeAppliedToSpaceSuitPart(this.inputSlots.getItem(0));
        }
        return false;
    }

    @Override
    public void removed(Player player) {
        super.removed(player); // no-op for items since access is ContainerLevelAccess.NULL
        if (blockEntity != null && blockEntity.isTabSwitching()) {
            blockEntity.engineUpgradeItems.set(0, inputSlots.getItem(0).copy());
            blockEntity.engineUpgradeItems.set(1, inputSlots.getItem(1).copy());
        } else {
            clearContainer(player, inputSlots);
        }
    }

    public static void openScreen(OpenBlockEntityMenusPacket.BlockEntityMenuProvider menuProvider, BlockPos blockPos) {
        NetworkManager.sendToServer(new OpenBlockEntityMenusPacket(menuProvider, blockPos));
    }

    public enum Error {
        NONE(Component.empty()),
        DUPLICATE_MODULE(Component.translatable("menu.duplicate_module")),
        FUEL_NOT_EMPTY(Component.translatable("menu.fuel_not_empty"));

        public final Component errorMessage;

        Error(Component errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
