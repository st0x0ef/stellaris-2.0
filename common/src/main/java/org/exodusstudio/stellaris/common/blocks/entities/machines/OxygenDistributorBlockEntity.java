package org.exodusstudio.stellaris.common.blocks.entities.machines;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.fej1fun.potentials.providers.FluidProvider;
import dev.architectury.fluid.FluidStack;
import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.fluid.FluidUtil;
import org.exodusstudio.stellaris.common.fluid.SingleFluidStorage;
import org.exodusstudio.stellaris.common.menus.OxygenDistributorMenu;
import org.exodusstudio.stellaris.common.network.packets.SyncFluidPacketWithoutDirection;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.registries.FluidsRegistry;
import org.exodusstudio.stellaris.common.utils.OxygenUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OxygenDistributorBlockEntity extends BaseEnergyContainerBlockEntity implements FluidProvider.BLOCK {

    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;

    public static final long OXYGEN_CAPACITY = 10000;

    private static final String STATUS_TAG = "oxygen_status";

    private final Set<BlockPos> oxygenatedPosition;
    private final Set<ChunkPos> coveredChunks;

    /// Insert-only to the outside: pipes can push oxygen in, but never siphon it back out.
    private final SingleFluidStorage oxygenTank;

    private int oxygenDistributedTickCounter = 0;
    private boolean isActive = false;
    private OxygenUtils.OxygenStatus status = OxygenUtils.OxygenStatus.NO_ENERGY;

    private boolean computed = false;

    public OxygenDistributorBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntitiesRegistry.OXYGEN_DISTRIBUTOR.get(), blockPos, blockState);

        this.oxygenatedPosition = new HashSet<>();
        this.coveredChunks = new HashSet<>();

        this.oxygenTank = new SingleFluidStorage(OXYGEN_CAPACITY, OXYGEN_CAPACITY, 0) {
            @Override
            protected void onChange() {
                setChanged();
                if (level != null && level.getServer() != null && !level.getServer().getPlayerList().getPlayers().isEmpty()) {
                    NetworkManager.sendToPlayers(level.getServer().getPlayerList().getPlayers(),
                            new SyncFluidPacketWithoutDirection(new FluidAmountMapDataComponent(List.of(getFluidInTank(0).getFluid()), List.of(getFluidValueInTank())), 0, getBlockPos()));
                }
            }

            @Override
            public boolean isFluidValid(int tank, FluidStack stack) {
                Fluid fluid = stack.getFluid();
                return fluid.isSame(FluidsRegistry.OXYGEN_STILL.get()) || fluid.isSame(FluidsRegistry.OXYGEN_FLOWING.get());
            }
        };
    }

    @Override
    public void tick(Level level, BlockState state) {
        // The slots feed and empty the tank every tick, independently of the oxygen update interval below.
        if (!getItem(INPUT_SLOT).isEmpty()) {
            FluidUtil.moveFluidFromItem(0, INPUT_SLOT, INPUT_SLOT, this, oxygenTank, Long.MAX_VALUE);
        }
        if (!getItem(OUTPUT_SLOT).isEmpty()) {
            FluidUtil.moveFluidToItem(0, oxygenTank, OUTPUT_SLOT, OUTPUT_SLOT, this, Long.MAX_VALUE, true);
        }

        if (!Stellaris.CONFIG.oxygenConfig.enableOxygenSystem) {
            return;
        }

        if (oxygenDistributedTickCounter > 0) {
            oxygenDistributedTickCounter--;
            return;
        }

        distributeOxygen(level);
    }

    private void distributeOxygen(Level level) {
        // Reset here rather than in tick, so a fill triggered by the first query does not get
        // charged for a second time when this block entity ticks later in the same tick.
        oxygenDistributedTickCounter = Stellaris.CONFIG.oxygenConfig.oxygenUpdateInterval;
        computed = true;
        oxygenatedPosition.clear();
        coveredChunks.clear();

        OxygenUtils.OxygenStatus newStatus;

        if (OxygenUtils.hasBreathableAtmosphere(level)) {
            newStatus = OxygenUtils.OxygenStatus.BREATHABLE_ATMOSPHERE;
        } else if (energyContainer.getEnergy() <= 0) {
            newStatus = OxygenUtils.OxygenStatus.NO_ENERGY;
        } else if (oxygenTank.isEmpty()) {
            newStatus = OxygenUtils.OxygenStatus.NO_OXYGEN;
        } else {
            OxygenUtils.AllowedArea area = OxygenUtils.getAllowedArea(level, worldPosition);
            coveredChunks.addAll(area.allowedChunks());

            OxygenUtils.OxygenResult result = OxygenUtils.propagateOxygen(level, worldPosition, area);
            newStatus = result.status();

            if (newStatus == OxygenUtils.OxygenStatus.OK && !result.positions().isEmpty()) {
                int livingEntitiesCount = OxygenUtils.getEntityWhoNeedsOxygen(level, result.positions());

                if (livingEntitiesCount == 0) {
                    oxygenatedPosition.addAll(result.positions());
                    energyContainer.extract(1, false);
                } else if (oxygenTank.getFluidValueInTank() >= livingEntitiesCount) {
                    oxygenTank.drainWithoutLimits(livingEntitiesCount, false);
                    energyContainer.extract(1, false);
                    oxygenatedPosition.addAll(result.positions());
                } else {
                    newStatus = OxygenUtils.OxygenStatus.NOT_ENOUGH_OXYGEN;
                }
            }
        }

        boolean newIsActive = !oxygenatedPosition.isEmpty();
        if (newIsActive != isActive || newStatus != status) {
            isActive = newIsActive;
            status = newStatus;
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    // Registered from both hooks because vanilla gives no single "added to the level" callback and
    // the two are called in either order depending on how the block entity got here. Registration
    // is a set insert, so doing it twice is free.
    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        OxygenUtils.registerDistributor(this);
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        OxygenUtils.registerDistributor(this);
    }

    private void ensureComputed() {
        if (computed || level == null || level.isClientSide() || !Stellaris.CONFIG.oxygenConfig.enableOxygenSystem) {
            return;
        }

        distributeOxygen(level);
    }

    @Override
    public void setRemoved() {
        OxygenUtils.unregisterDistributor(this);
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        oxygenTank.save(output, "oxygen_tank");
        output.putString(STATUS_TAG, status.name());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        oxygenTank.load(input, "oxygen_tank");
        status = input.getString(STATUS_TAG)
                .map(OxygenDistributorBlockEntity::readStatus)
                .orElse(OxygenUtils.OxygenStatus.NO_ENERGY);
        setChanged();
    }

    private static OxygenUtils.OxygenStatus readStatus(String name) {
        try {
            return OxygenUtils.OxygenStatus.valueOf(name);
        } catch (IllegalArgumentException exception) {
            return OxygenUtils.OxygenStatus.NO_ENERGY;
        }
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return Component.translatable("item.stellaris.oxygen_distributor");
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        if (inventory.player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, new SyncFluidPacketWithoutDirection(
                    new FluidAmountMapDataComponent(List.of(oxygenTank.getFluidInTank(0).getFluid()), List.of(oxygenTank.getFluidValueInTank())),
                    0, getBlockPos()));
        }
        return new OxygenDistributorMenu(containerId, inventory, this, this);
    }

    @Override
    public int getContainerSize() {
        return 2;
    }

    @Override
    public @Nullable SingleFluidStorage getFluidTank(@Nullable Direction direction) {
        return oxygenTank;
    }

    public SingleFluidStorage getOxygenTank() {
        return oxygenTank;
    }

    public boolean isOxygenated(BlockPos pos) {
        ensureComputed();
        return coversChunk(pos) && oxygenatedPosition.contains(pos);
    }

    public OxygenUtils.OxygenStatus getStatus() {
        return status;
    }

    public boolean coversChunk(BlockPos pos) {
        return coveredChunks.contains(ChunkPos.containing(pos));
    }

    public Set<ChunkPos> getCoveredChunks() {
        return coveredChunks;
    }

    public Set<BlockPos> getOxygenatedPositions() {
        return Collections.unmodifiableSet(oxygenatedPosition);
    }
}
