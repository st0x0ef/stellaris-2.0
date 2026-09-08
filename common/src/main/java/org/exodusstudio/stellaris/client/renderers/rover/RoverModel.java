package org.exodusstudio.stellaris.client.renderers.rover;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import org.exodusstudio.stellaris.common.registries.ModulesRegistry;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;


public class RoverModel extends EntityModel<RoverRenderState> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("rover"), "main");
    private final ModelPart Frame;
    private final ModelPart bone;
    private final ModelPart gbs;
    private final ModelPart wheels;
    private final ModelPart wheel_3;
    private final ModelPart wheel_2;
    private final ModelPart wheel_4;
    private final ModelPart wheel_1;
    private final ModelPart antenna_and_pillar;
    private final ModelPart antenna;
    private final ModelPart pillar;
    private final ModelPart cargo_module;
    private final ModelPart tank_module;
    private final ModelPart speed_module;

    public RoverModel(ModelPart root) {
        super(root);
        this.Frame = root.getChild("Frame");
        this.bone = this.Frame.getChild("bone");
        this.gbs = this.Frame.getChild("gbs");
        this.wheels = this.Frame.getChild("wheels");
        this.wheel_3 = this.wheels.getChild("wheel_3");
        this.wheel_2 = this.wheels.getChild("wheel_2");
        this.wheel_4 = this.wheels.getChild("wheel_4");
        this.wheel_1 = this.wheels.getChild("wheel_1");
        this.antenna_and_pillar = this.Frame.getChild("antenna_and_pillar");
        this.antenna = this.antenna_and_pillar.getChild("antenna");
        this.pillar = this.antenna_and_pillar.getChild("pillar");
        this.cargo_module = this.Frame.getChild("cargo_module");
        this.tank_module = this.Frame.getChild("tank_module");
        this.speed_module = this.Frame.getChild("speed_module");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Frame = partdefinition.addOrReplaceChild("Frame", CubeListBuilder.create().texOffs(0, 158).addBox(-14.0F, -19.9F, 8.0F, 9.0F, 11.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 21.0F, 2.0F));

        PartDefinition main_body = Frame.addOrReplaceChild("main_body", CubeListBuilder.create().texOffs(0, 99).addBox(9.0F, -12.0F, -21.0F, 4.0F, 2.0F, 42.0F, new CubeDeformation(0.0F))
                .texOffs(92, 99).addBox(-5.0F, -12.0F, -21.0F, 4.0F, 2.0F, 42.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-11.0F, -13.0F, -24.0F, 30.0F, 2.0F, 48.0F, new CubeDeformation(0.0F))
                .texOffs(144, 143).addBox(18.0F, -12.0F, -13.0F, 2.0F, 2.0F, 26.0F, new CubeDeformation(0.0F))
                .texOffs(98, 50).addBox(-12.0F, -14.0F, -23.0F, 2.0F, 2.0F, 47.0F, new CubeDeformation(0.0F))
                .texOffs(0, 50).addBox(18.0F, -14.0F, -23.0F, 2.0F, 2.0F, 47.0F, new CubeDeformation(0.0F))
                .texOffs(156, 32).addBox(-12.0F, -14.0F, 24.0F, 32.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(156, 26).addBox(-12.0F, -14.0F, -26.0F, 32.0F, 3.0F, 3.0F, new CubeDeformation(0.01F))
                .texOffs(122, 170).addBox(14.5564F, -7.4711F, 18.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(122, 170).mirror().addBox(-11.5564F, -7.4711F, 18.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(122, 170).mirror().addBox(-11.5564F, -7.4711F, -20.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(122, 170).addBox(14.5564F, -7.4711F, -20.0F, 5.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.0F, 4.1F, -2.0F));

        PartDefinition cube_r1 = main_body.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(122, 170).addBox(-2.9497F, -2.1508F, -1.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(122, 170).addBox(-2.9497F, -2.1508F, 37.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(11.0F, -8.9F, -19.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition cube_r2 = main_body.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(122, 170).mirror().addBox(-5.0503F, -2.1508F, -1.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(122, 170).mirror().addBox(-5.0503F, -2.1508F, 37.0F, 8.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-3.0F, -8.9F, -19.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition cube_r3 = main_body.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(144, 143).addBox(-1.0F, -1.0F, -13.0F, 2.0F, 2.0F, 26.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-11.0F, -11.0F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition bumper = Frame.addOrReplaceChild("bumper", CubeListBuilder.create().texOffs(162, 174).addBox(-11.0F, -1.147F, -5.7504F, 22.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.753F, -29.2496F));

        PartDefinition cube_r4 = bumper.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(196, 63).addBox(-0.55F, 0.0F, -0.85F, 8.0F, 3.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(11.0F, -1.147F, -4.7504F, 0.0F, -0.5672F, 0.0F));

        PartDefinition cube_r5 = bumper.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(194, 187).addBox(-7.45F, 0.0F, -0.85F, 8.0F, 3.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-11.0F, -1.147F, -4.7504F, 0.0F, 0.5672F, 0.0F));

        PartDefinition cube_r6 = bumper.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(121, 190).addBox(-11.0F, -1.0F, -4.0F, 4.0F, 2.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(-1, 190).addBox(7.0F, -1.0F, -4.0F, 4.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition bone = Frame.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(156, 37).addBox(-11.0F, -4.1F, -2.0F, 22.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -12.9F, -12.0F));

        PartDefinition steering_rectangle = bone.addOrReplaceChild("steering_rectangle", CubeListBuilder.create().texOffs(70, 190).addBox(6.0F, -23.0F, -16.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(196, 84).addBox(3.0F, -24.0F, -15.0F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 18.0F, 20.0F));

        PartDefinition cube_r7 = steering_rectangle.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(196, 192).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.0F, -19.0F, -18.0F, 0.48F, 0.0F, 0.0F));

        PartDefinition seats = Frame.addOrReplaceChild("seats", CubeListBuilder.create().texOffs(0, 143).addBox(-12.0F, -18.0F, -13.0F, 24.0F, 3.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.1F, 5.0F));

        PartDefinition cube_r8 = seats.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(72, 143).addBox(-12.0F, -1.0F, 0.0F, 24.0F, 3.0F, 12.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, -17.0F, -3.0F, 1.2217F, 0.0F, 0.0F));

        PartDefinition gbs = Frame.addOrReplaceChild("gbs", CubeListBuilder.create(), PartPose.offset(-19.0F, -10.9F, -4.0F));

        PartDefinition gb2 = gbs.addOrReplaceChild("gb2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r9 = gb2.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 182).mirror().addBox(-3.5F, -1.0F, -7.0F, 7.0F, 2.0F, 7.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offsetAndRotation(2.6446F, 5.0325F, -7.7F, -0.7854F, 0.0F, -0.3927F));

        PartDefinition cube_r10 = gb2.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(162, 179).addBox(-2.0F, -1.0F, -6.0F, 4.0F, 2.0F, 12.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(4.7696F, 0.5585F, -18.0F, 0.0F, 0.0F, 0.6545F));

        PartDefinition cube_r11 = gb2.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(76, 175).mirror().addBox(-3.0F, 0.0F, -24.0F, 7.0F, 2.0F, 12.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition gb1 = gbs.addOrReplaceChild("gb1", CubeListBuilder.create(), PartPose.offset(38.0F, 0.0F, 0.0F));

        PartDefinition cube_r12 = gb1.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(0, 182).addBox(-3.5F, -1.0F, -7.0F, 7.0F, 2.0F, 7.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(-2.6446F, 5.0325F, -7.7F, -0.7854F, 0.0F, 0.3927F));

        PartDefinition cube_r13 = gb1.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(162, 179).mirror().addBox(-2.0F, -1.0F, -6.0F, 4.0F, 2.0F, 12.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offsetAndRotation(-4.7696F, 0.5585F, -18.0F, 0.0F, 0.0F, -0.6545F));

        PartDefinition cube_r14 = gb1.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(76, 175).addBox(-4.0F, 0.0F, -24.0F, 7.0F, 2.0F, 12.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition gb4 = gbs.addOrReplaceChild("gb4", CubeListBuilder.create(), PartPose.offset(38.0F, 0.0F, 4.0F));

        PartDefinition cube_r15 = gb4.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(162, 179).mirror().addBox(0.0F, 0.0F, 12.0F, 4.0F, 2.0F, 12.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offsetAndRotation(-7.4F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition cube_r16 = gb4.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(0, 182).addBox(-3.5F, -1.0F, 0.0F, 7.0F, 2.0F, 7.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(-2.6446F, 5.0325F, 7.8F, 0.7854F, 0.0F, 0.3927F));

        PartDefinition cube_r17 = gb4.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(76, 175).addBox(-4.0F, 0.0F, 12.0F, 7.0F, 2.0F, 12.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition gb3 = gbs.addOrReplaceChild("gb3", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 4.0F));

        PartDefinition cube_r18 = gb3.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(162, 179).addBox(-4.0F, 0.0F, 12.0F, 4.0F, 2.0F, 12.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(7.4F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition cube_r19 = gb3.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(0, 182).mirror().addBox(-3.5F, -1.0F, 0.0F, 7.0F, 2.0F, 7.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offsetAndRotation(2.6446F, 5.0325F, 7.8F, 0.7854F, 0.0F, -0.3927F));

        PartDefinition cube_r20 = gb3.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(76, 175).mirror().addBox(-3.0F, 0.0F, 12.0F, 7.0F, 2.0F, 12.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition lights = Frame.addOrReplaceChild("lights", CubeListBuilder.create().texOffs(76, 189).addBox(-12.0F, -28.0F, 24.0F, 8.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(28, 190).addBox(19.0F, -28.0F, 24.0F, 8.0F, 7.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.0F, 15.1F, -1.0F));

        PartDefinition wheels = Frame.addOrReplaceChild("wheels", CubeListBuilder.create(), PartPose.offset(19.0F, -2.0F, -21.0F));

        PartDefinition wheel_3 = wheels.addOrReplaceChild("wheel_3", CubeListBuilder.create().texOffs(184, 109).mirror().addBox(-4.4091F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false)
                .texOffs(184, 135).mirror().addBox(-4.4091F, -2.0F, -5.0F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.12F)).mirror(false), PartPose.offset(-34.5909F, -0.3F, 38.0F));

        PartDefinition cube_r21 = wheel_3.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(184, 109).mirror().addBox(-3.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false), PartPose.offsetAndRotation(-0.9091F, 0.0F, 0.0F, -3.1416F, 0.0F, 0.0F));

        PartDefinition cube_r22 = wheel_3.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(184, 115).mirror().addBox(-0.5F, -2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(184, 109).mirror().addBox(0.0F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(-4.4091F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r23 = wheel_3.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(184, 109).mirror().addBox(-3.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(-0.9091F, 0.0F, 0.0F, -2.3562F, 0.0F, 0.0F));

        PartDefinition cube_r24 = wheel_3.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(184, 109).mirror().addBox(-3.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(-0.9091F, 0.0F, 0.0F, 2.3562F, 0.0F, 0.0F));

        PartDefinition cube_r25 = wheel_3.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(184, 135).mirror().addBox(-4.5F, -2.0F, -1.0F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.12F)).mirror(false), PartPose.offsetAndRotation(0.0909F, 0.0F, 4.0F, -3.1416F, 0.0F, 0.0F));

        PartDefinition cube_r26 = wheel_3.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(184, 109).mirror().addBox(-3.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(-0.9091F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition oct3 = wheel_3.addOrReplaceChild("oct3", CubeListBuilder.create().texOffs(184, 218).mirror().addBox(-0.5F, -5.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false)
                .texOffs(184, 242).mirror().addBox(-0.5F, 1.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false)
                .texOffs(184, 226).mirror().addBox(-0.5F, -2.0F, -5.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false)
                .texOffs(184, 234).addBox(-0.5F, -2.0F, 1.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)), PartPose.offset(-1.9091F, 0.0F, 0.0F));

        PartDefinition cube_r27 = oct3.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(171, 229).mirror().addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false)
                .texOffs(171, 223).mirror().addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r28 = oct3.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(171, 229).mirror().addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false)
                .texOffs(171, 223).mirror().addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition oct4 = wheel_3.addOrReplaceChild("oct4", CubeListBuilder.create().texOffs(184, 218).mirror().addBox(-0.5F, -5.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false)
                .texOffs(184, 242).mirror().addBox(-0.5F, 1.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false)
                .texOffs(184, 226).mirror().addBox(-0.5F, -2.0F, -5.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false)
                .texOffs(184, 234).addBox(-0.5F, -2.0F, 1.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)), PartPose.offset(2.0909F, 0.0F, 0.0F));

        PartDefinition cube_r29 = oct4.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(171, 229).mirror().addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false)
                .texOffs(171, 223).mirror().addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r30 = oct4.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(171, 229).mirror().addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false)
                .texOffs(171, 223).mirror().addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition wheel_2 = wheels.addOrReplaceChild("wheel_2", CubeListBuilder.create().texOffs(184, 109).mirror().addBox(-4.4091F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false)
                .texOffs(184, 135).mirror().addBox(-4.4091F, -2.0F, -5.0F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.12F)).mirror(false), PartPose.offset(-34.5909F, -0.3F, 0.0F));

        PartDefinition cube_r31 = wheel_2.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(184, 109).mirror().addBox(-3.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false), PartPose.offsetAndRotation(-0.9091F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition cube_r32 = wheel_2.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(184, 115).mirror().addBox(-0.5F, -2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(184, 109).mirror().addBox(0.0F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(-4.4091F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r33 = wheel_2.addOrReplaceChild("cube_r33", CubeListBuilder.create().texOffs(184, 109).mirror().addBox(-3.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(-0.9091F, 0.0F, 0.0F, 2.3562F, 0.0F, 0.0F));

        PartDefinition cube_r34 = wheel_2.addOrReplaceChild("cube_r34", CubeListBuilder.create().texOffs(184, 109).mirror().addBox(-3.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(-0.9091F, 0.0F, 0.0F, -2.3562F, 0.0F, 0.0F));

        PartDefinition cube_r35 = wheel_2.addOrReplaceChild("cube_r35", CubeListBuilder.create().texOffs(184, 135).mirror().addBox(-4.5F, -2.0F, -1.0F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.12F)).mirror(false), PartPose.offsetAndRotation(0.0909F, 0.0F, 4.0F, -3.1416F, 0.0F, 0.0F));

        PartDefinition cube_r36 = wheel_2.addOrReplaceChild("cube_r36", CubeListBuilder.create().texOffs(184, 109).mirror().addBox(-3.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(-0.9091F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition oct9 = wheel_2.addOrReplaceChild("oct9", CubeListBuilder.create().texOffs(184, 218).mirror().addBox(-0.5F, -5.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false)
                .texOffs(184, 242).mirror().addBox(-0.5F, 1.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false)
                .texOffs(184, 226).mirror().addBox(-0.5F, -2.0F, 1.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false)
                .texOffs(184, 234).addBox(-0.5F, -2.0F, -5.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)), PartPose.offset(-1.9091F, 0.0F, 0.0F));

        PartDefinition cube_r37 = oct9.addOrReplaceChild("cube_r37", CubeListBuilder.create().texOffs(171, 229).mirror().addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false)
                .texOffs(171, 223).mirror().addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r38 = oct9.addOrReplaceChild("cube_r38", CubeListBuilder.create().texOffs(171, 229).mirror().addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false)
                .texOffs(171, 223).mirror().addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition oct10 = wheel_2.addOrReplaceChild("oct10", CubeListBuilder.create().texOffs(184, 218).mirror().addBox(-0.5F, -5.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false)
                .texOffs(184, 242).mirror().addBox(-0.5F, 1.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false)
                .texOffs(184, 226).mirror().addBox(-0.5F, -2.0F, 1.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false)
                .texOffs(184, 234).addBox(-0.5F, -2.0F, -5.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)), PartPose.offset(2.0909F, 0.0F, 0.0F));

        PartDefinition cube_r39 = oct10.addOrReplaceChild("cube_r39", CubeListBuilder.create().texOffs(171, 229).mirror().addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false)
                .texOffs(171, 223).mirror().addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r40 = oct10.addOrReplaceChild("cube_r40", CubeListBuilder.create().texOffs(171, 229).mirror().addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false)
                .texOffs(171, 223).mirror().addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition wheel_4 = wheels.addOrReplaceChild("wheel_4", CubeListBuilder.create().texOffs(184, 109).addBox(-4.5909F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.12F))
                .texOffs(184, 135).addBox(-4.5909F, -2.0F, -5.0F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.12F)), PartPose.offset(-3.4091F, -0.3F, 38.0F));

        PartDefinition cube_r41 = wheel_4.addOrReplaceChild("cube_r41", CubeListBuilder.create().texOffs(184, 109).addBox(-5.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.12F)), PartPose.offsetAndRotation(0.9091F, 0.0F, 0.0F, -3.1416F, 0.0F, 0.0F));

        PartDefinition cube_r42 = wheel_4.addOrReplaceChild("cube_r42", CubeListBuilder.create().texOffs(184, 115).addBox(-7.5F, -2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(184, 109).addBox(-9.0F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(4.4091F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r43 = wheel_4.addOrReplaceChild("cube_r43", CubeListBuilder.create().texOffs(184, 109).addBox(-5.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(0.9091F, 0.0F, 0.0F, -2.3562F, 0.0F, 0.0F));

        PartDefinition cube_r44 = wheel_4.addOrReplaceChild("cube_r44", CubeListBuilder.create().texOffs(184, 109).addBox(-5.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(0.9091F, 0.0F, 0.0F, 2.3562F, 0.0F, 0.0F));

        PartDefinition cube_r45 = wheel_4.addOrReplaceChild("cube_r45", CubeListBuilder.create().texOffs(184, 135).addBox(-4.5F, -2.0F, -1.0F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.12F)), PartPose.offsetAndRotation(-0.0909F, 0.0F, 4.0F, -3.1416F, 0.0F, 0.0F));

        PartDefinition cube_r46 = wheel_4.addOrReplaceChild("cube_r46", CubeListBuilder.create().texOffs(184, 109).addBox(-5.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(0.9091F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition oct5 = wheel_4.addOrReplaceChild("oct5", CubeListBuilder.create().texOffs(184, 218).addBox(-0.5F, -5.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F))
                .texOffs(184, 242).addBox(-0.5F, 1.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F))
                .texOffs(184, 226).addBox(-0.5F, -2.0F, -5.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F))
                .texOffs(184, 234).mirror().addBox(-0.5F, -2.0F, 1.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false), PartPose.offset(1.9091F, 0.0F, 0.0F));

        PartDefinition cube_r47 = oct5.addOrReplaceChild("cube_r47", CubeListBuilder.create().texOffs(171, 229).addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F))
                .texOffs(171, 223).addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r48 = oct5.addOrReplaceChild("cube_r48", CubeListBuilder.create().texOffs(171, 229).addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F))
                .texOffs(171, 223).addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition oct6 = wheel_4.addOrReplaceChild("oct6", CubeListBuilder.create().texOffs(184, 218).addBox(-0.5F, -5.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F))
                .texOffs(184, 242).addBox(-0.5F, 1.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F))
                .texOffs(184, 226).addBox(-0.5F, -2.0F, -5.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F))
                .texOffs(184, 234).mirror().addBox(-0.5F, -2.0F, 1.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false), PartPose.offset(-2.0909F, 0.0F, 0.0F));

        PartDefinition cube_r49 = oct6.addOrReplaceChild("cube_r49", CubeListBuilder.create().texOffs(171, 229).addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F))
                .texOffs(171, 223).addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r50 = oct6.addOrReplaceChild("cube_r50", CubeListBuilder.create().texOffs(171, 229).addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F))
                .texOffs(171, 223).addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition wheel_1 = wheels.addOrReplaceChild("wheel_1", CubeListBuilder.create().texOffs(184, 109).addBox(-4.5909F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.12F))
                .texOffs(184, 135).addBox(-4.5909F, -2.0F, -5.0F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.12F)), PartPose.offset(-3.4091F, -0.3F, 0.0F));

        PartDefinition cube_r51 = wheel_1.addOrReplaceChild("cube_r51", CubeListBuilder.create().texOffs(184, 109).addBox(-5.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.12F)), PartPose.offsetAndRotation(0.9091F, 0.0F, 0.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition cube_r52 = wheel_1.addOrReplaceChild("cube_r52", CubeListBuilder.create().texOffs(184, 115).addBox(-7.5F, -2.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(184, 109).addBox(-9.0F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(4.4091F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r53 = wheel_1.addOrReplaceChild("cube_r53", CubeListBuilder.create().texOffs(184, 109).addBox(-5.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(0.9091F, 0.0F, 0.0F, 2.3562F, 0.0F, 0.0F));

        PartDefinition cube_r54 = wheel_1.addOrReplaceChild("cube_r54", CubeListBuilder.create().texOffs(184, 109).addBox(-5.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(0.9091F, 0.0F, 0.0F, -2.3562F, 0.0F, 0.0F));

        PartDefinition cube_r55 = wheel_1.addOrReplaceChild("cube_r55", CubeListBuilder.create().texOffs(184, 135).addBox(-4.5F, -2.0F, -1.0F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.12F)), PartPose.offsetAndRotation(-0.0909F, 0.0F, 4.0F, 3.1416F, 0.0F, 0.0F));

        PartDefinition cube_r56 = wheel_1.addOrReplaceChild("cube_r56", CubeListBuilder.create().texOffs(184, 109).addBox(-5.5F, -5.0F, -2.0F, 9.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(0.9091F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition oct7 = wheel_1.addOrReplaceChild("oct7", CubeListBuilder.create().texOffs(184, 218).addBox(-0.5F, -5.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F))
                .texOffs(184, 242).addBox(-0.5F, 1.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F))
                .texOffs(184, 226).addBox(-0.5F, -2.0F, 1.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F))
                .texOffs(184, 234).mirror().addBox(-0.5F, -2.0F, -5.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false), PartPose.offset(1.9091F, 0.0F, 0.0F));

        PartDefinition cube_r57 = oct7.addOrReplaceChild("cube_r57", CubeListBuilder.create().texOffs(171, 229).addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F))
                .texOffs(171, 223).addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r58 = oct7.addOrReplaceChild("cube_r58", CubeListBuilder.create().texOffs(171, 229).addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F))
                .texOffs(171, 223).addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition oct8 = wheel_1.addOrReplaceChild("oct8", CubeListBuilder.create().texOffs(184, 218).addBox(-0.5F, -5.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F))
                .texOffs(184, 242).addBox(-0.5F, 1.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F))
                .texOffs(184, 226).addBox(-0.5F, -2.0F, 1.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F))
                .texOffs(184, 234).mirror().addBox(-0.5F, -2.0F, -5.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.12F)).mirror(false), PartPose.offset(-2.0909F, 0.0F, 0.0F));

        PartDefinition cube_r59 = oct8.addOrReplaceChild("cube_r59", CubeListBuilder.create().texOffs(171, 229).addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F))
                .texOffs(171, 223).addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition cube_r60 = oct8.addOrReplaceChild("cube_r60", CubeListBuilder.create().texOffs(171, 229).addBox(-0.5F, 3.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F))
                .texOffs(171, 223).addBox(-0.5F, -5.0F, -2.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.121F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.0F, 0.0F));

        PartDefinition antenna_and_pillar = Frame.addOrReplaceChild("antenna_and_pillar", CubeListBuilder.create(), PartPose.offset(15.5946F, -35.4249F, 22.0F));

        PartDefinition antenna = antenna_and_pillar.addOrReplaceChild("antenna", CubeListBuilder.create(), PartPose.offset(-4.7753F, 5.2033F, -0.2006F));

        PartDefinition cube_r61 = antenna.addOrReplaceChild("cube_r61", CubeListBuilder.create().texOffs(74, 198).addBox(-0.0636F, -10.0422F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(20, 191).addBox(0.4364F, -7.0422F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(76, 158).addBox(-2.21F, -0.6887F, -7.5F, 8.0F, 2.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(60, 196).addBox(-0.21F, -0.6887F, -2.0F, 3.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.7709F, -5.2033F, 0.0F, 3.1416F, 0.0F, -1.5708F));

        PartDefinition cube_r62 = antenna.addOrReplaceChild("cube_r62", CubeListBuilder.create().texOffs(122, 174).addBox(4.8612F, 1.3894F, -7.5F, 5.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.7709F, -5.2033F, 0.0F, 3.1416F, 0.0F, -1.1781F));

        PartDefinition cube_r63 = antenna.addOrReplaceChild("cube_r63", CubeListBuilder.create().texOffs(122, 174).mirror().addBox(-6.4812F, 0.029F, -7.5F, 5.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(4.7709F, -5.2033F, 0.0F, -3.1416F, 0.0F, -1.9635F));

        PartDefinition cube_r64 = antenna.addOrReplaceChild("cube_r64", CubeListBuilder.create().texOffs(184, 102).addBox(-5.0F, -1.5F, -2.0F, 10.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 3.1416F, 0.0F, -1.5708F));

        PartDefinition pillar = antenna_and_pillar.addOrReplaceChild("pillar", CubeListBuilder.create().texOffs(114, 175).addBox(-5.7946F, -17.4751F, -1.1F, 2.0F, 29.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 0.0F));

        PartDefinition cube_r65 = pillar.addOrReplaceChild("cube_r65", CubeListBuilder.create().texOffs(189, 102).addBox(-5.2033F, 3.2709F, -2.0F, 5.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0044F, -4.0F, -0.2006F, 3.1416F, 0.0F, -1.5708F));

        PartDefinition cube_r66 = pillar.addOrReplaceChild("cube_r66", CubeListBuilder.create().texOffs(184, 102).addBox(-10.2033F, 3.2709F, -2.0F, 10.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0044F, 1.0F, -0.2006F, 3.1416F, 0.0F, -1.5708F));

        PartDefinition cargo_module = Frame.addOrReplaceChild("cargo_module", CubeListBuilder.create(), PartPose.offset(5.0F, -10.9F, 16.0F));

        PartDefinition cube_r67 = cargo_module.addOrReplaceChild("cube_r67", CubeListBuilder.create().texOffs(156, 0).addBox(-6.0F, -11.0F, -7.0F, 13.0F, 13.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.2182F, 0.0F));

        PartDefinition tank_module = Frame.addOrReplaceChild("tank_module", CubeListBuilder.create().texOffs(1, 0).addBox(0.0F, -1.0F, -13.0F, 1.0F, 6.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(1, 0).addBox(-11.0F, -1.0F, -13.0F, 1.0F, 6.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(2.1F, -14.0F, -15.5F));

        PartDefinition jerrican1_r1 = tank_module.addOrReplaceChild("jerrican1_r1", CubeListBuilder.create().texOffs(218, 226).addBox(-2.5F, -8.0F, -7.0F, 5.0F, 16.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.1F, 2.0F, -5.5F, 0.0F, 0.0F, -1.5708F));

        PartDefinition speed_module = Frame.addOrReplaceChild("speed_module", CubeListBuilder.create().texOffs(1, 0).addBox(0.9F, -7.5F, -7.5F, 1.0F, 6.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offset(7.0F, -7.5F, -21.0F));

        PartDefinition cube_r68 = speed_module.addOrReplaceChild("cube_r68", CubeListBuilder.create().texOffs(217, 213).addBox(-7.0F, -7.0F, -3.9F, 14.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }
    public void setupDefaultModules(RoverRenderState state) {
        this.cargo_module.visible = state.hasCargoModule;
        this.tank_module.visible = state.roverModules.contains(ModulesRegistry.ROVER_TANK.get());
        this.speed_module.visible = state.roverModules.contains(ModulesRegistry.ROVER_SPEED.get());
    }


    @Override
    public void setupAnim(RoverRenderState state) {
        super.setupAnim(state);

        // Calculate continuous wheel rotation based on movement speed
        double movementLength = Math.sqrt(
            state.deltaMovement.x * state.deltaMovement.x +
            state.deltaMovement.z * state.deltaMovement.z
        );
        // Scale based on speed: faster movement = faster rotation
        float wheelRotation = (float) movementLength * 2.0f;

        // Apply rotation based on forward/backward state
        if (state.isForward) {
            this.wheel_1.xRot = state.ageInTicks * wheelRotation;
            this.wheel_2.xRot = state.ageInTicks * wheelRotation;
            this.wheel_3.xRot = state.ageInTicks * wheelRotation;
            this.wheel_4.xRot = state.ageInTicks * wheelRotation;

        }
        else if (state.isBackward) {

            this.wheel_1.xRot = -state.ageInTicks * wheelRotation;
            this.wheel_2.xRot = -state.ageInTicks * wheelRotation;
            this.wheel_3.xRot = -state.ageInTicks * wheelRotation;
            this.wheel_4.xRot = -state.ageInTicks * wheelRotation;
        }
        else {
            // Stop wheels when not moving

            this.wheel_1.xRot = 0;
            this.wheel_2.xRot = 0;
            this.wheel_3.xRot = 0;
            this.wheel_4.xRot = 0;
        }

        if (state.xRot > 0) {

            this.wheel_1.xRot += state.xRot / 4;
            this.wheel_2.xRot += state.xRot / 4;
            this.wheel_3.xRot += state.xRot / 4;
            this.wheel_4.xRot += state.xRot / 4;
        }

        this.antenna.yRot = state.ageInTicks / 20;
    }



}
