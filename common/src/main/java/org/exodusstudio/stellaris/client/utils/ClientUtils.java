package org.exodusstudio.stellaris.client.utils;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
        return createEntity(level, maybeType);
    }

    public static Entity createEntity(Level level, Optional<EntityType<?>> maybeType) {
        if (maybeType.isEmpty()) {
            return EntityType.PIG.create(level, EntitySpawnReason.LOAD);
        }
        EntityType<?> type = maybeType.get();

        return type.create(level, EntitySpawnReason.LOAD);
    }

    /**
     * Render a living entity in a GUI with a default rotation and mouse-following movement.
     */
    public static void renderEntityInGui(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, int size, float offsetY, float mouseX, float mouseY, LivingEntity entity, Vector3f defaultRotation) {
        EntityRenderState renderState = extractRenderState(entity);

        if(defaultRotation == null){
            defaultRotation = new Vector3f(0.0F, 0.0F, 0.0F);
        }

        float centerX = (float) (x0 + x1) / 2.0F;
        float centerY = (float) (y0 + y1) / 2.0F;
        float xAngle = (float) Math.atan((centerX - mouseX) / 40.0F);
        float yAngle = (float) Math.atan((centerY - mouseY) / 40.0F);

        applyLivingEntityDefaults(renderState);

        float defaultPitch = defaultRotation.x;
        float defaultYaw = defaultRotation.y;
        float defaultRoll = defaultRotation.z;

        if (renderState instanceof LivingEntityRenderState livingRenderState) {
            livingRenderState.bodyRot = 180.0F + defaultYaw + xAngle * 20.0F;
            livingRenderState.yRot = defaultYaw + xAngle * 20.0F;

            if (livingRenderState.pose != Pose.FALL_FLYING) {
                livingRenderState.xRot = defaultPitch - yAngle * 20.0F;
            } else {
                livingRenderState.xRot = defaultPitch;
            }
        }

        Quaternionf rotation = new Quaternionf()
                .rotateZ((float) Math.PI)
                .rotateY(defaultYaw * ((float) Math.PI / 180F))
                .rotateX(defaultPitch * ((float) Math.PI / 180F))
                .rotateZ(defaultRoll * ((float) Math.PI / 180F));
        Quaternionf xRotation = new Quaternionf().rotateX(yAngle * 20.0F * ((float) Math.PI / 180F));
        rotation.mul(xRotation);

        Vector3f translation = new Vector3f(0.0F, renderState.boundingBoxHeight / 2.0F + offsetY, 0.0F);
        graphics.entity(renderState, (float) size, translation, rotation, xRotation, x0, y0, x1, y1);
    }

    private static void applyLivingEntityDefaults(EntityRenderState renderState) {
        if (renderState instanceof LivingEntityRenderState livingRenderState) {
            livingRenderState.boundingBoxWidth /= livingRenderState.scale;
            livingRenderState.boundingBoxHeight /= livingRenderState.scale;
            livingRenderState.scale = 1.0F;
        }
    }

    private static EntityRenderState extractRenderState(LivingEntity entity) {
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super LivingEntity, ?> renderer = entityRenderDispatcher.getRenderer(entity);
        EntityRenderState renderState = renderer.createRenderState(entity, 1.0F);
        renderState.shadowPieces.clear();
        renderState.outlineColor = 0;
        return renderState;
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
                    Optional<GameProfile> safeResult = throwable == null
                            ? Optional.ofNullable(optionalGameProfile).orElse(Optional.empty())
                            : Optional.empty();
                    uuidSupplier.accept(safeResult);
                }));
    }


}
