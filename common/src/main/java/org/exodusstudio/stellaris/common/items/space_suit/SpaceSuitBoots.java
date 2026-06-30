package org.exodusstudio.stellaris.common.items.space_suit;

import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.phys.Vec3;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.components.JetComponent;
import org.exodusstudio.stellaris.common.keybinds.KeyVariables;
import org.exodusstudio.stellaris.common.modules.space_suit.SpaceSuitModule;
import org.exodusstudio.stellaris.common.registries.DataComponentsRegistry;
import org.exodusstudio.stellaris.common.utils.ModuleUtils;
import org.exodusstudio.stellaris.common.utils.Utils;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SpaceSuitBoots extends SpaceSuitItem {
    public SpaceSuitBoots(Properties properties) {
        super(properties, ArmorType.BOOTS);
    }

    public float spacePressTime = 0.0f;

    private int nextFuelCheckTick = 0;

    public static int getMode(ItemStack itemStack) {
        if (!itemStack.has(DataComponentsRegistry.JET_COMPONENT.get())) {
            itemStack.set(DataComponentsRegistry.JET_COMPONENT.get(), new JetComponent(ModeType.DISABLED));
            return ModeType.DISABLED.ordinal();
        }

        return itemStack.get(DataComponentsRegistry.JET_COMPONENT.get()).type().getMode();
    }

    public static ModeType getModeType(ItemStack itemStack) {
        return switch (SpaceSuitBoots.getMode(itemStack)) {
            case 1 -> ModeType.NORMAL;
            case 2 -> ModeType.HOVER;
            case 3 -> ModeType.ELYTRA;
            default -> ModeType.DISABLED;
        };
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel serverLevel, Entity entity, @Nullable EquipmentSlot equipmentSlot) {
        super.inventoryTick(itemStack, serverLevel, entity, equipmentSlot);

        if (entity instanceof Player player && Utils.isLivingInSpaceSuit(player)) {
            if (player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof SpaceSuitChestplate chestplate) {
                UniversalFluidItemStorage storage = chestplate.getFluidTank(player.getItemBySlot(EquipmentSlot.CHEST));
                SpaceSuitModule.JetModule jetModule = ModuleUtils.getSpaceSuitModule(player.getItemBySlot(EquipmentSlot.FEET), SpaceSuitModule.JetModule.class);

                if (storage == null) {
                    return;
                }

                /** JET SUIT FAST BOOST */
                if (player.isSprinting()) {
                    this.boost(player, 1.3, true);
                }

                /** JET SUIT SLOW BOOST */
                if (player.zza > 0 && !player.isSprinting()) {
                    this.boost(player, 0.9, false);
                }

                switch (SpaceSuitBoots.getMode(itemStack)) {
                    case 1 -> this.normalFlyModeMovement(player, storage, jetModule);
                    case 2 -> this.hoverModeMovement(player, storage, jetModule);
                    case 3 -> this.elytraModeMovement(player);
                }

                /** CALCULATE PRESS SPACE TIME */
                this.calculateSpacePressTime(player, itemStack, storage, jetModule);
            }
        }
    }

    private void normalFlyModeMovement(Player player, UniversalFluidItemStorage storage, SpaceSuitModule.JetModule jetModule) {
        if (KeyVariables.isHoldingJump(player)) {
            if (storage.getFluidInTank(0).isEmpty()) return;

            if (nextFuelCheckTick <= 0 && !player.isCreative() && !player.isSpectator()) {
                storage.drain(storage.getFluidInTank(0).copyWithAmount(jetModule.getConsumptionPerTick()), false);
                nextFuelCheckTick = Stellaris.CONFIG.spaceSuitConfig.jetFuelConsumptionInterval;
            } else {
                nextFuelCheckTick--;
            }

            player.addDeltaMovement(new Vec3(0, 0.1, 0));
            Vec3 deltaMovement = player.getDeltaMovement();
            double maxJetUpwardSpeed = Stellaris.CONFIG.spaceSuitConfig.maxJetUpwardSpeed;
            if (deltaMovement.y() > maxJetUpwardSpeed) { // Limit upward speed
                player.setDeltaMovement(new Vec3(deltaMovement.x(), maxJetUpwardSpeed, deltaMovement.z()));
            }

            player.hurtMarked = true;
            player.resetFallDistance();
            Utils.disableFlyAntiCheat(player);
        } else if (!player.isCrouching()) {
            Vec3 vec3 = player.getDeltaMovement();
            if (vec3.y() > 0) {
                player.setDeltaMovement(new Vec3(vec3.x, vec3.y - 0.03, vec3.z)); // Slow down upward movement when not holding jump
            } else {
                player.setDeltaMovement(new Vec3(vec3.x, 0, vec3.z));
            }
            player.hurtMarked = true;
        }

        if (!player.onGround()) {
            if (KeyVariables.isHoldingUp(player)) {
                player.moveRelative(1.0F, new Vec3(0, 0, 0.03));
                player.hurtMarked = true;
            } else if (KeyVariables.isHoldingDown(player)) {
                player.moveRelative(1.0F, new Vec3(0, 0, -0.03));
                player.hurtMarked = true;
            }

            if (KeyVariables.isHoldingRight(player)) {
                player.moveRelative(1.0F, new Vec3(-0.03, 0, 0));
                player.hurtMarked = true;
            } else if (KeyVariables.isHoldingLeft(player)) {
                player.moveRelative(1.0F, new Vec3(0.03, 0, 0));
                player.hurtMarked = true;
            }
        }
    }
    private void hoverModeMovement(Player player, UniversalFluidItemStorage storage, SpaceSuitModule.JetModule jetModule) {
        Vec3 vec3 = player.getDeltaMovement();

        // Main movement logic
        if (!player.onGround() && !player.isInWater() && !player.isInLava() && KeyVariables.isHoldingJump(player)) {
            if (storage.getFluidInTank(0).isEmpty()) return;

            if (nextFuelCheckTick <= 0 && !player.isCreative() && !player.isSpectator()) {
                storage.drain(storage.getFluidInTank(0).copyWithAmount(jetModule.getConsumptionPerTick()), false);
                nextFuelCheckTick = Stellaris.CONFIG.spaceSuitConfig.jetFuelConsumptionInterval;
            } else {
                nextFuelCheckTick--;
            }


            player.setDeltaMovement(vec3.x, vec3.y + 0.04, vec3.z);
            player.hurtMarked = true;
            player.resetFallDistance();
            Utils.disableFlyAntiCheat(player);
        }

        // Move down
        if (player.isCrouching()) {
            player.moveRelative(0.05F, new Vec3(0, -0.08, 0));
            player.hurtMarked = true;
        }

        // Move forward and backward
        if (!player.onGround()) {
            if (KeyVariables.isHoldingUp(player)) {
                player.moveRelative(0.1F, new Vec3(0, 0, 0.1));
                player.hurtMarked = true;
            } else if (KeyVariables.isHoldingDown(player)) {
                player.moveRelative(0.1F, new Vec3(0, 0, -0.1));
                player.hurtMarked = true;
            }
        }

        // Move sideways
        if (!player.onGround()) {
            if (KeyVariables.isHoldingRight(player)) {
                player.moveRelative(0.1F, new Vec3(-0.1, 0, 0));
                player.hurtMarked = true;
            } else if (KeyVariables.isHoldingLeft(player)) {
                player.moveRelative(0.1F, new Vec3(0.1, 0, 0));
                player.hurtMarked = true;
            }
        }
    }

    private void elytraModeMovement(Player player) {
        if (player.isSprinting() && !player.onGround()) {
            player.startFallFlying();
            Utils.disableFlyAntiCheat(player);
        } else if (player.isSprinting() && player.onGround() && KeyVariables.isHoldingJump(player)) {
            player.move(MoverType.SELF, new Vec3(player.getX(), player.getY() + 2, player.getZ()));
            player.hurtMarked = true;
        }
    }


    public static void switchJetSuitMode(ItemStack itemStack) {
        if (itemStack.getItem() instanceof SpaceSuitBoots) {
            JetComponent jetComponent;
            if (getMode(itemStack) < 3) {
                jetComponent = new JetComponent(ModeType.fromInt(getMode(itemStack) + 1));
            } else {
                jetComponent = new JetComponent(ModeType.fromInt(0));
            }
            itemStack.set(DataComponentsRegistry.JET_COMPONENT.get(), jetComponent);
        }
    }

    public void calculateSpacePressTime(Player player, ItemStack itemStack, UniversalFluidItemStorage storage, SpaceSuitModule.JetModule jetModule) {
        int mode = getMode(itemStack);

        /** NORMAL MODE */
        if (mode == ModeType.NORMAL.getMode()) {
            if (KeyVariables.isHoldingJump(player)) {
                if (this.spacePressTime < 2.2F) {this.spacePressTime = this.spacePressTime + 0.2F;
                }
            }
            else if (this.spacePressTime > 0.0F) {
                this.spacePressTime = this.spacePressTime - 0.2F;
            }
        }

        /** HOVER MODE */
        if (mode == ModeType.HOVER.getMode()) {
            if (!player.onGround() && this.spacePressTime < 0.6F) {
                this.spacePressTime = this.spacePressTime + 0.2F;
            }
            else if (KeyVariables.isHoldingJump(player)) {
                if (this.spacePressTime < 1.4F) {
                    this.spacePressTime = this.spacePressTime + 0.2F;
                    hoverModeMovement(player, storage, jetModule);
                }
            }
            else if (this.spacePressTime >= 0.6F) {
                this.spacePressTime = this.spacePressTime - 0.2F;
            }

        }

        /** ELYTRA MODE */
        if (mode == ModeType.ELYTRA.getMode()) {
            if (KeyVariables.isHoldingUp(player) && player.isFallFlying()) {
                if (player.isSprinting()) {
                    if (this.spacePressTime < 2.8F) {
                        this.spacePressTime = this.spacePressTime + 0.2F;
                    }
                } else {
                    if (this.spacePressTime < 2.2F) {
                        this.spacePressTime = this.spacePressTime + 0.2F;
                    }
                }
            }
            else if (this.spacePressTime > 0.0F) {
                this.spacePressTime = this.spacePressTime - 0.2F;
            }
        }
    }

    public void boost(Player player, double boost, boolean sonicBoom) {
        Vec3 vec31 = player.getLookAngle();

        if (Utils.isLivingInSpaceSuit(player) && player.isFallFlying()) {
            Vec3 vec32 = player.getDeltaMovement();
            player.setDeltaMovement(vec32.add(vec31.x * 0.1D + (vec31.x * boost - vec32.x) * 0.5D, vec31.y * 0.1D + (vec31.y * boost - vec32.y) * 0.5D, vec31.z * 0.1D + (vec31.z * boost - vec32.z) * 0.5D));

            if (sonicBoom) {
                Vec3 vec33 = player.getLookAngle().scale(6.5D);

                if (player.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.FLAME, true, true, player.getX() - vec33.x, player.getY() - vec33.y, player.getZ() - vec33.z, 1, 0, 0, 0, 0.001);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        SpaceSuitModule.JetModule jetModule = ModuleUtils.getSpaceSuitModule(stack, SpaceSuitModule.JetModule.class);
        if (jetModule != null) {
            double consumption = jetModule.getConsumptionPerTick();
            tooltipAdder.accept(Component.literal("-- Jet Module --").withColor(Utils.getMinecraftColor("darkred")));
            tooltipAdder.accept(Component.literal("Consumption: " + consumption + " mb/tick").withColor(Utils.getMinecraftColor("darkred")));
        }
    }

    public enum ModeType implements StringRepresentable {
        DISABLED(Component.translatable("text." + Stellaris.MOD_ID + ".jet.mode.disabled"), ChatFormatting.RED, 0),
        NORMAL(Component.translatable("text." + Stellaris.MOD_ID + ".jet.mode.normal"), ChatFormatting.GREEN, 1),
        HOVER(Component.translatable("text." + Stellaris.MOD_ID + ".jet.mode.hover"), ChatFormatting.GREEN, 2),
        ELYTRA(Component.translatable("text." + Stellaris.MOD_ID + ".jet.mode.elytra"), ChatFormatting.GREEN, 3);

        private final int mode;
        private final ChatFormatting chatFormatting;
        private final Component component;

        public static final Codec<ModeType> CODEC = StringRepresentable.fromEnum(ModeType::values);


        ModeType(Component component, ChatFormatting chatFormatting, int mode) {
            this.mode = mode;
            this.chatFormatting = chatFormatting;
            this.component = component;
        }

        public ChatFormatting getChatFormatting() {
            return chatFormatting;
        }

        public MutableComponent getMutableComponent() {
            return MutableComponent.create(this.component.getContents()).withStyle(getChatFormatting());
        }

        public int getMode() {
            return this.mode;
        }

        @Override
        public String getSerializedName() {
            return String.valueOf(this.mode);
        }


        public static ModeType fromInt(int integer) {
            return fromString(Integer.toString(integer));
        }

        public static ModeType fromString(String string) {
            return switch (Integer.decode(string)) {
                case 1 -> NORMAL;
                case 2 -> HOVER;
                case 3 -> ELYTRA;
                default -> DISABLED;
            };
        }
    }
}
