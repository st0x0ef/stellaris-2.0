package org.exodusstudio.stellaris.client.renderers.mobs.starcrawler;

import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawler.StarCrawlerEntity;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawler.StarCrawlerEntity.AttackState;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class StarCrawlerModel extends EntityModel<StarCrawlerRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(IdentifierUtils.id("star_crawler"), "main");

    private static final float WALK_TIME_SCALE = 1.05F;
    private static final float WALK_AMPLITUDE_SCALE = 1.85F;
    private static final float WALK_WINDUP_FADE_TICKS = 3.0F;
    private static final float MAX_CHAIN_X_ROTATION = 1.35F;
    private static final float LEAP_SHOULDER_TUCK = 0.48F;
    private static final float LEAP_LIMB_TUCK = 0.70F;
    private static final float LEAP_HAND_TUCK = 0.82F;
    private static final float WHIPLASH_SHOULDER_OVERSHOOT = 0.06F;
    private static final float WHIPLASH_LIMB_OVERSHOOT = 0.10F;
    private static final float WHIPLASH_HAND_OVERSHOOT = 0.16F;

    private final ModelPart visualRoot;
    private final ModelPart body;
    private final ModelPart[] shoulders;
    private final ModelPart[] limbs;
    private final ModelPart[] hands;
    private final KeyframeAnimation walkAnimation;
    private final KeyframeAnimation attackAnimation;

    public StarCrawlerModel(ModelPart root) {
        super(root);
        this.visualRoot = root.getChild("root");
        this.body = this.visualRoot.getChild("body");

        ModelPart arm1 = this.visualRoot.getChild("arm1");
        ModelPart arm2 = this.visualRoot.getChild("arm2");
        ModelPart arm3 = this.visualRoot.getChild("arm3");
        ModelPart arm4 = this.visualRoot.getChild("arm4");
        ModelPart limb1 = arm1.getChild("limb");
        ModelPart limb2 = arm2.getChild("limb2");
        ModelPart limb3 = arm3.getChild("limb3");
        ModelPart limb4 = arm4.getChild("limb4");

        this.shoulders = new ModelPart[]{arm1, arm2, arm3, arm4};
        this.limbs = new ModelPart[]{limb1, limb2, limb3, limb4};
        this.hands = new ModelPart[]{
                limb1.getChild("hand"),
                limb2.getChild("hand2"),
                limb3.getChild("hand3"),
                limb4.getChild("hand4")
        };
        this.walkAnimation = StarCrawlerAnimations.WALK.bake(root);
        this.attackAnimation = StarCrawlerAnimations.ATTACK.bake(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition meshRoot = meshDefinition.getRoot();
        PartDefinition root = meshRoot.addOrReplaceChild(
                "root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-8.0F, -13.0F, -8.0F, 16.0F, 10.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 26).addBox(-7.0F, -9.0F, -7.0F, 14.0F, 9.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(48, 134).addBox(-1.5F, -20.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)),
                PartPose.ZERO);

        PartDefinition arm1 = root.addOrReplaceChild("arm1", CubeListBuilder.create()
                .texOffs(0, 49).addBox(-6.0F, -3.9F, 0.0F, 12.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 81).addBox(-5.0F, 4.1F, -2.0F, 10.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -7.0F, 7.0F));
        PartDefinition limb1 = arm1.addOrReplaceChild("limb", CubeListBuilder.create()
                .texOffs(0, 65).addBox(-5.0F, -4.3F, 0.25F, 10.0F, 7.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(126, 89).addBox(-1.0F, -4.8F, 3.75F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 105).addBox(-4.0F, 2.7F, -0.75F, 8.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 1.3F, 6.75F));
        PartDefinition hand1 = limb1.addOrReplaceChild("hand", CubeListBuilder.create()
                .texOffs(38, 93).addBox(-4.0F, -3.3F, 0.25F, 8.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(108, 38).addBox(-1.0F, -3.8F, 3.75F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(68, 108).addBox(-3.0F, 2.7F, -0.75F, 6.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.1F, 8.0F));

        PartDefinition arm2 = root.addOrReplaceChild("arm2", CubeListBuilder.create()
                .texOffs(40, 49).addBox(-6.0F, -3.9F, 0.0F, 12.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(38, 81).addBox(-5.0F, 4.1F, -2.0F, 10.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-8.0F, -7.0F, 0.0F, 0.0F, -90.0F * Mth.DEG_TO_RAD, 0.0F));
        PartDefinition limb2 = arm2.addOrReplaceChild("limb2", CubeListBuilder.create()
                .texOffs(38, 65).addBox(-5.0F, -4.3F, 0.25F, 10.0F, 7.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(138, 4).addBox(-1.0F, -4.8F, 3.75F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(106, 93).addBox(-4.0F, 2.7F, -0.75F, 8.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 1.3F, 6.75F));
        PartDefinition hand2 = limb2.addOrReplaceChild("hand2", CubeListBuilder.create()
                .texOffs(72, 93).addBox(-4.0F, -3.3F, 0.25F, 8.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(138, 8).addBox(-1.0F, -3.8F, 3.75F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(114, 58).addBox(-3.0F, 2.7F, -0.75F, 6.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.1F, 8.0F));

        PartDefinition arm3 = root.addOrReplaceChild("arm3", CubeListBuilder.create()
                .texOffs(56, 26).addBox(-6.0F, -3.9F, 0.0F, 12.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(76, 81).addBox(-5.0F, 4.1F, -2.0F, 10.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -7.0F, -8.0F, 0.0F, 180.0F * Mth.DEG_TO_RAD, 0.0F));
        PartDefinition limb3 = arm3.addOrReplaceChild("limb3", CubeListBuilder.create()
                .texOffs(76, 65).addBox(-5.0F, -4.3F, 0.25F, 10.0F, 7.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(138, 52).addBox(-1.0F, -4.8F, 3.75F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(106, 105).addBox(-4.0F, 2.7F, -0.75F, 8.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 1.3F, 6.75F));
        PartDefinition hand3 = limb3.addOrReplaceChild("hand3", CubeListBuilder.create()
                .texOffs(96, 16).addBox(-4.0F, -3.3F, 0.25F, 8.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(76, 138).addBox(-1.0F, -3.8F, 3.75F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(114, 70).addBox(-3.0F, 2.7F, -0.75F, 6.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.1F, 8.0F));

        PartDefinition arm4 = root.addOrReplaceChild("arm4", CubeListBuilder.create()
                .texOffs(64, 0).addBox(-6.0F, -3.9F, 0.0F, 12.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 93).addBox(-5.0F, 4.1F, -2.0F, 10.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(8.0F, -7.0F, 0.0F, 0.0F, 90.0F * Mth.DEG_TO_RAD, 0.0F));
        PartDefinition limb4 = arm4.addOrReplaceChild("limb4", CubeListBuilder.create()
                .texOffs(80, 42).addBox(-5.0F, -4.3F, 0.25F, 10.0F, 7.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(138, 82).addBox(-1.0F, -4.8F, 3.75F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(34, 108).addBox(-4.0F, 2.7F, -0.75F, 8.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 1.3F, 6.75F));
        PartDefinition hand4 = limb4.addOrReplaceChild("hand4", CubeListBuilder.create()
                .texOffs(104, 0).addBox(-4.0F, -3.3F, 0.25F, 8.0F, 6.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(86, 138).addBox(-1.0F, -3.8F, 3.75F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 117).addBox(-3.0F, 2.7F, -0.75F, 6.0F, 3.0F, 9.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.1F, 8.0F));

        rotatedCube(body, "cube_r1", 88, 16, -1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F,
                0.0F, -10.5F, 7.86603F, -30.0F, 0.0F, 0.0F);
        rotatedCube(body, "cube_r2", 98, 108, -1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F,
                7.86603F, -10.5F, 0.0F, 0.0F, 0.0F, 30.0F);
        rotatedCube(body, "cube_r3", 130, 52, -1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F,
                -7.86603F, -10.5F, 0.0F, 0.0F, 0.0F, -30.0F);
        rotatedCube(body, "cube_r4", 60, 134, -1.0F, -9.0F, -1.0F, 2.0F, 9.0F, 2.0F,
                0.12061F, -8.34202F, 0.06031F, 0.0F, 0.0F, 20.0F);
        rotatedCube(body, "cube_r5", 68, 134, -1.0F, -9.0F, -1.0F, 2.0F, 9.0F, 2.0F,
                0.0F, -8.34202F, 0.06031F, 0.0F, 0.0F, -20.0F);
        rotatedCube(body, "cube_r6", 0, 136, -1.0F, -9.0F, -1.0F, 2.0F, 9.0F, 2.0F,
                0.06031F, -8.34202F, 0.0F, 20.0F, 0.0F, 0.0F);
        rotatedCube(body, "cube_r7", 8, 136, -1.0F, -9.0F, -1.0F, 2.0F, 9.0F, 2.0F,
                0.06031F, -8.34202F, 0.12061F, -20.0F, 0.0F, 0.0F);
        rotatedCube(body, "cube_r8", 138, 12, -1.5F, -1.0F, -2.0F, 3.0F, 1.0F, 2.0F,
                -3.0F, -12.5F, -4.0F, 0.0F, 45.0F, 0.0F);
        rotatedCube(body, "cube_r9", 138, 86, -1.5F, -1.0F, 0.0F, 3.0F, 1.0F, 2.0F,
                3.0F, -12.5F, 4.0F, 0.0F, 45.0F, 0.0F);
        rotatedCube(body, "cube_r10", 136, 89, -2.0F, -1.0F, -1.5F, 2.0F, 1.0F, 3.0F,
                -4.0F, -12.5F, 3.0F, 0.0F, 45.0F, 0.0F);
        rotatedCube(body, "cube_r11", 138, 0, 0.0F, -1.0F, -1.5F, 2.0F, 1.0F, 3.0F,
                4.0F, -12.5F, -3.0F, 0.0F, 45.0F, 0.0F);
        rotatedCube(body, "cube_r12", 104, 58, -2.0F, -3.0F, -1.0F, 3.0F, 3.0F, 2.0F,
                0.5F, -10.15F, -8.75F, -17.5F, 0.0F, 0.0F);
        rotatedCube(body, "cube_r13", 64, 23, -3.0F, -1.0F, -1.0F, 4.0F, 1.0F, 2.0F,
                1.0F, -12.25F, -8.5F, -15.0F, 0.0F, 0.0F);
        rotatedCube(body, "cube_r14", 76, 23, -3.0F, -1.0F, -1.0F, 4.0F, 1.0F, 2.0F,
                1.0F, -10.25F, -8.85F, -15.0F, 0.0F, 0.0F);

        rotatedCube(arm1, "cube_r15", 114, 89, -2.0F, -1.0F, -1.0F, 3.0F, 1.0F, 3.0F,
                0.5F, -3.4F, 5.0F, -10.0F, 0.0F, 0.0F);
        rotatedCube(arm1, "cube_r16", 56, 42, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                5.0F, 4.3F, 3.0F, 0.0F, 0.0F, 20.0F);
        rotatedCube(arm1, "cube_r17", 98, 117, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                -5.0F, 4.3F, 4.0F, 0.0F, 180.0F, -20.0F);
        rotatedCube(limb1, "cube_r18", 80, 58, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                4.0F, 3.0F, 4.25F, 0.0F, 0.0F, 20.0F);
        rotatedCube(limb1, "cube_r19", 64, 16, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                -4.0F, 3.0F, 5.25F, 0.0F, 180.0F, -20.0F);
        rotatedCube(hand1, "cube_r20", 96, 31, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                3.0F, 2.9F, 4.25F, 0.0F, 0.0F, 20.0F);
        rotatedCube(hand1, "cube_r21", 114, 82, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                -3.0F, 2.9F, 5.25F, 0.0F, 180.0F, -20.0F);

        rotatedCube(arm2, "cube_r22", 118, 52, -2.0F, -1.0F, -1.0F, 3.0F, 1.0F, 3.0F,
                0.5F, -3.4F, 4.0F, -10.0F, 0.0F, 0.0F);
        rotatedCube(arm2, "cube_r23", 118, 38, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                5.0F, 4.3F, 3.0F, 0.0F, 0.0F, 20.0F);
        rotatedCube(arm2, "cube_r24", 118, 45, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                -5.0F, 4.3F, 4.0F, 0.0F, 180.0F, -20.0F);
        rotatedCube(limb2, "cube_r25", 30, 120, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                4.0F, 3.0F, 4.25F, 0.0F, 0.0F, 20.0F);
        rotatedCube(limb2, "cube_r26", 120, 31, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                -4.0F, 3.0F, 5.25F, 0.0F, 180.0F, -20.0F);
        rotatedCube(hand2, "cube_r27", 54, 120, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                3.0F, 2.9F, 4.25F, 0.0F, 0.0F, 20.0F);
        rotatedCube(hand2, "cube_r28", 122, 117, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                -3.0F, 2.9F, 5.25F, 0.0F, 180.0F, -20.0F);

        rotatedCube(arm3, "cube_r29", 78, 120, -5.10382F, -0.34223F, 3.90794F, 3.0F, 1.0F, 3.0F,
                -3.60382F, -2.52391F, 10.94502F, 15.0F, 180.0F, 0.0F);
        rotatedCube(arm3, "cube_r30", 88, 22, -4.0F, -5.36958F, 5.51038F, 1.0F, 3.0F, 1.0F,
                -3.60382F, -3.02391F, 10.94502F, -20.0F, -150.0F, 0.0F);
        rotatedCube(arm3, "cube_r31", 92, 22, -3.242F, -6.60216F, 8.89687F, 1.0F, 3.0F, 1.0F,
                -3.60382F, -3.02391F, 10.94502F, -20.0F, 150.0F, 0.0F);
        rotatedCube(arm3, "cube_r32", 78, 124, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                5.0F, 4.3F, 3.0F, 0.0F, 0.0F, 20.0F);
        rotatedCube(arm3, "cube_r33", 102, 124, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                -5.0F, 4.3F, 4.0F, 0.0F, 180.0F, -20.0F);
        rotatedCube(limb3, "cube_r34", 126, 124, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                4.0F, 3.0F, 4.25F, 0.0F, 0.0F, 20.0F);
        rotatedCube(limb3, "cube_r35", 30, 127, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                -4.0F, 3.0F, 5.25F, 0.0F, 180.0F, -20.0F);
        rotatedCube(hand3, "cube_r36", 54, 127, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                3.0F, 2.9F, 4.25F, 0.0F, 0.0F, 20.0F);
        rotatedCube(hand3, "cube_r37", 0, 129, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                -3.0F, 2.9F, 5.25F, 0.0F, 180.0F, -20.0F);

        rotatedCube(arm4, "cube_r38", 96, 38, -2.0F, -1.0F, -1.0F, 3.0F, 1.0F, 3.0F,
                0.5F, -3.4F, 4.0F, -10.0F, 0.0F, 0.0F);
        rotatedCube(arm4, "cube_r39", 130, 15, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                5.0F, 4.3F, 3.0F, 0.0F, 0.0F, 20.0F);
        rotatedCube(arm4, "cube_r40", 130, 22, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                -5.0F, 4.3F, 4.0F, 0.0F, 180.0F, -20.0F);
        rotatedCube(limb4, "cube_r41", 78, 131, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                4.0F, 3.0F, 4.25F, 0.0F, 0.0F, 20.0F);
        rotatedCube(limb4, "cube_r42", 102, 131, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                -4.0F, 3.0F, 5.25F, 0.0F, 180.0F, -20.0F);
        rotatedCube(hand4, "cube_r43", 126, 131, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                3.0F, 2.9F, 4.25F, 0.0F, 0.0F, 20.0F);
        rotatedCube(hand4, "cube_r44", 24, 134, 0.0F, 0.0F, -3.0F, 5.0F, 0.0F, 7.0F,
                -3.0F, 2.9F, 5.25F, 0.0F, 180.0F, -20.0F);

        return LayerDefinition.create(meshDefinition, 256, 256);
    }

    private static void rotatedCube(
            PartDefinition parent,
            String name,
            int textureU,
            int textureV,
            float boxX,
            float boxY,
            float boxZ,
            float sizeX,
            float sizeY,
            float sizeZ,
            float pivotX,
            float pivotY,
            float pivotZ,
            float xDegrees,
            float yDegrees,
            float zDegrees
    ) {
        parent.addOrReplaceChild(name,
                CubeListBuilder.create().texOffs(textureU, textureV)
                        .addBox(boxX, boxY, boxZ, sizeX, sizeY, sizeZ, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(
                        pivotX, pivotY, pivotZ,
                        xDegrees * Mth.DEG_TO_RAD,
                        yDegrees * Mth.DEG_TO_RAD,
                        zDegrees * Mth.DEG_TO_RAD));
    }

    @Override
    public void setupAnim(StarCrawlerRenderState state) {
        this.resetAllPoses();

        float walkFade = state.attackState == AttackState.NORMAL
                ? 1.0F
                : state.attackState == AttackState.LEAP_WINDUP
                ? 1.0F - smoothStep(state.attackStateTicks / WALK_WINDUP_FADE_TICKS)
                : 0.0F;
        if (walkFade > 0.0F && state.walkAnimationSpeed > 0.001F) {
            this.walkAnimation.applyWalk(
                    state.walkAnimationPos,
                    state.walkAnimationSpeed * walkFade,
                    WALK_TIME_SCALE,
                    WALK_AMPLITUDE_SCALE
            );
        }

        if ((state.attackState == AttackState.LEAP_WINDUP || state.attackState == AttackState.LEAPING)
                && state.attackAnimationState.isStarted()) {
            this.attackAnimation.apply(state.attackAnimationState.getTimeInMillis(state.ageInTicks), 1.0F);
        }

        this.visualRoot.yRot += Mth.wrapDegrees(state.spinAngle) * Mth.DEG_TO_RAD;
        this.root().xRot += Mth.clamp(state.leanX, -0.18F, 0.18F);
        this.root().zRot += Mth.clamp(state.leanZ, -0.18F, 0.18F);

        switch (state.attackState) {
            case NORMAL -> this.applyNormalMotion(state);
            case LEAP_WINDUP -> this.applyWindup(state);
            case LEAPING -> this.applyTuck(LEAP_SHOULDER_TUCK, LEAP_LIMB_TUCK, LEAP_HAND_TUCK);
            case WHIPLASH -> this.applyWhiplash(state.attackStateTicks);
            case RECOVERY -> this.applyRecovery(state.attackStateTicks);
        }

        this.applySpinFlow(state);
        this.clampChainRotations();
    }

    private void applyNormalMotion(StarCrawlerRenderState state) {
        if (state.movementSpeed <= 0.005F) {
            float time = state.ageInTicks * 0.07F;
            for (int i = 0; i < this.shoulders.length; i++) {
                float phase = i * Mth.HALF_PI;
                this.shoulders[i].xRot += Mth.sin(time + phase) * 0.030F;
                this.shoulders[i].yRot += Mth.cos(time * 0.83F + phase) * 0.018F;
                this.limbs[i].xRot += Mth.sin(time + phase + 0.55F) * 0.038F;
                this.hands[i].xRot += Mth.sin(time + phase + 1.05F) * 0.046F;
            }
        }

        float spinTuck = smoothStep(
                (Math.abs(state.spinVelocity) - StarCrawlerEntity.LIMB_TUCK_SPIN_START_SPEED)
                        / (StarCrawlerEntity.ATTACK_SPIN_SPEED - StarCrawlerEntity.LIMB_TUCK_SPIN_START_SPEED)
        );
        this.applyTuck(0.12F * spinTuck, 0.18F * spinTuck, 0.23F * spinTuck);

        // The keyframed root/body channels now own the entire walking weight shift. Keeping a
        // second procedural bob here would drift out of phase and make the feet look slippery.
    }

    private void applySpinFlow(StarCrawlerRenderState state) {
        float absoluteSpin = Math.abs(state.spinVelocity);
        float flowBlend = smoothStep((absoluteSpin - 5.0F) / (StarCrawlerEntity.ATTACK_SPIN_SPEED - 5.0F));
        if (flowBlend <= 0.0F) {
            return;
        }

        float stateScale = switch (state.attackState) {
            case LEAPING -> Mth.lerp(smoothStep(state.attackStateTicks / 3.0F), 1.0F, 0.55F);
            case WHIPLASH -> 0.25F;
            case RECOVERY -> 0.35F;
            default -> 1.0F;
        };
        float signedSpin = Mth.clamp(
                state.spinVelocity / StarCrawlerEntity.ATTACK_SPIN_SPEED,
                -1.0F,
                1.0F
        );
        float amount = flowBlend * stateScale;
        float apparentSpin = Mth.wrapDegrees(state.spinAngle - state.bodyRot);
        float spinRadians = apparentSpin * Mth.DEG_TO_RAD;

        for (int i = 0; i < this.shoulders.length; i++) {
            float phase = spinRadians + i * Mth.HALF_PI;
            this.shoulders[i].xRot += Mth.sin(phase) * 0.045F * amount;
            this.limbs[i].xRot += Mth.sin(phase - 0.35F) * 0.075F * amount;
            this.hands[i].xRot += Mth.sin(phase - 0.70F) * 0.11F * amount;

            this.shoulders[i].yRot -= signedSpin * 0.025F * amount;
            this.limbs[i].yRot -= signedSpin * 0.055F * amount;
            this.hands[i].yRot -= signedSpin * 0.085F * amount;
        }
    }

    private void applyWindup(StarCrawlerRenderState state) {
        float progress = smoothStep(state.attackStateTicks / StarCrawlerEntity.LEAP_WINDUP_TICKS);
        float lowerBodyProgress = smoothStep(state.attackStateTicks / 6.0F);
        float normalSpinTuck = smoothStep(
                (Math.abs(state.spinVelocity) - StarCrawlerEntity.LIMB_TUCK_SPIN_START_SPEED)
                        / (StarCrawlerEntity.ATTACK_SPIN_SPEED - StarCrawlerEntity.LIMB_TUCK_SPIN_START_SPEED)
        );
        this.body.y += 2.6F * lowerBodyProgress;
        this.applyTuck(
                Mth.lerp(progress, 0.12F * normalSpinTuck, LEAP_SHOULDER_TUCK),
                Mth.lerp(progress, 0.18F * normalSpinTuck, LEAP_LIMB_TUCK),
                Mth.lerp(progress, 0.23F * normalSpinTuck, LEAP_HAND_TUCK)
        );
    }

    private void applyWhiplash(float stateTicks) {
        float clampedTicks = Mth.clamp(stateTicks, 0.0F, StarCrawlerEntity.WHIPLASH_TICKS);
        float shoulderProgress = smoothStep(clampedTicks / 2.5F);
        float limbProgress = smoothStep((clampedTicks - 0.75F) / 2.5F);
        float handProgress = smoothStep((clampedTicks - 1.5F) / 2.5F);
        for (int i = 0; i < this.shoulders.length; i++) {
            this.shoulders[i].xRot += Mth.lerp(
                    shoulderProgress, LEAP_SHOULDER_TUCK, -WHIPLASH_SHOULDER_OVERSHOOT);
            this.limbs[i].xRot += Mth.lerp(
                    limbProgress, LEAP_LIMB_TUCK, -WHIPLASH_LIMB_OVERSHOOT);
            this.hands[i].xRot += Mth.lerp(
                    handProgress, LEAP_HAND_TUCK, -WHIPLASH_HAND_OVERSHOOT);
        }
    }

    private void applyRecovery(float stateTicks) {
        float progress = smoothStep(stateTicks / StarCrawlerEntity.RECOVERY_TICKS);
        float decay = 1.0F - progress;
        float dampedSettle = decay * (1.0F + Mth.sin(progress * Mth.PI * 3.0F) * 0.10F * decay);
        this.applyOutward(
                WHIPLASH_SHOULDER_OVERSHOOT * dampedSettle,
                WHIPLASH_LIMB_OVERSHOOT * dampedSettle,
                WHIPLASH_HAND_OVERSHOOT * dampedSettle);
    }

    private void applyTuck(float shoulderAmount, float limbAmount, float handAmount) {
        for (int i = 0; i < this.shoulders.length; i++) {
            this.shoulders[i].xRot += shoulderAmount;
            this.limbs[i].xRot += limbAmount;
            this.hands[i].xRot += handAmount;
        }
    }

    private void applyOutward(float shoulderAmount, float limbAmount, float handAmount) {
        this.applyTuck(-shoulderAmount, -limbAmount, -handAmount);
    }

    private void clampChainRotations() {
        for (int i = 0; i < this.shoulders.length; i++) {
            this.shoulders[i].xRot = Mth.clamp(this.shoulders[i].xRot, -MAX_CHAIN_X_ROTATION, MAX_CHAIN_X_ROTATION);
            this.limbs[i].xRot = Mth.clamp(this.limbs[i].xRot, -MAX_CHAIN_X_ROTATION, MAX_CHAIN_X_ROTATION);
            this.hands[i].xRot = Mth.clamp(this.hands[i].xRot, -MAX_CHAIN_X_ROTATION, MAX_CHAIN_X_ROTATION);
        }
    }

    private void resetAllPoses() {
        this.root().getAllParts().forEach(ModelPart::resetPose);
    }

    private static float smoothStep(float value) {
        float clamped = Mth.clamp(value, 0.0F, 1.0F);
        return clamped * clamped * (3.0F - 2.0F * clamped);
    }
}