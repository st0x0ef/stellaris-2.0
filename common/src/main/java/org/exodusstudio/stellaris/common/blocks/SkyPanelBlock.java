package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.exodusstudio.stellaris.common.blocks.base.BaseMachineBlock;
import org.jetbrains.annotations.NotNull;

public class SkyPanelBlock extends BaseMachineBlock {

    public final SkyPanelType type;

    public SkyPanelBlock(Properties properties, SkyPanelType type) {
        super(properties);
        this.type = type;
    }

    @Override
    protected @NotNull MapCodec<? extends SkyPanelBlock> codec() {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                propertiesCodec(),
                SkyPanelType.CODEC.fieldOf("type").forGetter(panel -> panel.type)
        ).apply(instance, SkyPanelBlock::new));
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return type.getBlockEntityType();
    }

    @Override
    public boolean hasTicker(Level level) {
        return !level.isClientSide();
    }
}
