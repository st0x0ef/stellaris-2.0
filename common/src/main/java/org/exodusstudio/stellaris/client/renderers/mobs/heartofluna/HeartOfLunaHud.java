package org.exodusstudio.stellaris.client.renderers.mobs.heartofluna;

import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.exodusstudio.stellaris.client.renderers.StellarisBossHudSlots;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaBossEntity;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaBossEntity.State;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class HeartOfLunaHud {
    private static final Map<UUID, Bar> BARS = new HashMap<>();
    private static Object world;

    public static void init() {
        ClientGuiEvent.RENDER_HUD.register((graphics, delta) -> render(graphics, delta.getGameTimeDeltaPartialTick(true)));
    }

    private static void render(GuiGraphicsExtractor graphics, float partial) {
        Minecraft mc = Minecraft.getInstance();
        if (world != mc.level) { BARS.clear(); world = mc.level; }
        if (mc.level == null || mc.player == null) return;
        Set<UUID> seen = new HashSet<>();
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof HeartOfLunaBossEntity boss) || boss.isRemoved()) continue;
            int slot = StellarisBossHudSlots.slot(boss.bossEventId(), graphics.guiHeight());
            if (slot < 0) continue;
            seen.add(boss.getUUID());
            Bar bar = BARS.computeIfAbsent(boss.getUUID(), id -> new Bar(boss.getHealth()));
            draw(graphics, boss, bar, slot, partial);
        }
        BARS.keySet().retainAll(seen);
    }

    private static void draw(GuiGraphicsExtractor g, HeartOfLunaBossEntity boss, Bar bar, int y, float partial) {
        long now = System.nanoTime();
        double dt = Math.min(0.1, (now - bar.lastTime) / 1.0E9);
        bar.lastTime = now;
        float target = boss.state() == State.DEATH ? 0 : boss.getHealth();
        float lost = Math.max(0, bar.lastHealth - target);
        boolean phaseChanged = target < 200 && bar.lastHealth >= 200 || target <= 20 && bar.lastHealth > 20;
        float elapsed = boss.effectTicks(partial);
        boolean released = boss.state() == State.PULSE_RAY && elapsed >= 23 && (bar.lastAction != State.PULSE_RAY || bar.lastEffectTick < 23)
                || boss.state() == State.ROAR && elapsed >= 18 && (bar.lastAction != State.ROAR || bar.lastEffectTick < 18);
        bar.motion.impulse(lost, phaseChanged, released);
        bar.lastAction = boss.state();
        bar.lastEffectTick = elapsed;
        if (target < bar.lastHealth) bar.damageTime = now;
        if (phaseChanged) bar.transitionTime = now;
        bar.lastHealth = target;
        bar.health += (target - bar.health) * (float) (1-Math.exp(-dt*12));
        if (now - bar.damageTime > 180_000_000L) bar.trail += (target - bar.trail) * (float) (1-Math.exp(-dt*3));
        bar.beat += dt * (target < 20 ? 3.2 : target < 200 ? 2.1 : 0.85 + (1-target/550)*0.8);
        float damage = Math.max(0, 1-(now-bar.damageTime)/350_000_000F);
        float transition = Math.max(0, 1-(now-bar.transitionTime)/1_200_000_000F);
        float charge = boss.state() == State.PULSE_RAY && elapsed < 23 ? Mth.clamp(elapsed/23,0,1) : 0;
        float flash = boss.state() == State.PULSE_RAY && elapsed >= 23 && elapsed < 27 ? (27-elapsed)/4 : 0;
        float visibility = boss.state() == State.DEATH ? Mth.clamp((100-elapsed)/45,0,1) : Math.min(1,(now-bar.created)/700_000_000F);
        if (visibility <= 0) return;
        var motion = bar.motion.sample(dt, bar.beat, charge, flash, visibility);
        float beat = motion.beat();
        int width = Math.min(292, g.guiWidth() - 36), x = (g.guiWidth() - width) / 2, cx = g.guiWidth() / 2;
        g.nextStratum();
        int reveal = (int)Math.round((width + 36) * (1 - Math.pow(1 - visibility, 3)) / 2);
        g.enableScissor(cx - reveal, Math.max(0, y - 13), cx + reveal, y + 12);
        g.pose().pushMatrix();
        g.pose().translate(cx + motion.x(), y + 3 + motion.y());
        g.pose().scale(motion.scaleX(), motion.scaleY());
        g.pose().translate(-cx, -y - 3);
        for (int layer = 3; layer > 0; layer--) {
            float glow = (0.1F + beat * 0.07F + charge * 0.12F) * visibility / layer;
            g.fill(x - layer, y - layer, x + width + layer, y + 7 + layer, color(0xFF753399, glow));
        }
        g.fill(x - 2, y, x + width + 2, y + 7, color(0xFF100B17, visibility));
        g.fill(x, y - 1, x + width, y + 8, color(0xFF514058, visibility));
        g.fill(x, y, x + width, y + 7, color(0xFF09070E, visibility));
        g.fill(x + 1, y + 1, x + width - 1, y + 6, color(0xFF21142C, visibility));
        int available = width - 4;
        int trail = Mth.clamp(Math.round(available * bar.trail / 550), 0, available);
        int health = Mth.clamp(Math.round(available * bar.health / 550), 0, available);
        g.fill(x + 2, y + 1, x + 2 + trail, y + 6, color(0xFF945878, visibility));
        g.fillGradient(x + 2, y + 1, x + 2 + health, y + 6, color(0xFFC88CE8, visibility), color(0xFF643189, visibility));
        g.fill(x + 2, y + 1, x + 2 + health, y + 2, color(0xFFF0CFFF, visibility * (0.55F + beat * 0.2F)));
        g.fill(x + 2, y + 5, x + 2 + health, y + 6, color(0xFF3C1959, visibility));
        for (int i = 0; i < health; i++) {
            double position = i / (double) Math.max(1, available);
            float light = (float) Math.pow(Math.max(0, Math.cos(position * Math.PI * 2 - bar.beat * 0.6)), 12);
            g.fill(x + 2 + i, y + 2, x + 3 + i, y + 5, color(0xFFEDB9FF, light * (0.1F + charge * 0.15F) * visibility));
        }
        double cycle = bar.beat % 1;
        for (int side : new int[]{-1, 1}) {
            int pulse = cx + side * (int)(cycle * width / 2);
            for (int tail = 0; tail < 5; tail++) {
                int px = pulse - side * tail;
                if (px >= x + 2 && px < x + 2 + health) {
                    g.fill(px, y + 2, px + 1, y + 5, color(0xFFECC7FF, (float)((1 - cycle) * (1 - tail / 5F)) * 0.45F * visibility));
                }
            }
            if (charge > 0) {
                int inward = cx + side * Math.round(width * 0.48F * (1 - charge));
                g.fill(Math.min(cx, inward), y - 1, Math.max(cx, inward) + 1, y, color(0xFFE4A8FF, charge * 0.65F * visibility));
                disc(g, inward, y, 1, color(0xFFF3D8FF, charge * visibility));
            }
        }
        if (health > 0) {
            int edge = x + 1 + health;
            g.fill(edge, y + 1, edge + 1, y + 6, color(0xFFF4D7FF, visibility * (0.4F + damage * 0.6F)));
        }
        for (float threshold : new float[]{200, 20}) {
            int marker = x + 2 + Math.round(available * threshold / 550);
            g.fill(marker, y + 1, marker + 1, y + 6, color(0xB0251436, visibility));
            g.fill(marker, y + 7, marker + 1, y + 9, color(0xFFB58AC9, visibility * (0.6F + transition * 0.4F)));
        }
        for (int side : new int[]{-1, 1}) {
            int edge = (side < 0 ? x - 4 : x + width + 3) - side * Math.round(charge * 3);
            disc(g, edge, y + 3, 5, color(0xFF130B1C, visibility));
            disc(g, edge, y + 3, 3, color(0xFFB898C9, visibility));
            disc(g, edge - side, y + 2, 3, color(0xFF130B1C, visibility));
            g.fill(edge, y + 3, edge + 1, y + 4, color(0xFFF7DCFF, visibility * (0.5F + beat * 0.5F)));
        }
        g.fill(x + 2, y + 1, x + 2 + health, y + 6, color(0xFFEED3FF, visibility * (damage * 0.16F + flash * 0.25F)));
        HeartOfLunaTitle.draw(g, cx, y - 10, 0.95F, visibility);
        g.pose().popMatrix();
        g.disableScissor();
    }

    private static void disc(GuiGraphicsExtractor g,int cx,int cy,int r,int color) {
        for(int y=-r;y<=r;y++) {
            int x=(int)Math.sqrt(r*r-y*y);
            g.fill(cx-x,cy+y,cx+x+1,cy+y+1,color);
        }
    }
    private static int color(int rgb,float alpha) { return ((int)((rgb>>>24)*Mth.clamp(alpha,0,1))<<24)|(rgb&0xFFFFFF); }
    private static final class Bar {
        final HeartOfLunaHudMotion motion = new HeartOfLunaHudMotion();
        State lastAction = State.IDLE;
        float lastEffectTick;
        float health, trail, lastHealth;
        double beat;
        final long created = System.nanoTime();
        long lastTime = created, damageTime, transitionTime;
        Bar(float health) { this.health=health; this.trail=health; this.lastHealth=health; }
    }
    private HeartOfLunaHud() {}
}
