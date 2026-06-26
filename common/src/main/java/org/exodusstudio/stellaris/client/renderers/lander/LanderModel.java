package org.exodusstudio.stellaris.client.renderers.lander;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

public class LanderModel extends EntityModel<EntityRenderState> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("lander"), "main");

    private final ModelPart bone;
    private final ModelPart body;
    private final ModelPart shields;
    private final ModelPart shield3;
    private final ModelPart shield2;
    private final ModelPart shield5;
    private final ModelPart shield4;
    private final ModelPart pieds;
    private final ModelPart pied3;
    private final ModelPart pied2;
    private final ModelPart pied4;
    private final ModelPart pied;

    public LanderModel(ModelPart root) {
        super(root);
        this.bone = root.getChild("bone");
        this.body = this.bone.getChild("body");
        this.shields = this.bone.getChild("shields");
        this.shield3 = this.shields.getChild("shield3");
        this.shield2 = this.shields.getChild("shield2");
        this.shield5 = this.shields.getChild("shield5");
        this.shield4 = this.shields.getChild("shield4");
        this.pieds = this.bone.getChild("pieds");
        this.pied3 = this.pieds.getChild("pied3");
        this.pied2 = this.pieds.getChild("pied2");
        this.pied4 = this.pieds.getChild("pied4");
        this.pied = this.pieds.getChild("pied");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create(), PartPose.offset(0.0F, -16.5F, 0.0F));

        PartDefinition body = bone.addOrReplaceChild("body", CubeListBuilder.create().texOffs(88, 64).addBox(-10.0F, 19.5F, -10.0F, 20.0F, 6.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(0, 64).addBox(-11.0F, -11.5F, -11.0F, 22.0F, 27.0F, 22.0F, new CubeDeformation(0.0F))
                .texOffs(88, 90).addBox(-10.0F, -19.2F, -10.0F, 20.0F, 3.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-14.0F, 14.5F, -14.0F, 28.0F, 5.0F, 28.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 33).addBox(-14.0F, 6.393F, -14.0F, 28.0F, 3.0F, 28.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.893F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition cube_r2 = body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(112, 0).addBox(1.5F, 14.0F, -4.5F, 0.0F, 8.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.5F, -32.5F, -6.5F, 0.0F, 0.0F, -0.2182F));

        PartDefinition cube_r3 = body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(112, 50).addBox(-4.5F, 14.0F, -1.5F, 22.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.5F, -32.5F, -5.5F, -0.2182F, 0.0F, 0.0F));

        PartDefinition cube_r4 = body.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(112, 0).addBox(-1.5F, 14.0F, -17.5F, 0.0F, 8.0F, 22.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.5F, -32.5F, 6.5F, 0.0F, 0.0F, 0.2182F));

        PartDefinition cube_r5 = body.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(112, 50).addBox(-17.5F, 14.0F, 1.5F, 22.0F, 8.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.5F, -32.5F, 5.5F, 0.2182F, 0.0F, 0.0F));

        PartDefinition cube_r6 = body.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(74, 134).mirror().addBox(-1.5F, 14.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(6.5F, -32.5F, -6.5F, -0.2182F, 0.0F, -0.2182F));

        PartDefinition cube_r7 = body.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(74, 134).addBox(-1.5F, 14.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.5F, -32.5F, 6.5F, 0.2182F, 0.0F, -0.2182F));

        PartDefinition cube_r8 = body.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(74, 134).addBox(-1.5F, 14.0F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.5F, -32.5F, -6.5F, -0.2182F, 0.0F, 0.2182F));

        PartDefinition cube_r9 = body.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(74, 134).mirror().addBox(-1.5F, 12.5F, -1.5F, 3.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-6.8136F, -31.0855F, 6.8882F, 0.2182F, 0.0F, 0.2182F));

        PartDefinition cube_r10 = body.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(58, 134).addBox(-2.0F, -5.0F, -2.0F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(88, 126).addBox(-3.0F, -10.0F, -3.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 34.5F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition shields = bone.addOrReplaceChild("shields", CubeListBuilder.create(), PartPose.offset(13.0929F, 17.0F, 13.0466F));

        PartDefinition shield3 = shields.addOrReplaceChild("shield3", CubeListBuilder.create().texOffs(28, 113).addBox(3.6071F, -33.5F, -7.8466F, 2.0F, 34.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r11 = shield3.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(58, 124).addBox(-6.0F, -4.5F, -1.5F, 12.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6464F, 1.0F, -0.6464F, 0.829F, -0.7854F, 3.1416F));

        PartDefinition cube_r12 = shield3.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(120, 113).addBox(-6.0F, -4.0F, -1.5F, 12.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.4284F, -35.0134F, -1.4284F, -2.3126F, 0.7854F, 0.0F));

        PartDefinition cube_r13 = shield3.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(28, 113).mirror().addBox(-1.0F, -17.0F, -2.5F, 2.0F, 34.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.2929F, -16.5F, 4.6534F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r14 = shield3.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(0, 113).addBox(-6.0F, -0.5F, 0.0F, 12.0F, 34.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, -3.1416F));

        PartDefinition shield2 = shields.addOrReplaceChild("shield2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -26.0931F));

        PartDefinition cube_r15 = shield2.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(58, 124).addBox(-6.0F, -4.5F, -1.5F, 12.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6464F, 1.0F, 0.6464F, -0.829F, 0.7854F, 3.1416F));

        PartDefinition cube_r16 = shield2.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(120, 113).addBox(-6.0F, -2.5F, -1.5F, 12.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6464F, -34.0F, 0.6464F, 0.829F, 0.7854F, -3.1416F));

        PartDefinition cube_r17 = shield2.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(28, 113).mirror().addBox(-1.0F, -17.0F, -2.5F, 2.0F, 34.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.6071F, -16.5F, 5.3466F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r18 = shield2.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(28, 113).addBox(-0.5F, -17.0F, -2.5F, 2.0F, 34.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.2929F, -16.5F, -4.1534F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r19 = shield2.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(0, 113).addBox(-6.0F, -17.0F, -1.0F, 12.0F, 34.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7071F, -16.5F, -0.7071F, -3.1416F, -0.7854F, 0.0F));

        PartDefinition shield5 = shields.addOrReplaceChild("shield5", CubeListBuilder.create().texOffs(28, 113).mirror().addBox(-5.6071F, -33.5F, -7.8466F, 2.0F, 34.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-26.1858F, 0.0F, 0.0F));

        PartDefinition cube_r20 = shield5.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(58, 124).addBox(-6.0F, -4.5F, -1.5F, 12.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6464F, 1.0F, -0.6464F, 0.829F, 0.7854F, -3.1416F));

        PartDefinition cube_r21 = shield5.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(120, 113).addBox(-6.0F, -2.5F, -1.5F, 12.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6464F, -34.0F, -0.6464F, -2.3126F, -0.7854F, 0.0F));

        PartDefinition cube_r22 = shield5.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(28, 113).addBox(-1.0F, -17.0F, -2.5F, 2.0F, 34.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.2929F, -16.5F, 4.6534F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r23 = shield5.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(0, 113).addBox(-6.0F, -0.5F, 0.0F, 12.0F, 34.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 3.1416F));

        PartDefinition shield4 = shields.addOrReplaceChild("shield4", CubeListBuilder.create(), PartPose.offset(-26.1858F, 0.0F, -26.0931F));

        PartDefinition cube_r24 = shield4.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(58, 124).addBox(-6.0F, -4.5F, -1.5F, 12.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6464F, 1.0F, 0.6464F, -0.829F, -0.7854F, -3.1416F));

        PartDefinition cube_r25 = shield4.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(120, 113).addBox(-6.0F, -2.5F, -1.5F, 12.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.6464F, -34.0F, 0.6464F, 0.829F, -0.7854F, 3.1416F));

        PartDefinition cube_r26 = shield4.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(28, 113).addBox(-1.0F, -17.0F, -2.5F, 2.0F, 34.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.6071F, -16.5F, 5.3466F, 0.0F, 3.1416F, 0.0F));

        PartDefinition cube_r27 = shield4.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(28, 113).mirror().addBox(-1.0F, -17.0F, -2.5F, 2.0F, 34.0F, 5.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(5.2929F, -16.5F, -4.6534F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r28 = shield4.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(0, 113).addBox(-6.0F, -17.0F, -1.0F, 12.0F, 34.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7071F, -16.5F, -0.7071F, 0.0F, 2.3562F, 3.1416F));

        PartDefinition pieds = bone.addOrReplaceChild("pieds", CubeListBuilder.create(), PartPose.offset(23.0929F, 23.5F, 23.0466F));

        PartDefinition pied3 = pieds.addOrReplaceChild("pied3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r29 = pied3.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(90, 113).addBox(-3.0F, 2.5F, -1.0F, 6.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-17.0F, 3.5F, -17.0F, 0.3054F, -0.7854F, -3.1416F));

        PartDefinition cube_r30 = pied3.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(120, 124).addBox(-3.0F, -7.0F, -2.0F, 6.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.6179F, -2.6992F, -8.6179F, 2.8362F, 0.7854F, 0.0F));

        PartDefinition cube_r31 = pied3.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(42, 113).addBox(-2.0F, -13.5F, -2.0F, 4.0F, 27.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 3.5F, -5.0F, 0.48F, 0.7854F, 0.0F));

        PartDefinition cube_r32 = pied3.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(58, 113).addBox(-4.0F, 14.0F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, -0.7854F, -3.1416F));

        PartDefinition pied2 = pieds.addOrReplaceChild("pied2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -46.0931F));

        PartDefinition cube_r33 = pied2.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(90, 113).addBox(-3.0F, 2.5F, -8.0F, 6.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-17.0F, 3.5F, 17.0F, -0.3054F, 0.7854F, -3.1416F));

        PartDefinition cube_r34 = pied2.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(120, 124).addBox(-3.0F, -0.5F, -2.0F, 6.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-10.0F, 3.5F, 10.0F, -0.3054F, 0.7854F, -3.1416F));

        PartDefinition cube_r35 = pied2.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(42, 113).addBox(-2.0F, -13.5F, -2.0F, 4.0F, 27.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 3.5F, 5.0F, -2.6616F, 0.7854F, -3.1416F));

        PartDefinition cube_r36 = pied2.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(58, 113).addBox(-4.0F, 14.0F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.1416F, 0.7854F, -3.1416F));

        PartDefinition pied4 = pieds.addOrReplaceChild("pied4", CubeListBuilder.create(), PartPose.offset(-46.1858F, 0.0F, 0.0F));

        PartDefinition cube_r37 = pied4.addOrReplaceChild("cube_r37", CubeListBuilder.create().texOffs(90, 113).addBox(-3.0F, 2.5F, -1.0F, 6.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.0F, 3.5F, -17.0F, 0.3054F, 0.7854F, 3.1416F));

        PartDefinition cube_r38 = pied4.addOrReplaceChild("cube_r38", CubeListBuilder.create().texOffs(120, 124).addBox(-3.0F, -0.5F, -2.0F, 6.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, 3.5F, -10.0F, 2.8362F, -0.7854F, 0.0F));

        PartDefinition cube_r39 = pied4.addOrReplaceChild("cube_r39", CubeListBuilder.create().texOffs(42, 113).addBox(-2.0F, -13.5F, -2.0F, 4.0F, 27.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 3.5F, -5.0F, 0.48F, -0.7854F, 0.0F));

        PartDefinition cube_r40 = pied4.addOrReplaceChild("cube_r40", CubeListBuilder.create().texOffs(58, 113).addBox(-4.0F, 14.0F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.7854F, 3.1416F));

        PartDefinition pied = pieds.addOrReplaceChild("pied", CubeListBuilder.create(), PartPose.offset(-46.1858F, 0.0F, -46.0931F));

        PartDefinition cube_r41 = pied.addOrReplaceChild("cube_r41", CubeListBuilder.create().texOffs(112, 30).mirror().addBox(-2.0F, -1.0F, -11.5F, 4.0F, 2.0F, 18.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(33.0749F, 4.0F, 32.9822F, -0.2618F, -0.7854F, -3.1416F));

        PartDefinition cube_r42 = pied.addOrReplaceChild("cube_r42", CubeListBuilder.create().texOffs(112, 30).addBox(-2.0F, -1.0F, -11.5F, 4.0F, 2.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(13.1109F, 4.0F, 32.9822F, -0.2618F, 0.7854F, 3.1416F));

        PartDefinition cube_r43 = pied.addOrReplaceChild("cube_r43", CubeListBuilder.create().texOffs(112, 30).addBox(-2.0F, -1.0F, -6.5F, 4.0F, 2.0F, 18.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(13.1109F, 4.0F, 13.1109F, 0.2618F, -0.7854F, 3.1416F));

        PartDefinition cube_r44 = pied.addOrReplaceChild("cube_r44", CubeListBuilder.create().texOffs(112, 30).mirror().addBox(-2.0F, -1.0F, -6.5F, 4.0F, 2.0F, 18.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(33.0749F, 4.0F, 13.1109F, 0.2618F, 0.7854F, -3.1416F));

        PartDefinition cube_r45 = pied.addOrReplaceChild("cube_r45", CubeListBuilder.create().texOffs(90, 113).addBox(-3.0F, 2.5F, -8.0F, 6.0F, 4.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(17.0F, 3.5F, 17.0F, -0.3054F, -0.7854F, 3.1416F));

        PartDefinition cube_r46 = pied.addOrReplaceChild("cube_r46", CubeListBuilder.create().texOffs(120, 124).addBox(-3.0F, -0.5F, -2.0F, 6.0F, 14.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(10.0F, 3.5F, 10.0F, -0.3054F, -0.7854F, 3.1416F));

        PartDefinition cube_r47 = pied.addOrReplaceChild("cube_r47", CubeListBuilder.create().texOffs(42, 113).addBox(-2.0F, -13.5F, -2.0F, 4.0F, 27.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 3.5F, 5.0F, -2.6616F, -0.7854F, 3.1416F));

        PartDefinition cube_r48 = pied.addOrReplaceChild("cube_r48", CubeListBuilder.create().texOffs(58, 113).addBox(-4.0F, 14.0F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.1416F, -0.7854F, 3.1416F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }


}
