package org.exodusstudio.stellaris.common.compats.jade;

import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.blocks.entities.machines.base.ProgressingBlockEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ProgressView;
import snownee.jade.api.view.ViewGroup;

import java.util.List;

public enum MachineProgressProvider implements IServerExtensionProvider<ProgressView.Data>, IClientExtensionProvider<ProgressView.Data, ProgressView> {
    INSTANCE;

    private static final Identifier UID = IdentifierUtils.id("machine_progress");

    @Override
    public @Nullable List<ViewGroup<ProgressView.Data>> getGroups(Accessor<?> accessor) {
        if (!(accessor.getTarget() instanceof ProgressingBlockEntity machine)) {
            return null;
        }
        if (!machine.isInProgress()) {
            return List.of();
        }
        return List.of(new ViewGroup<>(List.of(new ProgressView.Data(machine.getProgress()))));
    }

    @Override
    public List<ClientViewGroup<ProgressView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ProgressView.Data>> groups) {
        return ClientViewGroup.map(groups, ProgressView::read, null);
    }

    @Override
    public Identifier getUid() {
        return UID;
    }
}
