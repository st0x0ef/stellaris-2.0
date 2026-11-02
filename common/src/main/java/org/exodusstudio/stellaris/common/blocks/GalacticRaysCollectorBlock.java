package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.exodusstudio.stellaris.common.blocks.base.BaseMachineBlock;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.NotNull;

public class GalacticRaysCollectorBlock extends BaseMachineBlock {


    public GalacticRaysCollectorBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends GalacticRaysCollectorBlock> codec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                propertiesCodec()
        ).apply(instance, GalacticRaysCollectorBlock::new));
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntitiesRegistry.GALACTIC_RAYS_COLLECTOR.get();
    }

    @Override
    public boolean hasTicker(Level level) {
        return !level.isClientSide();
    }
}
