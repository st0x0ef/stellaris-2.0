package org.exodusstudio.stellaris.common.blocks.entities;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

import java.util.Optional;
import java.util.UUID;

public class FlagBlockEntity extends BlockEntity {

    private ResolvableProfile profile;
    private DyeColor color = DyeColor.GRAY;

    public FlagBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntitiesRegistry.FLAG.get(), pos, blockState);
    }


    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if(this.profile != null) {
            output.store("profile", ResolvableProfile.CODEC, this.profile);
        }
        output.putInt("color", this.color.getId());
    }


    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        Optional<ResolvableProfile> optProfile = input.read("profile", ResolvableProfile.CODEC);
        optProfile.ifPresent(profile -> this.profile = profile);

        input.getInt("color").ifPresent((color) -> this.color = DyeColor.byId(color));
    }

    public ResolvableProfile getGameProfile() {
        return profile;
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
}