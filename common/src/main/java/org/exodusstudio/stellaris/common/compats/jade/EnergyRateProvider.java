package org.exodusstudio.stellaris.common.compats.jade;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseEnergyContainerBlockEntity;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.BaseGeneratorBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum EnergyRateProvider implements StreamServerDataProvider<BlockAccessor, EnergyRateProvider.Rate> {
    INSTANCE;

    private static final Identifier UID = IdentifierUtils.id("energy_rate");

    public record Rate(boolean generating, int perTick) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Rate> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Rate::generating,
                ByteBufCodecs.VAR_INT, Rate::perTick,
                Rate::new
        );
    }

    @Override
    public @Nullable Rate streamData(BlockAccessor accessor) {
        if (accessor.getBlockEntity() instanceof BaseGeneratorBlockEntity generator) {
            return new Rate(true, generator.getGeneratedLastTick());
        }
        if (accessor.getBlockEntity() instanceof BaseEnergyContainerBlockEntity machine && machine.getMaxEnergyUsage() > 0) {
            return new Rate(false, machine.getCurrentEnergyUsage());
        }
        return null;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Rate> streamCodec() {
        return Rate.STREAM_CODEC;
    }

    @Override
    public Identifier getUid() {
        return UID;
    }

    public enum Client implements IBlockComponentProvider {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            EnergyRateProvider.INSTANCE.decodeFromData(accessor).ifPresent(rate -> {
                String key = rate.generating() ? "jade.stellaris.energy_generation" : "jade.stellaris.energy_usage";
                tooltip.add(Component.translatable(key, IThemeHelper.get().info(rate.perTick())));
            });
        }

        @Override
        public Identifier getUid() {
            return UID;
        }
    }
}
