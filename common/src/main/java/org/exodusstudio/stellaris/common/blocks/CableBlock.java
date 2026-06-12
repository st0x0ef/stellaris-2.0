package org.exodusstudio.stellaris.common.blocks;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.base.BaseCableBlock;
import org.exodusstudio.stellaris.common.blocks.entities.machines.CableBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CableBlock extends BaseCableBlock {

    public final int transferRate;

    public CableBlock(BlockBehaviour.Properties properties, final int transferRate) {
        super(properties);
        this.transferRate = transferRate;
    }

    @Override
    public boolean isConnectable(Level level, BlockPos pos, Direction direction) {
        BlockState targetState = level.getBlockState(pos);

        // Cables are bufferless and expose no capability, so connect to sibling cables by block type.
        if (targetState.getBlock() instanceof CableBlock) {
            return true;
        }

        if (Capabilities.Energy.BLOCK.getCapability(level, pos, direction) != null) {
            return true;
        }

        if (targetState.getBlock() instanceof PumpjackProxyBlock) {
            BlockPos mainPos = PumpjackProxyBlock.getMainPos(pos, targetState);
            return Capabilities.Energy.BLOCK.getCapability(level, mainPos, direction) != null;
        }

        return false;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(pos, state);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntitiesRegistry.CABLES.get();
    }

    @Override
    public boolean hasTicker(Level level) {
        return false;
    }

    @Override
    protected @NotNull MapCodec<? extends CableBlock> codec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                propertiesCodec(),
                Codec.INT.fieldOf("transferRate").forGetter(cable -> cable.transferRate)
        ).apply(instance, CableBlock::new));
    }

}
