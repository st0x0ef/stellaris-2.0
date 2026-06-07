package org.exodusstudio.stellaris.common.modules.space_suit;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.registries.StellarisRegistries;

import java.util.List;

public class SpaceSuitModules extends Modules<SpaceSuitModule> {

    public static final Codec<Modules<SpaceSuitModule>> CODEC =
            createCodec(StellarisRegistries.SPACE_SUIT_MODULES, SpaceSuitModules::new, spaceSuitModule -> spaceSuitModule.modules);

    public static final StreamCodec<RegistryFriendlyByteBuf, Modules<SpaceSuitModule>> STREAM_CODEC = createStreamCodec(CODEC);

    public static SpaceSuitModules empty() {
        return new SpaceSuitModules(List.of());
    }

    public SpaceSuitModules(List<SpaceSuitModule> modules) {
        super(modules);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SpaceSuitModules other) {
            return this.modules.equals(other.modules);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
