package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.MenuUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.function.Function;

/**
 * Packet to open rocket station menus (crafting, upgrading)
 */
public class OpenRocketStationMenusPacket implements CustomPacketPayload {

    public static CustomPacketPayload.Type<OpenRocketStationMenusPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Stellaris.MOD_ID, "open_rocket_station_menu"));

    /**
     * Menu provider for rocket crafting station
     */
    public static final RocketStationMenuProvider ROCKET_CRAFTING = new RocketStationMenuProvider("crafting", MenuUtils::createRocketStationMenu);
    public static final RocketStationMenuProvider ROCKET_UPGRADE = new RocketStationMenuProvider("upgrade", MenuUtils::createRocketUpgraderMenu);

    private final String menuId;
    private final BlockPos stationPos;

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenRocketStationMenusPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull OpenRocketStationMenusPacket decode(RegistryFriendlyByteBuf buf) {
            return new OpenRocketStationMenusPacket(buf);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, OpenRocketStationMenusPacket packet) {
            buf.writeUtf(packet.menuId);
            buf.writeBlockPos(packet.stationPos);
        }
    };

    public OpenRocketStationMenusPacket(RegistryFriendlyByteBuf buffer) {
        this.menuId = buffer.readUtf();
        this.stationPos = buffer.readBlockPos();
    }

    public OpenRocketStationMenusPacket(String menuId, BlockPos stationPos) {
        this.menuId = menuId;
        this.stationPos = stationPos;
    }

    public static void handle(OpenRocketStationMenusPacket packet, NetworkManager.PacketContext context) {
        if (context.getPlayer() instanceof ServerPlayer player) {
            RocketStationMenuProvider provider = RocketStationMenuProvider.TYPES.get(packet.menuId);
            ExtendedMenuProvider menuProvider = provider.menu.apply(packet.stationPos);
            MenuRegistry.openExtendedMenu(player, menuProvider);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record RocketStationMenuProvider(String id, Function<BlockPos, ExtendedMenuProvider> menu) {

        static final HashMap<String, RocketStationMenuProvider> TYPES = new HashMap<>();

        public RocketStationMenuProvider(String id, Function<BlockPos, ExtendedMenuProvider> menu) {
            this.id = id;
            this.menu = menu;
            TYPES.put(this.id, this);
        }
    }

}
