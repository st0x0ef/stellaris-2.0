package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.exodusstudio.stellaris.common.blocks.base.BaseLitMachineBlock;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.NotNull;

public class ElectrolyzerBlock extends BaseLitMachineBlock {

    public ElectrolyzerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<ElectrolyzerBlock> codec() {
        return simpleCodec(ElectrolyzerBlock::new);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntitiesRegistry.ELECTROLYZER.get();
    }

    @Override
    public boolean hasTicker(Level level) {
        return !level.isClientSide();
    }

}
