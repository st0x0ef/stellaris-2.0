package org.exodusstudio.stellaris.client.renderers.space_suit;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;

public class SpaceSuitModel extends HumanoidModel<HumanoidRenderState> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(IdentifierUtils.id("space_suit"), "main");
    public static final Identifier TEXTURE = IdentifierUtils.texture("entity/equipment/space_suit");

    private final ModelPart head;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart waist;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftShoe;
    private final ModelPart rightShoe;
    private final ModelPart antenna;
    private final ModelPart lamp;
    private final ModelPart hat;

    private final EquipmentSlot slot;

    public SpaceSuitModel(ModelPart root, EquipmentSlot slot, ItemStack stack, @Nullable HumanoidModel<HumanoidRenderState> parentModel) {
        super(root, RenderTypes::armorTranslucent);

        this.head = root.getChild("head");
        this.antenna = head.getChild("antenna_r1");
        this.lamp = head.getChild("lamp_r1");
        this.hat = head.getChild("hat");
        this.body = root.getChild("body");
        this.leftArm = root.getChild("left_arm");
        this.rightArm = root.getChild("right_arm");
        this.waist = root.getChild("waist");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.leftShoe = root.getChild("left_shoe");
        this.rightShoe = root.getChild("right_shoe");

        this.slot = slot;
        this.setVisible();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();


        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.75F))
                .texOffs(16, 34).addBox(4.7F, -5.8F, 1.2F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.ZERO);

        head.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);

        head.addOrReplaceChild("lamp_r1", CubeListBuilder.create().texOffs(48, 48).addBox(-2.0F, -1.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.7F, -0.5F, -0.2618F, 0.0F, 0.0F));

        head.addOrReplaceChild("antenna_r1", CubeListBuilder.create().texOffs(50, 16).addBox(0.0F, -2.0F, -1.0F, 0.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.2F, -5.8F, 4.2F, -0.7854F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(26, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.75F))
                .texOffs(0, 16).addBox(-4.0F, 1.0F, 2.0F, 8.0F, 13.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(26, 32).addBox(-3.0F, 2.0F, 2.0F, 6.0F, 9.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.ZERO);

        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(0, 34).mirror().addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.35F))
                        .texOffs(48, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.75F)),
                PartPose.offset(5.0F, 2.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(48, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.75F))
                        .texOffs(0, 34).mirror().addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.35F)),
                PartPose.offset(-5.0F, 2.0F, 0.0F));

        partdefinition.addOrReplaceChild("waist", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, 0.0F, -2.1F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.ZERO);

        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(16, 45).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
                        .texOffs(16, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        partdefinition.addOrReplaceChild("left_shoe", CubeListBuilder.create()
                        .texOffs(32, 45).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.75F)),
                PartPose.offset(1.9F, 12.0F, 0.0F));

        partdefinition.addOrReplaceChild("right_shoe", CubeListBuilder.create()
                        .texOffs(32, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.75F)),
                PartPose.offset(-1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setAllVisible(boolean visible) {
        super.setAllVisible(visible);

        this.antenna.visible = visible;
        this.lamp.visible = visible;
        this.waist.visible = visible;
        this.head.visible = visible;
        this.body.visible = visible;
        this.leftArm.visible = visible;
        this.rightArm.visible = visible;
        this.leftLeg.visible = visible;
        this.rightLeg.visible = visible;
        this.leftShoe.visible = visible;
        this.rightShoe.visible = visible;
        this.hat.visible = visible;
    }

    private void setVisible() {
        this.setAllVisible(false);
        switch (this.slot) {
            case HEAD -> {
                this.head.visible = true;
                this.lamp.visible = true;
                this.antenna.visible = true;
            }
            case CHEST -> {
                this.body.visible = true;
                this.rightArm.visible = true;
                this.leftArm.visible = true;
            }
            case LEGS -> {
                this.waist.visible = true;
                this.rightLeg.visible = true;
                this.leftLeg.visible = true;
            }
            case FEET ->  {
                this.leftShoe.visible = true;
                this.rightShoe.visible = true;
            }
        }
    }

    @Override
    public ModelPart getArm(HumanoidArm side) {
        return switch (side) {
            case LEFT -> this.leftArm;
            case RIGHT -> this.rightArm;
        };
    }
}