package org.exodusstudio.stellaris.client.renderers.trophy;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Set;

public record TrophyFit(float scale, float x, float y, float z) {

    /**
     * Everything here is in block units, not model pixels: ModelPart divides its own pose by 16 and
     * Vertex#worldX divides cube coordinates by 16, so both the PoseStack and the visited geometry speak
     * blocks. Feeding pixel values in puts the model 16x too big and 16 blocks away.
     */
    private static final float TARGET_SIZE = 14.0F / 16.0F;

    /**
     * Block floor under the standard entity render flip (translate 0.5/1.5/0.5 then scale -1/-1/1). The flip
     * makes larger part-space Y lower in the world, so the model's footing is its MAXIMUM measured Y.
     */
    private static final float FLOOR_Y = 1.5F;

    /** Block centre under that same flip. */
    private static final float CENTRE_Y = 1.0F;

    /**
     * Anchors the trophy on the block floor: what a placed block wants.
     */
    public static TrophyFit resting(ModelPart part, Set<String> ignored) {
        float[] bounds = measure(part, ignored);
        if (bounds == null) {
            return new TrophyFit(1.0F, 0.0F, FLOOR_Y, 0.0F);
        }
        float scale = scaleFor(bounds);
        return anchored(bounds, scale, FLOOR_Y - scale * bounds[4]);
    }

    /**
     * Anchors the trophy in the middle of the block: what the item wants. Item display transforms rotate
     * about model-space centre, so a floor-resting model hangs off the bottom of the slot — worst for the
     * wide, flat Star Crawler, which barely rises off the floor at all.
     */
    public static TrophyFit centred(ModelPart part, Set<String> ignored) {
        float[] bounds = measure(part, ignored);
        if (bounds == null) {
            return new TrophyFit(1.0F, 0.0F, CENTRE_Y, 0.0F);
        }
        float scale = scaleFor(bounds);
        return anchored(bounds, scale, CENTRE_Y - scale * (bounds[1] + bounds[4]) / 2.0F);
    }

    private static float scaleFor(float[] bounds) {
        float largest = Math.max(bounds[3] - bounds[0], Math.max(bounds[4] - bounds[1], bounds[5] - bounds[2]));
        return largest > 0.0F ? TARGET_SIZE / largest : 1.0F;
    }

    private static TrophyFit anchored(float[] bounds, float scale, float y) {
        return new TrophyFit(scale,
                -scale * (bounds[0] + bounds[3]) / 2.0F,
                y,
                -scale * (bounds[2] + bounds[5]) / 2.0F);
    }

    /**
     * Boss models are authored at entity scale and sit nowhere near the block origin, so measure the baked
     * geometry and derive the transform instead of hand-tuning offsets per boss.
     *
     * <p>Walks the tree via {@link ModelPart#visit} rather than {@code getExtentsForGui} so {@code ignored}
     * can drop parts by name — see {@link TrophyBoss#ignoredParts()} for why that is needed.
     *
     * @return min/max XYZ in block units, or null when nothing was measured.
     */
    private static float[] measure(ModelPart part, Set<String> ignored) {
        float[] bounds = {
                Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
                Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY
        };
        Vector3f corner = new Vector3f();

        part.visit(new PoseStack(), (pose, path, index, cube) -> {
            if (isIgnored(path, ignored)) {
                return;
            }
            Matrix4f matrix = pose.pose();
            for (ModelPart.Polygon polygon : cube.polygons) {
                for (ModelPart.Vertex vertex : polygon.vertices()) {
                    corner.set(vertex.worldX(), vertex.worldY(), vertex.worldZ());
                    matrix.transformPosition(corner);
                    bounds[0] = Math.min(bounds[0], corner.x);
                    bounds[1] = Math.min(bounds[1], corner.y);
                    bounds[2] = Math.min(bounds[2], corner.z);
                    bounds[3] = Math.max(bounds[3], corner.x);
                    bounds[4] = Math.max(bounds[4], corner.y);
                    bounds[5] = Math.max(bounds[5], corner.z);
                }
            }
        });

        return (Float.isFinite(bounds[0]) && Float.isFinite(bounds[3])) ? bounds : null;
    }

    private static boolean isIgnored(String path, Set<String> ignored) {
        if (ignored.isEmpty()) {
            return false;
        }
        for (String segment : path.split("/")) {
            if (ignored.contains(segment)) {
                return true;
            }
        }
        return false;
    }

    public void apply(PoseStack poseStack) {
        poseStack.translate(this.x, this.y, this.z);
        poseStack.scale(this.scale, this.scale, this.scale);
    }
}
