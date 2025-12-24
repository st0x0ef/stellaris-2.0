package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.exodusstudio.stellaris.common.menus.RocketMenu;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OpenRocketMenuPacket implements CustomPacketPayload {

    public int rocketId;
    public static final Type<OpenRocketMenuPacket> TYPE = new Type<>(ResourceLocationUtils.id("open_rocket_menu"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenRocketMenuPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull OpenRocketMenuPacket decode(RegistryFriendlyByteBuf buf) {
            return new OpenRocketMenuPacket(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, OpenRocketMenuPacket packet) {
            buf.writeInt(packet.rocketId);
        }
    };

    public OpenRocketMenuPacket(RegistryFriendlyByteBuf buffer) {
        this.rocketId = buffer.readInt();
    }

    public OpenRocketMenuPacket(int rocketId) {
        this.rocketId = rocketId;
    }

    public static void handle(OpenRocketMenuPacket packet, NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof ServerPlayer player) {
            Stellaris.LOG.error("id: " + packet.rocketId);
            RocketEntity entity = (RocketEntity) player.level().getEntity(packet.rocketId);
            Stellaris.LOG.error(entity.toString());

            MenuRegistry.openExtendedMenu(player, new ExtendedMenuProvider() {
                @Override
                public void saveExtraData(FriendlyByteBuf buf) {
                    buf.writeInt(packet.rocketId);
                }

                @Override
                public Component getDisplayName() {
                    return Component.translatable("entity.stellaris.rocket");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int syncId, Inventory inv, Player player) {
                    FriendlyByteBuf packetBuffer = new FriendlyByteBuf(Unpooled.buffer());
                    packetBuffer.writeVarInt(packet.rocketId);
                    return new RocketMenu(syncId, inv, entity.inventory, entity);
                }
            });
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}