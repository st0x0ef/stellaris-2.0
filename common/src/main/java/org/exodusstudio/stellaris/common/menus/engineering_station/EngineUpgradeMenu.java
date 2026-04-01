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
import org.exodusstudio.stellaris.common.items.RocketItem;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitItem;
import org.exodusstudio.stellaris.common.menus.base.BaseItemCombinerMenu;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModules;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModules;
import org.exodusstudio.stellaris.common.network.packets.OpenRocketStationMenusPacket;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.jetbrains.annotations.NotNull;


public class EngineUpgradeMenu extends BaseItemCombinerMenu {

    private final BlockPos engineeringStationPos;

    public static EngineUpgradeMenu create(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        return new EngineUpgradeMenu(containerId, playerInventory, ContainerLevelAccess.NULL, buf.readBlockPos());
    }

    //We need to save the position of the rocket station to be able to open the crafting menu from here and get/save items in it.
    public EngineUpgradeMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access, BlockPos pos) {
        super(MenuTypesRegistry.ENGINE_UPGRADE.get(), containerId, playerInventory, access);
        this.engineeringStationPos = pos;
    }

    @Override
    protected boolean mayPickup(Player player, boolean hasStack) {
        return true;
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        inputSlots.getItem(0).shrink(stack.getCount());
        inputSlots.getItem(1).shrink(stack.getCount());
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

                Modules<RocketModule>.Mutable mutable = rocketModule.toMutable();
                mutable.insert(validModule);
                itemToUpgrade.set(DataComponentsRegistry.ROCKET_MODULES.get(), mutable.toImmutable());

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
                .withSlot(0, 31, 48, itemStack -> itemStack.getItem() instanceof RocketItem || itemStack.getItem() instanceof SpaceSuitItem)
                .withSlot(1, 75, 48, itemStack -> itemStack.getItem() instanceof RocketModule || itemStack.getItem() instanceof SpaceSuitModule)
                .withResultSlot(2, 127, 48)
                .build();
    }

    public void openCraftingMenu() {
        this.player.closeContainer();
        NetworkManager.sendToServer(new OpenRocketStationMenusPacket("crafting", this.engineeringStationPos));

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
