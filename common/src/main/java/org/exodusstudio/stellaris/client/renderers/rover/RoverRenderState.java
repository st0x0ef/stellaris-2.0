package org.exodusstudio.stellaris.client.renderers.rover;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rover.RoverModule;

public class RoverRenderState extends EntityRenderState {
    public boolean isForward;
    public boolean isBackward;
    public float xRot;
    public float yRot;
    public Vec3 deltaMovement;
    public Direction direction;
    public float ageInTicks;
    public Modules<RoverModule> roverModules;
    public boolean hasCargoModule;
}
