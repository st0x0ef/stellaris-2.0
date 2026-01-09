package org.exodusstudio.stellaris.platform.neoforge;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.exodusstudio.stellaris.platform.ArmorPlatform;

import java.util.HashMap;
import java.util.Map;

public class ArmorPlatformImpl {
    public static final Map<Item, ArmorRenderer> ARMOR_RENDERERS = new HashMap<>();

    public static void registerArmor(ModelLayerLocation layer, ArmorPlatform.ArmorFactory factory, Identifier identifier, Item... items) {
        for (Item item : items) {
            ARMOR_RENDERERS.put(item, new ArmorRenderer(identifier, layer, factory));
        }
    }

    public record ArmorRenderer(Identifier texture, ModelLayerLocation layer, ArmorPlatform.ArmorFactory factory) {

    }
}
