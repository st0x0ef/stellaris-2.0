package org.exodusstudio.stellaris.client.data.fluid;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.screens.utils.GUISprites;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FluidInfoPack extends SimpleJsonResourceReloadListener<Map<Identifier, FluidInfos>> {

    public static Map<Identifier, FluidInfos> FLUID_INFOS = new HashMap<>();

    public static FluidInfos DEFAULT_FLUID_INFOS = new FluidInfos(
            GUISprites.WATER_OVERLAY, Optional.empty(), Optional.empty(), 0xFFFFFF);

    public FluidInfoPack() {
        super(FluidInfos.CONTAINER, FileToIdConverter.json("fluid_infos"));
    }

    @Override
    protected void apply(Map<Identifier, Map<Identifier, FluidInfos>> IdentifierJsonElementMap, ResourceManager resourceManager, ProfilerFiller profiler) {

        IdentifierJsonElementMap.forEach((key, entry) -> {
            Stellaris.LOG.error(FluidInfoPack.FLUID_INFOS.keySet().toString());

            FLUID_INFOS.putAll(entry);

        });

    }

}
