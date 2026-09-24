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
import org.exodusstudio.stellaris.common.modules.rover.RoverUpgrades;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModules;
import org.exodusstudio.stellaris.common.network.packets.OpenBlockEntityMenusPacket;
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
            blockEntity.restoreEngineUpgradeItems(this.player, this.inputSlots);
        }
    }

    @Override
    protected boolean mayPickup(Player player, boolean hasStack) {
        return hasStack;
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

        this.resultSlots.setItem(0, ItemStack.EMPTY);

        if(itemToUpgrade.isEmpty() || module.isEmpty()) {
            return;
        }

        /** ROCKET MODULES HANDLING */
        Modules<RocketModule> rocketModule = itemToUpgrade.getOrDefault(DataComponentsRegistry.ROCKET_MODULES.get(), RocketModules.empty());

        if (itemToUpgrade.getItem() instanceof RocketItem && module.getItem() instanceof RocketModule validModule) {
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
        if (itemToUpgrade.getItem() instanceof RoverItem) {
            RoverUpgrades upgrades = itemToUpgrade.getOrDefault(DataComponentsRegistry.ROVER_MODULES.get(), RoverUpgrades.empty());
            RoverUpgrades upgraded = null;

            if (module.getItem() instanceof RocketModule validModule && validModule.fitsRover()
                    && !upgrades.rocketModules().contains(validModule)) {
                Modules<RocketModule>.Mutable modules = upgrades.rocketModules().toMutable();
                modules.removeIf(installed -> installed.getRocketFeature() == validModule.getRocketFeature());
                upgraded = upgrades.withRocketModules(new RocketModules(modules.insert(validModule).getModules()));
            }
            else if (module.getItem() instanceof RoverModule validModule
                    && !upgrades.roverModules().contains(validModule)) {
                Modules<RoverModule>.Mutable modules = upgrades.roverModules().toMutable();
                modules.removeIf(installed -> installed.getRoverFeature() == validModule.getRoverFeature());
                upgraded = upgrades.withRoverModules(new RoverModules(modules.insert(validModule).getModules()));
            }

            if (upgraded != null) {
                itemToUpgrade.set(DataComponentsRegistry.ROVER_MODULES.get(), upgraded);
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
            Modules<SpaceSuitModule>.Mutable mutable = spaceSuitModules.toMutable();
            mutable.removeIf(installed -> installed.getSpaceSuitFeature() == validModule.getSpaceSuitFeature());

            if (!itemToUpgrade.isEmpty() && !module.isEmpty()
                    && canUpgradeFuel(module, itemToUpgrade).equals(Error.NONE)
                    && validModule.canBeAppliedToSpaceSuitPart(itemToUpgrade)
                    && isCompatibleWithAll(validModule, mutable.getModules())
            ) {
                mutable.insert(validModule);
                itemToUpgrade.set(DataComponentsRegistry.SPACE_SUIT_MODULES.get(), mutable.toImmutable());

                if (itemToUpgrade.getItem() instanceof SpaceSuitItem spaceSuitItem) {
                    spaceSuitItem.refreshAttributes(itemToUpgrade);
                }

                this.resultSlots.setItem(0, itemToUpgrade);
                this.broadcastChanges();
            }
            else {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
            }
        }
    }

    private static boolean isCompatibleWithAll(SpaceSuitModule incoming, List<SpaceSuitModule> installed) {
        for (SpaceSuitModule other : installed) {
            if (!incoming.isCompatibleWith(other) || !other.isCompatibleWith(incoming)) {
                return false;
            }
        }
        return true;
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

        if (rocket.getItem() instanceof RocketItem && module.getItem() instanceof RocketModule validModule) {
            if( rocketModule.contains(validModule)) {
                return Error.DUPLICATE_MODULE;
            }
            return canUpgradeFuel(module, rocket);
        }

        RoverUpgrades roverUpgrades = rocket.getOrDefault(DataComponentsRegistry.ROVER_MODULES.get(), RoverUpgrades.empty());

        if (rocket.getItem() instanceof RoverItem) {
            if (module.getItem() instanceof RocketModule validModule && roverUpgrades.rocketModules().contains(validModule)
                    || module.getItem() instanceof RoverModule roverModule && roverUpgrades.roverModules().contains(roverModule)) {
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
            return module.getItem() instanceof RocketModule rocketModule && rocketModule.fitsRover() && StellarisRegistries.ROCKET_MODULES.containsValue(rocketModule)
                    || module.getItem() instanceof RoverModule roverModule && StellarisRegistries.ROVER_MODULES.containsValue(roverModule);
        } else if (this.inputSlots.getItem(0).is(TagsRegistry.ItemTags.SPACE_SUIT)) {
            return module.getItem() instanceof SpaceSuitModule spaceSuitModule && StellarisRegistries.SPACE_SUIT_MODULES.containsValue(spaceSuitModule) && spaceSuitModule.canBeAppliedToSpaceSuitPart(this.inputSlots.getItem(0));
        }
        return false;
    }

    @Override
    public void removed(Player player) {
        super.removed(player); // no-op for items since access is ContainerLevelAccess.NULL
        if (blockEntity == null) {
            clearContainer(player, inputSlots);
        } else if (blockEntity.isTabSwitching()) {
            blockEntity.stashEngineUpgradeItems(player, inputSlots);
        } else {
            clearContainer(player, inputSlots);
            clearContainer(player, blockEntity.takeStashedItems(player));
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
