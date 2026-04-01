package org.exodusstudio.stellaris.common.registries;

import com.fej1fun.potentials.components.FluidAmountMapDataComponent;
import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.components.JetComponent;
import org.exodusstudio.stellaris.common.components.PathogenStorageComponent;
import org.exodusstudio.stellaris.common.components.TimerComponent;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModules;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModules;

import java.util.function.UnaryOperator;

public class DataComponentsRegistry {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPE = DeferredRegister.create(Stellaris.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    public static final RegistrySupplier<DataComponentType<Integer>> ENERGY =
            register("energy", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));
    public static final RegistrySupplier<DataComponentType<FluidAmountMapDataComponent>> FLUID_LIST = register("fluids", builder -> builder
            .persistent(FluidAmountMapDataComponent.CODEC)
            .networkSynchronized(FluidAmountMapDataComponent.STREAM_CODEC));

    public static final RegistrySupplier<DataComponentType<Integer>> SD_CARD_ID =
            register("sd_card_id", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    public static final RegistrySupplier<DataComponentType<Modules<RocketModule>>> ROCKET_MODULES =
            register("rocket_modules", builder -> builder
                    .persistent(RocketModules.CODEC).networkSynchronized(RocketModules.STREAM_CODEC).cacheEncoding());

    public static final RegistrySupplier<DataComponentType<Modules<SpaceSuitModule>>> SPACE_SUIT_MODULES =
            register("space_suit_modules", builder -> builder
                    .persistent(SpaceSuitModules.CODEC).networkSynchronized(SpaceSuitModules.STREAM_CODEC).cacheEncoding());
    public static final RegistrySupplier<DataComponentType<JetComponent>> JET_COMPONENT =
            register("jet_component", builder -> builder
                    .persistent(JetComponent.CODEC).networkSynchronized(JetComponent.STREAM_CODEC));

    public static final RegistrySupplier<DataComponentType<TimerComponent>> TIMER =
            register("timer", builder -> builder.persistent(TimerComponent.CODEC).networkSynchronized(TimerComponent.STREAM_CODEC));
    public static final RegistrySupplier<DataComponentType<PathogenStorageComponent>> PATHOGEN_STORED =
            register("pathogen_stored", builder -> builder.persistent(PathogenStorageComponent.CODEC).networkSynchronized(PathogenStorageComponent.STREAM_CODEC));

    public static final RegistrySupplier<DataComponentType<Float>> PLAYER_FADE =
            register("player_fade", builder -> builder.persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT));

    private static <T> RegistrySupplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPE.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }


}