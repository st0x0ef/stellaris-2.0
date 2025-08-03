package org.exodusstudio.stellaris.registries;

import dev.architectury.registry.registries.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.exodusstudio.stellaris.util.BlockSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.exodusstudio.stellaris.Stellaris.*;
import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;

public final class BlocksRegistry {

    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registries.BLOCK);

    public static final BlockSupplier<Block, BlockItem> MOON_ROCK = blockWithItem("moon_rock", ofFullCopy(Blocks.STONE));

    public static <B extends Block> @NotNull RegistrySupplier<B> block(String name,
                                                               BlockBehaviour.Properties properties,
                                                               Function<BlockBehaviour.Properties, B> blockFunc) {
        ResourceLocation id = id(name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        return BLOCKS.register(id, () -> blockFunc.apply(properties.setId(key)));
    }

    public static RegistrySupplier<Block> block(String name, BlockBehaviour.Properties properties) {
        return block(name, properties, Block::new);
    }

    public static <B extends Block> @NotNull BlockSupplier<B, BlockItem> blockWithItem(String name,
                                                                      BlockBehaviour.Properties properties,
                                                                      Function<BlockBehaviour.Properties, B> blockFunc) {
        RegistrySupplier<B> b = block(name, properties, blockFunc);
        RegistrySupplier<BlockItem> i = ItemsRegistry.item(
                name,
                new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_MAIN),
                p -> new BlockItem(b.get(), p));
        return new BlockSupplier<>(b,i);
    }

    public static @NotNull BlockSupplier<Block, BlockItem> blockWithItem(String name, BlockBehaviour.Properties properties) {
        RegistrySupplier<Block> b = block(name, properties, Block::new);
        RegistrySupplier<BlockItem> i = ItemsRegistry.item(
                name,
                new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_MAIN),
                p -> new BlockItem(b.get(), p));
        return new BlockSupplier<>(b,i);
    }

    public static <B extends Block> @NotNull BlockSupplier<B, BlockItem> blockWithItem(String name,
                                                                                       BlockBehaviour.Properties properties,
                                                                                       Function<BlockBehaviour.Properties, B> blockFunc,
                                                                                       Item.Properties itemProperties) {
        RegistrySupplier<B> b = block(name, properties, blockFunc);
        RegistrySupplier<BlockItem> i = ItemsRegistry.item(name, itemProperties, p -> new BlockItem(b.get(), p));
        return new BlockSupplier<>(b,i);
    }

    public static <B extends Block, I extends BlockItem> @NotNull BlockSupplier<B, I> blockWithCustomItem(String name,
                                                                            BlockBehaviour.Properties properties,
                                                                            Function<BlockBehaviour.Properties, B> blockFunc,
                                                                            Item.Properties itemProperties,
                                                                            BiFunction<Block, Item.Properties, I> itemSupplier) {
        RegistrySupplier<B> b = block(name, properties, blockFunc);
        RegistrySupplier<I> i = ItemsRegistry.item(name, itemProperties, p -> itemSupplier.apply(b.get(), p));
        return new BlockSupplier<>(b,i);
    }

    private BlocksRegistry() {}

}
