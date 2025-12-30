package org.exodusstudio.stellaris.common.network.packets;

import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.ExtendedMenuProvider;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.RocketStationBlock;
import org.exodusstudio.stellaris.common.menus.RocketStationMenu;
import org.exodusstudio.stellaris.common.menus.UpgradeStationMenu;
import org.exodusstudio.stellaris.common.network.NetworkRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

public class OpenRocketStationMenusPacket implements CustomPacketPayload {

    public static CustomPacketPayload.Type<OpenRocketStationMenusPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Stellaris.MOD_ID, "open_rocket_station_menu"));


    public static final RocketStationMenuProvider ROCKET_CRAFTING = new RocketStationMenuProvider("crafting", ( pos) -> new ExtendedMenuProvider() {
        @Override
        public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
            return RocketStationMenu.create(i, inventory, pos);
        }

        @Override
        public void saveExtraData(FriendlyByteBuf buf) {
            buf.writeBlockPos(pos);
        }
        @Override
        public Component getDisplayName() {
            return Component.translatable("container.stellaris.rocket_crafting");
        }
    });

    public static final RocketStationMenuProvider ROCKET_UPGRADE = new RocketStationMenuProvider("upgrade", RocketStationBlock::getUpgraderMenuProvider);

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
