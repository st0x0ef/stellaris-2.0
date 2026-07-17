package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.TabSwitchableBlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Packet to open rocket station menus (crafting, upgrading)
 */
public class OpenBlockEntityMenusPacket implements CustomPacketPayload {

    public static CustomPacketPayload.Type<OpenBlockEntityMenusPacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(Stellaris.MOD_ID, "open_engine_station_menu"));

    private final BlockEntityMenuProvider menuProvider;
    private final BlockPos stationPos;

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenBlockEntityMenusPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull OpenBlockEntityMenusPacket decode(RegistryFriendlyByteBuf buf) {
            return new OpenBlockEntityMenusPacket(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, OpenBlockEntityMenusPacket packet) {
            buf.writeUtf(packet.menuProvider.id);
            buf.writeBlockPos(packet.stationPos);
        }
    };

    public OpenBlockEntityMenusPacket(RegistryFriendlyByteBuf buffer) {
        this.menuProvider = BlockEntityMenuProvider.TYPES.get(buffer.readUtf());
        this.stationPos = buffer.readBlockPos();
    }

    public OpenBlockEntityMenusPacket(BlockEntityMenuProvider menuProvider, BlockPos stationPos) {
        this.menuProvider = menuProvider;
        this.stationPos = stationPos;
    }

    public static void handle(OpenBlockEntityMenusPacket packet, NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (context.getPlayer() instanceof ServerPlayer player) {
                if (packet.menuProvider == null) {
                    return;
                }

                if (player.distanceToSqr(Vec3.atCenterOf(packet.stationPos)) > 64.0) {
                    return;
                }

                TabSwitchableBlockEntity be = player.level().getBlockEntity(packet.stationPos) instanceof TabSwitchableBlockEntity t ? t : null;
                if (be != null) be.setTabSwitching(true);

                ExtendedMenuProvider menuProvider = packet.menuProvider.menu.apply(packet.stationPos);
                MenuRegistry.openExtendedMenu(player, menuProvider);

                if (be != null) be.setTabSwitching(false);
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record BlockEntityMenuProvider(String id, Function<BlockPos, ExtendedMenuProvider> menu) {

        static final HashMap<String, BlockEntityMenuProvider> TYPES = new HashMap<>();

        public BlockEntityMenuProvider(String id, Function<BlockPos, ExtendedMenuProvider> menu) {
            this.id = id;
            this.menu = menu;
            TYPES.put(this.id, this);
        }
    }
}
