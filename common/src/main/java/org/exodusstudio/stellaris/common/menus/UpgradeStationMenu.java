package org.exodusstudio.stellaris.common.menus;

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
import org.exodusstudio.stellaris.common.menus.base.BaseItemCombinerMenu;
import org.exodusstudio.stellaris.common.module.Modules;
import org.exodusstudio.stellaris.common.module.rocket.RocketModule;
import org.exodusstudio.stellaris.common.module.rocket.RocketModules;
import org.exodusstudio.stellaris.common.network.packets.OpenRocketStationMenusPacket;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.jetbrains.annotations.NotNull;


public class UpgradeStationMenu extends BaseItemCombinerMenu {

    private final BlockPos rocketStationPos;

    //TODO: change name
    public static UpgradeStationMenu create(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        return new UpgradeStationMenu(containerId, playerInventory, ContainerLevelAccess.NULL, buf.readBlockPos());
    }

    public UpgradeStationMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access, BlockPos pos) {
        super(MenuTypesRegistry.ROCKET_UPGRADE.get(), containerId, playerInventory, access);
        this.rocketStationPos = pos;
    }

    @Override
    protected boolean mayPickup(Player player, boolean hasStack) {
        return true;
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        this.inputSlots.setItem(0, ItemStack.EMPTY);
        this.inputSlots.setItem(1, ItemStack.EMPTY);
    }

    @Override
    protected boolean isValidBlock(BlockState state) {
        return state.is(BlocksRegistry.ROCKET_STATION.block().get());
    }

    @Override
    public void createResult() {
        if (this.player.level().isClientSide) {
            return;
        }


        ItemStack itemToUpgrade = this.inputSlots.getItem(0).copy();
        ItemStack module = this.inputSlots.getItem(1);

        if(itemToUpgrade.isEmpty() || module.isEmpty()) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
            return;
        }

        Modules<RocketModule> rocketModule = itemToUpgrade.getOrDefault(DataComponentsRegistry.ROCKET_MODULES.get(), RocketModules.empty());

        if (module.getItem() instanceof RocketModule validModule) {
            if (!itemToUpgrade.isEmpty() && !module.isEmpty()
                    && !rocketModule.contains(validModule)
                    && canUpgradeFuel(module, itemToUpgrade) == Error.NONE
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

    public ItemStack getInputModule() {
        return this.inputSlots.getItem(1);
    }

    public ItemStack getInputRocket() {
        return this.inputSlots.getItem(0);
    }


    @Override
    protected @NotNull ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 31, 48, itemStack -> itemStack.getItem() instanceof RocketItem)
                .withSlot(1, 75, 48, itemStack -> itemStack.getItem() instanceof RocketModule)
                .withResultSlot(2, 127, 48)
                .build();
    }

    public void openCraftingMenu() {
        this.player.closeContainer();
        NetworkManager.sendToServer(new OpenRocketStationMenusPacket("crafting", this.rocketStationPos));

    }

    public enum Error {
        NONE(Component.empty()),
        FUEL_NOT_EMPTY(Component.translatable("menu.fuel_not_empty")),;

        public final Component errorMessage;

        Error(Component errorMessage) {
            this.errorMessage = errorMessage;
        }
    }
}
