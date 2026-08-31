package org.exodusstudio.stellaris.common.events;

import dev.architectury.event.events.common.LootEvent;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.exodusstudio.stellaris.common.registries.ItemsRegistry;

public class LootEvents {

    public static void init() {
        LootEvent.MODIFY_LOOT_TABLE.register(new LootEvent.ModifyLootTable() {
            @Override
            @SuppressWarnings("removal")
            public void modifyLootTable(ResourceKey<LootTable> key, LootEvent.LootTableModificationContext context,
                                        boolean builtin) {
                modifyLootTable(null, key, context, builtin);
            }

            @Override
            public void modifyLootTable(HolderLookup.Provider registries, ResourceKey<LootTable> key,
                                        LootEvent.LootTableModificationContext context, boolean builtin) {
                // Only extend the built-in table, so a data pack overriding it stays a full overwrite.
                if (builtin && BuiltInLootTables.ABANDONED_MINESHAFT.equals(key)) {
                    context.addPool(titaniumPool());
                }
            }
        });
    }

    private static LootPool.Builder titaniumPool() {
        return LootPool.lootPool()
                .setRolls(UniformGenerator.between(1, 2))
                .add(LootItem.lootTableItem(ItemsRegistry.TITANIUM_INGOT.get())
                        .setWeight(2)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 8))))
                .add(EmptyLootItem.emptyItem().setWeight(3));
    }
}
