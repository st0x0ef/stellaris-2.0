package org.exodusstudio.stellaris.common.data;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

/**
 * Utility class to create fast and easy synced pack
 * WIP
 * @param <T>
 */
public interface SyncedPack<T> {

    public Codec<T> getCodec();

    StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec();

    void onSynced(List<T> list);

}
