package org.exodusstudio.stellaris.neoforge.common.registries;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.overlays.FadingHolder;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.HashMap;
import java.util.function.Supplier;

public class DataAttachmentRegistry {

    public static HashMap<Identifier, Supplier<AttachmentType<?>>> ATTACHMENTS = new HashMap<>();
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Stellaris.MOD_ID);

    private static final Supplier<AttachmentType<?>> OIL = ATTACHMENT_TYPES.register(
            "oil", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("mana")).sync(ByteBufCodecs.INT).build()
    );

    private static final Supplier<AttachmentType<?>> PLAYER_FADE = ATTACHMENT_TYPES.register(
            "player_fade", () -> AttachmentType.builder(() -> new FadingHolder(false, 0f)).serialize(FadingHolder.CODEC.fieldOf("player_fade")).sync(FadingHolder.STREAM_CODEC).build()
    );

    public static void register(IEventBus bus) {
        ATTACHMENTS.put(IdentifierUtils.id("oil"), OIL);
        ATTACHMENTS.put(IdentifierUtils.id("player_fade"), PLAYER_FADE);

        ATTACHMENT_TYPES.register(bus);
    }
}
