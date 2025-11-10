package org.exodusstudio.stellaris.common.rocket;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.exodusstudio.stellaris.common.items.modules.RocketModule;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public record RocketModules(List<ItemStack> modules) implements Serializable {

    public static RocketModules empty() {
        return new RocketModules(List.of());
    }

    public static final Codec<RocketModules> CODEC = ItemStack.OPTIONAL_CODEC.listOf().xmap(RocketModules::new, modules -> modules.modules);
    public static final StreamCodec<RegistryFriendlyByteBuf, RocketModules> STREAM_CODEC = ItemStack.OPTIONAL_STREAM_CODEC
            .apply(ByteBufCodecs.list())
            .map(RocketModules::new, modules -> modules.modules);


    public ItemStack getItemUnsafe(int index) {
        return this.modules.get(index);
    }

    public Stream<ItemStack> itemCopyStream() {
        return this.modules.stream().map(ItemStack::copy);
    }

    public Iterable<ItemStack> items() {
        return this.modules;
    }

    public Iterable<ItemStack> itemsCopy() {
        return Lists.transform(this.modules, ItemStack::copy);
    }

    public static ItemStack getIfContains(Entity entity, Item module) {
        ItemStack moduleToReturn = ItemStack.EMPTY;
        RocketModules rocketModules = entity.getEntityData().get(RocketEntity.ROCKET_MODULES);
        if (rocketModules == null || rocketModules.items() == null) {
            return moduleToReturn;
        }
        for (ItemStack moduleStack : rocketModules.items()) {
            if (moduleStack.is(module)) {
                moduleToReturn = moduleStack;
                break;
            }
        }
        return moduleToReturn;
    }

    public static boolean containsAllInModules(Entity entity, List<Item> modules) {
        boolean containsAll = true;
        for (Item item : modules) {
            if (item instanceof RocketModule validModule) {
                if (!containsInModules(entity, validModule)) {
                    containsAll = false;
                }
                break;
            }
        }
        return containsAll;
    }

    public static boolean containsInModules(Entity entity, ItemStack module) {
        return containsInModules(entity, getModule(module));
    }

    public static boolean containsInModules(Entity entity, RocketModule module) {

        RocketModules rocketModules = entity.getEntityData().get(RocketEntity.ROCKET_MODULES);
        if (rocketModules == null) {
            return false;
        }
        boolean boolToReturn = false;
        for (RocketModule module1 : rocketModules.getModules()) {
            if (module1 == module) {
                boolToReturn = true;
                break;
            }
        }

        return boolToReturn;
    }

    public List<RocketModule> getModules() {
        return Lists.transform(this.modules, RocketModules::getModule);
    }

    private static RocketModule getModule(ItemStack itemStack) {
        if (itemStack.getItem() instanceof RocketModule spaceSuitModule) {
            return spaceSuitModule;
        }
        return null; //failsafe, shouldn't happen unless tampered with or incorrect checks for upgrade station
    }

    public static class Mutable implements Iterable<ItemStack> {

        public static Mutable EMPTY = new Mutable(RocketModules.empty());

        private final List<ItemStack> modules;

        public Mutable(RocketModules contents) {
            this.modules = new ArrayList<>(contents.modules);
        }

        public Mutable insert(ItemStack stack) {
            if (!stack.isEmpty() && stack.getItem().canFitInsideContainerItems()) {
                this.modules.add(stack);
            }
            return this;
        }

        public RocketModules toImmutable() {
            return new RocketModules(List.copyOf(this.modules));
        }

        @Override
        public @NotNull Iterator<ItemStack> iterator() {
            return this.modules.iterator();
        }
    }

}
