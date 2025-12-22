package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.exodusstudio.stellaris.common.module.Modules;
import org.exodusstudio.stellaris.common.module.rocket.RocketModule;
import org.exodusstudio.stellaris.common.module.rocket.RocketModules;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.jetbrains.annotations.NotNull;

public record SyncRocketModule(int entityId, Modules<RocketModule> rocketModules) implements CustomPacketPayload {

    public static final Type<SyncRocketModule> TYPE = new Type<>(ResourceLocationUtils.id("sync_rocket_module"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncRocketModule> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncRocketModule::entityId,
            RocketModules.STREAM_CODEC, SyncRocketModule::rocketModules,
            SyncRocketModule::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final SyncRocketModule data, final NetworkManager.PacketContext context) {
        context.queue(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null && level.getEntity(data.entityId) instanceof RocketEntity rocketEntity) {
                rocketEntity.setRocketModules(data.rocketModules);
            }
        });
    }
}
