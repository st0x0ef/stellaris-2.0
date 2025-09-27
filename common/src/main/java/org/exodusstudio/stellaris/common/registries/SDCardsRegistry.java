package org.exodusstudio.stellaris.common.registries;

import org.exodusstudio.stellaris.common.sdcard.SDCard;
import org.exodusstudio.stellaris.common.sdcard.TestSDCard;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.function.Supplier;

public class SDCardsRegistry {

    private static final HashMap<Integer, SDCard> REGISTRY = new HashMap<>();

    public static <T extends SDCard> SDCard register(int ID, Supplier<T> factory) {
        return SDCardsRegistry.REGISTRY.computeIfAbsent(ID, k -> factory.get());
    }

    public static SDCard get(int ID) {
        return SDCardsRegistry.REGISTRY.get(ID);
    }

    public static Collection<SDCard> values() {
        return Collections.unmodifiableCollection(SDCardsRegistry.REGISTRY.values());
    }

    public static void register() {
        register(-1, TestSDCard::new);
    }

}
