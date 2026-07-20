package org.exodusstudio.stellaris.common.modules.rover;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.registries.StellarisRegistries;

import java.util.List;

public class RoverModules extends Modules<RoverModule> {

    public static final Codec<Modules<RoverModule>> CODEC =
            createCodec(StellarisRegistries.ROVER_MODULES, RoverModules::new, roverModuleModules -> roverModuleModules.modules);

    public static final StreamCodec<RegistryFriendlyByteBuf, Modules<RoverModule>> STREAM_CODEC = createStreamCodec(CODEC);

    public static RoverModules empty() {
        return new RoverModules(List.of());
    }

    public RoverModules(List<RoverModule> modules) {
        super(modules);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RoverModules other) {
            return this.modules.equals(other.modules);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
