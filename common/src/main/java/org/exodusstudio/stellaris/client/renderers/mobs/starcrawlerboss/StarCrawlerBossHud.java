package org.exodusstudio.stellaris.client.renderers.mobs.starcrawlerboss;

import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossIntroController;
import org.exodusstudio.stellaris.client.cinematic.StarCrawlerBossDeathController;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity.CombatPhase;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity.CombatState;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity.IntroState;
import org.exodusstudio.stellaris.common.entities.mobs.starcrawlerboss.StarCrawlerBossEntity.DeathCinematicState;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class StarCrawlerBossHud {

    private static final int VANILLA_BAR_WIDTH = 182;
    private static final int BAR_WIDTH = 236;
    private static final int BAR_HEIGHT = 5;

    private static final int FIRST_BAR_Y = 12;
    private static final int SLOT_SPACING = 19;

    private static final int TITLE_OFFSET = 10;

    private static final long DAMAGE_SHAKE_DURATION_MS = 430L;
    private static final long DAMAGE_FLASH_DURATION_MS = 220L;

    private static final float NORMAL_HEALTH_RESPONSE_MS = 70.0F;
    private static final float REGEN_HEALTH_RESPONSE_MS = 125.0F;
    private static final float DAMAGE_TRAIL_RESPONSE_MS = 480.0F;

    private static final long PHASE_TRANSITION_DURATION_MS = 900L;

    private static final long HUD_REVEAL_DURATION_MS = 780L;

    private static final long REGEN_START_DURATION_MS = 900L;

    private static final double REGEN_PULSE_SPEED = 0.0105D;

    private static final long REGEN_SWEEP_ONE_SPEED = 7L;
    private static final long REGEN_SWEEP_TWO_SPEED = 11L;


    private static final Map<UUID, BossHudState> BOSS_STATES =
            new HashMap<>();

    private static boolean initialized;

    private static boolean bossEventsFieldResolved;

    private static Field bossEventsField;

    private StarCrawlerBossHud() {
    }

    public static void init() {
        if (initialized) {
            return;
        }

        initialized = true;

        ClientGuiEvent.RENDER_HUD.register(
                StarCrawlerBossHud::renderHud
        );
    }

    private static void renderHud(
            GuiGraphicsExtractor graphics,
            DeltaTracker deltaTracker
    ) {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.level == null
                || minecraft.player == null) {

            BOSS_STATES.clear();

            return;
        }

        long now =
                now();

        List<StarCrawlerBossEntity> bosses =
                findLoadedBosses(
                        minecraft
                );

        bosses.sort(
                Comparator.comparingInt(
                        StarCrawlerBossEntity::getId
                )
        );

        Set<UUID> loadedIds =
                new HashSet<>();

        for (StarCrawlerBossEntity boss :
                bosses) {

            loadedIds.add(
                    boss.getUUID()
            );

            BossHudState state =
                    BOSS_STATES.computeIfAbsent(
                            boss.getUUID(),
                            id ->
                                    new BossHudState(
                                            boss,
                                            now
                                    )
                    );

            state.update(
                    boss,
                    now
            );
        }

        BOSS_STATES.entrySet()
                .removeIf(
                        entry ->
                                !loadedIds.contains(
                                        entry.getKey()
                                )
                                        && now
                                        - entry.getValue().lastSeenTime
                                        > 1500L
                );

        List<Integer> starCrawlerSlots =
                findVanillaStarCrawlerSlots(
                        minecraft,
                        graphics.guiHeight()
                );

        if (starCrawlerSlots != null
                && !starCrawlerSlots.isEmpty()) {

            renderIntoVanillaSlots(
                    graphics,
                    minecraft.font,
                    bosses,
                    starCrawlerSlots,
                    now
            );

            return;
        }

        renderFallbackStack(
                graphics,
                minecraft.font,
                bosses,
                now
        );
    }

    private static List<StarCrawlerBossEntity> findLoadedBosses(
            Minecraft minecraft
    ) {
        List<StarCrawlerBossEntity> result =
                new ArrayList<>();

        for (Entity entity :
                minecraft.level.entitiesForRendering()) {

            if (!(entity
                    instanceof StarCrawlerBossEntity boss)) {

                continue;
            }

            if (!boss.isAlive()
                    || boss.isRemoved()) {

                continue;
            }

            result.add(
                    boss
            );
        }

        return result;
    }

    private static List<Integer> findVanillaStarCrawlerSlots(
            Minecraft minecraft,
            int guiHeight
    ) {
        Map<UUID, LerpingBossEvent> events =
                getVanillaBossEvents(
                        minecraft
                );

        if (events == null) {
            return null;
        }

        List<Integer> slots =
                new ArrayList<>();

        int y =
                FIRST_BAR_Y;

        for (LerpingBossEvent event :
                events.values()) {

            if (y >= guiHeight / 3) {
                break;
            }

            if (event.getName()
                    .getString()
                    .isEmpty()) {

                slots.add(
                        y
                );
            }

            y +=
                    SLOT_SPACING;
        }

        return slots;
    }

    @SuppressWarnings("unchecked")
    private static Map<UUID, LerpingBossEvent> getVanillaBossEvents(
            Minecraft minecraft
    ) {
        if (!bossEventsFieldResolved) {
            resolveBossEventsField();
        }

        if (bossEventsField == null) {
            return null;
        }

        try {
            BossHealthOverlay overlay =
                    minecraft.gui
                            .getBossOverlay();

            Object value =
                    bossEventsField.get(
                            overlay
                    );

            if (value instanceof Map<?, ?> map) {
                return (Map<UUID, LerpingBossEvent>) map;
            }
        } catch (Throwable ignored) {

        }

        return null;
    }

    private static void resolveBossEventsField() {
        bossEventsFieldResolved =
                true;

        for (Field field :
                BossHealthOverlay.class
                        .getDeclaredFields()) {

            if (!Map.class
                    .isAssignableFrom(
                            field.getType()
                    )) {

                continue;
            }

            try {
                if (!field.trySetAccessible()) {
                    continue;
                }

                bossEventsField =
                        field;

                return;
            } catch (Throwable ignored) {
            }
        }
    }

    private static void renderIntoVanillaSlots(
            GuiGraphicsExtractor graphics,
            Font font,
            List<StarCrawlerBossEntity> bosses,
            List<Integer> slots,
            long now
    ) {
        int bossIndex =
                0;

        for (int slotY :
                slots) {

            drawVanillaReservationCover(
                    graphics,
                    slotY
            );

            if (bossIndex
                    >= bosses.size()) {

                continue;
            }

            StarCrawlerBossEntity boss =
                    bosses.get(
                            bossIndex++
                    );

            BossHudState state =
                    BOSS_STATES.get(
                            boss.getUUID()
                    );

            if (state == null) {
                continue;
            }

            if (StarCrawlerBossIntroController.shouldHideBossHud(boss)) {
                continue;
            }

            renderBoss(
                    graphics,
                    font,
                    boss,
                    state,
                    slotY,
                    now
            );
        }

        int y =
                slots.isEmpty()
                        ? FIRST_BAR_Y
                        : slots.get(
                        slots.size() - 1
                )
                        + SLOT_SPACING;

        while (bossIndex
                < bosses.size()
                && y < graphics.guiHeight() / 3) {

            StarCrawlerBossEntity boss =
                    bosses.get(
                            bossIndex++
                    );

            BossHudState state =
                    BOSS_STATES.get(
                            boss.getUUID()
                    );

            if (state != null
                    && !StarCrawlerBossIntroController.shouldHideBossHud(boss)) {
                renderBoss(
                        graphics,
                        font,
                        boss,
                        state,
                        y,
                        now
                );
            }

            y +=
                    SLOT_SPACING;
        }
    }

    private static void renderFallbackStack(
            GuiGraphicsExtractor graphics,
            Font font,
            List<StarCrawlerBossEntity> bosses,
            long now
    ) {
        int y =
                FIRST_BAR_Y;

        for (StarCrawlerBossEntity boss :
                bosses) {

            if (y >= graphics.guiHeight() / 3) {
                break;
            }

            drawVanillaReservationCover(
                    graphics,
                    y
            );

            BossHudState state =
                    BOSS_STATES.get(
                            boss.getUUID()
                    );

            if (state != null
                    && !StarCrawlerBossIntroController.shouldHideBossHud(boss)) {
                renderBoss(
                        graphics,
                        font,
                        boss,
                        state,
                        y,
                        now
                );
            }

            y +=
                    SLOT_SPACING;
        }
    }

    private static void renderBoss(
            GuiGraphicsExtractor graphics,
            Font font,
            StarCrawlerBossEntity boss,
            BossHudState state,
            int slotY,
            long now
    ) {

        if (boss.getDeathCinematicState()
                == DeathCinematicState.FINALIZED) {
            return;
        }

        int centerX =
                graphics.guiWidth()
                        / 2;

        int baseX =
                centerX
                        - BAR_WIDTH
                        / 2;

        float hudReveal =
                Math.min(
                        state.hudRevealElapsed(now),
                        StarCrawlerBossIntroController.hudRevealProgress(
                                boss.getUUID()
                        )
                );

        float deathTicks =
                StarCrawlerBossDeathController.getDeathVisualTicks(boss, 0.0F);

        boolean dying =
                boss.getDeathCinematicState()
                        == DeathCinematicState.DYING;

        float entityDeathFade =
                dying
                        ? 1.0F - smoothstep01(
                        Mth.clamp(
                                (deathTicks - 118.0F) / 20.0F,
                                0.0F,
                                1.0F
                        )
                )
                        : 1.0F;

        float hudVisibility =
                Math.min(
                        hudReveal,
                        Math.min(
                                entityDeathFade,
                                StarCrawlerBossDeathController.hudFade(
                                        boss.getUUID()
                                )
                        )
                );

        boolean clipped =
                hudVisibility < 0.999F;

        if (clipped) {
            float expansion =
                    easeOutQuint(hudVisibility);

            int halfWidth =
                    Math.max(
                            1,
                            Math.round(
                                    (BAR_WIDTH / 2.0F + 10.0F)
                                            * expansion
                            )
                    );

            graphics.enableScissor(
                    centerX - halfWidth,
                    Math.max(0, slotY - TITLE_OFFSET - 3),
                    centerX + halfWidth,
                    slotY + BAR_HEIGHT + 4
            );
        }

        int shakeX =
                calculateShakeX(
                        state,
                        now
                );

        int shakeY =
                calculateShakeY(
                        state,
                        now
                );

        int x =
                baseX
                        + shakeX;

        int y =
                slotY
                        + shakeY;

        CombatPhase phase =
                boss.getPhase();

        CombatState combatState =
                boss.getCombatState();

        boolean healing =
                combatState.isHealing();

        boolean finalRegen =
                combatState
                        == CombatState.HEALING_PHASE_3;

        int mainColor =
                getMainColor(
                        phase,
                        healing
                );

        int highlightColor =
                getHighlightColor(
                        phase,
                        healing
                );

        int shadowColor =
                getShadowColor(
                        phase,
                        healing
                );

        float visualProgress =
                Mth.clamp(
                        state.visualHealth
                                / boss.getMaxHealth(),
                        0.0F,
                        1.0F
                );

        float trailProgress =
                Mth.clamp(
                        state.trailHealth
                                / boss.getMaxHealth(),
                        0.0F,
                        1.0F
                );

        if (dying) {
            visualProgress *= 1.0F - smoothstep01(
                    Mth.clamp(deathTicks / 11.0F, 0.0F, 1.0F)
            );
            trailProgress *= 1.0F - smoothstep01(
                    Mth.clamp((deathTicks - 2.0F) / 38.0F, 0.0F, 1.0F)
            );
        }

        drawCompactGlow(
                graphics,
                x,
                y,
                mainColor,
                healing,
                finalRegen,
                state,
                now
        );

        drawFrame(
                graphics,
                x,
                y,
                healing,
                finalRegen,
                now
        );

        drawHealth(
                graphics,
                x,
                y,
                visualProgress,
                trailProgress,
                mainColor,
                highlightColor,
                shadowColor,
                state,
                now
        );

        if (!dying) {
            drawRemainingThresholds(
                    graphics,
                    boss,
                    x,
                    y
            );
        }

        if (healing) {
            drawRegenerationAnimation(
                    graphics,
                    boss,
                    state,
                    x,
                    y,
                    visualProgress,
                    finalRegen,
                    now
            );
        }

        drawPhaseTransitionAnimation(
                graphics,
                x,
                y,
                mainColor,
                state,
                now
        );

        drawTitle(
                graphics,
                font,
                boss.getDisplayName(),
                slotY,
                state,
                now
        );

        if (dying) {
            drawDeathHudEffects(
                    graphics,
                    x,
                    y,
                    highlightColor,
                    deathTicks,
                    StarCrawlerBossDeathController.hudImpact(boss.getUUID())
            );
        }

        if (clipped) {
            graphics.disableScissor();

            if (!dying) {
                drawHudRevealImpact(
                        graphics,
                        centerX,
                        slotY,
                        mainColor,
                        hudReveal
                );
            }
        }
    }

    private static void drawDeathHudEffects(
            GuiGraphicsExtractor graphics,
            int x,
            int y,
            int highlightColor,
            float deathTicks,
            float packetImpact
    ) {
        float impact = Math.max(
                packetImpact,
                1.0F - smoothstep01(
                        Mth.clamp(deathTicks / 18.0F, 0.0F, 1.0F)
                )
        );
        if (impact > 0.001F) {
            int extension = Math.round(impact * 13.0F);
            graphics.fill(
                    x - extension,
                    y - 1,
                    x + BAR_WIDTH + extension,
                    y,
                    withAlpha(highlightColor, Math.round(impact * 185.0F))
            );
            graphics.fill(
                    x - extension,
                    y + BAR_HEIGHT,
                    x + BAR_WIDTH + extension,
                    y + BAR_HEIGHT + 1,
                    withAlpha(0xFFFFFFFF, Math.round(impact * 115.0F))
            );
        }

        float fracture = smoothstep01(
                Mth.clamp((deathTicks - 3.0F) / 15.0F, 0.0F, 1.0F)
        );
        float fractureFade = 1.0F - smoothstep01(
                Mth.clamp((deathTicks - 88.0F) / 28.0F, 0.0F, 1.0F)
        );
        int fractureAlpha = Math.round(fracture * fractureFade * 205.0F);
        if (fractureAlpha <= 0) {
            return;
        }

        int fractureColor = withAlpha(0xFFE8E1F0, fractureAlpha);
        int center = x + BAR_WIDTH / 2;
        graphics.fill(center - 1, y, center + 1, y + BAR_HEIGHT, fractureColor);
        graphics.fill(center - 16, y + 1, center - 4, y + 2, fractureColor);
        graphics.fill(center + 5, y + 3, center + 21, y + 4, fractureColor);
        graphics.fill(x + BAR_WIDTH / 4, y + 2, x + BAR_WIDTH / 4 + 9, y + 3, fractureColor);
        graphics.fill(x + BAR_WIDTH * 3 / 4 - 7, y + 1, x + BAR_WIDTH * 3 / 4, y + 2, fractureColor);
    }

    private static void drawHudRevealImpact(
            GuiGraphicsExtractor graphics,
            int centerX,
            int slotY,
            int color,
            float progress
    ) {
        float expansion =
                easeOutQuint(progress);

        float remaining =
                1.0F - progress;

        int halfWidth =
                Math.round(
                        (BAR_WIDTH / 2.0F + 14.0F)
                                * expansion
                );

        int glowAlpha =
                Mth.clamp(
                        Math.round(remaining * 105.0F),
                        0,
                        105
                );

        graphics.fill(
                centerX - halfWidth,
                slotY - 1,
                centerX + halfWidth,
                slotY + BAR_HEIGHT + 1,
                withAlpha(color, glowAlpha)
        );

        if (progress > 0.15F
                && progress < 0.72F) {
            float sweepProgress =
                    (progress - 0.15F)
                            / 0.57F;

            int sweepX =
                    centerX - BAR_WIDTH / 2
                            + Math.round(BAR_WIDTH * sweepProgress);

            graphics.fill(
                    sweepX - 5,
                    slotY,
                    sweepX + 5,
                    slotY + 1,
                    withAlpha(0xFFFFFFFF, 145)
            );
        }
    }

    private static void drawVanillaReservationCover(
            GuiGraphicsExtractor graphics,
            int slotY
    ) {
        int centerX =
                graphics.guiWidth()
                        / 2;

        int vanillaX =
                centerX
                        - VANILLA_BAR_WIDTH
                        / 2;

        graphics.fill(
                vanillaX - 3,
                slotY - 1,
                vanillaX + VANILLA_BAR_WIDTH + 3,
                slotY + BAR_HEIGHT + 1,
                0xFF09070D
        );
    }

    private static void drawTitle(
            GuiGraphicsExtractor graphics,
            Font font,
            Component title,
            int slotY,
            BossHudState state,
            long now
    ) {
        int centerX =
                graphics.guiWidth()
                        / 2;

        int titleY =
                Math.max(
                        2,
                        slotY
                                - TITLE_OFFSET
                );

        int width =
                font.width(
                        title
                );

        int x =
                centerX
                        - width
                        / 2;

        int color =
                0xFFF4ECFF;

        if (state.isPhaseTransitionActive(now)) {
            float remaining =
                    state.phaseTransitionRemaining(
                            now
                    );

            int brightness =
                    Mth.clamp(
                            235
                                    + Math.round(
                                    remaining
                                            * 20.0F
                            ),
                            0,
                            255
                    );

            color =
                    0xFF000000
                            | brightness << 16
                            | brightness << 8
                            | brightness;
        }

        graphics.text(
                font,
                title,
                x + 1,
                titleY + 1,
                0xA0000000
        );

        graphics.text(
                font,
                title,
                x,
                titleY,
                color
        );
    }

    private static void drawCompactGlow(
            GuiGraphicsExtractor graphics,
            int x,
            int y,
            int color,
            boolean healing,
            boolean finalRegen,
            BossHudState state,
            long now
    ) {
        int alpha =
                healing
                        ? 62
                        : 28;

        if (healing) {
            float pulse =
                    regenerationPulse(
                            now
                    );

            alpha +=
                    Math.round(
                            pulse
                                    * (
                                    finalRegen
                                            ? 55.0F
                                            : 38.0F
                            )
                    );
        }

        if (state.isPhaseTransitionActive(now)) {
            alpha +=
                    Math.round(
                            state.phaseTransitionRemaining(now)
                                    * 55.0F
                    );
        }

        graphics.fill(
                x - 7,
                y - 1,
                x + BAR_WIDTH + 7,
                y + BAR_HEIGHT + 1,
                withAlpha(
                        color,
                        Mth.clamp(
                                alpha,
                                0,
                                145
                        )
                )
        );
    }

    private static void drawFrame(
            GuiGraphicsExtractor graphics,
            int x,
            int y,
            boolean healing,
            boolean finalRegen,
            long now
    ) {
        int rimColor =
                0xFF4A3454;

        if (healing) {
            float pulse =
                    regenerationPulse(
                            now
                    );

            int gold =
                    Mth.clamp(
                            120
                                    + Math.round(
                                    pulse
                                            * (
                                            finalRegen
                                                    ? 100.0F
                                                    : 70.0F
                                    )
                            ),
                            0,
                            255
                    );

            rimColor =
                    0xFF000000
                            | gold << 16
                            | Mth.clamp(
                            gold - 35,
                            0,
                            255
                    ) << 8
                            | 40;
        }

        graphics.fill(
                x - 3,
                y - 1,
                x + BAR_WIDTH + 3,
                y + BAR_HEIGHT + 1,
                0xF4110B16
        );

        graphics.fill(
                x - 2,
                y,
                x + BAR_WIDTH + 2,
                y + BAR_HEIGHT,
                rimColor
        );

        graphics.fill(
                x - 1,
                y,
                x + BAR_WIDTH + 1,
                y + BAR_HEIGHT,
                0xFF09070D
        );

        graphics.fill(
                x,
                y + 1,
                x + BAR_WIDTH,
                y + BAR_HEIGHT - 1,
                0xFF160F1B
        );
    }

    private static void drawHealth(
            GuiGraphicsExtractor graphics,
            int x,
            int y,
            float visualProgress,
            float trailProgress,
            int mainColor,
            int highlightColor,
            int shadowColor,
            BossHudState state,
            long now
    ) {
        int healthWidth =
                Mth.clamp(
                        Math.round(
                                BAR_WIDTH
                                        * visualProgress
                        ),
                        0,
                        BAR_WIDTH
                );

        int trailWidth =
                Mth.clamp(
                        Math.round(
                                BAR_WIDTH
                                        * trailProgress
                        ),
                        0,
                        BAR_WIDTH
                );

        if (trailWidth > healthWidth) {
            graphics.fill(
                    x + healthWidth,
                    y + 1,
                    x + trailWidth,
                    y + BAR_HEIGHT - 1,
                    0xECF43D56
            );

            graphics.fill(
                    x + healthWidth,
                    y + 1,
                    x + trailWidth,
                    y + 2,
                    0xFFFFDDE2
            );
        }

        if (healthWidth > 0) {
            graphics.fill(
                    x,
                    y + 1,
                    x + healthWidth,
                    y + BAR_HEIGHT - 1,
                    mainColor
            );

            graphics.fill(
                    x,
                    y + 1,
                    x + healthWidth,
                    y + 2,
                    highlightColor
            );

            graphics.fill(
                    x,
                    y + BAR_HEIGHT - 2,
                    x + healthWidth,
                    y + BAR_HEIGHT - 1,
                    shadowColor
            );

            if (healthWidth > 1) {
                graphics.fill(
                        x + healthWidth - 1,
                        y + 1,
                        x + healthWidth,
                        y + BAR_HEIGHT - 1,
                        highlightColor
                );
            }
        }

        if (state.isDamageFlashActive(now)
                && healthWidth > 0) {

            float remaining =
                    state.damageFlashRemaining(
                            now
                    );

            int alpha =
                    Mth.clamp(
                            Math.round(
                                    remaining
                                            * 180.0F
                            ),
                            0,
                            180
                    );

            graphics.fill(
                    x,
                    y + 1,
                    x + healthWidth,
                    y + BAR_HEIGHT - 1,
                    withAlpha(
                            0xFFFFFFFF,
                            alpha
                    )
            );
        }
    }

    private static void drawRemainingThresholds(
            GuiGraphicsExtractor graphics,
            StarCrawlerBossEntity boss,
            int x,
            int y
    ) {
        CombatPhase phase =
                boss.getPhase();

        CombatState state =
                boss.getCombatState();

        boolean firstComplete =
                phase.isAtLeast(
                        CombatPhase.PHASE_2
                )
                        || state
                        == CombatState.HEALING_PHASE_2
                        || state
                        == CombatState.HEALING_PHASE_3;

        boolean secondComplete =
                phase.isAtLeast(
                        CombatPhase.PHASE_3
                )
                        || state
                        == CombatState.HEALING_PHASE_3;

        if (!firstComplete) {
            drawThreshold(
                    graphics,
                    x + Math.round(
                            BAR_WIDTH
                                    * 0.50F
                    ),
                    y
            );
        }

        if (!secondComplete) {
            drawThreshold(
                    graphics,
                    x + Math.round(
                            BAR_WIDTH
                                    * 0.25F
                    ),
                    y
            );
        }
    }

    private static void drawThreshold(
            GuiGraphicsExtractor graphics,
            int x,
            int y
    ) {
        graphics.fill(
                x - 1,
                y,
                x + 2,
                y + BAR_HEIGHT,
                0xB0000000
        );

        graphics.fill(
                x,
                y,
                x + 1,
                y + BAR_HEIGHT,
                0xFFF2EAF7
        );
    }

    private static int calculateShakeX(
            BossHudState state,
            long now
    ) {
        float strength =
                0.0F;

        if (state.isDamageShakeActive(now)) {
            float remaining =
                    state.damageShakeRemaining(
                            now
                    );

            strength +=
                    state.damageShakeStrength
                            * remaining
                            * remaining;
        }

        if (state.isPhaseTransitionActive(now)) {
            strength +=
                    state.phaseTransitionRemaining(now)
                            * 4.25F;
        }

        if (strength <= 0.01F) {
            return 0;
        }

        double waveOne =
                Math.sin(
                        now
                                * 0.091D
                );

        double waveTwo =
                Math.sin(
                        now
                                * 0.197D
                                + 1.8D
                );

        return (int) Math.round(
                (
                        waveOne
                                + waveTwo
                                * 0.47D
                )
                        * strength
        );
    }

    private static int calculateShakeY(
            BossHudState state,
            long now
    ) {

        float strength =
                0.0F;

        if (state.isDamageShakeActive(now)) {
            strength +=
                    state.damageShakeRemaining(now)
                            * 1.0F;
        }

        if (state.isPhaseTransitionActive(now)) {
            strength +=
                    state.phaseTransitionRemaining(now)
                            * 0.8F;
        }

        if (strength <= 0.35F) {
            return 0;
        }

        int result =
                (int) Math.round(
                        Math.cos(
                                now
                                        * 0.151D
                        )
                                * strength
                );

        return Mth.clamp(
                result,
                -1,
                1
        );
    }

    private static void drawRegenerationAnimation(
            GuiGraphicsExtractor graphics,
            StarCrawlerBossEntity boss,
            BossHudState state,
            int x,
            int y,
            float visualProgress,
            boolean finalRegen,
            long now
    ) {
        int filledWidth =
                Mth.clamp(
                        Math.round(
                                BAR_WIDTH
                                        * visualProgress
                        ),
                        0,
                        BAR_WIDTH
                );

        float pulse =
                regenerationPulse(
                        now
                );

        int auraAlpha =
                Mth.clamp(
                        Math.round(
                                (
                                        finalRegen
                                                ? 95.0F
                                                : 70.0F
                                )
                                        + pulse
                                        * (
                                        finalRegen
                                                ? 100.0F
                                                : 70.0F
                                )
                        ),
                        0,
                        210
                );

        graphics.fill(
                x,
                y,
                x + BAR_WIDTH,
                y + 1,
                withAlpha(
                        0xFFFFD85A,
                        auraAlpha
                )
        );

        graphics.fill(
                x,
                y + BAR_HEIGHT - 1,
                x + BAR_WIDTH,
                y + BAR_HEIGHT,
                withAlpha(
                        finalRegen
                                ? 0xFFFFF0A8
                                : 0xFFFFB93D,
                        auraAlpha
                )
        );

        int actualWidth =
                Mth.clamp(
                        Math.round(
                                BAR_WIDTH
                                        * (
                                        boss.getHealth()
                                                / boss.getMaxHealth()
                                )
                        ),
                        0,
                        BAR_WIDTH
                );

        if (actualWidth > filledWidth) {
            int incomingAlpha =
                    finalRegen
                            ? 150
                            : 115;

            graphics.fill(
                    x + filledWidth,
                    y + 1,
                    x + actualWidth,
                    y + BAR_HEIGHT - 1,
                    withAlpha(
                            0xFFFFF0A4,
                            incomingAlpha
                    )
            );
        }

        if (filledWidth > 0) {
            int sweepWidth =
                    finalRegen
                            ? 18
                            : 14;

            int sweepTravel =
                    filledWidth
                            + sweepWidth;

            int sweepX =
                    (int) (
                            (now
                                    / REGEN_SWEEP_ONE_SPEED)
                                    % Math.max(
                                    1,
                                    sweepTravel
                            )
                    )
                            - sweepWidth;

            drawClippedSweep(
                    graphics,
                    x,
                    y,
                    filledWidth,
                    sweepX,
                    sweepWidth,
                    finalRegen
                            ? 0xEFFFFFFF
                            : 0xDFFFF5C2
            );

            int reversePosition =
                    (int) (
                            (now
                                    / REGEN_SWEEP_TWO_SPEED)
                                    % Math.max(
                                    1,
                                    sweepTravel
                            )
                    );

            int reverseX =
                    filledWidth
                            - reversePosition;

            drawClippedSweep(
                    graphics,
                    x,
                    y,
                    filledWidth,
                    reverseX,
                    9,
                    finalRegen
                            ? 0xAAFFF0A0
                            : 0x88FFD35A
            );
        }

        if (filledWidth > 3) {
            int sparkCount =
                    finalRegen
                            ? 5
                            : 3;

            for (int i = 0;
                 i < sparkCount;
                 i++) {

                long speed =
                        9L
                                + i * 3L;

                int sparkX =
                        (int) (
                                (
                                        now / speed
                                                + i
                                                * 47L
                                )
                                        % filledWidth
                        );

                float individualPulse =
                        0.5F
                                + 0.5F
                                * (float) Math.sin(
                                now
                                        * (
                                        0.018D
                                                + i
                                                * 0.004D
                                )
                                        + i
                                        * 1.7D
                        );

                int alpha =
                        Mth.clamp(
                                Math.round(
                                        100.0F
                                                + individualPulse
                                                * 155.0F
                                ),
                                0,
                                255
                        );

                graphics.fill(
                        x + sparkX,
                        y + 1,
                        x + sparkX + 1,
                        y + BAR_HEIGHT - 1,
                        withAlpha(
                                finalRegen
                                        ? 0xFFFFFFFF
                                        : 0xFFFFF0B0,
                                alpha
                        )
                );
            }
        }

        if (filledWidth > 0) {
            int tipAlpha =
                    Mth.clamp(
                            Math.round(
                                    145.0F
                                            + pulse
                                            * 110.0F
                            ),
                            0,
                            255
                    );

            graphics.fill(
                    x + filledWidth - 1,
                    y,
                    x + filledWidth + 1,
                    y + BAR_HEIGHT,
                    withAlpha(
                            finalRegen
                                    ? 0xFFFFFFFF
                                    : 0xFFFFF0A8,
                            tipAlpha
                    )
            );
        }

        if (state.isRegenStartActive(now)) {
            drawRegenStartSurge(
                    graphics,
                    x,
                    y,
                    state,
                    finalRegen,
                    now
            );
        }
    }

    private static void drawClippedSweep(
            GuiGraphicsExtractor graphics,
            int barX,
            int y,
            int filledWidth,
            int localX,
            int sweepWidth,
            int color
    ) {
        int left =
                Math.max(
                        0,
                        localX
                );

        int right =
                Math.min(
                        filledWidth,
                        localX
                                + sweepWidth
                );

        if (right <= left) {
            return;
        }

        graphics.fill(
                barX + left,
                y + 1,
                barX + right,
                y + BAR_HEIGHT - 1,
                color
        );
    }

    private static void drawRegenStartSurge(
            GuiGraphicsExtractor graphics,
            int x,
            int y,
            BossHudState state,
            boolean finalRegen,
            long now
    ) {
        float elapsed =
                state.regenStartElapsed(
                        now
                );

        float remaining =
                1.0F
                        - elapsed;

        float spread =
                1.0F
                        - (float) Math.pow(
                        1.0F - elapsed,
                        3.0D
                );

        int halfWidth =
                Math.round(
                        BAR_WIDTH
                                * 0.5F
                                * spread
                );

        int center =
                x
                        + BAR_WIDTH
                        / 2;

        int alpha =
                Mth.clamp(
                        Math.round(
                                remaining
                                        * (
                                        finalRegen
                                                ? 255.0F
                                                : 220.0F
                                )
                        ),
                        0,
                        255
                );

        int railColor =
                withAlpha(
                        finalRegen
                                ? 0xFFFFFFFF
                                : 0xFFFFE17A,
                        alpha
                );

        graphics.fill(
                center - halfWidth,
                y,
                center + halfWidth,
                y + 1,
                railColor
        );

        graphics.fill(
                center - halfWidth,
                y + BAR_HEIGHT - 1,
                center + halfWidth,
                y + BAR_HEIGHT,
                railColor
        );

        if (elapsed < 0.28F) {
            float flashRemaining =
                    1.0F
                            - elapsed
                            / 0.28F;

            int flashAlpha =
                    Mth.clamp(
                            Math.round(
                                    flashRemaining
                                            * (
                                            finalRegen
                                                    ? 185.0F
                                                    : 135.0F
                                    )
                            ),
                            0,
                            200
                    );

            graphics.fill(
                    x,
                    y + 1,
                    x + BAR_WIDTH,
                    y + BAR_HEIGHT - 1,
                    withAlpha(
                            finalRegen
                                    ? 0xFFFFFFFF
                                    : 0xFFFFF2BC,
                            flashAlpha
                    )
            );
        }

        if (finalRegen
                && elapsed > 0.22F
                && elapsed < 0.72F) {

            float pulseProgress =
                    (elapsed - 0.22F)
                            / 0.50F;

            int pulseHalf =
                    Math.round(
                            BAR_WIDTH
                                    * 0.5F
                                    * pulseProgress
                    );

            int pulseAlpha =
                    Mth.clamp(
                            Math.round(
                                    (
                                            1.0F
                                                    - pulseProgress
                                    )
                                            * 190.0F
                            ),
                            0,
                            190
                    );

            int pulseColor =
                    withAlpha(
                            0xFFFFFFFF,
                            pulseAlpha
                    );

            graphics.fill(
                    center - pulseHalf,
                    y + 1,
                    center - pulseHalf + 1,
                    y + BAR_HEIGHT - 1,
                    pulseColor
            );

            graphics.fill(
                    center + pulseHalf - 1,
                    y + 1,
                    center + pulseHalf,
                    y + BAR_HEIGHT - 1,
                    pulseColor
            );
        }
    }

    private static float regenerationPulse(
            long now
    ) {
        return 0.5F
                + 0.5F
                * (float) Math.sin(
                now
                        * REGEN_PULSE_SPEED
        );
    }

    private static void drawPhaseTransitionAnimation(
            GuiGraphicsExtractor graphics,
            int x,
            int y,
            int phaseColor,
            BossHudState state,
            long now
    ) {
        if (!state.isPhaseTransitionActive(now)) {
            return;
        }

        float elapsed =
                state.phaseTransitionElapsed(
                        now
                );

        float remaining =
                1.0F
                        - elapsed;


        int extension =
                Math.round(
                        elapsed
                                * 18.0F
                );

        int alpha =
                Mth.clamp(
                        Math.round(
                                remaining
                                        * 220.0F
                        ),
                        0,
                        220
                );

        int color =
                withAlpha(
                        phaseColor,
                        alpha
                );

        graphics.fill(
                x - extension,
                y,
                x + BAR_WIDTH + extension,
                y + 1,
                color
        );

        graphics.fill(
                x - extension,
                y + BAR_HEIGHT - 1,
                x + BAR_WIDTH + extension,
                y + BAR_HEIGHT,
                color
        );

        if (elapsed < 0.28F) {
            int flashAlpha =
                    Mth.clamp(
                            Math.round(
                                    (
                                            1.0F
                                                    - elapsed
                                                    / 0.28F
                                    )
                                            * 160.0F
                            ),
                            0,
                            160
                    );

            graphics.fill(
                    x,
                    y + 1,
                    x + BAR_WIDTH,
                    y + BAR_HEIGHT - 1,
                    withAlpha(
                            0xFFFFFFFF,
                            flashAlpha
                    )
            );
        }
    }

    private static int getMainColor(
            CombatPhase phase,
            boolean healing
    ) {
        if (healing) {
            return 0xFFFFB72E;
        }

        return switch (phase) {
            case PHASE_1 ->
                    0xFF8D45D6;

            case PHASE_2 ->
                    0xFF27AACC;

            case PHASE_3 ->
                    0xFFE13E57;
        };
    }

    private static int getHighlightColor(
            CombatPhase phase,
            boolean healing
    ) {
        if (healing) {
            return 0xFFFFE687;
        }

        return switch (phase) {
            case PHASE_1 ->
                    0xFFC57CFF;

            case PHASE_2 ->
                    0xFF6CE8FF;

            case PHASE_3 ->
                    0xFFFF7B8D;
        };
    }

    private static int getShadowColor(
            CombatPhase phase,
            boolean healing
    ) {
        if (healing) {
            return 0xFFB86B16;
        }

        return switch (phase) {
            case PHASE_1 ->
                    0xFF55227E;

            case PHASE_2 ->
                    0xFF17647A;

            case PHASE_3 ->
                    0xFF861D31;
        };
    }

    private static long now() {
        return System.nanoTime()
                / 1_000_000L;
    }

    private static float smoothingAlpha(
            long elapsedMillis,
            float responseMillis
    ) {
        if (elapsedMillis <= 0L) {
            return 0.0F;
        }

        return (float) (
                1.0D
                        - Math.exp(
                        -elapsedMillis
                                / responseMillis
                )
        );
    }

    private static float easeOutQuint(float value) {
        float inverse =
                1.0F - Mth.clamp(value, 0.0F, 1.0F);

        return 1.0F
                - inverse * inverse * inverse * inverse * inverse;
    }

    private static float smoothstep01(float value) {
        float t = Mth.clamp(value, 0.0F, 1.0F);
        return t * t * (3.0F - 2.0F * t);
    }

    private static int withAlpha(
            int color,
            int alpha
    ) {
        return (
                Mth.clamp(
                        alpha,
                        0,
                        255
                ) << 24
        )
                | (
                color
                        & 0x00FFFFFF
        );
    }

    private static final class BossHudState {

        private float observedHealth;
        private float visualHealth;
        private float trailHealth;

        private CombatPhase observedPhase;
        private CombatState observedCombatState;
        private IntroState observedIntroState;
        private DeathCinematicState observedDeathCinematicState;

        private long lastRenderTime;
        private long lastSeenTime;

        private long damageStartTime;
        private long damageEndTime;
        private long damageFlashEndTime;

        private float damageShakeStrength;

        private long phaseTransitionStartTime;
        private long phaseTransitionEndTime;

        private long regenStartTime;
        private long regenStartEndTime;

        private long hudRevealStartTime;
        private long hudRevealEndTime;

        private BossHudState(
                StarCrawlerBossEntity boss,
                long now
        ) {
            this.observedHealth =
                    boss.getHealth();

            this.visualHealth =
                    boss.getHealth();

            this.trailHealth =
                    boss.getHealth();

            this.observedPhase =
                    boss.getPhase();

            this.observedCombatState =
                    boss.getCombatState();

            this.observedIntroState =
                    boss.getIntroState();

            this.observedDeathCinematicState =
                    boss.getDeathCinematicState();

            this.lastRenderTime =
                    now;

            this.lastSeenTime =
                    now;
        }

        private void update(
                StarCrawlerBossEntity boss,
                long now
        ) {
            this.lastSeenTime =
                    now;

            float currentHealth =
                    boss.getHealth();

            CombatPhase currentPhase =
                    boss.getPhase();

            CombatState currentCombatState =
                    boss.getCombatState();

            IntroState currentIntroState =
                    boss.getIntroState();

            DeathCinematicState currentDeathCinematicState =
                    boss.getDeathCinematicState();

            if (this.observedDeathCinematicState != DeathCinematicState.DYING
                    && currentDeathCinematicState == DeathCinematicState.DYING) {
                this.trailHealth = Math.max(this.trailHealth, this.observedHealth);
                this.damageStartTime = now;
                this.damageEndTime = now + 650L;
                this.damageFlashEndTime = now + 380L;
                this.damageShakeStrength = 9.0F;
            }

            if (this.observedIntroState != IntroState.COMPLETE
                    && currentIntroState == IntroState.COMPLETE) {

                this.hudRevealStartTime =
                        now;

                this.hudRevealEndTime =
                        now + HUD_REVEAL_DURATION_MS;
            }

            // DAMAGE

            if (currentHealth
                    < this.observedHealth - 0.01F
                    && currentDeathCinematicState != DeathCinematicState.DYING) {

                float damageTaken =
                        this.observedHealth
                                - currentHealth;

                this.trailHealth =
                        Math.max(
                                this.trailHealth,
                                this.observedHealth
                        );

                this.damageStartTime =
                        now;

                this.damageEndTime =
                        now
                                + DAMAGE_SHAKE_DURATION_MS;

                this.damageFlashEndTime =
                        now
                                + DAMAGE_FLASH_DURATION_MS;

                this.damageShakeStrength =
                        Mth.clamp(
                                2.2F
                                        + damageTaken
                                        / 5.0F,
                                2.2F,
                                7.5F
                        );
            }

            // PHASE CHANGE

            if (this.observedPhase != null
                    && currentPhase
                    != this.observedPhase) {

                this.phaseTransitionStartTime =
                        now;

                this.phaseTransitionEndTime =
                        now
                                + PHASE_TRANSITION_DURATION_MS;
            }

            // REGEN START

            if (this.observedCombatState != null
                    && currentCombatState
                    != this.observedCombatState
                    && currentCombatState.isHealing()) {

                this.regenStartTime =
                        now;

                this.regenStartEndTime =
                        now
                                + REGEN_START_DURATION_MS;
            }

            // HEALTH

            long elapsed =
                    Math.max(
                            0L,
                            Math.min(
                                    100L,
                                    now
                                            - this.lastRenderTime
                            )
                    );

            float response =
                    currentCombatState.isHealing()
                            ? REGEN_HEALTH_RESPONSE_MS
                            : NORMAL_HEALTH_RESPONSE_MS;

            float visualAlpha =
                    smoothingAlpha(
                            elapsed,
                            response
                    );

            this.visualHealth =
                    Mth.lerp(
                            visualAlpha,
                            this.visualHealth,
                            currentHealth
                    );

            if (this.trailHealth
                    > this.visualHealth) {

                float trailAlpha =
                        smoothingAlpha(
                                elapsed,
                                DAMAGE_TRAIL_RESPONSE_MS
                        );

                this.trailHealth =
                        Mth.lerp(
                                trailAlpha,
                                this.trailHealth,
                                currentHealth
                        );
            }

            if (currentCombatState.isHealing()
                    && this.trailHealth
                    < this.visualHealth) {

                this.trailHealth =
                        this.visualHealth;
            }

            if (Math.abs(
                    this.visualHealth
                            - currentHealth
            ) < 0.01F) {

                this.visualHealth =
                        currentHealth;
            }

            if (Math.abs(
                    this.trailHealth
                            - currentHealth
            ) < 0.01F) {

                this.trailHealth =
                        currentHealth;
            }

            this.observedHealth =
                    currentHealth;

            this.observedPhase =
                    currentPhase;

            this.observedCombatState =
                    currentCombatState;

            this.observedIntroState =
                    currentIntroState;

            this.observedDeathCinematicState =
                    currentDeathCinematicState;

            this.lastRenderTime =
                    now;
        }

        private boolean isDamageShakeActive(
                long now
        ) {
            return now
                    < this.damageEndTime;
        }

        private float damageShakeRemaining(
                long now
        ) {
            return normalizedRemaining(
                    now,
                    this.damageStartTime,
                    this.damageEndTime
            );
        }

        private boolean isDamageFlashActive(
                long now
        ) {
            return now
                    < this.damageFlashEndTime;
        }

        private float damageFlashRemaining(
                long now
        ) {
            return normalizedRemaining(
                    now,
                    this.damageStartTime,
                    this.damageFlashEndTime
            );
        }

        private boolean isPhaseTransitionActive(
                long now
        ) {
            return now
                    < this.phaseTransitionEndTime;
        }

        private float phaseTransitionRemaining(
                long now
        ) {
            return normalizedRemaining(
                    now,
                    this.phaseTransitionStartTime,
                    this.phaseTransitionEndTime
            );
        }

        private float phaseTransitionElapsed(
                long now
        ) {
            return normalizedElapsed(
                    now,
                    this.phaseTransitionStartTime,
                    this.phaseTransitionEndTime
            );
        }

        private boolean isRegenStartActive(
                long now
        ) {
            return now
                    < this.regenStartEndTime;
        }

        private float regenStartElapsed(
                long now
        ) {
            return normalizedElapsed(
                    now,
                    this.regenStartTime,
                    this.regenStartEndTime
            );
        }

        private float hudRevealElapsed(
                long now
        ) {
            return normalizedElapsed(
                    now,
                    this.hudRevealStartTime,
                    this.hudRevealEndTime
            );
        }
    }

    private static float normalizedRemaining(
            long now,
            long start,
            long end
    ) {
        if (end <= start) {
            return 0.0F;
        }

        return Mth.clamp(
                (end - now)
                        / (float) (
                        end - start
                ),
                0.0F,
                1.0F
        );
    }

    private static float normalizedElapsed(
            long now,
            long start,
            long end
    ) {
        if (end <= start) {
            return 1.0F;
        }

        return Mth.clamp(
                (now - start)
                        / (float) (
                        end - start
                ),
                0.0F,
                1.0F
        );
    }
}
