package org.exodusstudio.stellaris.common.blocks.entities;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FlagBlockEntity extends BaseContainerBlockEntity {

    private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);

    private ResolvableProfile profile;
    private DyeColor color = DyeColor.GRAY;

    public STATE flagState = STATE.PLAYER_HEAD;

    public FlagBlockEntity( BlockPos pos, BlockState blockState) {
        this(BlockEntitiesRegistry.FLAG.get(), pos, blockState);
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return null;
    }

    public FlagBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if(this.profile != null) {
            output.store("profile", ResolvableProfile.CODEC, this.profile);
        }
        output.putInt("color", this.color.getId());
        flagState.toNBT(output);
    }


    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        Optional<ResolvableProfile> optProfile = input.read("profile", ResolvableProfile.CODEC);
        optProfile.ifPresent(profile -> this.profile = profile);

        input.getInt("color").ifPresent((color) -> this.color = DyeColor.byId(color));
        this.flagState = STATE.fromNBT(input);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.stellaris.flag");
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
        this.setChanged();
    }


    public ResolvableProfile getGameProfile() {
        return profile;
    }

    public STATE getFlagState() {
        return flagState;
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithFullMetadata(registries);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentGetter) {
        super.applyImplicitComponents(componentGetter);
        this.profile = componentGetter.getOrDefault(DataComponents.PROFILE, new ResolvableProfile(new GameProfile(UUID.fromString("fe40f09c-fdaa-497f-8e2b-bed31180bfbd"), "TATHAN_06")));

    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.PROFILE, this.profile);
    }

    public void setDyeColor(DyeColor color) {
        this.color = color;
        this.setChanged();
    }

    public void setProfile(ResolvableProfile profile) {
        this.profile = profile;
        this.setChanged();
    }

    @Override
    public int getContainerSize() {
        return 0;
    }

    public void setFlagState(STATE flagState) {
        this.flagState = flagState;
    }

    public enum STATE {
        PLAYER_HEAD(true, false),
        CUSTOM_PNG(false, true);

        public final boolean playerHead;
        public final boolean customPng;

        STATE(boolean playerHead, boolean customPng) {
            this.playerHead = playerHead;
            this.customPng = customPng;
        }

        public void toNBT(ValueOutput output) {
            output.putBoolean("playerHead", playerHead);
            output.putBoolean("customPng", customPng);
        }

        public static STATE fromNBT(ValueInput input) {
            boolean playerHead = input.getBooleanOr("playerHead", true);
            boolean customPng = input.getBooleanOr("playerHead", false);
            for (STATE state : values()) {
                if (state.playerHead == playerHead && state.customPng == customPng) {
                    return state;
                }
            }
            return STATE.PLAYER_HEAD;
        }

        public static STATE fromValues(boolean playerHead, boolean customPng) {
            for (STATE state : values()) {
                if (state.playerHead == playerHead && state.customPng == customPng) {
                    return state;
                }
            }
            throw new NoSuchElementException("No STATE found for playerHead: " + playerHead + " and customPng: " + customPng);
        }
    }
}