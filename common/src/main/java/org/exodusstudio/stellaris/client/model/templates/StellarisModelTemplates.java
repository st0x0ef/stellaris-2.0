package org.exodusstudio.stellaris.client.model.templates;

import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;

import java.util.Optional;

@SuppressWarnings("unused")
public class StellarisModelTemplates {
    public static final ModelTemplate ICICLE = create("template_icicle", TextureSlot.CROSS);
    public static final ModelTemplate ICICLE_BASE = create("template_icicle_base", TextureSlot.CROSS, TextureSlot.TOP, TextureSlot.SIDE);

    private static ModelTemplate create(final TextureSlot... slots) {
        return new ModelTemplate(Optional.empty(), Optional.empty(), slots);
    }

    private static ModelTemplate create(final String id, final TextureSlot... slots) {
        return new ModelTemplate(Optional.of(IdentifierUtils.id("block/" + id)), Optional.empty(), slots);
    }

    private static ModelTemplate createItem(final String id, final TextureSlot... slots) {
        return new ModelTemplate(Optional.of(IdentifierUtils.id("item/" + id)), Optional.empty(), slots);
    }

    private static ModelTemplate createItem(final String id, final String suffix, final TextureSlot... slots) {
        return new ModelTemplate(Optional.of(IdentifierUtils.id("item/" + id)), Optional.of(suffix), slots);
    }

    private static ModelTemplate create(final String id, final String suffix, final TextureSlot... slots) {
        return new ModelTemplate(Optional.of(IdentifierUtils.id("block/" + id)), Optional.of(suffix), slots);
    }
}
