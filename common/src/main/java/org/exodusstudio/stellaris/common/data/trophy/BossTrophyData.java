package org.exodusstudio.stellaris.common.data.trophy;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BossTrophyData extends SimpleJsonResourceReloadListener<BossTrophy> {

    public static HashMap<Identifier, BossTrophy> TROPHY_BOSSES = new HashMap<>();

    //Use for the default trophy boss if the boss is not found in the map. This is to prevent crashes when a boss is not found.
    public static BossTrophy HEART_OF_LUNA = new BossTrophy(
            IdentifierUtils.texture("entity/heart_of_luna"),
            new Vector3f(0, 180, 0),
            List.of("Upper Full Body", "Upper Body", "Head"),
            List.of("cube_12"),
            new ModelLayerLocation(IdentifierUtils.id("heart_of_luna"), "main")
    );

    public static final String ID = "boss_trophy";

    public BossTrophyData() {
        super(BossTrophy.CODEC, FileToIdConverter.json(ID));
    }

    @Override
    protected void apply(Map<Identifier, BossTrophy> preparations, ResourceManager manager, ProfilerFiller profiler) {
        TROPHY_BOSSES.clear();

        Stellaris.LOG.error("Loading trophy bosses: " + preparations.keySet());
        TROPHY_BOSSES.putAll(preparations);
    }
}
