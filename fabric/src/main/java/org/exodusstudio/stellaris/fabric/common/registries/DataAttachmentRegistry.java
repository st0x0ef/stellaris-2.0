package org.exodusstudio.stellaris.fabric.common.registries;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

import java.util.HashMap;

public class DataAttachmentRegistry {

    public static HashMap<ResourceLocation, AttachmentType<?>> ATTACHMENT_TYPES = new HashMap<>();

    public static final AttachmentType<Integer> OIL;

    public static void register() {

    }

    static {
        OIL = AttachmentRegistry.create(
                ResourceLocationUtils.id("oil"),
                builder -> builder
                        .initializer(() -> 20) // start with a default value like hunger
                        .persistent(Codec.INT) // persist across restarts
                        .syncWith(ByteBufCodecs.INT, AttachmentSyncPredicate.targetOnly()) // only the player's own client needs the value for rendering
        );
        ATTACHMENT_TYPES.put(OIL.identifier(), OIL);
    }


}
