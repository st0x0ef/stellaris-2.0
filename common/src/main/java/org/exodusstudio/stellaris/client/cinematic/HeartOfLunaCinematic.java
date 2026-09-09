package org.exodusstudio.stellaris.client.cinematic;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientRawInputEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaBossEntity;
import org.exodusstudio.stellaris.common.entities.mobs.heartofluna.HeartOfLunaBossEntity.State;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public final class HeartOfLunaCinematic {
    private static HeartOfLunaBossEntity active;
    private static final Set<String> SKIPPED = new HashSet<>();
    private static Object world;
    private static final Map<UUID, Authorization> AUTHORIZED = new HashMap<>();
    private static String trackKey;
    private static Vec3 trackOrigin;
    private static CameraPose entry;
    private static float trackYaw;
    private static float startClock;
    private static float initialTicks;
    private static float lastClock;
    private static double collisionFraction = 1;

    public static void init() {
        ClientTickEvent.CLIENT_POST.register(HeartOfLunaCinematic::tick);
        ClientRawInputEvent.KEY_PRESSED.register((minecraft, action, event) -> {
            if (action == GLFW.GLFW_PRESS && isVisualActive() && (event.key() == GLFW.GLFW_KEY_SPACE || event.key() == GLFW.GLFW_KEY_ESCAPE)) {
                SKIPPED.add(key(active));
                active = null;
                trackKey = null;
                return EventResult.interruptTrue();
            }
            if (isAuthoritativelyLocked() && (minecraft.options.keyAttack.matches(event)
                    || minecraft.options.keyUse.matches(event) || minecraft.options.keyPickItem.matches(event)
                    || minecraft.options.keyDrop.matches(event) || minecraft.options.keySwapOffhand.matches(event)
                    || minecraft.options.keyInventory.matches(event))) return EventResult.interruptTrue();
            return EventResult.pass();
        });
        ClientRawInputEvent.MOUSE_CLICKED_PRE.register((minecraft, button, action) -> {
            if (!isAuthoritativelyLocked() || minecraft.screen != null) return EventResult.pass();
            var event = new net.minecraft.client.input.MouseButtonEvent(0, 0, button);
            return minecraft.options.keyAttack.matchesMouse(event) || minecraft.options.keyUse.matchesMouse(event)
                    || minecraft.options.keyPickItem.matchesMouse(event) ? EventResult.interruptTrue() : EventResult.pass();
        });
        ClientGuiEvent.RENDER_HUD.register((graphics, delta) -> render(graphics, delta.getGameTimeDeltaPartialTick(true)));
    }

    private static String key(HeartOfLunaBossEntity boss) { return boss.getUUID()+":"+boss.state(); }

    public static void sync(UUID boss, int state, int remainingTicks) {
        Minecraft mc = Minecraft.getInstance();
        resetWorld(mc);
        if (remainingTicks <= 0) {
            Authorization authorization = AUTHORIZED.get(boss);
            if (authorization != null && authorization.state == state) AUTHORIZED.remove(boss);
        } else if (mc.level != null && (state == State.INTRO.ordinal() || state == State.DEATH.ordinal())) {
            AUTHORIZED.put(boss, new Authorization(state, mc.level.getGameTime() + remainingTicks + 60));
        }
        if (active != null && !authorized(active)) { active = null; trackKey = null; }
    }

    private static void resetWorld(Minecraft mc) {
        if (world != mc.level) {
            active = null;
            trackKey = null;
            SKIPPED.clear();
            AUTHORIZED.clear();
            world = mc.level;
        }
    }

    private static boolean authorized(HeartOfLunaBossEntity boss) {
        Authorization authorization = AUTHORIZED.get(boss.getUUID());
        return authorization != null && authorization.state == boss.state().ordinal();
    }

    public static boolean isAuthoritativelyLocked() { return !AUTHORIZED.isEmpty(); }

    private record Authorization(int state, long expires) {}

    private static void tick(Minecraft mc) {
        resetWorld(mc);
        if (mc.level == null || mc.player == null || !mc.player.isAlive() || mc.player.isSpectator() || mc.player.isPassenger()) {
            active=null; trackKey=null; AUTHORIZED.clear(); return;
        }
        AUTHORIZED.values().removeIf(authorization -> mc.level.getGameTime() >= authorization.expires);
        if (isAuthoritativelyLocked()) {
            mc.options.keyAttack.setDown(false);
            mc.options.keyUse.setDown(false);
            mc.options.keyPickItem.setDown(false);
            mc.options.keyDrop.setDown(false);
            mc.options.keySwapOffhand.setDown(false);
        }
        Set<String> present = new HashSet<>();
        HeartOfLunaBossEntity candidate = null;
        double distance = 48*48;
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof HeartOfLunaBossEntity boss) || !boss.cinematic() || boss.isRemoved()) continue;
            String key = key(boss);
            present.add(key);
            if (boss.distanceToSqr(mc.player) < distance && !SKIPPED.contains(key) && authorized(boss)) {
                candidate=boss;
                distance=boss.distanceToSqr(mc.player);
            }
        }
        SKIPPED.retainAll(present);
        if (active == null || !active.cinematic() || !authorized(active) || active.isRemoved() || active.distanceToSqr(mc.player)>4096) active=candidate;
        if (active == null) trackKey = null;
    }

    public static boolean isVisualActive() {
        return active != null && active.cinematic() && authorized(active) && !active.isRemoved() && !SKIPPED.contains(key(active))
                && !StarCrawlerBossIntroController.isVisualActive() && !StarCrawlerBossDeathController.isVisualActive();
    }

    public static CameraPose sampleCamera(Vec3 vanilla, float yaw, float pitch, float partial) {
        if (!isVisualActive()) return null;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null || !mc.player.isAlive()) return null;
        float clock = mc.player.tickCount + partial;
        if (!key(active).equals(trackKey)) {
            trackKey = key(active);
            trackOrigin = active.position();
            trackYaw = active.getYRot();
            entry = new CameraPose(vanilla, yaw, pitch);
            startClock = lastClock = clock;
            initialTicks = Math.max(0, active.elapsed() - 1 + partial);
            collisionFraction = 1;
        }
        float t = cinematicTicks(partial);
        boolean death = active.state()==State.DEATH;
        var shot = HeartOfLunaCameraPath.sample(trackOrigin, trackYaw, death, t, active.state().duration);
        Vec3 camera = entry.position().lerp(shot.camera(), shot.entrance()).lerp(vanilla, shot.exit());
        double fraction = 1;
        Vec3 delta = camera.subtract(vanilla);
        for (int i=0;i<8;i++) {
            Vec3 offset = new Vec3((i&1)==0 ? -0.18 : 0.18,(i&2)==0 ? -0.18 : 0.18,(i&4)==0 ? -0.18 : 0.18);
            Vec3 from = vanilla.add(offset);
            var hit = mc.level.clip(new ClipContext(from,camera.add(offset),ClipContext.Block.COLLIDER,ClipContext.Fluid.NONE,mc.player));
            if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK && delta.lengthSqr()>0.0001) fraction=Math.min(fraction,Math.max(0,(hit.getLocation().distanceTo(from)-0.2)/delta.length()));
        }
        double dt = Math.max(0, Math.min(2, clock - lastClock));
        lastClock = clock;
        collisionFraction = fraction < collisionFraction ? fraction : Mth.lerp(1 - Math.exp(-dt * 0.18), collisionFraction, fraction);
        camera=vanilla.add(delta.scale(collisionFraction));
        Vec3 look=shot.aim().subtract(camera);
        float targetYaw=(float)(Math.atan2(-look.x,look.z)*Mth.RAD_TO_DEG);
        float targetPitch=(float)(-Math.atan2(look.y,look.horizontalDistance())*Mth.RAD_TO_DEG);
        float cameraYaw = Mth.rotLerp(shot.entrance(), entry.yaw(), targetYaw);
        float cameraPitch = Mth.lerp(shot.entrance(), entry.pitch(), targetPitch);
        return new CameraPose(camera,Mth.rotLerp(shot.exit(),cameraYaw,yaw),Mth.lerp(shot.exit(),cameraPitch,pitch));
    }

    private static float cinematicTicks(float partial) {
        Minecraft mc = Minecraft.getInstance();
        return active != null && key(active).equals(trackKey) && mc.player != null
                ? Math.max(0, initialTicks + mc.player.tickCount + partial - startClock)
                : active == null ? 0 : Math.max(0, active.elapsed() - 1 + partial);
    }

    private static void render(GuiGraphicsExtractor g,float partial) {
        if (!isVisualActive()) return;
        Minecraft mc=Minecraft.getInstance();
        float t=cinematicTicks(partial);
        boolean death=active.state()==State.DEATH;
        float duration=active.state().duration;
        float strength=smooth(Math.min(t/18,(duration-t)/18));
        int width=g.guiWidth(), height=g.guiHeight();
        int bars=Math.round(height*0.075F*strength);
        g.nextStratum();
        g.fill(0,0,width,bars,0xF509060F);
        g.fill(0,height-bars,width,height,0xF509060F);
        for(int edge=0;edge<12;edge++) {
            int inset=edge*3;
            int color=((int)(strength*(12-edge)*1.2F)<<24)|0x250B39;
            g.fill(inset,bars,width-inset,bars+3,color);
            g.fill(inset,height-bars-3,width-inset,height-bars,color);
            g.fill(inset,bars,inset+3,height-bars,color);
            g.fill(width-inset-3,bars,width-inset,height-bars,color);
        }
        float title=HeartOfLunaCameraPath.titleOpacity(death,t,duration);
        if(title>0) {
            int cy=Math.round(height*0.7F), half=Math.round(Math.min(width*0.42F,175)*title);
            g.enableScissor(width/2-half,cy-25,width/2+half,cy+40);
            org.exodusstudio.stellaris.client.renderers.mobs.heartofluna.HeartOfLunaTitle.draw(g, width / 2F, cy, 1.65F, title);
            String subtitle=death ? "THE HEART GOES STILL" : "THE MOON STILL BLEEDS";
            g.text(mc.font,subtitle,(width-mc.font.width(subtitle))/2,cy+23,((int)(235*title)<<24)|0xB687C9);
            g.fill(width/2-half,cy-8,width/2+half,cy-7,((int)(180*title)<<24)|0xA059C6);
            g.disableScissor();
        }
        String skip="SPACE / ESC  ·  SKIP";
        g.text(mc.font,skip,(width-mc.font.width(skip))/2,height-bars+Math.max(2,(bars-9)/2),0xBBA995B6);
        float flash=death ? Math.max(0,1-Math.abs(t-48)/3) : Math.max(0,1-Math.abs(t-118)/3);
        if(flash>0) g.fill(0,0,width,height,((int)(flash*55)<<24)|0xF6DBFF);
    }

    private static float smooth(float value) { float x=Mth.clamp(value,0,1); return x*x*(3-2*x); }
    public record CameraPose(Vec3 position,float yaw,float pitch) {}
    private HeartOfLunaCinematic() {}
}
