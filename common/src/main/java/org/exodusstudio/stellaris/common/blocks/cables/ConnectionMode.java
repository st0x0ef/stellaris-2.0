package org.exodusstudio.stellaris.common.blocks.cables;

import net.minecraft.util.StringRepresentable;

public enum ConnectionMode implements StringRepresentable {
    NORMAL("normal"),     // Passive network link (connects to cables & machines)
    PULL("pull"),         // Actively pulls resources from adjacent machines
    PUSH("push"),         // Actively pushes resources to adjacent machines
    DISABLED("disabled"); // Severed face; blocks all connections and rendering

    private final String name;

    ConnectionMode(String name) {
        this.name = name;
    }

    public ConnectionMode next() {
        ConnectionMode[] values = values();
        return values[(this.ordinal() + 1) % values.length];
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
