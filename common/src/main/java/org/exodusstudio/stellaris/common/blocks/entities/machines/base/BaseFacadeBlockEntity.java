package org.exodusstudio.stellaris.common.blocks.entities.machines.base;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

/**
 * This is the base class for block that can be facade
 * This was supposed to be an Interface but i think it's better for now to be an abstract class
 */
public abstract class BaseFacadeBlockEntity extends BlockEntity {

    public BlockState facadeState = null;


    public BaseFacadeBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if(facadeState != null) {
            output.store("facade", BlockState.CODEC, facadeState);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        facadeState = input.read("facade", BlockState.CODEC).orElse(null);
    }


    public void setFacadeState(@Nullable BlockState state) {

        this.facadeState = state;
        setChanged();
    }



    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = this.saveWithoutMetadata(registries);

        if(facadeState != null) {
            tag.put("facade", BlockState.CODEC.encodeStart(NbtOps.INSTANCE, facadeState).result().orElseThrow(() -> new IllegalStateException("Failed to encode facade state")));
        }

        return tag;
    }

    public BlockState getFacadeState() {
        return facadeState;
    }

    // Return our packet here. This method returning a non-null result tells the game to use this packet for syncing.
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();

        if (level == null) return;

        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }
}
