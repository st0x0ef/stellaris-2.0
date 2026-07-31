package org.exodusstudio.stellaris.common.menus.laboratory;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.LaboratoryBlockEntity;
import org.exodusstudio.stellaris.common.components.PathogenStorageComponent;
import org.exodusstudio.stellaris.common.menus.MenuQuickMoveHelper;
import org.exodusstudio.stellaris.common.menus.slot.ResultSlot;
import org.exodusstudio.stellaris.common.menus.slot.SpecificItemsSlot;
import org.exodusstudio.stellaris.common.networking.packets.InfectionResearchPacket;
import org.exodusstudio.stellaris.common.networking.packets.OpenBlockEntityMenusPacket;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.registries.MenuProviderRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.exodusstudio.stellaris.common.utils.MoonLoreUtils;


public class ResearchMenu extends AbstractContainerMenu {

    private final Container inventory;
    private final Player player;
    public final LaboratoryBlockEntity blockEntity;

    public static ResearchMenu create(int syncId, Inventory inventory, FriendlyByteBuf buffer) {
        return create(syncId, inventory, buffer.readBlockPos());
    }

    public static ResearchMenu create(int syncId, Inventory inventory, BlockPos pos) {
        LaboratoryBlockEntity blockEntity = (LaboratoryBlockEntity) inventory.player.level().getBlockEntity(pos);
        return new ResearchMenu(syncId, inventory, blockEntity, blockEntity);
    }

    public ResearchMenu(int syncId, Inventory playerInventory, Container container, LaboratoryBlockEntity blockEntity) {
        super(MenuTypesRegistry.LABORATORY_RESEARCH.get(), syncId);

        checkContainerSize(container, 2);
        this.inventory = container;
        this.player = playerInventory.player;
        this.blockEntity = blockEntity;
        addSlots(inventory);

        for (int i = 0; i < getItems().size(); i++) {
            this.inventory.setItem(i, getItems().get(i));
        }

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int invSlot) {
        return MenuQuickMoveHelper.quickMoveMachineFirst(this, player, invSlot, 2);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
    }


    private void addSlots(Container inventory) {
        this.addSlot(new SpecificItemsSlot(inventory, 0, 40, 40, ItemsRegistry.PATHOGEN_STORAGE_CELL.get()));
        this.addSlot(new ResultSlot(inventory, 1, 124, 40));
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 10 + j * 18, 106 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 10 + i * 18, 164));
        }
    }

    public void openVaccineTab() {
        this.player.closeContainer();
        NetworkManager.sendToServer(new OpenBlockEntityMenusPacket(MenuProviderRegistry.VACCINE, this.blockEntity.getBlockPos()));
    }

    public void researchButton() {
        if (blockEntity.progressTickLeft == -1) {
            blockEntity.progressTickLeft = Stellaris.CONFIG.parasiteConfig.researchDelay;
        }
    }

    public boolean tryResearch() {
        ItemStack storageCell = getItems().getFirst();
        int parasiteStored = storageCell.getOrDefault(DataComponentsRegistry.PATHOGEN_STORED.get(), new PathogenStorageComponent(0, 500)).stored();
        boolean success = MoonLoreUtils.tryIncrementResearchProgressionStageIfLucky(player, parasiteStored);

        InfectionResearchPacket packet = new InfectionResearchPacket(blockEntity.getBlockPos(), success);
        NetworkManager.sendToServer(packet);

        blockEntity.progressTickLeft = -1;

        return success;
    }
}
