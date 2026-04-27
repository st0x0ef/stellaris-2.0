package org.exodusstudio.stellaris.common.utils.capabilities.energy;

import com.fej1fun.potentials.capabilities.Capabilities;
import com.fej1fun.potentials.energy.UniversalEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.exodusstudio.stellaris.common.blocks.PumpjackProxyBlock;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EnergyUtil {

    public static int moveEnergyToItem(UniversalEnergyStorage from, ItemStack stackTo, int amount) {
        if (stackTo.isEmpty()) return 0;
        UniversalEnergyStorage to = Capabilities.Energy.ITEM.getCapability(stackTo);
        if (to == null) {
            return 0;
        }
        return moveEnergy(from, to, amount);
    }

    public static int moveEnergyFromItem(UniversalEnergyStorage to, ItemStack stackFrom, int amount) {
        if (stackFrom.isEmpty()) return 0;
        UniversalEnergyStorage from = Capabilities.Energy.ITEM.getCapability(stackFrom);
        if (from == null) {
            return 0;
        }
        return moveEnergy(from, to, amount);
    }

    public static void distributeEnergyNearby(Level level, BlockPos pos, int amount) {
        distributeEnergyNearby(level, pos, amount, null);
    }

    public static void distributeEnergyNearby(Level level, BlockPos pos, int amount, List<Direction> outputDirections) {
        if (outputDirections == null || outputDirections.isEmpty()) {
            distributeInAllDirections(level, pos, amount);
        }
        else {
            distributeInDirections(level, pos, amount, outputDirections);
        }
    }

    private static int distributeInDirections(Level level, BlockPos pos, int amount, List<Direction> outputDirections) {
        Map<UniversalEnergyStorage, UniversalEnergyStorage> pairs = new HashMap<>();

        for (Direction direction : outputDirections) {
            UniversalEnergyStorage from = getEnergyCapability(level, pos, direction);

            if (from == null || !from.canExtractEnergy() || from.extract(amount, true) == 0) {
                continue;
            }

            UniversalEnergyStorage to = getEnergyCapability(level, pos.relative(direction), direction.getOpposite());

            if (to == null || !to.canInsertEnergy() || to.insert(amount, true) == 0) {
                continue;
            }

            pairs.put(from, to);
        }

        AtomicInteger toDistribute = new AtomicInteger(amount);
        AtomicInteger receivers = new AtomicInteger(pairs.size());

        pairs.forEach((energyFrom, energyTo) -> {
            int receiverCount = receivers.get();

            if (receiverCount <= 0) {
                return;
            }

            toDistribute.addAndGet(-moveEnergy(energyFrom, energyTo, toDistribute.get() / receiverCount));
            receivers.getAndDecrement();
        });

        return amount - toDistribute.get();
    }

    private static void distributeInAllDirections(Level level, BlockPos pos, int amount) {
        UniversalEnergyStorage from = getEnergyCapability(level, pos, null);

        if (from == null || !from.canExtractEnergy()) {
            return;
        }

        int finalAmount = from.extract(amount, true);

        if (finalAmount == 0) {
            return;
        }

        List<UniversalEnergyStorage> toSend = Direction.stream()
                .map(direction -> getEnergyCapability(level, pos.relative(direction), direction.getOpposite()))
                .filter(Objects::nonNull)
                .filter(UniversalEnergyStorage::canInsertEnergy)
                .sorted(Comparator.comparing(energyStorage -> energyStorage.insert(finalAmount, true)))
                .toList();

        if (toSend.isEmpty()) {
            return;
        }

        int receivers = toSend.size();

        for (UniversalEnergyStorage to : toSend) {
            moveEnergy(from, to, finalAmount / receivers);
        }
    }

    public static int moveEnergy(UniversalEnergyStorage from, UniversalEnergyStorage to, int amount) {
        int inserted = to.insert(from.extract(amount, true), true);

        if (inserted > 0) {
            from.extract(inserted, false);
            to.insert(inserted, false);
        }

        return inserted;
    }

    private static UniversalEnergyStorage getEnergyCapability(Level level, BlockPos pos, Direction direction) {
        UniversalEnergyStorage direct = Capabilities.Energy.BLOCK.getCapability(level, pos, direction);

        if (direct != null) {
            return direct;
        }

        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof PumpjackProxyBlock) {
            BlockPos mainPos = PumpjackProxyBlock.getMainPos(pos, state);
            return Capabilities.Energy.BLOCK.getCapability(level, mainPos, direction);
        }

        return null;
    }
}