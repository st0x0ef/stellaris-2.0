package org.exodusstudio.stellaris.common.blocks.entities.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.TickingBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

public class SpaceFarmBlockEntity extends BlockEntity implements TickingBlockEntity {

    public BlockState cropState = null;

    public SpaceFarmBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(BlockEntitiesRegistry.SPACE_FARM.get(), worldPosition, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if(cropState != null) {
            output.store("crop", BlockState.CODEC, cropState);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        cropState = input.read("crop", BlockState.CODEC).orElse(null);
    }

    @Override
    public void tick(Level level, BlockState state) {

        if(cropState == null) {
            return;
        }

        Block block = cropState.getBlock();

        if(block instanceof CropBlock cropBlock && level instanceof ServerLevel) {

            int age = cropBlock.getAge(cropState);
            if (age < cropBlock.getMaxAge()) {
                float growthSpeed = 1.0F;

                RandomSource random = level.getRandom();

                if (random.nextInt((int)(200 / growthSpeed) + 1) == 0) {
                    this.cropState = cropBlock.getStateForAge(age + 1);
                    setChanged();
                }
            }
        }
    }

    public void setCrop(CropBlock crop) {
        this.cropState = crop.defaultBlockState();
        setChanged();
    }

    public void performBoneMeal() {
        CropBlock crop = (CropBlock) this.cropState.getBlock();
        int age = Math.min(crop.getMaxAge(), crop.getAge(this.cropState) + Mth.nextInt(level.getRandom(), 2, 5));
        this.cropState = crop.getStateForAge(age);
        setChanged();
    }


    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = this.saveWithoutMetadata(registries);

        if(cropState != null) {
            tag.put("crop", BlockState.CODEC.encodeStart(NbtOps.INSTANCE, cropState).result().orElseThrow(() -> new IllegalStateException("Failed to encode crop state")));
        }


        return tag;
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
