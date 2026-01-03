package org.exodusstudio.stellaris.common.module;

import com.mojang.serialization.Codec;
import dev.architectury.registry.registries.Registrar;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.platform.RegistrarUtilPlatform;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
public class Modules<M extends Module<M>> implements Serializable, Iterable<M> {

    public final List<M> modules;

    public Modules(List<M> modules) {
        this.modules = modules;
    }


    public List<? extends Item> items() {
        return this.modules.stream().map(Module::asItem).toList();
    }

    public List<ItemStack> itemStacks() {
        return this.modules.stream().map(Module::asItem).map(ItemStack::new).toList();
    }

    public boolean contains(ModuleLike<M> module) {
        return this.modules.contains(module.asModule());
    }

    @SafeVarargs
    public final boolean contains(ModuleLike<M>... modules) {
        if (modules == null)
            return true;

        for (ModuleLike<M> like : modules)
            if (!contains(like.asModule()))
                return false;


        return true;
    }

    public Mutable toMutable() {
        return new Mutable(this);
    }

    public List<M> getModules() {
        return modules;
    }

    @Override
    public @NotNull Iterator<M> iterator() {
        return this.modules.iterator();
    }

    public class Mutable {

        private final List<M> modules;

        public Mutable(Modules<M> contents) {
            this.modules = new ArrayList<>(contents.modules);
        }

        public Mutable insert(M module) {
            this.modules.add(module);
            return this;
        }

        public Mutable insert(ModuleLike<M> module) {
            this.modules.add((module.asModule()));
            return this;
        }

        public Modules<M> toImmutable() {
            return new Modules<>(this.modules);
        }
    }

    protected static <T extends Module<T>> Codec<Modules<T>> createCodec(
            Registrar<T> registrar,
            Function<? super List<T>, ? extends Modules<T>> from,
            Function<? super Modules<T>, ? extends List<T>> to
    ) {
        return createCodec(RegistrarUtilPlatform.getBaseRegistry(registrar), from, to);
    }

    protected static <T extends Module<T>> Codec<Modules<T>> createCodec(
            Registry<T> registry,
            Function<? super List<T>, ? extends Modules<T>> from,
            Function<? super Modules<T>, ? extends List<T>> to
    ) {
        return registry.byNameCodec().listOf().xmap(from, to);
    }

    protected static <T extends Module<T>> StreamCodec<RegistryFriendlyByteBuf, Modules<T>> createStreamCodec(Codec<Modules<T>> codec) {
        return ByteBufCodecs.fromCodecWithRegistries(codec);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Modules<?> other) {
            return this.modules.equals(other.modules);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
