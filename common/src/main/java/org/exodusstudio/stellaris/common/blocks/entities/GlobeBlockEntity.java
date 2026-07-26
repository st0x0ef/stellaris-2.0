package org.exodusstudio.stellaris.common.blocks.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;

public class GlobeBlockEntity extends BlockEntity {

    private float rotationalInertia;
    private float yaw;
    private float yaw0;

    public GlobeBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.GLOBE.get(), pos, state);
        rotationalInertia = 0.0f;
        yaw = 16.0f; // a little rotation for placement looks
        yaw0 = yaw;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putFloat("inertia", this.rotationalInertia);
        //output.putFloat("yaw", this.yaw); this caused desync issues
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.rotationalInertia = input.getFloatOr("inertia", 0.0f);
        //this.yaw = input.getFloatOr("yaw", 0.0f);
        //this.yaw0 = this.yaw;
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
        if (this.level == null)
            return;

        yaw0 = yaw;

        if (rotationalInertia > 0) {
            yaw = Mth.wrapDegrees(yaw - rotationalInertia);

            rotationalInertia = Math.max(0, rotationalInertia * 0.96f);
            if (rotationalInertia <= 0.0005f)
                rotationalInertia = 0.0f;
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
