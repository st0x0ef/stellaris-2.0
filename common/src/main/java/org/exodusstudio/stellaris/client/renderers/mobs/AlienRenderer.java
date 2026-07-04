package org.exodusstudio.stellaris.client.renderers.mobs;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.Identifier;
import org.exodusstudio.stellaris.common.entities.alien.AlienEntity;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Renders the Alien with a per-profession texture, mirroring Beyond Earth's {@code AlienRenderer}.
 * Note the vanilla profession paths {@code weaponsmith}/{@code toolsmith}/{@code leatherworker} map
 * to the {@code weapon_smith}/{@code tool_smith}/{@code leather_worker} textures.
 */
public class AlienRenderer extends MobRenderer<AlienEntity, AlienRenderState, AlienModel> {

    private static final Identifier DEFAULT_TEXTURE = texture("alien");

    private static final Map<String, Identifier> TEXTURES = Map.ofEntries(
            Map.entry("farmer", texture("farmer")),
            Map.entry("fisherman", texture("fisherman")),
            Map.entry("shepherd", texture("shepherd")),
            Map.entry("fletcher", texture("fletcher")),
            Map.entry("librarian", texture("librarian")),
            Map.entry("cartographer", texture("cartographer")),
            Map.entry("cleric", texture("cleric")),
            Map.entry("armorer", texture("armorer")),
            Map.entry("weaponsmith", texture("weapon_smith")),
            Map.entry("toolsmith", texture("tool_smith")),
            Map.entry("butcher", texture("butcher")),
            Map.entry("leatherworker", texture("leather_worker")),
            Map.entry("mason", texture("mason")));

    public AlienRenderer(EntityRendererProvider.Context context) {
        super(context, new AlienModel(context.bakeLayer(AlienModel.LAYER_LOCATION)), 0.5F);
    }

    private static Identifier texture(String name) {
        return IdentifierUtils.texture("entity/alien/" + name);
    }

    @Override
    public @NotNull AlienRenderState createRenderState() {
        return new AlienRenderState();
    }

    @Override
    public void extractRenderState(AlienEntity entity, AlienRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.professionPath = entity.getVillagerData().profession().unwrapKey()
                .map(key -> key.identifier().getPath())
                .orElse("none");
    }

    @Override
    public @NotNull Identifier getTextureLocation(AlienRenderState state) {
        return TEXTURES.getOrDefault(state.professionPath, DEFAULT_TEXTURE);
    }
}
