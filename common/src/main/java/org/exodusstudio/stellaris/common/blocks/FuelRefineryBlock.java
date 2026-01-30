package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.exodusstudio.stellaris.common.blocks.base.BaseLitMachineBlock;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.NotNull;

public class FuelRefineryBlock extends BaseLitMachineBlock {

    public FuelRefineryBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntitiesRegistry.FUEL_REFINERY.get();
    }

    @Override
    public boolean hasTicker(Level level) {
        return !level.isClientSide();
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(FuelRefineryBlock::new);
    }
}
