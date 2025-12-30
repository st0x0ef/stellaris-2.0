package org.exodusstudio.stellaris.common.events;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.BlockEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.apache.commons.io.FileUtils;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.FlagBlockEntity;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;

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

        blockEvents();
    }

    public static void regenStellarisDim(MinecraftServer server) {
        List<ServerLevel> levelList = new ArrayList<>((Collection<ServerLevel>) server.getAllLevels());
        List<ResourceLocation> dimensionsToRegen = List.of(Stellaris.CONFIG.admin.dimensionsToRegen);


        Stellaris.LOG.warn("---------- Dimension Regeneration Enabled ----------");
        Stellaris.LOG.warn("All theses dimensions will be regenerated on join");
        Stellaris.LOG.warn("This is used for development only");

        Stellaris.LOG.warn("Do disable this, go to the config.");
        Stellaris.LOG.warn("Dimensions Regenerated:");

        levelList.stream()
                .map(Level::dimension)
                .filter((level -> dimensionsToRegen.contains(level.location())))
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
                                        Stellaris.LOG.warn("    - {}", level.location());
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
                    stack.set(DataComponentsRegistry.DYE_COLOR.get(), flagBlock.getColor());
                    if(flagBlock.getGameProfile() != null){
                        stack.set(DataComponents.PROFILE, flagBlock.getGameProfile());
                    }
                }
                Block.popResource(level, pos, stack);
            }


            return EventResult.pass();
        });
    }
}
