package org.exodusstudio.stellaris.common.items;

import com.fej1fun.potentials.fluid.ItemFluidStorage;
import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.common.blocks.RocketLaunchPadBlock;
import org.exodusstudio.stellaris.common.entities.RocketEntity;
import org.exodusstudio.stellaris.common.modules.Modules;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModule;
import org.exodusstudio.stellaris.common.modules.rocket.RocketModules;
import org.exodusstudio.stellaris.common.registries.BlocksRegistry;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.registries.EntityTypesRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class RocketItem extends Item implements FluidProvider.ITEM {

    public RocketItem(Properties properties) {
        super(properties.component(DataComponentsRegistry.ROCKET_MODULES.get(), RocketModules.empty()));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockState = context.getLevel().getBlockState(blockpos);
        ItemStack itemStack = context.getItemInHand();

        if(blockState.is(BlocksRegistry.ROCKET_LAUNCH_PAD.block().get()) && blockState.getValue(RocketLaunchPadBlock.STAGE)) {
            //check if space is free above the launch pad. 0.3 is to avoid clipping into the block
            Vec3 vec3 = Vec3.upFromBottomCenterOf(blockpos, 0.3);
            //the size of the rocket's bounding box
            AABB aabb = EntityTypesRegistry.ROCKET.get().getDimensions().makeBoundingBox(vec3.x(), vec3.y(), vec3.z());

            if (level.noCollision(aabb)) {

                /** POS */
                int x = blockpos.getX();
                int y = blockpos.getY();
                int z = blockpos.getZ();

                /** CHECK IF NO ENTITY ON THE LAUNCH PAD */
                AABB scanAbove = new AABB(x, y, z, x + 1, y + 1, z + 1);
                List<Entity> entities = level.getEntitiesOfClass(Entity.class, scanAbove);

                if (entities.isEmpty()) {
                    if (!level.isClientSide()) {
                        RocketEntity rocket = RocketEntity.fromItemStack(level, itemStack);
                        /** SET PRE POS */
                        rocket.setPos(blockpos.getX() + 0.5D, blockpos.getY() + 1.0D, blockpos.getZ() + 0.5D);

                        //double yOffset = RocketItem.getYOffset(level, blockpos, true, rocket.getBoundingBox());
                        double yOffset = 1.7D;
                        float rocketRotation = (float) Mth.floor((Mth.wrapDegrees(context.getRotation() - 180.0F) + 45.0F) / 90.0F) * 90.0F;

                        /** SET FINAL POS */
                        rocket.setPos(new Vec3(blockpos.getX() + 0.5D, blockpos.getY() + yOffset, blockpos.getZ() + 0.5D));
                        rocket.setYRot(rocketRotation);
                        rocket.yRotO = rocket.getYRot();

                        if (level.addFreshEntity(rocket)) {
                            /** ITEM REMOVE */
                             if (!player.getAbilities().instabuild) {
                                itemStack.shrink(1);
                            }

                            /** PLACE SOUND */
                            //this.rocketPlaceSound(pos, level);

                            return InteractionResult.CONSUME;
                        }
                    }

                    return InteractionResult.SUCCESS;
                }
            }

        }

        return super.useOn(context);
    }


    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        Modules<RocketModule> modules = stack.get(DataComponentsRegistry.ROCKET_MODULES.get());

        if (modules != null && !modules.items().isEmpty()) {
            tooltipAdder.accept(Component.literal("Modules:"));
            for (RocketModule module : modules.modules) {
                // TODO: Fix module tooltips
                //tooltipAdder.accept(Component.literal("- ").append( module.displayName()).withStyle(ChatFormatting.GRAY));
            }
        } else {
            tooltipAdder.accept(Component.literal("No Modules"));
        }
        tooltipAdder.accept(Component.literal("-----------").withStyle(ChatFormatting.GRAY));

        UniversalFluidItemStorage storage = getFluidTank(stack);
        tooltipAdder.accept(Component.literal(storage.getFluidInTank(0).getAmount() + " / " + storage.getTankCapacity(0) + "mb").withStyle(ChatFormatting.GRAY));
        tooltipAdder.accept(Component.literal("Fuel: " + storage.getFluidInTank(0).getFluid().arch$registryName()).withStyle(ChatFormatting.GRAY));

    }

    @Override
    public @Nullable UniversalFluidItemStorage getFluidTank(@NotNull ItemStack stack) {
        return new ItemFluidStorage(DataComponentsRegistry.FLUID_LIST.get(), stack, 1, 3000);
    }
}
