package org.exodusstudio.stellaris.common.utils;

import dev.architectury.networking.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.client.overlays.FadingHolder;
import org.exodusstudio.stellaris.common.antennas.Antenna;
import org.exodusstudio.stellaris.common.blocks.entities.AntennaBlockEntity;
import org.exodusstudio.stellaris.common.data.space_station.SpaceStationRecipe;
import org.exodusstudio.stellaris.common.network.packets.StartFadePacket;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;

import java.util.List;
import java.util.Random;
import java.util.Set;
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
                if (!entity.getType().is(TagsRegistry.EntityTags.NO_OXYGEN_NEEDED)) {
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


        CompletableFuture.delayedExecutor(2, java.util.concurrent.TimeUnit.SECONDS)
                .execute(action);
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
        return isLivingInArmor(entity, EquipmentSlot.FEET, ItemsRegistry.SPACE_SUIT_BOOTS.get()) && isLivingInArmor(entity, EquipmentSlot.HEAD, ItemsRegistry.SPACE_SUIT_HELMET.get()) && isLivingInArmor(entity, EquipmentSlot.CHEST, ItemsRegistry.SPACE_SUIT_CHESTPLATE.get()) && isLivingInArmor(entity, EquipmentSlot.LEGS, ItemsRegistry.SPACE_SUIT_LEGGINGS.get());
    }

    public static boolean isSpaceSuitPart(ItemStack stack) {
        return stack.is(ItemsRegistry.SPACE_SUIT_BOOTS.get()) || stack.is(ItemsRegistry.SPACE_SUIT_HELMET.get()) || stack.is(ItemsRegistry.SPACE_SUIT_CHESTPLATE.get()) || stack.is(ItemsRegistry.SPACE_SUIT_LEGGINGS.get());
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

    public static BlockPos placeSpaceStation(Player player, ServerLevel serverLevel, SpaceStationRecipe recipe) {
        StructureTemplate structureTemplate = serverLevel.getStructureManager().getOrCreate(recipe.structureId());
        BlockPos pos = new BlockPos((int) player.getX() - (structureTemplate.getSize().getX() / 2), 100, (int) player.getZ() - (structureTemplate.getSize().getZ() / 2));

        structureTemplate.placeInWorld(serverLevel, pos, pos, new StructurePlaceSettings(), serverLevel.random, 2);

        Antenna antenna = new Antenna(
                null, //Will change after
                player.level().dimension(),
                player.getGameProfile().name() + "'s Antenna",
                false,
                player.getGameProfile().id(),
                List.of()
        );

        return placeAntennaBlock(pos, serverLevel, recipe, antenna);
    }

    public static BlockPos placeAntennaBlock(BlockPos initialPos, ServerLevel serverLevel, SpaceStationRecipe recipe, Antenna antenna) {
        BlockPos pos = initialPos.offset(recipe.antenna_position());
        AntennaBlockEntity antennaBlockEntity = new AntennaBlockEntity(pos, BlocksRegistry.ANTENNA.block().get().defaultBlockState());

        antenna.blockPos = pos;
        antennaBlockEntity.setAntenna(antenna, null, false);

        serverLevel.setBlock(pos, BlocksRegistry.ANTENNA.block().get().defaultBlockState(), 1);
        serverLevel.setBlockEntity(antennaBlockEntity);

        return pos;
    }

}
