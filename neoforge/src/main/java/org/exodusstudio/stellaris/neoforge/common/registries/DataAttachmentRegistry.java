package org.exodusstudio.stellaris.neoforge.common.registries;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;

import java.util.HashMap;
import java.util.function.Supplier;

public class DataAttachmentRegistry {

    public static HashMap<ResourceLocation, AttachmentType<?>> ATTACHMENTS = new HashMap<>();
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Stellaris.MOD_ID);

    private static final Supplier<AttachmentType<Integer>> OIL = ATTACHMENT_TYPES.register(
            "oil", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT.fieldOf("mana")).build()
    );


    public static void register() {

    }

    static {
        ATTACHMENTS.put(ResourceLocationUtils.id("oil"), OIL.get());
    }


}
