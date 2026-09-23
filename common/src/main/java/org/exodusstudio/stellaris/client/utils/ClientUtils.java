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
import org.exodusstudio.stellaris.client.renderers.mobs.StellarisMobRenderState;
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

    private static final float GUI_VIEW_PITCH = 20.0F;
    private static final float GUI_VIEW_YAW = -30.0F;
    private static final int FULL_BRIGHT = 0xF000F0;

    public static void renderEntityInGui(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, float size, float offsetY, Entity entity, Vector3f rotation) {
        renderEntityInGui(graphics, x0, y0, x1, y1, size, offsetY, extractRenderState(entity), rotation);
    }

    public static void renderEntityIcon(GuiGraphicsExtractor graphics, int x, int y, int boxSize, float size, float offsetY, Entity entity, Vector3f rotation) {
        EntityRenderState renderState = extractRenderState(entity);
        if (size <= 0) {
            float extent = Math.max(renderState.boundingBoxHeight, renderState.boundingBoxWidth);
            size = extent > 0 ? boxSize * 0.85F / extent : boxSize;
        }
        renderEntityInGui(graphics, x, y, x + boxSize, y + boxSize, size, offsetY, renderState, rotation);
    }

    private static void renderEntityInGui(GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, float size, float offsetY, EntityRenderState renderState, Vector3f rotation) {
        if (rotation == null) {
            rotation = new Vector3f();
        }

        if (renderState instanceof LivingEntityRenderState livingRenderState) {
            livingRenderState.bodyRot = 180.0F;
            livingRenderState.yRot = 0.0F;
            livingRenderState.xRot = 0.0F;
        } else if (renderState instanceof StellarisMobRenderState mobRenderState) {
            mobRenderState.bodyRotation = 180.0F;
            mobRenderState.headYaw = 0.0F;
            mobRenderState.headPitch = 0.0F;
            mobRenderState.inWater = true;
            mobRenderState.swimAmount = 1.0F;
        }
        renderState.lightCoords = FULL_BRIGHT;

        Quaternionf view = new Quaternionf().rotateX((float) Math.toRadians(GUI_VIEW_PITCH + rotation.x));
        Quaternionf pose = new Quaternionf()
                .rotateZ((float) Math.PI)
                .mul(view)
                .rotateY((float) Math.toRadians(GUI_VIEW_YAW + rotation.y))
                .rotateZ((float) Math.toRadians(rotation.z));

        Vector3f translation = new Vector3f(0.0F, renderState.boundingBoxHeight / 2.0F + offsetY, 0.0F);
        graphics.entity(renderState, size, translation, pose, view, x0, y0, x1, y1);
    }

    private static EntityRenderState extractRenderState(Entity entity) {
        EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderer<? super Entity, ?> renderer = entityRenderDispatcher.getRenderer(entity);
        EntityRenderState renderState = renderer.createRenderState(entity, 1.0F);
        renderState.shadowPieces.clear();
        renderState.outlineColor = 0;
        if (renderState instanceof LivingEntityRenderState livingRenderState) {
            livingRenderState.boundingBoxWidth /= livingRenderState.scale;
            livingRenderState.boundingBoxHeight /= livingRenderState.scale;
            livingRenderState.scale = 1.0F;
        }
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
