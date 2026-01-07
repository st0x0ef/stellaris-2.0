package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.exodusstudio.stellaris.common.blocks.base.BaseLitMachineBlock;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.NotNull;

public class OxygenPropagatorBlock extends BaseLitMachineBlock {

    public OxygenPropagatorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<OxygenPropagatorBlock> codec() {
        return simpleCodec(OxygenPropagatorBlock::new);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntitiesRegistry.OXYGEN_PROPAGATOR.get();
    }

    @Override
    public boolean hasTicker(Level level) {
        return !level.isClientSide();
    }

}
