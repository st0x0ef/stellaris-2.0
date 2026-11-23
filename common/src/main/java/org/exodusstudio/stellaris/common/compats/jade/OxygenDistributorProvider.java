package org.exodusstudio.stellaris.common.compats.jade;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.OxygenDistributorBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.OxygenUtils.OxygenStatus;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum OxygenDistributorProvider implements StreamServerDataProvider<BlockAccessor, OxygenDistributorProvider.Data> {
    INSTANCE;

    private static final Identifier UID = IdentifierUtils.id("oxygen_distributor");

    public record Data(OxygenStatus status, int oxygenatedBlocks) {
        private static final StreamCodec<RegistryFriendlyByteBuf, OxygenStatus> STATUS_CODEC = ByteBufCodecs.VAR_INT.map(i -> OxygenStatus.values()[i], OxygenStatus::ordinal).cast();

        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                STATUS_CODEC, Data::status,
                ByteBufCodecs.VAR_INT, Data::oxygenatedBlocks,
                Data::new
        );
    }

    @Override
    public @Nullable Data streamData(BlockAccessor accessor) {
        if (!Stellaris.CONFIG.oxygenConfig.enableOxygenSystem || !(accessor.getBlockEntity() instanceof OxygenDistributorBlockEntity distributor)) {
            return null;
        }
        return new Data(distributor.getStatus(), distributor.getOxygenatedPositions().size());
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec() {
        return Data.STREAM_CODEC;
    }

    @Override
    public Identifier getUid() {
        return UID;
    }

    public enum Client implements IBlockComponentProvider {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
            OxygenDistributorProvider.INSTANCE.decodeFromData(accessor).ifPresent(data -> {
                IThemeHelper theme = IThemeHelper.get();
                tooltip.add(switch (data.status()) {
                    case OK -> theme.success(Component.translatable("jade.stellaris.oxygenated_blocks", data.oxygenatedBlocks()));
                    case BREATHABLE_ATMOSPHERE -> theme.info(Component.translatable(data.status().translationKey()));
                    default -> theme.danger(Component.translatable(data.status().translationKey()));
                });
            });
        }

        @Override
        public Identifier getUid() {
            return UID;
        }
    }
}
