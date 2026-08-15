package org.exodusstudio.stellaris.common.blocks;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.utils.DayCycleUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;
import java.util.function.Supplier;

public enum SkyPanelType implements StringRepresentable {
    SOLAR("solar", DayCycleUtils::isDay, BlockEntitiesRegistry.SOLAR_PANEL::get),
    STAR_LIGHT("star_light", DayCycleUtils::isNight, BlockEntitiesRegistry.STAR_LIGHT_PANEL::get);

    public static final Codec<SkyPanelType> CODEC = StringRepresentable.fromEnum(SkyPanelType::values);

    private final String name;
    private final Predicate<Level> skyCondition;
    private final Supplier<BlockEntityType<?>> blockEntityType;

    SkyPanelType(String name, Predicate<Level> skyCondition, Supplier<BlockEntityType<?>> blockEntityType) {
        this.name = name;
        this.skyCondition = skyCondition;
        this.blockEntityType = blockEntityType;
    }

    public boolean canGenerate(Level level) {
        return skyCondition.test(level);
    }

    public BlockEntityType<?> getBlockEntityType() {
        return blockEntityType.get();
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
