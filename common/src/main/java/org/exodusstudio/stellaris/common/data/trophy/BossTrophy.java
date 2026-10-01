package org.exodusstudio.stellaris.common.data.trophy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.exodusstudio.stellaris.common.data.SdCard;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

public record BossTrophy(Identifier texture, Vector3fc rotation, List<String> renderedPartPath, List<String> ignoredPart, ModelLayerLocation modelLayerLocation) {

    public static Codec<Vector3fc> VEC3F = Codec.FLOAT.listOf().comapFlatMap((list) -> Util.fixedSize(list, 3).map((listx) -> new Vector3f(listx.getFirst(), listx.get(1), listx.getLast())), (vector3f) -> List.of(vector3f.x(), vector3f.y(), vector3f.z()));

    public static final Codec<ModelLayerLocation> MODEL_LAYER_LOCATION_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("model").forGetter(ModelLayerLocation::model),
            Codec.STRING.fieldOf("layer").forGetter(ModelLayerLocation::layer)
    ).apply(instance, ModelLayerLocation::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ModelLayerLocation> MODEL_LAYER_STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, ModelLayerLocation::model,
            ByteBufCodecs.STRING_UTF8, ModelLayerLocation::layer,
            ModelLayerLocation::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, BossTrophy> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, BossTrophy::texture,
            ByteBufCodecs.VECTOR3F, BossTrophy::rotation,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), BossTrophy::renderedPartPath,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), BossTrophy::ignoredPart,
            MODEL_LAYER_STREAM_CODEC, BossTrophy::modelLayerLocation,
            BossTrophy::new
    );

    public static final Codec<BossTrophy> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("texture").forGetter(BossTrophy::texture),
            VEC3F.fieldOf("rotation").forGetter(BossTrophy::rotation),
            Codec.list(Codec.STRING).fieldOf("renderedPartPath").forGetter(BossTrophy::renderedPartPath),
            Codec.list(Codec.STRING).fieldOf("ignoredParts").forGetter(BossTrophy::ignoredPart),
            MODEL_LAYER_LOCATION_CODEC.fieldOf("modelLayerLocation").forGetter(BossTrophy::modelLayerLocation)
    ).apply(instance, BossTrophy::new));


    public ModelPart bake(Function<ModelLayerLocation, ModelPart> baker) {
        ModelPart modelPart = baker.apply(this.modelLayerLocation);
        for (String partName : this.renderedPartPath) {
            modelPart = modelPart.getChild(partName);
        }
        return modelPart;
    }




    /**
     * Parts skipped when measuring where the trophy rests. The Heart of Luna's lower jaw carries an
     * inflated duplicate, cube_12, whose underside is fully transparent on the base texture: it renders as
     * nothing but still bounds the geometry, which would float the head ~1px above the floor.
     */
    public Set<String> ignoredParts() {
        return Set.of(this.ignoredPart.toArray(String[]::new));
    }


}
