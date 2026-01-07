package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.exodusstudio.stellaris.common.blocks.base.BaseMachineBlock;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.NotNull;

public class OxygenDistributorBlock extends BaseMachineBlock {

    public OxygenDistributorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<OxygenDistributorBlock> codec() {
        return simpleCodec(OxygenDistributorBlock::new);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntitiesRegistry.OXYGEN_DISTRIBUTOR.get();
    }

    @Override
    public boolean hasTicker(Level level) {
        return !level.isClientSide();
    }

}
