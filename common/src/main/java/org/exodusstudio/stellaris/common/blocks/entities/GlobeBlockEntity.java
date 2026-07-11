package org.exodusstudio.stellaris.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

public class GlobeBlockEntity extends BlockEntity {

    private float rotationalInertia = 0.0f;
    private float yaw = 0.0f;
    private float yaw0 = 0.0f;

    public GlobeBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.GLOBE.get(), pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putFloat("inertia", this.rotationalInertia);
        output.putFloat("yaw", this.yaw);
        output.putFloat("yaw0", this.yaw0);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.rotationalInertia = input.getFloatOr("inertia", 0.0f);
        this.yaw = input.getFloatOr("yaw", 0.0f);
        this.yaw0 = input.getFloatOr("yaw0", 0.0f);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithFullMetadata(registries);
    }

    public void tick() {
        if (this.level == null) {
            return;
        }
        if (this.rotationalInertia > 0) {
            this.rotationalInertia -= 0.0075f;
            if (this.rotationalInertia < 0) {
                this.rotationalInertia = 0;
            }
            this.yaw0 = this.yaw;
            this.yaw -= this.rotationalInertia;
            if (this.rotationalInertia == 0 && !this.level.isClientSide()) {
                this.setChanged();
            }
        }
    }

    public float getRotationalInertia() {
        return this.rotationalInertia;
    }

    public void setRotationalInertia(float value) {
        this.rotationalInertia = value;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getYaw0() {
        return this.yaw0;
    }
}
