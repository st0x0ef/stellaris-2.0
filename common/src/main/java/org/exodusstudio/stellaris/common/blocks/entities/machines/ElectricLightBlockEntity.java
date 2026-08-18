package org.exodusstudio.stellaris.common.blocks.entities.machines;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.blocks.ElectricLightBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.menus.ElectricLightMenu;
import org.exodusstudio.stellaris.common.network.packets.SyncElectricLightDataPacketC2S;
import org.exodusstudio.stellaris.common.network.packets.SyncElectricLightDataPacketS2C;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.utils.Utils;

public class ElectricLightBlockEntity extends BaseEnergyContainerBlockEntity {

    public static final String BRIGHTNESS_TAG = "brightness";
    public static final int CAPACITY = 1000;

    private static final int TICKS_PER_ENERGY_PER_STEP = 10;

    private int brightness = 0;
    private int ticksSinceDrain = 0;

    public ElectricLightBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.ELECTRIC_LIGHT.get(), pos, state, CAPACITY);
    }

    public static int ticksPerEnergy(int brightness) {
        return TICKS_PER_ENERGY_PER_STEP * (ElectricLightBlock.MAX_BRIGHTNESS + 1 - brightness);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("block.stellaris.electric_light");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new ElectricLightMenu(containerId, this);
    }

    @Override
    public void tick(Level level, BlockState state) {
        if (isLit()) {
            ticksSinceDrain++;
            if (ticksSinceDrain >= ticksPerEnergy(brightness)) {
                ticksSinceDrain = 0;
                this.energyContainer.extract(1, false);
            }
        } else {
            ticksSinceDrain = 0;
        }

        updateBlockState(level);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(BRIGHTNESS_TAG, this.brightness);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.brightness = clampBrightness(input.getIntOr(BRIGHTNESS_TAG, this.brightness));
    }

    public boolean isLit() {
        return this.brightness > 0 && this.energyContainer.getEnergy() > 0;
    }

    public int getBrightness() {
        return this.brightness;
    }

    public int getEffectiveBrightness() {
        return isLit() ? this.brightness : 0;
    }

    public void setBrightness(int brightness, boolean shouldSync) {
        int clamped = clampBrightness(brightness);
        if (clamped == this.brightness) {
            return;
        }

        this.brightness = clamped;
        setChanged();

        if (this.level != null && !this.level.isClientSide()) {
            updateBlockState(this.level);
        }

        if (shouldSync) {
            syncDataAccess();
        }
    }

    public void syncDataAccess() {
        if (this.level == null) {
            return;
        }

        if (this.level.isClientSide()) {
            NetworkManager.sendToServer(new SyncElectricLightDataPacketC2S(getBlockPos(), getBrightness()));
        } else {
            NetworkManager.sendToPlayers(Utils.getPlayersIn3x3Chunks(this.level, worldPosition),
                    new SyncElectricLightDataPacketS2C(getBlockPos(), getBrightness()));
        }
    }

    private static int clampBrightness(int brightness) {
        return Mth.clamp(brightness, 0, ElectricLightBlock.MAX_BRIGHTNESS);
    }

    private void updateBlockState(Level level) {
        BlockState state = getBlockState();
        int effective = getEffectiveBrightness();

        if (state.getValue(ElectricLightBlock.BRIGHTNESS) != effective) {
            level.setBlock(worldPosition, state.setValue(ElectricLightBlock.BRIGHTNESS, effective), Block.UPDATE_ALL);
        }
    }
}
