package org.exodusstudio.stellaris.neoforge.common.registries;

import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.VanillaContainerWrapper;
import net.neoforged.neoforge.transfer.item.WorldlyContainerWrapper;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.jetbrains.annotations.Nullable;

public class CapabilityRegistry {

    public static void register(IEventBus bus) {
        bus.addListener(CapabilityRegistry::registerCapabilities);
    }

    @SuppressWarnings("unchecked")
    private static void registerCapabilities(RegisterCapabilitiesEvent event) {
        for (RegistrySupplier<BlockEntityType<?>> type : BlockEntitiesRegistry.BLOCK_ENTITY_TYPE) {
            event.registerBlockEntity(Capabilities.Item.BLOCK, (BlockEntityType<BlockEntity>) type.get(), CapabilityRegistry::itemHandler);
        }
    }

    private static @Nullable ResourceHandler<ItemResource> itemHandler(BlockEntity blockEntity, @Nullable Direction side) {
        if (blockEntity instanceof WorldlyContainer container) {
            return new WorldlyContainerWrapper(container, side);
        }
        if (blockEntity instanceof Container container) {
            return VanillaContainerWrapper.of(container);
        }
        return null;
    }
}
