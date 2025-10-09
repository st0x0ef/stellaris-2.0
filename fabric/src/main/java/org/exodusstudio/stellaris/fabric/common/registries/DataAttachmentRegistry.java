package org.exodusstudio.stellaris.fabric.common.registries;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import org.exodusstudio.stellaris.common.rocket.RocketModules;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

import java.util.HashMap;

public class DataAttachmentRegistry {

    public static HashMap<ResourceLocation, AttachmentType<?>> ATTACHMENT_TYPES = new HashMap<>();

    public static final AttachmentType<Integer> OIL;
    public static final AttachmentType<RocketModules> ROCKET_MODULES;

    public static void register() {

    }

    static {
        OIL = AttachmentRegistry.create(
                ResourceLocationUtils.id("oil"),
                builder -> builder
                        .initializer(() -> 20) // start with a default value like hunger
                        .persistent(Codec.INT) // persist across restarts
                        .syncWith(ByteBufCodecs.INT, AttachmentSyncPredicate.all()) // only the player's own client needs the value for rendering
        );

        ROCKET_MODULES = AttachmentRegistry.create(
                ResourceLocationUtils.id("rocket_modules"),
                builder -> builder
                        .initializer(RocketModules::empty) // start with a default value like hunger
                        .persistent(RocketModules.CODEC) // persist across restarts
                        .syncWith(RocketModules.STREAM_CODEC, AttachmentSyncPredicate.all()) // only the player's own client needs the value for rendering
        );

        ATTACHMENT_TYPES.put(OIL.identifier(), OIL);
        ATTACHMENT_TYPES.put(ROCKET_MODULES.identifier(), OIL);

    }


}
