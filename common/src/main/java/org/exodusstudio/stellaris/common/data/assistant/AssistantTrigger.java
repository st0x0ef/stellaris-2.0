package org.exodusstudio.stellaris.common.data.assistant;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum AssistantTrigger implements StringRepresentable {
    /** The player just landed on a planet they had never set foot on before. */
    PLANET_FIRST_VISIT("planet_first_visit"),
    /** The player just caught the parasite infection for the first time. */
    INFECTED("infected"),
    /** A laboratory research moved the player up a tier. Args: {@code %1$s} tier reached, {@code %2$s} parasites needed for the next one. */
    RESEARCH_SUCCESS("research_success"),
    /** A laboratory research failed. Args: {@code %1$s} tier being worked on, {@code %2$s} parasites it needs. */
    RESEARCH_FAILURE("research_failure"),
    /** A laboratory research happened with every tier already done. No args. */
    RESEARCH_COMPLETE("research_complete");

    public static final Codec<AssistantTrigger> CODEC = StringRepresentable.fromEnum(AssistantTrigger::values);

    private final String name;

    AssistantTrigger(String name) {
        this.name = name;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name;
    }
}
