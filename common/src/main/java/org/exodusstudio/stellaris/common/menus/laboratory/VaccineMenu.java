package org.exodusstudio.stellaris.common.menus.laboratory;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.entities.machines.LaboratoryBlockEntity;
import org.exodusstudio.stellaris.common.data.recipes.VaccineRecipe;
import org.exodusstudio.stellaris.common.data.recipes.input.VaccineInput;
import org.exodusstudio.stellaris.common.menus.base.BaseItemCombinerMenu;
import org.exodusstudio.stellaris.common.networking.packets.OpenBlockEntityMenusPacket;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.MenuProviderRegistry;
import org.exodusstudio.stellaris.common.registries.MenuTypesRegistry;
import org.exodusstudio.stellaris.common.registries.RecipesRegistry;
import org.exodusstudio.stellaris.common.utils.MoonLoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;


public class VaccineMenu extends BaseItemCombinerMenu {

    private final LaboratoryBlockEntity laboratoryBlockEntity;
    private final RecipeManager.CachedCheck<VaccineInput, VaccineRecipe> quickCheck = RecipeManager.createCheck(RecipesRegistry.VACCINE_TYPE.get());


    public static VaccineMenu create(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
        return new VaccineMenu(containerId, playerInventory, ContainerLevelAccess.NULL, buf.readBlockPos());
    }

    public VaccineMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access, BlockPos pos) {
        super(MenuTypesRegistry.LABORATORY_VACCINE.get(), containerId, playerInventory, access);
        this.laboratoryBlockEntity = (LaboratoryBlockEntity) player.level().getBlockEntity(pos);
        if (laboratoryBlockEntity != null) {
            this.inputSlots.setItem(0, laboratoryBlockEntity.vaccineItems.get(0));
            this.inputSlots.setItem(1, laboratoryBlockEntity.vaccineItems.get(1));
            this.inputSlots.setItem(2, laboratoryBlockEntity.vaccineItems.get(2));
            this.inputSlots.setItem(3, laboratoryBlockEntity.vaccineItems.get(3));
        }
    }

    @Override
    public void removed(Player player) {
        super.removed(player); // no-op for items since access is ContainerLevelAccess.NULL
        if (laboratoryBlockEntity != null && laboratoryBlockEntity.isTabSwitching()) {
            laboratoryBlockEntity.vaccineItems.set(0, inputSlots.getItem(0).copy());
            laboratoryBlockEntity.vaccineItems.set(1, inputSlots.getItem(1).copy());
            laboratoryBlockEntity.vaccineItems.set(2, inputSlots.getItem(2).copy());
            laboratoryBlockEntity.vaccineItems.set(3, inputSlots.getItem(3).copy());
        } else {
            clearContainer(player, inputSlots);
        }
    }

    @Override
    protected boolean mayPickup(Player player, boolean hasStack) {
        return true;
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        inputSlots.getItem(0).shrink(stack.getCount());
        inputSlots.getItem(1).shrink(stack.getCount());
        inputSlots.getItem(2).shrink(stack.getCount());
        inputSlots.getItem(3).shrink(stack.getCount());
    }

    @Override
    protected boolean isValidBlock(BlockState state) {
        return state.is(BlocksRegistry.LABORATORY.block().get());
    }

    @Override
    public void createResult() {
        if (this.player.level().isClientSide()) {
            return;
        }

        ItemStack outputStack = getItems().get(4);
        if ((outputStack.isEmpty() || outputStack.getCount() < outputStack.getMaxStackSize()) && player.level() instanceof ServerLevel serverLevel) {
            VaccineInput input = new VaccineInput(this.laboratoryBlockEntity, getItems());
            Optional<RecipeHolder<VaccineRecipe>> recipeHolder = quickCheck.getRecipeFor(input, serverLevel);

            if (recipeHolder.isPresent()) {
                VaccineRecipe recipe = recipeHolder.get().value();
                if (recipe.matches(input, player.level())) {
                    ItemStack resultStack = recipe.assemble(input);
                    if (outputStack.isEmpty() || (ItemStack.isSameItemSameComponents(outputStack, resultStack)
                            && outputStack.getCount() + resultStack.getCount() <= outputStack.getMaxStackSize())) {

                        if (outputStack.isEmpty()) {
                            setItem(4, 4, resultStack.copy());
                        }
                        else if (ItemStack.isSameItemSameComponents(outputStack, resultStack)) {
                            outputStack.grow(1);
                        }
                        else {
                            return;
                        }

                        for (int i = 0; i < 4; i++) {
                            ItemStack stack = getItems().get(i);
                            stack.shrink(1);

                            if (stack.isEmpty()) {
                                setItem(i, i, ItemStack.EMPTY);
                            }
                        }

                        laboratoryBlockEntity.setChanged();
                    }
                }
            }
        }

    }

    @Override
    protected @NotNull ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                .withSlot(0, 40, 33, stack -> true)
                .withSlot(1, 68, 33, stack -> true)
                .withSlot(2, 96, 33, stack -> true)
                .withSlot(3, 124, 33, stack -> true)
                .withResultSlot(4, 82, 64)
                .build();
    }

    public void openResearchMenu() {
        this.player.closeContainer();
        NetworkManager.sendToServer(new OpenBlockEntityMenusPacket(MenuProviderRegistry.RESEARCH, this.laboratoryBlockEntity.getBlockPos()));
    }

    public int getUnknownVaccineItem() {
        int stage = MoonLoreUtils.getResearchProgressionStage(player);
        return 4 - Math.max(stage, 0);
    }
}
