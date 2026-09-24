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
import org.exodusstudio.stellaris.common.network.packets.OpenBlockEntityMenusPacket;
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
    }

    @Override
    public void removed(Player player) {
        super.removed(player); // no-op for items since access is ContainerLevelAccess.NULL
        clearContainer(player, inputSlots);
    }

    @Override
    protected boolean mayPickup(Player player, boolean hasStack) {
        return hasStack;
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        for (int i = 0; i < 4; i++) {
            inputSlots.removeItem(i, 1);
        }
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

        ItemStack result = ItemStack.EMPTY;
        if (this.laboratoryBlockEntity != null && player.level() instanceof ServerLevel serverLevel) {
            VaccineInput input = new VaccineInput(this.laboratoryBlockEntity, getItems());
            Optional<RecipeHolder<VaccineRecipe>> recipeHolder = quickCheck.getRecipeFor(input, serverLevel);

            if (recipeHolder.isPresent() && recipeHolder.get().value().matches(input, serverLevel)) {
                result = recipeHolder.get().value().assemble(input);
            }
        }

        this.resultSlots.setItem(0, result);

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
        return MoonLoreUtils.VACCINE_UNLOCK_STAGE - Math.max(stage, 0);
    }
}
