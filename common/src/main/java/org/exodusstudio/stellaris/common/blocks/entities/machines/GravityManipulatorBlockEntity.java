package org.exodusstudio.stellaris.common.blocks.entities.machines;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.menus.GravityManipulatorMenu;
import org.exodusstudio.stellaris.common.network.packets.SyncGravityManipulatorDataPacketC2S;
import org.exodusstudio.stellaris.common.network.packets.SyncGravityManipulatorDataPacketS2C;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.utils.Utils;

public class GravityManipulatorBlockEntity extends BaseEnergyContainerBlockEntity {

    private double gravity = 9.81;

    public GravityManipulatorBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.GRAVITY_MANIPULATOR.get(), pos, state, 3000);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("item.stellaris.gravity_manipulator");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new GravityManipulatorMenu(containerId, this);
    }

    @Override
    public void tick(Level level, BlockState state) {
        if  (isActive() && this.level != null) {
            this.energyContainer.extract(1, false); // TODO : adjust energy consumption based on new gravity
            syncDataAccess();
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putDouble("gravity", getGravity());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        setGravity(input.getDoubleOr("gravity", getGravity()), false); // can't sync since there's no player yet
    }

    public boolean isActive() {
        return this.energyContainer.getEnergy() > 0;
    }

    public double getDifferenceGravity(double targetGravity) {
        return this.gravity - targetGravity;
    }

    public double getGravity() {
        return Mth.clamp(this.gravity, 0.0, Stellaris.CONFIG.gravityConfig.maxGravityManipulatorValue);
    }

    public double getNormalizedGravity() {
        return Mth.clamp(this.gravity / Stellaris.CONFIG.gravityConfig.maxGravityManipulatorValue, 0.0, 1.0);
    }

    public void setGravity(double gravity, boolean shouldSyncC2S) {
        if (Math.abs(getGravity() - gravity) < 0.01) {
            return;
        }

        this.gravity = Mth.clamp(gravity, 0.0, Stellaris.CONFIG.gravityConfig.maxGravityManipulatorValue);

        if  (shouldSyncC2S) {
            syncDataAccess();
        }
    }

    public void syncDataAccess() {
        if (this.level != null) {
            if (this.level.isClientSide()) {
                NetworkManager.sendToServer(new SyncGravityManipulatorDataPacketC2S(getBlockPos(), getGravity()));
            } else {
                NetworkManager.sendToPlayers(Utils.getPlayersIn3x3Chunks(level, worldPosition), new SyncGravityManipulatorDataPacketS2C(getBlockPos(), getGravity()));
            }
        }
    }
}
