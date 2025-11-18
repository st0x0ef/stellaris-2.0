package org.exodusstudio.stellaris.common.events;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.platform.Platform;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.apache.commons.io.FileUtils;
import org.exodusstudio.stellaris.Stellaris;

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
            if(Platform.isDevelopmentEnvironment() && Stellaris.CONFIG.regenWorld) {
                regenStellarisDim(server);
            }

        });

    }

    public static void regenStellarisDim(MinecraftServer server) {
        List<ServerLevel> levelList = new ArrayList<>((Collection<ServerLevel>) server.getAllLevels());
        List<ResourceLocation> dimensionsToRegen = List.of(Stellaris.CONFIG.dimensionsToRegen);

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
                                    }
                                } catch(IOException e) {
                                    throw new RuntimeException(e);

                                }
                            });
                });

    }

}
