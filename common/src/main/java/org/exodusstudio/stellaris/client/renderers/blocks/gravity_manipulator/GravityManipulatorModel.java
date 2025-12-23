package org.exodusstudio.stellaris.client.renderers.blocks.gravity_manipulator;

import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import org.exodusstudio.stellaris.common.blocks.entities.machines.GravityManipulatorBlockEntity;

public class GravityManipulatorModel<T extends GravityManipulatorBlockEntity> extends Model {
    public GravityManipulatorModel(ModelPart root) {
        super(root, RenderType::entityCutout);
    }
}
