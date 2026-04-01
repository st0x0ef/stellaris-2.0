package org.exodusstudio.stellaris.common.items.modules.rocket;

import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.utils.IdentifierUtils;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.util.function.Consumer;

public class RocketSkinModuleItem extends Item implements RocketModule {
    public static final Identifier GALAXY_SKIN_TEXTURE = IdentifierUtils.texture("entity/rocket/galaxy");


    private final Identifier texture;

    public RocketSkinModuleItem(Properties properties, final Identifier skinTexture) {
        super(properties);
        this.texture = skinTexture;
    }

    @Override
    public RocketFeature getRocketFeature() {
        return RocketFeature.SKIN;
    }

    @Override
    public RenderType getRenderType(RocketRenderer.RenderingContext context) {
        return RocketRenderer.getRenderType(texture);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_rocket_module").withColor(Utils.getMinecraftColor("gray")));
    }
}
