package org.exodusstudio.stellaris.common.blocks;

import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class AshLayerBlock extends SnowLayerBlock {
    public static final IntegerProperty LAYERS = BlockStateProperties.LAYERS;

    public AshLayerBlock(Properties properties) {
        super(properties);
    }
}
