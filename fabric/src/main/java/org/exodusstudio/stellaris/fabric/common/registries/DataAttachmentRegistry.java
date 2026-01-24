package org.exodusstudio.stellaris.fabric.common.registries;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModules;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModules;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.HashMap;

@SuppressWarnings("all")
public class DataAttachmentRegistry {

    public static HashMap<Identifier, AttachmentType<?>> ATTACHMENT_TYPES = new HashMap<>();

    public static final AttachmentType<Integer> OIL;
    public static final AttachmentType<? extends Modules<RocketModule>> ROCKET_MODULES;
    public static final AttachmentType<? extends Modules<SpaceSuitModule>> SPACE_SUIT_MODULES;

    public static void register() {

    }

    static {
        OIL = AttachmentRegistry.create(
                IdentifierUtils.id("oil"),
                builder -> builder
                        .initializer(() -> -1) // start with a default value like hunger
                        .persistent(Codec.INT) // persist across restarts
                        .syncWith(ByteBufCodecs.INT, AttachmentSyncPredicate.all()) // only the player's own client needs the value for rendering
        );

        ROCKET_MODULES = AttachmentRegistry.create(
                IdentifierUtils.id("rocket_modules"),
                builder -> builder
                        .initializer(RocketModules::empty) // start with a default value like hunger
                        .persistent(RocketModules.CODEC) // persist across restarts
                        .syncWith(RocketModules.STREAM_CODEC, AttachmentSyncPredicate.all()) // only the player's own client needs the value for rendering
        );

        SPACE_SUIT_MODULES = AttachmentRegistry.create(
                IdentifierUtils.id("space_suit_modules"),
                builder -> builder
                        .initializer(SpaceSuitModules::empty) // start with a default value like hunger
                        .persistent(SpaceSuitModules.CODEC) // persist across restarts
                        .syncWith(SpaceSuitModules.STREAM_CODEC, AttachmentSyncPredicate.all()) // only the player's own client needs the value for rendering
        );

        ATTACHMENT_TYPES.put(OIL.identifier(), OIL);
        ATTACHMENT_TYPES.put(ROCKET_MODULES.identifier(), ROCKET_MODULES);
        ATTACHMENT_TYPES.put(SPACE_SUIT_MODULES.identifier(), SPACE_SUIT_MODULES);
    }


}
