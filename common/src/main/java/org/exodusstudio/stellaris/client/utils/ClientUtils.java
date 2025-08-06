package org.exodusstudio.stellaris.client.utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Optional;

public class ClientUtils {

    public static Entity createEntity(Level level, ResourceLocation location) {

        Optional<EntityType<?>> maybeType = BuiltInRegistries.ENTITY_TYPE.getOptional(location);
        if (maybeType.isEmpty()) {
            return EntityType.PIG.create(level, EntitySpawnReason.LOAD);
        }
        EntityType<?> type = maybeType.get();

        return type.create(level, EntitySpawnReason.LOAD);
    }

    public static void renderEntityInInventory(GuiGraphics guiGraphics, float x, float y, float scale, Vector3f translate, Quaternionf pose, @Nullable Quaternionf cameraOrientation, Entity entity) {
    }




}
