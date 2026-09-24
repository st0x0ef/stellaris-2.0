package org.exodusstudio.stellaris.common.utils;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.timeline.Timeline;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.overlays.FadingHolder;
import org.exodusstudio.stellaris.common.antennas.Antenna;
import org.exodusstudio.stellaris.common.antennas.AntennaSavedData;
import org.exodusstudio.stellaris.common.blocks.entities.AntennaBlockEntity;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.Temperature;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitItem;
import org.exodusstudio.stellaris.common.network.packets.StartFadePacket;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Utils {

    public static int getColorHexCode(String colorName) {
        // Custom Colour Hex Code Support
        if (colorName.startsWith("#")) {
            try {
                return Integer.parseInt(colorName.substring(1), 16);
            } catch (NumberFormatException e) {
                return 0xFFFFFF; // Return white if invalid hex format
            }
        }

        return switch (colorName.toLowerCase()) {
            case "black" -> 0x000000;
            case "red" -> 0xFF0000;
            case "green" -> 0x008000;
            case "blue" -> 0x0000FF;
            case "yellow" -> 0xFFFF00;
            case "cyan" -> 0x00FFFF;
            case "magenta" -> 0xFF00FF;
            case "gray", "grey" -> 0x808080;
            case "maroon" -> 0x800000;
            case "olive" -> 0x808000;
            case "purple" -> 0x800080;
            case "teal" -> 0x008080;
            case "navy" -> 0x000080;
            case "orange" -> 0xFFA500;
            case "brown" -> 0xA52A2A;
            case "lime" -> 0x00FF00;
            case "pink" -> 0xFFC0CB;
            case "coral" -> 0xFF7F50;
            case "gold" -> 0xFFD700;
            case "silver" -> 0xC0C0C0;
            case "beige" -> 0xF5F5DC;
            case "lavender" -> 0xE6E6FA;
            case "turquoise" -> 0x40E0D0;
            case "salmon" -> 0xFA8072;
            case "khaki" -> 0xF0E68C;
            case "darkred" -> 0x8B0000;
            case "rainbow" -> Utils.generateRandomHexColor();
            default -> 0xFFFFFF;
        };
    }

    public static int generateRandomHexColor() {
        Random random = new Random();
        return random.nextInt(0xFFFFFF + 1);
    }

    public static int getMinecraftColor(String colorName) {
        int colorHex = getColorHexCode(colorName);
        Vec3 vector3i = hexToVec3(colorHex);
        return ARGB.color(vector3i);
    }

    public static Vec3 hexToVec3(int hex) {
        int r = (hex >> 16) & 0xFF;
        int g = (hex >> 8) & 0xFF;
        int b = hex & 0xFF;
        return new Vec3(r / 255.0, g / 255.0, b / 255.0);
    }

    public static int getSurvivalLivingEntityCountInChunks(Level level, Set<ChunkPos> chunks) {
        int count = 0;

        for (ChunkPos chunkPos : chunks) {
            AABB aabb = new AABB(
                    chunkPos.getMinBlockX(), level.getMinY(), chunkPos.getMinBlockZ(),
                    chunkPos.getMaxBlockX() + 1, level.getMaxY() + 1, chunkPos.getMaxBlockZ() + 1
            );

            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
                if (!entity.is(TagsRegistry.EntityTags.NO_OXYGEN_NEEDED)) {
                    if (entity instanceof Player player) {
                        if (!player.isCreative() && !player.isSpectator()) {
                            count++;
                        }
                    } else {
                        count++;
                    }
                }
            }
        }

        return count;
    }

    /** Every player who has the block's chunk loaded, i.e. everyone who can see or render it. */
    public static List<ServerPlayer> getPlayersTrackingBlock(Level level, BlockPos pos) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return List.of();
        }

        return serverLevel.getChunkSource().chunkMap.getPlayers(ChunkPos.containing(pos), false);
    }

    public static List<ServerPlayer> getPlayersIn3x3Chunks(Level level, BlockPos pos) {
        List<ServerPlayer> playersInChunks = List.of();
        if (level != null) {
            ChunkPos chunkPos = ChunkPos.containing(pos);
            AABB chunkAABB = new AABB(chunkPos.getMinBlockX(), level.getMinY(), chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), level.getMaxY(), chunkPos.getMaxBlockZ());
            AABB expandedAABB = chunkAABB.inflate(16.0 * 1.5); // 1.5 chunks in each direction to cover 3x3 chunks
            playersInChunks = level.getEntitiesOfClass(ServerPlayer.class, expandedAABB);
        }
        return playersInChunks;
    }

    public static void startFade(Player player) {
        var fadingHolder = new FadingHolder(true, 0);
        if(player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, new StartFadePacket(fadingHolder));
        } else {
            player.stellaris$saveDataAttachments(IdentifierUtils.id("player_fade"), fadingHolder);
        }
    }

    public static void stopFade(Player player) {
        var fadingHolder = new FadingHolder(false, 1);
        if(player instanceof ServerPlayer serverPlayer) {
            NetworkManager.sendToPlayer(serverPlayer, new StartFadePacket(fadingHolder));
        } else {
            player.stellaris$saveDataAttachments(IdentifierUtils.id("player_fade"), fadingHolder);
        }
    }

    // Executes the given action after a 2-second delay, with a fade effect for the player.
    public static void executeWithFade(Player player, Runnable action, boolean startFade) {
        if(startFade) startFade(player);
        else stopFade(player);

        MinecraftServer server = player.level().getServer();
        if (server == null) {
            return;
        }

        CompletableFuture.delayedExecutor(2, java.util.concurrent.TimeUnit.SECONDS, server)
                .execute(() -> {
                    if (player instanceof ServerPlayer serverPlayer && serverPlayer.hasDisconnected()) {
                        return;
                    }
                    action.run();
                });
    }

    public static String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static String getRelativeDirection(Direction looking, Direction direction) {
        if (looking == null || direction == null) {
            return null;
        }

        if (looking == direction) {
            return "Front";
        }
        if (looking == direction.getOpposite()) {
            return "Back";
        }

        // Vertical targets are not left/right relative to horizontal facing.
        if (direction == Direction.UP) {
            return "Up";
        }
        if (direction == Direction.DOWN) {
            return "Down";
        }

        if (!looking.getAxis().isHorizontal()) {
            return "Unknown";
        }

        if (direction == looking.getCounterClockWise()) {
            return "Left";
        }
        if (direction == looking.getClockWise()) {
            return "Right";
        }

        return null;
    }


    public static boolean isLivingInSpaceSuit(LivingEntity entity) {
        return isSpaceSuitPart(entity.getItemBySlot(EquipmentSlot.FEET), ArmorType.BOOTS)
                && isSpaceSuitPart(entity.getItemBySlot(EquipmentSlot.HEAD), ArmorType.HELMET)
                && isSpaceSuitPart(entity.getItemBySlot(EquipmentSlot.CHEST), ArmorType.CHESTPLATE)
                && isSpaceSuitPart(entity.getItemBySlot(EquipmentSlot.LEGS), ArmorType.LEGGINGS);
    }

    public static boolean isSpaceSuitPart(ItemStack stack) {
        return stack.getItem() instanceof SpaceSuitItem;
    }

    public static boolean isSpaceSuitPart(ItemStack stack, ArmorType armorType) {
        return stack.getItem() instanceof SpaceSuitItem spaceSuitItem && spaceSuitItem.getArmorType() == armorType;
    }

    public static boolean isLivingInArmor(LivingEntity entity, EquipmentSlot slot, Item item) {
        return entity.getItemBySlot(slot).getItem().equals(item);
    }

    public static void disableFlyAntiCheat(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.aboveGroundTickCount = 0;
        }
    }

    public static boolean checkIfAntennaIsNear(BlockPos pos, Level level, int distance) {
        return level.getBlockStates(new AABB(pos).inflate(distance)).anyMatch(blockState -> blockState.is(BlocksRegistry.ANTENNA.block().get()));
    }

    public static boolean checkIfRocketIsNear(BlockPos pos, Level level, int distance) {
        return !level.getEntities(EntityTypesRegistry.ROCKET.get(), new AABB(pos).inflate(distance, distance + 2, distance), entity -> true).isEmpty();
    }

    /** Altitude every space station is built at. */
    private static final int STATION_Y = 100;
    /** How many rings of candidate plots to try around the arrival point before giving up. */
    private static final int STATION_PLOT_RINGS = 6;
    /** Gap left between two neighbouring stations, in blocks. */
    private static final int STATION_PLOT_MARGIN = 16;

    @Nullable
    public static BlockPos placeSpaceStation(Player player, ServerLevel serverLevel, SpaceStationRecipe recipe, BlockPos landingPos) {
        // getOrCreate() silently hands back an empty template when the structure is missing, and caches it
        // for the rest of the session, so the build would fail forever without a word. Fail loudly instead.
        Optional<StructureTemplate> template = serverLevel.getStructureManager().get(recipe.structureId());
        if (template.isEmpty()) {
            Stellaris.LOG.error("Cannot build space station: structure {} was not found", recipe.structureId());
            return null;
        }

        StructureTemplate structureTemplate = template.get();
        BlockPos pos = findFreeStationPlot(serverLevel, landingPos, structureTemplate.getSize(), recipe.antenna_position());
        if (pos == null) {
            Stellaris.LOG.error("Cannot build space station: no free plot near {} in {}", landingPos, serverLevel.dimension().identifier());
            return null;
        }

        if (!structureTemplate.placeInWorld(serverLevel, pos, pos, new StructurePlaceSettings(), serverLevel.getRandom(), 2)) {
            Stellaris.LOG.error("Cannot build space station: placing {} at {} in {} failed", recipe.structureId(), pos, serverLevel.dimension().identifier());
            return null;
        }

        Stellaris.LOG.info("Built space station {} at {} in {} for {}", recipe.structureId(), pos, serverLevel.dimension().identifier(), player.getGameProfile().name());

        Antenna antenna = new Antenna(
                null, //Will change after
                // The level the station went into, not wherever the player happens to be standing.
                serverLevel.dimension(),
                player.getGameProfile().name() + "'s Antenna",
                false,
                player.getGameProfile().id(),
                List.of()
        );

        return placeAntennaBlock(pos, serverLevel, recipe, antenna);
    }

    /**
     * Finds somewhere the station actually fits. Stations used to be dropped at a fixed altitude
     * straight under the arriving player, so two players launching from nearby coordinates on Earth
     * would land their stations on top of each other in orbit.
     */
    @Nullable
    private static BlockPos findFreeStationPlot(ServerLevel serverLevel, BlockPos landingPos, Vec3i size, Vec3i antennaOffset) {
        int step = Math.max(size.getX(), size.getZ()) + STATION_PLOT_MARGIN;
        // Line the plot up so the launch pad - which sits directly over the antenna - ends up on the
        // block the lander touches down on. Going by the rider's position instead puts the pad off by
        // the passenger attachment offset, and centring the template misses it by a block as well.
        int baseX = landingPos.getX() - antennaOffset.getX();
        int baseZ = landingPos.getZ() - antennaOffset.getZ();

        for (int ring = 0; ring <= STATION_PLOT_RINGS; ring++) {
            for (int dx = -ring; dx <= ring; dx++) {
                for (int dz = -ring; dz <= ring; dz++) {
                    // Only the edge of each ring; the inside was covered by the previous ones.
                    if (ring > 0 && Math.max(Math.abs(dx), Math.abs(dz)) != ring) {
                        continue;
                    }

                    BlockPos candidate = new BlockPos(baseX + dx * step, STATION_Y, baseZ + dz * step);
                    if (isStationPlotFree(serverLevel, candidate, size)) {
                        return candidate;
                    }
                }
            }
        }

        return null;
    }

    private static boolean isStationPlotFree(ServerLevel serverLevel, BlockPos origin, Vec3i size) {
        if (origin.getY() < serverLevel.getMinY() || origin.getY() + size.getY() > serverLevel.getMaxY()) {
            return false;
        }

        // Another station always carries an antenna, so the saved antennas are the cheap, reliable
        // way to spot one without reading a hundred thousand block states.
        for (Antenna antenna : AntennaSavedData.getSavedAntennas(serverLevel.getServer()).antennas.values()) {
            if (antenna.blockPos == null || !antenna.dimension.equals(serverLevel.dimension())) {
                continue;
            }

            if (antenna.blockPos.getX() >= origin.getX() && antenna.blockPos.getX() < origin.getX() + size.getX()
                    && antenna.blockPos.getY() >= origin.getY() && antenna.blockPos.getY() < origin.getY() + size.getY()
                    && antenna.blockPos.getZ() >= origin.getZ() && antenna.blockPos.getZ() < origin.getZ() + size.getZ()) {
                return false;
            }
        }

        // Then a coarse sweep for anything else already standing there - player builds, leftovers.
        for (int x = 0; x < size.getX(); x += 4) {
            for (int y = 0; y < size.getY(); y += 4) {
                for (int z = 0; z < size.getZ(); z += 4) {
                    if (!serverLevel.getBlockState(origin.offset(x, y, z)).isAir()) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static BlockPos placeAntennaBlock(BlockPos initialPos, ServerLevel serverLevel, SpaceStationRecipe recipe, Antenna antenna) {
        BlockPos pos = initialPos.offset(recipe.antenna_position());
        BlockState antennaState = BlocksRegistry.ANTENNA.block().get().defaultBlockState();
        AntennaBlockEntity antennaBlockEntity = new AntennaBlockEntity(pos, antennaState);

        antenna.blockPos = pos;

        serverLevel.setBlock(pos, antennaState, 3);
        serverLevel.setBlockEntity(antennaBlockEntity);

        registerAntenna(serverLevel.getServer(), antennaBlockEntity, antenna);

        return pos;
    }

    private static void registerAntenna(MinecraftServer server, AntennaBlockEntity blockEntity, Antenna antenna) {
        AntennaSavedData savedData = AntennaSavedData.getSavedAntennas(server);
        Map.Entry<UUID, Antenna> existing = savedData.getAntenna(antenna);

        blockEntity.launchPadId = existing != null ? existing.getKey() : savedData.addAntenna(antenna);
        blockEntity.setChanged();
    }

    public static float getCurrentTemperature(Planet planet, Holder.Reference<Timeline> timelineReference, Level level)  {
        Temperature temperature = planet.temperature().get();
        Timeline timeline = timelineReference.value();
        int minTemp = temperature.nightTimeTemperature();
        int maxTemp = temperature.dayTimeTemperature();
        int tempDiff = maxTemp - minTemp;
        float time = timeline.getCurrentTicks(level.clockManager());
        float halfDayDuration = timeline.periodTicks().get() / 2f;

        if (time < halfDayDuration) {
            return minTemp + (time / halfDayDuration) * tempDiff;
        }

        return maxTemp - (time / halfDayDuration - 1) * tempDiff;


    }

}
