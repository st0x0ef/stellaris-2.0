package org.exodusstudio.stellaris.client.renderers.rockets.models;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderState;

public class RocketModel extends EntityModel<RocketRenderState> {
    public ModelPart MainBody;
    public ModelPart tank_upgrade;
    public ModelPart sunflare_protection;
    public ModelPart shield2;
    public ModelPart shield1;
    public ModelPart storage_upgrade;
    public ModelPart motor_upgrade;
    public ModelPart pipes;
    public ModelPart Roof;
    public ModelPart RoofPlanes;
    public ModelPart RoofBars;
    public ModelPart RoofFrame;
    public ModelPart RoofTop;
    public ModelPart Bottom;
    public ModelPart BottomPlanes;
    public ModelPart BottomBars;
    public ModelPart BottomFrame;
    public ModelPart Wings;

    protected RocketModel(ModelPart root) {
        super(root);
    }

    /**
     * Sets the rocket model to its default state, hiding all upgrades.
     * These upgrades can be made visible with modules.
     */
    public void setDefaultModel() {
        if (this.storage_upgrade != null) { // Big rocket model doesn't have it for now
            this.storage_upgrade.visible = false;
        }
        this.tank_upgrade.visible = false;
        this.motor_upgrade.visible = false;
        this.shield1.visible = false;
        this.shield2.visible = false;
    }
}
