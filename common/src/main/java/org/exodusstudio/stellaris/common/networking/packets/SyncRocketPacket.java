package org.exodusstudio.stellaris.common.networking.packets;

import dev.architectury.networking.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.exodusstudio.stellaris.common.entities.vehicles.RocketEntity;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModules;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.InventorySaver;
import org.jetbrains.annotations.NotNull;

public record SyncRocketPacket(int entityId, Modules<RocketModule> rocketModules, InventorySaver inventory) implements CustomPacketPayload {

    public static final Type<SyncRocketPacket> TYPE = new Type<>(IdentifierUtils.id("sync_rocket_module"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncRocketPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, SyncRocketPacket::entityId,
            RocketModules.STREAM_CODEC, SyncRocketPacket::rocketModules,
            InventorySaver.STREAM_CODEC, SyncRocketPacket::inventory,
            SyncRocketPacket::new
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final SyncRocketPacket data, final NetworkManager.PacketContext context) {
        context.queue(() -> {
            ClientLevel level = Minecraft.getInstance().level;
            if (level != null && level.getEntity(data.entityId) instanceof RocketEntity rocketEntity) {
                rocketEntity.setRocketModules(data.rocketModules);
                data.inventory.readInventory(rocketEntity.inventory);
            }
        });
    }
}
