package org.exodusstudio.stellaris.mixin;

import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.entities.EntityDataAttachmentAccessor;
import org.exodusstudio.stellaris.common.utils.PlanetUtil;
import org.exodusstudio.stellaris.common.utils.TeleportUtil;
import org.exodusstudio.stellaris.platform.DataAttachmentsPlatform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin implements EntityDataAttachmentAccessor {

    /** ATTACHMENTS */
    @Override
    public boolean stellaris$hasDataAttachments(Identifier key) {
        Entity entity = (Entity)(Object)this;
        return DataAttachmentsPlatform.hasEntityData(entity, key);
    }

    @Override
    public <T> void stellaris$saveDataAttachments(Identifier key, T value) {
        Entity entity = (Entity)(Object)this;
        DataAttachmentsPlatform.saveEntityData(entity, key, value);
    }

    @Override
    public <T> T stellaris$getDataAttachments(Identifier key, Class<T> clazz) {
        Entity entity = (Entity)(Object)this;
        return DataAttachmentsPlatform.getEntityData(entity, key, clazz);
    }


    @Inject( method = "tick", at = @At("HEAD"))
    public void entityFalling(CallbackInfo ci) {


        Entity entity = (Entity)(Object)this;
        Level level = entity.level();

        if(level == null || level.isClientSide()) {
            return;
        }


        MinecraftServer server = level.getServer();
        Planet orbit = PlanetsData.getPlanet(level.dimension());

        if(orbit != null && orbit.parentPlanet().isPresent()) {
            Planet mainPlanet = PlanetsData.getPlanet(orbit.parentPlanet().get());

            if(mainPlanet != null && entity.getY() <= Stellaris.CONFIG.vehicleConfig.orbitTeleportationYCoord) {


                ServerLevel mainPlanetLevel = PlanetsData.getPlanetLevel(server, mainPlanet);
                TeleportUtil.teleportFallingEntityToPlanet(entity, mainPlanetLevel, entity.blockPosition());
            }
        }
    }

}
