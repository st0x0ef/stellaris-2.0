package org.exodusstudio.stellaris.common.items.modules.rocket;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.apache.commons.lang3.StringUtils;
import org.exodusstudio.stellaris.client.renderers.rockets.RocketRenderer;
import org.exodusstudio.stellaris.client.renderers.rockets.models.RocketModel;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.utils.Utils;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

public class RocketModelModuleItem<T extends RocketModel> extends Item implements RocketModule.CustomModelModule {
    private final Class<T> model;
    private T bakedModel;
    private final String name;
    private final float playerYOffset;

    public RocketModelModuleItem(Properties properties, Class<T> rocketModel, String name, float playerYOffset) {
        super(properties);
        this.model = rocketModel;
        this.name = name;
        this.playerYOffset = playerYOffset;
    }

    @Override
    public RocketFeature getRocketFeature() {
        return RocketFeature.MODEL;
    }

    @Override
    public String getDisplayName() {
        return StringUtils.capitalize(name) + " Rocket Model";
    }

    public T getRocketModel() {
        try {
            if (this.bakedModel == null) {
                this.bakedModel = model.getDeclaredConstructor(EntityModelSet.class).newInstance(Minecraft.getInstance().getEntityModels());
            }

            return this.bakedModel;
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    public String getModelName() {
        return name;
    }

    @Override
    public void preRenderModel(SubmitNodeCollector nodeCollector, PoseStack poseStack, RocketRenderer.RenderingContext context, RenderType renderType) {
        RocketModel rocketModel = getRocketModel();
        rocketModel.setDefaultModel();
        context.setRocketModel(rocketModel);
        nodeCollector.submitModelPart(rocketModel.root(), poseStack, renderType, context.packedLight, OverlayTexture.NO_OVERLAY, null);
    }



    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        tooltipAdder.accept(Component.translatable("tooltip.item.stellaris.can_be_applied_to_rocket_module").withColor(Utils.getMinecraftColor("gray")));
    }

    @Override
    public float getPlayerYOffset() {
        return this.playerYOffset;
    }
}
