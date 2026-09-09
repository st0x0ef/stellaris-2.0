package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;

import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaBossEntity;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaBossEntity.State;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaPose;

public final class HeartOfLunaParticles {
    public static void init() { ClientTickEvent.CLIENT_POST.register(HeartOfLunaParticles::tick); }

    private static void tick(Minecraft mc) {
        if(mc.level==null || mc.player==null || mc.isPaused()) return;
        for(Entity entity:mc.level.entitiesForRendering()) {
            if(!(entity instanceof HeartOfLunaBossEntity boss) || boss.distanceToSqr(mc.player)>4096 || boss.isRemoved()) continue;
            int lod=boss.distanceToSqr(mc.player)>1024 ? 3 : 1;
            int t=(int)boss.effectTicks(0);
            Vec3 heart=boss.anchor(HeartOfLunaPose.HEART,0);
            if((boss.state()==State.ROAR && t<18 || boss.state()==State.INTRO && t<115) && boss.tickCount%lod==0) {
                double angle=boss.tickCount*2.39996;
                Vec3 floor=boss.position().add(Math.cos(angle)*3.8,0.1,Math.sin(angle)*3.8);
                add(mc,ParticleTypes.ASH,floor,new Vec3(0,0.06,0));
            }
            if(boss.state()==State.PULSE_RAY && t>=23 && t<=32 && t%lod==0) {
                Vec3 end=boss.rayEnd();
                add(mc,ParticleTypes.ELECTRIC_SPARK,end,new Vec3(mc.level.getRandom().nextGaussian()*0.15,0.05,mc.level.getRandom().nextGaussian()*0.15));
                add(mc,new BlockParticleOption(ParticleTypes.BLOCK,mc.level.getBlockState(BlockPos.containing(end))),end,new Vec3(0,0.12,0));
            }
            if(boss.state()==State.DEATH && t>=18 && t<=24) {
                for(int i=0;i<3/lod+1;i++) {
                    Vec3 p=heart.add(mc.level.getRandom().nextGaussian()*2,mc.level.getRandom().nextDouble()*3,mc.level.getRandom().nextGaussian()*2);
                    add(mc,ParticleTypes.REVERSE_PORTAL,p,heart.subtract(p).scale(0.1));
                }
            }
        }
    }

    private static void add(Minecraft mc,ParticleOptions type,Vec3 p,Vec3 velocity) { mc.level.addParticle(type,p.x,p.y,p.z,velocity.x,velocity.y,velocity.z); }
    private HeartOfLunaParticles() {}
}
