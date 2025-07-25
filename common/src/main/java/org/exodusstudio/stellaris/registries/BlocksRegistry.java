package org.exodusstudio.stellaris.registries;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.exodusstudio.stellaris.Stellaris.*;
import static org.exodusstudio.stellaris.registries.ItemsRegistry.item;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;

public final class BlocksRegistry extends ModRegistries {

    private static final BlocksRegistry blocksRegistry = new BlocksRegistry();
    public static Registrar<Block> BLOCKS = MANAGER.get().get(Registries.BLOCK);
    private static final Set<BlockSupplier<?, ?>> BLOCK_SUPPLIERS = new HashSet<>();
    private static boolean blockItemsRegistered = false;

    public static final BlockSupplier<Block, BlockItem> MOON_ROCK = blockWithItem("moon_rock", ofFullCopy(Blocks.STONE), Block::new);


    protected static <B extends Block> RegistrySupplier<B> block(String name,
                                                              BlockBehaviour.Properties properties,
                                                              Function<BlockBehaviour.Properties, B> block) {
        ResourceLocation id = id(name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        return BLOCKS.register(id, () -> block.apply(properties.setId(key)));
    }

    public static <B extends Block> BlockSupplier<B, BlockItem> blockWithItem(String name,
                                                                              BlockBehaviour.Properties properties,
                                                                              Function<BlockBehaviour.Properties, B> block) {

        return blockWithItem(name, properties, block, new Item.Properties()); // TODO add blocks tab
    }

    public static <B extends Block> BlockSupplier<B, BlockItem> blockWithItem(String name,
                                                                              BlockBehaviour.Properties properties,
                                                                              Function<BlockBehaviour.Properties, B> block,
                                                                              Item.Properties itemProperties) {

        return blockWithCustomItem(name, properties, block,itemProperties, b -> p -> new BlockItem(b, p));
    }

    public static <B extends Block, I extends BlockItem> BlockSupplier<B, I> blockWithCustomItem(String name,
                                                                                                 BlockBehaviour.Properties properties,
                                                                                                 Function<BlockBehaviour.Properties, B> blockFunc,
                                                                                                 Item.Properties itemProperties,
                                                                                                 Function<B, Function<Item.Properties, I>> itemFunc) {

        RegistrySupplier<B> block = block(name, properties, blockFunc);
        Function<B, RegistrySupplier<I>> blockItemGetter = b -> item(name, itemProperties, itemFunc.apply(b));
        return new BlockSupplier<>(block, blockItemGetter);
    }

    public static class BlockSupplier<B extends Block, I extends BlockItem> implements Supplier<B> {

        private final @NotNull RegistrySupplier<B> blockSupplier;
        private final @NotNull Function<B, RegistrySupplier<I>> blockItemGetter;

        private @Nullable RegistrySupplier<I> itemSupplier;

        public BlockSupplier(final @NotNull RegistrySupplier<B> blockSupplier, final @NotNull Function<B, RegistrySupplier<I>> blockItemGetter) {
            this.blockSupplier = blockSupplier;
            this.blockItemGetter = blockItemGetter;
            this.itemSupplier = null;
            BLOCK_SUPPLIERS.add(this);
        }

        @Override
        public @NotNull B get() {
            return blockSupplier.get();
        }

        public @NotNull I getItem() {
            return getItemSupplier().get();
        }

        public @NotNull RegistrySupplier<B> getBlockSupplier() {
            return blockSupplier;
        }

        public @NotNull RegistrySupplier<I> getItemSupplier() {
            if (itemSupplier == null) {
                itemSupplier = blockItemGetter.apply(blockSupplier.get());
            }
            return itemSupplier;
        }

        public void registerItem() {
            getItemSupplier();
        }
    }

    public static void registerBlockItems() {
        if (!blockItemsRegistered) {
            BLOCK_SUPPLIERS.forEach(BlockSupplier::registerItem);
            blockItemsRegistered = true;
        }
    }

    @Override
    public Registrar<?> getRegistrar() {
        return BLOCKS;
    }

    @Override
    public ModRegistries getStaticInstance() {
        return blocksRegistry;
    }

    private BlocksRegistry() {
        super();
    }

}
