package org.exodusstudio.stellaris.client.renderers.trophy;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;

public class TrophyModel extends Model<BossTrophyRenderState> {

    private static final float BLEND_IN_TICKS = 4.0F;
    private static final float BLEND_OUT_TICKS = 10.0F;

    private final KeyframeAnimation animation;
    private final float length;

    public TrophyModel(TrophyBoss boss, ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.animation = boss.bakeAnimation(root);
        this.length = boss.animationTicks();
    }

    @Override
    public void setupAnim(BossTrophyRenderState state) {
        super.setupAnim(state);
        float ticks = state.animationTicks;
        if (ticks < 0.0F || ticks >= this.length + BLEND_OUT_TICKS) {
            return;
        }

        float weight = smoothStep(ticks / BLEND_IN_TICKS) * (1.0F - smoothStep((ticks - this.length) / BLEND_OUT_TICKS));
        this.animation.apply((long) (Math.min(ticks, this.length) * 50.0F), weight);
    }

    private static float smoothStep(float t) {
        t = Mth.clamp(t, 0.0F, 1.0F);
        return t * t * t;
    }
}
