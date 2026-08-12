package org.exodusstudio.stellaris.common.compats.jade;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import org.exodusstudio.stellaris.common.blocks.entities.machines.SpaceFarmBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.jspecify.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.JadeIds;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.Element;
import snownee.jade.api.ui.JadeUI;

public class SpaceFarmBlockProvider implements IBlockComponentProvider {
    public static final SpaceFarmBlockProvider INSTANCE = new SpaceFarmBlockProvider();

    public SpaceFarmBlockProvider() {
    }

    public @Nullable Element getIcon(BlockAccessor accessor, IPluginConfig config, @Nullable Element currentIcon) {

        SpaceFarmBlockEntity entity = accessor.typedBlockEntity();
        if (entity.cropState != null) {
            CropBlock crop = (CropBlock) entity.cropState.getBlock();
            return JadeUI.item(new ItemStack(crop.asItem()));
        }

        return JadeUI.item(new ItemStack(BlocksRegistry.SPACE_FARM.item().get()));
    }

    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        SpaceFarmBlockEntity entity = accessor.typedBlockEntity();
        if (entity.cropState != null) {

            CropBlock crop = (CropBlock) entity.cropState.getBlock();

            addMaturityTooltip(tooltip, (float)crop.getAge(entity.cropState) / (float)crop.getMaxAge());
        }

    }

    private static void addMaturityTooltip(ITooltip tooltip, float growthValue) {
        MutableComponent component;
        if (growthValue < 1.0F) {
            component = IThemeHelper.get().info(String.format("%.0f%%", growthValue * 100.0F));
        } else {
            component = IThemeHelper.get().success(Component.translatable("tooltip.jade.crop_mature"));
        }

        tooltip.add(Component.translatable("tooltip.jade.crop_growth", new Object[]{component}));
    }

    public Identifier getUid() {
        return JadeIds.MC_CROP_PROGRESS;
    }
}