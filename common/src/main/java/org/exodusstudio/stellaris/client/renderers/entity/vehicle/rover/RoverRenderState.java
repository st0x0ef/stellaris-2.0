package org.exodusstudio.stellaris.client.renderers.entity.vehicle.rover;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class RoverRenderState extends EntityRenderState {
    public boolean isForward;
    public boolean isBackward;
    public float xRot;
    public float yRot;
    public Vec3 deltaMovement;
    public Direction direction;
    public float ageInTicks;
}
