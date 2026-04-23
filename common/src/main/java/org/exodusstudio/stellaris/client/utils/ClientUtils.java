package org.exodusstudio.stellaris.client.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ClientUtils {

    /**
     * Create an entity from a location. Used to render entity in GUI
     * @param level the player level used to render the entity.
     * @param location the location of the entity
     * @return The entity created
     */
    public static Entity createEntity(Level level, Identifier location) {

        Optional<EntityType<?>> maybeType = BuiltInRegistries.ENTITY_TYPE.getOptional(location);
        if (maybeType.isEmpty()) {
            return EntityType.PIG.create(level, EntitySpawnReason.LOAD);
        }
        EntityType<?> type = maybeType.get();

        return type.create(level, EntitySpawnReason.LOAD);
    }

    /**
     * Add a button to a page. If the page don't have space, it creates a new page.
     * Used for pagination
     * @param pages the list of list we are adding button
     * @param button the button we want to add
     * @param size the size of a page
     * @param <T> the type of the button
     */
    public static <T> void addButtonToList(ArrayList<ArrayList<T>> pages, T button, int size) {
        if (pages.isEmpty()) {
            ArrayList<T> list = new ArrayList<>();
            list.add(button);
            pages.add(list);
            return;
        }

        for (ArrayList<T> buttons : pages) {
            if (buttons.size() < size) {
                buttons.add(button);
                break;
            }
            else if (buttons.size() == size) {
                if (pages.indexOf(buttons) + 1 >= pages.size()) {
                    ArrayList<T> list = new ArrayList<>();
                    list.add(button);
                    pages.add(list);
                    break;
                }
            }
        }
    }

    public static void resolveUUIDAsync(UUID uuid, Consumer<Optional<GameProfile>> uuidSupplier) {
        Minecraft minecraft = Minecraft.getInstance();
        CompletableFuture.supplyAsync(() -> minecraft.services().profileResolver().fetchById(uuid))
                .whenComplete((optionalGameProfile, throwable) -> minecraft.execute(() -> {
                    Optional<GameProfile> safeResult = throwable == null && optionalGameProfile != null
                            ? optionalGameProfile
                            : Optional.empty();
                    uuidSupplier.accept(safeResult);
                }));
    }



}
