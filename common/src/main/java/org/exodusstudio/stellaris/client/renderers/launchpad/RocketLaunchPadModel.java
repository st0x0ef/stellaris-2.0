package org.exodusstudio.stellaris.client.renderers.launchpad;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.joml.Vector3fc;

import java.util.function.Consumer;
import java.util.function.Function;

public class RocketLaunchPadModel extends Model<BlockEntityRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("rocket_launch_pad"), "main");


    private final ModelPart LStructure;
    private final ModelPart L_low;
    private final ModelPart L_high;
    private final ModelPart LUpBar;
    private final ModelPart LBars;
    private final ModelPart RStructure;
    private final ModelPart R_mid;
    private final ModelPart R_top;
    private final ModelPart RUpBar;
    private final ModelPart RBars;
    private final ModelPart Platform;
    private final ModelPart EdgePlatform;
    private final ModelPart CenterPlatform;
    private final ModelPart antenna;

    public RocketLaunchPadModel(ModelPart root) {
        this(root, RenderTypes::entityCutoutCull);
    }

    private RocketLaunchPadModel(ModelPart root, Function<Identifier, RenderType> renderType) {
        super(root, renderType);

        this.LStructure = root.getChild("LStructure");
        this.L_low = this.LStructure.getChild("L_low");
        this.L_high = this.LStructure.getChild("L_high");
        this.LUpBar = this.LStructure.getChild("LUpBar");
        this.LBars = this.LStructure.getChild("LBars");
        this.RStructure = root.getChild("RStructure");
        this.R_mid = this.RStructure.getChild("R_mid");
        this.R_top = this.RStructure.getChild("R_top");
        this.RUpBar = this.R_top.getChild("RUpBar");
        this.RBars = this.RStructure.getChild("RBars");
        this.Platform = root.getChild("Platform");
        this.EdgePlatform = this.Platform.getChild("EdgePlatform");
        this.CenterPlatform = this.Platform.getChild("CenterPlatform");
        this.antenna = root.getChild("antenna");
    }

    public void setTowersVisible(boolean visible) {
        this.LStructure.visible = visible;
        this.RStructure.visible = visible;
        this.antenna.visible = visible;
    }

    public void setAntennaVisible(boolean visible) {
        this.antenna.visible = visible;
    }


    public void setBaseVisible(boolean visible) {
        this.Platform.visible = visible;
    }

    /** Swings the towers' horizontal service bars around their tower hinge by {@code radians} (0 = at rest). */
    public void setBarsAngle(float radians) {
        this.L_low.yRot = radians;
        this.L_high.yRot = radians;
        this.LUpBar.yRot = radians;
        this.R_mid.yRot = -radians;
        this.R_top.yRot = -radians;
    }

    public ModelPart platform() {
        return this.Platform;
    }

    public void getTowerExtents(PoseStack poseStack, Consumer<Vector3fc> output) {
        this.LStructure.getExtentsForGui(poseStack, output);
        this.RStructure.getExtentsForGui(poseStack, output);
        this.antenna.getExtentsForGui(poseStack, output);
    }


    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition LStructure = partdefinition.addOrReplaceChild("LStructure", CubeListBuilder.create(), PartPose.offset(-27.25F, -39.9F, 0.0F));

        PartDefinition L_low = LStructure.addOrReplaceChild("L_low", CubeListBuilder.create().texOffs(88, 56).addBox(5.25F, -11.9F, -2.0F, 17.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition L_high = LStructure.addOrReplaceChild("L_high", CubeListBuilder.create().texOffs(88, 56).addBox(5.25F, 20.1F, -2.0F, 17.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition LUpBar = LStructure.addOrReplaceChild("LUpBar", CubeListBuilder.create().texOffs(88, 56).addBox(-3.75F, -24.9F, -2.0F, 17.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(90, 44).addBox(13.25F, -24.9F, -2.0F, 9.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition LBars = LStructure.addOrReplaceChild("LBars", CubeListBuilder.create().texOffs(28, 0).addBox(2.25F, -17.1F, -2.0F, 3.0F, 81.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-7.75F, -27.1F, -2.0F, 3.0F, 93.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(14, 0).addBox(-4.75F, -25.1F, 0.0F, 7.0F, 91.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = LBars.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(88, 63).addBox(-20.6666F, -16.3425F, -2.0F, 14.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6981F));

        PartDefinition RStructure = partdefinition.addOrReplaceChild("RStructure", CubeListBuilder.create(), PartPose.offset(0.75F, -39.9F, 0.0F));

        PartDefinition R_mid = RStructure.addOrReplaceChild("R_mid", CubeListBuilder.create(), PartPose.offset(26.5F, 0.0F, 0.0F));

        PartDefinition cube_r2 = R_mid.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(88, 56).addBox(-8.5F, -1.5F, -2.0F, 17.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(88, 56).addBox(-8.5F, -33.5F, -2.0F, 17.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-13.75F, 21.6F, 0.0F, 0.0F, 3.1416F, 0.0F));

        PartDefinition R_top = RStructure.addOrReplaceChild("R_top", CubeListBuilder.create(), PartPose.offset(26.5F, 0.0F, 0.0F));

        PartDefinition RUpBar = R_top.addOrReplaceChild("RUpBar", CubeListBuilder.create().texOffs(90, 70).addBox(4.25F, -24.9F, -2.0F, 9.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(88, 56).mirror().addBox(13.25F, -24.9F, -2.0F, 17.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-26.5F, 0.0F, 0.0F));

        PartDefinition RBars = RStructure.addOrReplaceChild("RBars", CubeListBuilder.create().texOffs(28, 0).mirror().addBox(-5.25F, -17.1F, -2.0F, 3.0F, 81.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(14, 0).mirror().addBox(-2.25F, -25.1F, 0.0F, 7.0F, 91.0F, 0.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 0).mirror().addBox(4.75F, -27.1F, -2.0F, 3.0F, 93.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(26.5F, 0.0F, 0.0F));

        PartDefinition cube_r3 = RBars.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(88, 63).mirror().addBox(6.6666F, -16.3425F, -2.0F, 14.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6981F));

        PartDefinition Platform = partdefinition.addOrReplaceChild("Platform", CubeListBuilder.create(), PartPose.offset(32.0F, 26.0F, -32.0F));

        PartDefinition EdgePlatform = Platform.addOrReplaceChild("EdgePlatform", CubeListBuilder.create().texOffs(90, 20).addBox(-8.0F, -2.0F, 0.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(42, 73).addBox(-24.0F, -2.0F, 0.0F, 16.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(42, 73).addBox(-40.0F, -2.0F, 0.0F, 16.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(42, 73).addBox(-56.0F, -2.0F, 0.0F, 16.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(90, 20).addBox(-64.0F, -2.0F, 0.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(42, 38).addBox(-8.0F, -2.0F, 8.0F, 8.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(42, 20).addBox(-64.0F, -2.0F, 8.0F, 8.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(42, 20).addBox(-64.0F, -2.0F, 24.0F, 8.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(42, 38).addBox(-8.0F, -2.0F, 24.0F, 8.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(42, 38).addBox(-8.0F, -2.0F, 40.0F, 8.0F, 2.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(90, 20).addBox(-8.0F, -2.0F, 56.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(42, 73).addBox(-24.0F, -2.0F, 56.0F, 16.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(42, 73).addBox(-40.0F, -2.0F, 56.0F, 16.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(42, 73).addBox(-56.0F, -2.0F, 56.0F, 16.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(90, 20).addBox(-64.0F, -2.0F, 56.0F, 8.0F, 2.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(42, 20).addBox(-64.0F, -2.0F, 40.0F, 8.0F, 2.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition CenterPlatform = Platform.addOrReplaceChild("CenterPlatform", CubeListBuilder.create().texOffs(42, 0).addBox(-8.0F, -4.0F, -8.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(42, 0).addBox(-8.0F, -4.0F, -24.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(42, 0).addBox(-8.0F, -4.0F, -40.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(42, 0).addBox(8.0F, -4.0F, -40.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(42, 0).addBox(8.0F, -4.0F, -8.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(42, 0).addBox(8.0F, -4.0F, -24.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(42, 0).addBox(24.0F, -4.0F, -8.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(42, 0).addBox(24.0F, -4.0F, -24.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(42, 0).addBox(24.0F, -4.0F, -40.0F, 16.0F, 4.0F, 16.0F, new CubeDeformation(0.0F)), PartPose.offset(-48.0F, 0.0F, 48.0F));

        PartDefinition antenna = partdefinition.addOrReplaceChild("antenna", CubeListBuilder.create(), PartPose.offset(-36.4054F, -79.8686F, 0.5591F));

        PartDefinition cube_r4 = antenna.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(82, 83).addBox(5.4676F, 1.1383F, -2.0591F, 5.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, 0.0F, 0.0F, -1.1781F));

        PartDefinition cube_r5 = antenna.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(28, 85).addBox(0.2541F, -1.1528F, -2.5591F, 3.0F, 7.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(90, 77).addBox(0.4005F, -10.5063F, -2.0591F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(32, 96).addBox(0.9005F, -7.5063F, -1.5591F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(42, 56).addBox(-1.7459F, -1.1528F, -8.0591F, 8.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition cube_r6 = antenna.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(42, 83).addBox(-6.2301F, -0.5773F, -2.0591F, 5.0F, 2.0F, 15.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -6.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition cube_r7 = antenna.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(90, 30).addBox(-5.0F, -1.5F, -2.3324F, 13.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.6912F, 11.3996F, -0.2268F, 0.0F, 0.0F, -0.7854F));

        PartDefinition cube_r8 = antenna.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(14, 96).addBox(-4.0F, -1.5F, -2.3324F, 5.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0912F, 12.6996F, -0.2268F, 0.0F, 0.0F, 0.7854F));

        PartDefinition cube_r9 = antenna.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(14, 96).addBox(-4.0F, -1.5F, -2.3324F, 5.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.4912F, 7.3996F, -0.2268F, 0.0F, 0.0F, 0.7854F));

        PartDefinition cube_r10 = antenna.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(90, 37).addBox(-5.0F, -1.5F, -2.3324F, 10.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3088F, 6.3996F, -0.2268F, 0.0F, 0.0F, -0.7854F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }
}
