package org.exodusstudio.stellaris.common.registries;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import org.exodusstudio.stellaris.Stellaris;

import java.util.function.UnaryOperator;

public class DataComponentsRegistry {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE = DeferredRegister.create(Stellaris.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<Integer>> ENERGY =
            register("energy", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    private static <T> RegistrySupplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPE.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

}