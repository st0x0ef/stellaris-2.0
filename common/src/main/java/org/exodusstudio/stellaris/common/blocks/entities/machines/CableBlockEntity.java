package org.exodusstudio.stellaris.common.blocks.entities.machines;

import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import com.fej1fun.potentials.providers.EnergyProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.CableBlock;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.transport.PassthroughEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * A cable is a bufferless connector: it stores no energy. Transport between Stellaris machines is
 * handled by {@link org.exodusstudio.stellaris.common.transport.TransportGraph}, which floods the
 * connected cable network when a producer pushes into it. The cable additionally exposes a stateless
 * passthrough energy capability ({@link PassthroughEnergyStorage}) so other mods' cables/conduits can
 * push directly into a Stellaris line; that capability routes straight into the network and stores
 * nothing. The block entity itself neither ticks nor persists anything.
 */
public class CableBlockEntity extends BlockEntity implements EnergyProvider.BLOCK {

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.CABLES.get(), pos, state);
    }

    @Override
    public @Nullable UniversalEnergyStorage getEnergy(@Nullable Direction direction) {
        if (level == null || !(getBlockState().getBlock() instanceof CableBlock cable)) {
            return null;
        }
        return new PassthroughEnergyStorage(level, worldPosition, direction, cable.transferRate);
    }
}
