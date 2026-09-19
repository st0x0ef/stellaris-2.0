package org.exodusstudio.stellaris.client.renderers.trophy;

import com.mojang.serialization.Codec;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import org.exodusstudio.stellaris.client.renderers.mobs.heartofluna.LunaBoss;
import org.exodusstudio.stellaris.client.renderers.mobs.starcrawlerboss.StarCrawlerBossModel;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.joml.Vector3f;

import java.util.Set;
import java.util.function.Function;

public enum TrophyBoss implements StringRepresentable {
    HEART_OF_LUNA("heart_of_luna", IdentifierUtils.texture("entity/heart_of_luna"), new Vector3f(0, 180, 0)),
    STAR_CRAWLER_BOSS("star_crawler_boss", IdentifierUtils.texture("entity/star_crawler_boss"), new Vector3f(0, 180, 0));

    public static final Codec<TrophyBoss> CODEC = StringRepresentable.fromEnum(TrophyBoss::values);

    private final String name;
    private final Identifier texture;
    private final Vector3f rotation;

    TrophyBoss(String name, Identifier texture, Vector3f rotation) {
        this.name = name;
        this.texture = texture;
        this.rotation = rotation;
    }

    public Identifier texture() {
        return this.texture;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public ModelPart bake(Function<ModelLayerLocation, ModelPart> baker) {
        return switch (this) {
            case HEART_OF_LUNA -> baker.apply(LunaBoss.LAYER_LOCATION)
                    .getChild("Upper Full Body").getChild("Upper Body").getChild("Head");
            // The Star Crawler Boss has no head bone, so the whole model stands in as its trophy.
            case STAR_CRAWLER_BOSS -> baker.apply(StarCrawlerBossModel.LAYER_LOCATION);
        };
    }

    /**
     * Parts skipped when measuring where the trophy rests. The Heart of Luna's lower jaw carries an
     * inflated duplicate, cube_12, whose underside is fully transparent on the base texture: it renders as
     * nothing but still bounds the geometry, which would float the head ~1px above the floor.
     */
    public Set<String> ignoredParts() {
        return switch (this) {
            case HEART_OF_LUNA -> Set.of("cube_12");
            case STAR_CRAWLER_BOSS -> Set.of();
        };
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
