package org.exodusstudio.stellaris.common.registries;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.exodusstudio.stellaris.common.utils.ResourceLocationUtils;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.world.level.block.state.BlockBehaviour.Properties.ofFullCopy;
import static org.exodusstudio.stellaris.Stellaris.MOD_ID;

public final class BlocksRegistry {

    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registries.BLOCK);

    public static final RegistrySupplier<Block> MOON_ROCK = blockWithItem("moon_rock", ofFullCopy(Blocks.STONE));




    public static <B extends Block> @NotNull RegistrySupplier<B> block(String name,
                                                               BlockBehaviour.Properties properties,
                                                               Function<BlockBehaviour.Properties, B> blockFunc) {
        ResourceLocation id = ResourceLocationUtils.id(name);
        ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, id);
        return BLOCKS.register(id, () -> blockFunc.apply(properties.setId(key)));
    }

    public static @NotNull RegistrySupplier<Block> block(String name, BlockBehaviour.Properties properties) {
        return block(name, properties, Block::new);
    }

    public static @NotNull RegistrySupplier<Block> blockWithItem(String name, BlockBehaviour.Properties properties) {
        RegistrySupplier<Block> toReturn = block(name, properties);
        ItemsRegistry.item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_BLOCKS), p -> new BlockItem(toReturn.get(), p));
        return toReturn;
    }

    public static <B extends Block> @NotNull RegistrySupplier<B> blockWithItem(String name, BlockBehaviour.Properties properties,
                                                                               Function<BlockBehaviour.Properties, B> blockFunc) {
        RegistrySupplier<B> toReturn = block(name, properties, blockFunc);
        ItemsRegistry.item(name, new Item.Properties().arch$tab(CreativeTabsRegistry.STELLARIS_BLOCKS), p -> new BlockItem(toReturn.get(), p));
        return toReturn;
    }

    public static <B extends Block> @NotNull RegistrySupplier<B> blockWithItem(String name, BlockBehaviour.Properties properties,
                                                                               Function<BlockBehaviour.Properties, B> blockFunc,
                                                                               Item.Properties itemProperties) {
        RegistrySupplier<B> b = block(name, properties, blockFunc);
        ItemsRegistry.item(name, itemProperties, p -> new BlockItem(b.get(), p));
        return b;
    }

    public static <B extends Block, I extends BlockItem> @NotNull RegistrySupplier<B> blockWithCustomItem(String name, BlockBehaviour.Properties properties,
                                                                            Function<BlockBehaviour.Properties, B> blockFunc, Item.Properties itemProperties,
                                                                            BiFunction<Block, Item.Properties, I> itemSupplier) {
        RegistrySupplier<B> b = block(name, properties, blockFunc);
        ItemsRegistry.item(name, itemProperties, p -> itemSupplier.apply(b.get(), p));
        return b;
    }

    private BlocksRegistry() {}

}
