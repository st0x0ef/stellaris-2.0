package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;

import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaBossEntity;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaBossEntity.State;

public final class HeartOfLunaScreenEffects {
    private static float pressure, previous;
    private static Object world;

    public static void init() {
        ClientTickEvent.CLIENT_POST.register(mc -> {
            if(world!=mc.level) { pressure=previous=0; world=mc.level; }
            previous=pressure;
            pressure=0;
            if(mc.level==null || mc.player==null || !mc.player.isAlive()) return;
            for(Entity entity:mc.level.entitiesForRendering()) {
                if(!(entity instanceof HeartOfLunaBossEntity boss) || boss.isRemoved()) continue;
                float t=(int)boss.effectTicks(0);
                float distance=Mth.clamp(1-boss.distanceTo(mc.player)/28,0,1);
                float strength=boss.state()==State.PULSE_RAY ? Mth.clamp(t/23,0,1)*Mth.clamp((40-t)/8,0,1)
                        : boss.state()==State.ROAR ? Mth.clamp(1-Math.abs(t-18)/12,0,1)
                        : boss.state()==State.DEATH ? Mth.clamp(1-Math.abs(t-48)/9,0,1) : 0;
                pressure=Math.max(pressure,strength*distance);
            }
        });
        ClientGuiEvent.RENDER_HUD.register((g,delta) -> {
            float alpha=pressure(delta.getGameTimeDeltaPartialTick(true));
            if(alpha<0.01F) return;
            int width=g.guiWidth(),height=g.guiHeight();
            g.nextStratum();
            int tint=((int)(alpha*65)<<24)|0x311344;
            g.fillGradient(0,0,width,height/7,tint,0x00311344);
            g.fillGradient(0,height-height/7,width,height,0x00311344,tint);
            for(int i=0;i<12;i++) {
                int c=((int)(alpha*(12-i)*3.5F)<<24)|0x311344;
                g.fill(i*3,0,i*3+3,height,c);
                g.fill(width-i*3-3,0,width-i*3,height,c);
            }
        });
    }

    public static float pressure(float partial) {
        Minecraft mc=Minecraft.getInstance();
        return mc.level!=world || mc.player==null || !mc.player.isAlive() ? 0 : Mth.lerp(partial,previous,pressure);
    }
    private HeartOfLunaScreenEffects() {}
}
