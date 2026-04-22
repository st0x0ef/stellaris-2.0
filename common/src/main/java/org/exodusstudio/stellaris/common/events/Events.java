package org.exodusstudio.stellaris.common.events;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import org.apache.commons.io.FileUtils;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.antennas.Antenna;
import org.exodusstudio.stellaris.common.antennas.AntennaSavedData;
import org.exodusstudio.stellaris.common.blocks.CoalLanternBlock;
import org.exodusstudio.stellaris.common.blocks.RocketLaunchPadBlock;
import org.exodusstudio.stellaris.common.blocks.WallCoalTorchBlock;
import org.exodusstudio.stellaris.common.blocks.entities.AntennaBlockEntity;
import org.exodusstudio.stellaris.common.blocks.entities.FlagBlockEntity;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitHelmet;
import org.exodusstudio.stellaris.common.network.packets.AntennasOperations;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.utils.OxygenUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Events {

    public static void init() {
        LifecycleEvent.SERVER_STARTING.register((MinecraftServer server) -> {
            if(Stellaris.CONFIG.admin.debugMode && Stellaris.CONFIG.admin.regenDimension) {
                regenStellarisDim(server);
            }
        });

        EntityEvent.ENTER_SECTION.register((entity, sectionX, sectionY, sectionZ, prevX, prevY, prevZ) -> {
            if (entity instanceof Player player && (sectionX != prevX || sectionZ != prevZ)) {
                ItemStack headStack = player.getItemBySlot(EquipmentSlot.HEAD);
                SpaceSuitHelmet.tickOilFinderEnergy(headStack);
            }
        });


        blockEvents();
    }

    /**
     * Regenerates specified dimensions by deleting their region, data, poi, and entities folders.
     * Useful for development purposes to reset dimensions on server start.
     * @param server
     */
    public static void regenStellarisDim(MinecraftServer server) {
        List<ServerLevel> levelList = new ArrayList<>((Collection<ServerLevel>) server.getAllLevels());
        List<Identifier> dimensionsToRegen = List.of(Stellaris.CONFIG.admin.dimensionsToRegen);


        Stellaris.LOG.warn("---------- Dimension Regeneration Enabled ----------");
        Stellaris.LOG.warn("All theses dimensions will be regenerated on join");
        Stellaris.LOG.warn("This is used for development only");

        Stellaris.LOG.warn("Do disable this, go to the config.");
        Stellaris.LOG.warn("Dimensions Regenerated:");

        levelList.stream()
                .map(Level::dimension)
                .filter((level -> dimensionsToRegen.contains(level.identifier())))
                .forEach((level) -> {
                    Path dimensionPath = server.storageSource.getDimensionPath(level);
                    String[] folderToDelete = new String[]{"region", "data", "poi", "entities"};

                    Arrays.stream(folderToDelete)
                            .map(dimensionPath::resolve)
                            .map(Path::toFile)
                            .forEach((file) -> {
                                try {
                                    if (Files.exists(file.toPath())) {
                                        FileUtils.deleteDirectory(file);
                                        Stellaris.LOG.warn("    - {}", level.identifier());
                                    }
                                } catch(IOException e) {
                                    throw new RuntimeException(e);

                                }
                            });
                });
        Stellaris.LOG.warn("---------- Dimension Regeneration Enabled ----------");
    }

    public static void blockEvents() {
        BlockEvent.BREAK.register((level, pos, state, player, xp) -> {

            if(level.getBlockEntity(pos) instanceof FlagBlockEntity flagBlock) {
                ItemStack stack = new ItemStack(BlocksRegistry.FLAG.item().get());

                if(player.isCrouching()) {
                    stack.set(DataComponents.BASE_COLOR, flagBlock.getColor());
                    if(flagBlock.getGameProfile() != null){
                        stack.set(DataComponents.PROFILE, flagBlock.getGameProfile());
                    }
                }
                Block.popResource(level, pos, stack);
            } else if(state.is(BlocksRegistry.ROCKET_LAUNCH_PAD.block().get())) {

                if(Utils.checkIfAntennaIsNear(pos, level, 1)) {
                    return EventResult.interruptFalse();
                }
            }

            if(level instanceof ServerLevel serverLevel) {
                MinecraftServer server = serverLevel.getServer();

                if(state.is(BlocksRegistry.ANTENNA.block().get()) ) {
                    AntennaBlockEntity antennaBlockEntity = (AntennaBlockEntity) level.getBlockEntity(pos);
                    if (antennaBlockEntity != null && antennaBlockEntity.launchPadId != null) {

                        AntennaSavedData antennaSavedData = AntennaSavedData.getSavedAntennas(server);
                        Antenna antenna = antennaSavedData.getAntenna(antennaBlockEntity.launchPadId);

                        if(!antennaSavedData.isPlayerOwner(antennaBlockEntity.launchPadId, player)) {
                            player.sendSystemMessage(Component.literal("You don't have permission to break this antenna.").withStyle(ChatFormatting.GRAY));
                            return EventResult.interruptFalse();
                        }

                        if(antenna != null) {
                            NetworkManager.sendToServer(new AntennasOperations(antenna, "remove"));
                        }
                    }

                }

            }


            return EventResult.pass();
        });

        BlockEvent.PLACE.register((level, pos, state, player) -> {
            if (level instanceof ServerLevel serverLevel && !OxygenUtils.isOxygenated(level, pos)) {
                if (state.is(Blocks.TORCH)) {
                    serverLevel.setBlockAndUpdate(pos, BlocksRegistry.COAL_TORCH_BLOCK.block().get().defaultBlockState());
                    return EventResult.interruptFalse();
                }
                else if (state.is(Blocks.WALL_TORCH)) {
                    serverLevel.setBlockAndUpdate(pos, BlocksRegistry.WALL_COAL_TORCH_BLOCK.get().defaultBlockState().setValue(WallCoalTorchBlock.FACING, state.getValue(WallTorchBlock.FACING)));
                    return EventResult.interruptFalse();
                }
                else if (state.is(Blocks.LANTERN)) {
                    serverLevel.setBlockAndUpdate(pos, BlocksRegistry.COAL_LANTERN_BLOCK.block().get().defaultBlockState().setValue(CoalLanternBlock.HANGING, state.getValue(LanternBlock.HANGING)));
                    return EventResult.interruptFalse();
                }
            }

            if(state.is(BlocksRegistry.ANTENNA.block().get())) {
                if (level.getBlockState(pos.above()).is(BlocksRegistry.ROCKET_LAUNCH_PAD.block().get()) && level.getBlockState(pos.above()).getValue(RocketLaunchPadBlock.STAGE)) {
                    return EventResult.pass();
                }
                return EventResult.interruptFalse();
            }

            return EventResult.pass();
        });
    }
}
