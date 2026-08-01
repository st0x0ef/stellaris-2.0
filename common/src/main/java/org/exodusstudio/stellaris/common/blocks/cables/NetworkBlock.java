package org.exodusstudio.stellaris.common.blocks.cables;

import com.fej1fun.potentials.capabilities.types.BlockCapabilityHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.phys.BlockHitResult;
import org.exodusstudio.stellaris.common.networks.Network;
import org.exodusstudio.stellaris.common.networks.NetworkManager;

import java.util.*;

public abstract class NetworkBlock<N extends Network, BE extends NetworkBlockEntity<N>> extends BaseCableLikeBlock {

    public NetworkBlock(Properties properties) {
        super(properties);
    }

    // ==========================================
    // ABSTRACT REGISTRY & LIFECYCLE HOOKS
    // ==========================================

    public abstract SavedDataType<NetworkManager<N>> getNetworkDataType();
    public abstract BlockCapabilityHolder<N, Direction> getNetworkCapability();
    public abstract N createEmptyNetwork();

    protected abstract boolean isExternalConnectable(Level level, BlockPos selfPos, Direction direction, BlockPos neighborPos);

    protected abstract void onCableAdded(ServerLevel level, N network, BlockState state, BlockPos pos);
    protected abstract void onNetworksMerged(N survivor, N dead);
    protected abstract void recalculateNetwork(ServerLevel level, N network);
    protected abstract void splitNetwork(
            ServerLevel level,
            N originalNetwork,
            List<N> newSubNetworks,
            List<Set<BlockPos>> components
    );

    // ==========================================
    // SHAPE & CONNECTION LOGIC
    // ==========================================


    @Override
    protected boolean isConnectable(Level level, BlockPos selfPos, Direction direction) {
        BlockState selfState = level.getBlockState(selfPos);

        // 1. Check self state from BlockState
        if (selfState.hasProperty(MODE_BY_DIRECTION.get(direction))) {
            ConnectionMode selfMode = selfState.getValue(MODE_BY_DIRECTION.get(direction));
            if (selfMode == ConnectionMode.DISABLED) {
                return false;
            }
        }

        BlockPos neighborPos = selfPos.relative(direction);
        BlockState neighborState = level.getBlockState(neighborPos);

        // 2. Check adjacent network cable connection
        N adjacentNet = getNetworkCapability().getCapability(level, neighborPos, direction.getOpposite());
        if (adjacentNet != null) {
            if (neighborState.hasProperty(MODE_BY_DIRECTION.get(direction.getOpposite()))) {
                ConnectionMode neighborMode = neighborState.getValue(MODE_BY_DIRECTION.get(direction.getOpposite()));
                if (neighborMode == ConnectionMode.DISABLED) {
                    return false;
                }
            }
            return true;
        }

        // 3. Check for external machine connection
        return isExternalConnectable(level, selfPos, direction, neighborPos);
    }

    // ==========================================
    // ITEM WRENCH / CONFIGURATOR INTERACTION
    // ==========================================

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (isConfiguratorTool(stack)) {
            if (player.isShiftKeyDown()) {
                level.destroyBlock(pos, true, player);
                return InteractionResult.SUCCESS;
            }

            // Resolve target face from sub-box hit location
            Direction face = getTargetedDirection(pos, hit.getLocation(), hit.getDirection());
            EnumProperty<ConnectionMode> modeProp = MODE_BY_DIRECTION.get(face);

            if (state.hasProperty(modeProp)) {
                ConnectionMode oldMode = state.getValue(modeProp);
                ConnectionMode nextMode = oldMode.next();

                if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                    BlockState newState = state.setValue(modeProp, nextMode);
                    boolean isConnected = isConnectable(level, pos, face);
                    newState = newState.setValue(PROPERTY_BY_DIRECTION.get(face), isConnected);

                    level.setBlock(pos, newState, Block.UPDATE_ALL);

                    player.sendOverlayMessage(Component.literal("Side " + face.getName().toUpperCase() + ": " + nextMode.name()));

                    handleSideModeTopologyChange(serverLevel, pos, face, oldMode, nextMode);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    private void handleSideModeTopologyChange(ServerLevel level, BlockPos pos, Direction face, ConnectionMode oldMode, ConnectionMode newMode) {
        boolean wasDisabled = (oldMode == ConnectionMode.DISABLED);
        boolean isDisabled = (newMode == ConnectionMode.DISABLED);

        if (!wasDisabled && isDisabled) {
            // Face disabled: sever connection (handles split + rebuild internally)
            handleFaceDisabled(level, pos, face);
        } else if (wasDisabled && !isDisabled) {
            // Face enabled: re-connect (handles merge + rebuild internally)
            handleFaceEnabled(level, pos, face);
        } else {
            // Fast path: NORMAL <-> PULL <-> PUSH mode toggle
            NetworkManager<N> manager = level.getDataStorage().computeIfAbsent(getNetworkDataType());
            N network = manager.getNetworkAt(pos);
            if (network != null) {
                // Incremental update for just this single face!
                network.updateEndpoint(pos, face, newMode);
            }
        }
    }

    // ==========================================
    // FACE SEVERING & RE-CONNECTING LOGIC
    // ==========================================

    private void handleFaceDisabled(ServerLevel level, BlockPos pos, Direction face) {
        NetworkManager<N> manager = level.getDataStorage().computeIfAbsent(getNetworkDataType());
        N network = manager.getNetworkAt(pos);
        if (network == null) return;

        BlockPos neighborPos = pos.relative(face);

        // Only process graph split if the neighbor is actually a cable in the same network
        if (!network.cables().contains(neighborPos)) return;

        // BFS starting from 'pos' to see if 'neighborPos' is still reachable via another loop/path
        Set<BlockPos> componentA = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        queue.add(pos);
        componentA.add(pos);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);

                if (network.cables().contains(neighbor) && !componentA.contains(neighbor) && isConnectable(level, current, dir)) {
                    componentA.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        // If neighborPos was reached, loop is intact -> No network split
        if (componentA.contains(neighborPos)) {
            return;
        }

        // GRID SPLIT: componentA contains pos, componentB contains neighborPos
        Set<BlockPos> componentB = new HashSet<>(network.cables());
        componentB.removeAll(componentA);

        // Survivor network keeps componentA (which contains pos)
        network.cables().retainAll(componentA);

        // Create new sub-network for componentB (which contains neighborPos)
        N newSubNetwork = createEmptyNetwork();
        for (BlockPos cablePos : componentB) {
            newSubNetwork.addCable(cablePos);
            setUUID(newSubNetwork.id(), level, cablePos);
        }

        // Delegate resource distribution & capacity split
        splitNetwork(level, network, List.of(newSubNetwork), List.of(componentA, componentB));

        // Register the new split network
        manager.addNetwork(newSubNetwork);
    }

    private void handleFaceEnabled(ServerLevel level, BlockPos pos, Direction face) {
        NetworkManager<N> manager = level.getDataStorage().computeIfAbsent(getNetworkDataType());

        N posNet = manager.getNetworkAt(pos);
        BlockPos neighborPos = pos.relative(face);
        N neighborNet = manager.getNetworkAt(neighborPos);

        // If both sides belong to different networks and are now connectable, merge them
        if (posNet != null && neighborNet != null && posNet != neighborNet) {
            if (isConnectable(level, pos, face)) {
                // Merge neighborNet into posNet
                for (BlockPos oldCablePos : neighborNet.cables()) {
                    posNet.addCable(oldCablePos);
                    setUUID(posNet.id(), level, oldCablePos);
                }

                onNetworksMerged(posNet, neighborNet);
                manager.removeNetwork(neighborNet.id());
            }
        }
    }

    // ==========================================
    // GRAPH TOPOLOGY LOGIC
    // ==========================================

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        // Prevent neighbor updateShape() calls from re-running network placement logic!
        if (oldState.is(state.getBlock())) return;

        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel)) return;

        joinNetwork(serverLevel, pos, state);
    }

    private void joinNetwork(ServerLevel level, BlockPos pos, BlockState state) {
        NetworkManager<N> manager = level.getDataStorage().computeIfAbsent(getNetworkDataType());

        Set<N> adjacentNetworks = new HashSet<>();
        for (Direction dir : Direction.values()) {
            if (!isConnectable(level, pos, dir)) continue;

            BlockPos neighborPos = pos.relative(dir);
            N adjNet = getNetworkCapability().getCapability(level, neighborPos, dir.getOpposite());
            if (adjNet != null) {
                adjacentNetworks.add(adjNet);
            }
        }

        if (adjacentNetworks.isEmpty()) {
            N newNetwork = createEmptyNetwork();
            newNetwork.addCable(pos);
            onCableAdded(level, newNetwork, state, pos);
            manager.addNetwork(newNetwork);

            setUUID(newNetwork.id(), level, pos);

        } else if (adjacentNetworks.size() == 1) {
            N existingNetwork = adjacentNetworks.iterator().next();
            existingNetwork.addCable(pos);
            onCableAdded(level, existingNetwork, state, pos);

            setUUID(existingNetwork.id(), level, pos);

        } else {
            Iterator<N> iter = adjacentNetworks.iterator();
            N survivorNetwork = iter.next();

            survivorNetwork.addCable(pos);
            onCableAdded(level, survivorNetwork, state, pos);
            setUUID(survivorNetwork.id(), level, pos);

            while (iter.hasNext()) {
                N deadNetwork = iter.next();

                for (BlockPos oldCablePos : deadNetwork.cables()) {
                    survivorNetwork.addCable(oldCablePos);
                    setUUID(survivorNetwork.id(), level, oldCablePos);
                }

                onNetworksMerged(survivorNetwork, deadNetwork);
                manager.removeNetwork(deadNetwork.id());
            }
        }

        N activeNetwork = manager.getNetworkAt(pos);
        if (activeNetwork != null) {
            activeNetwork.rebuildEndpoints(level);
        }
    }

    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        handleCableBreak(level, pos);
        super.affectNeighborsAfterRemoval(state, level, pos, movedByPiston);
    }

    private void handleCableBreak(ServerLevel level, BlockPos brokenPos) {
        NetworkManager<N> manager = level.getDataStorage().computeIfAbsent(getNetworkDataType());

        N network = manager.getNetworkAt(brokenPos);
        if (network == null) return;

        // 1. Remove all endpoints attached to the destroyed cable position
        network.removeCableEndpoints(brokenPos);
        network.cables().remove(brokenPos);

        if (network.cables().isEmpty()) {
            manager.removeNetwork(network.id());
            return;
        }

        List<BlockPos> adjacentCables = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            BlockPos neighbor = brokenPos.relative(dir);
            if (network.cables().contains(neighbor) && isConnectable(level, brokenPos, dir)) {
                adjacentCables.add(neighbor);
            }
        }

        if (adjacentCables.size() <= 1) {
            recalculateNetwork(level, network);
            // 2. Rebuild endpoints for survivor network (no split occurred)
            network.rebuildEndpoints(level);
            return;
        }

        Set<BlockPos> remainingUnvisitedCables = new HashSet<>(network.cables());
        List<Set<BlockPos>> disconnectedSubNetworks = new ArrayList<>();

        for (BlockPos startNode : adjacentCables) {
            if (!remainingUnvisitedCables.contains(startNode)) continue;

            Set<BlockPos> component = new HashSet<>();
            Queue<BlockPos> queue = new ArrayDeque<>();

            queue.add(startNode);
            component.add(startNode);
            remainingUnvisitedCables.remove(startNode);

            while (!queue.isEmpty()) {
                BlockPos current = queue.poll();

                for (Direction dir : Direction.values()) {
                    BlockPos neighbor = current.relative(dir);

                    if (remainingUnvisitedCables.contains(neighbor) && isConnectable(level, current, dir)) {
                        remainingUnvisitedCables.remove(neighbor);
                        component.add(neighbor);
                        queue.add(neighbor);
                    }
                }
            }

            disconnectedSubNetworks.add(component);
        }

        if (disconnectedSubNetworks.size() == 1) {
            recalculateNetwork(level, network);
            // 3. Rebuild endpoints for survivor network (network stayed connected via another loop)
            network.rebuildEndpoints(level);
        } else {
            Set<BlockPos> survivorComponent = disconnectedSubNetworks.get(0);
            network.cables().retainAll(survivorComponent);

            List<N> newSubNetworks = new ArrayList<>();
            for (int i = 1; i < disconnectedSubNetworks.size(); i++) {
                Set<BlockPos> newComponent = disconnectedSubNetworks.get(i);
                N newSubNetwork = createEmptyNetwork();

                for (BlockPos pos : newComponent) {
                    newSubNetwork.addCable(pos);
                    setUUID(newSubNetwork.id(), level, pos);
                }
                newSubNetworks.add(newSubNetwork);
            }

            splitNetwork(level, network, newSubNetworks, disconnectedSubNetworks);

            // 4. Rebuild endpoints for survivor network after resource/capacity splitting
            network.rebuildEndpoints(level);

            // 5. Rebuild endpoints for each newly generated sub-network
            for (N newSubNetwork : newSubNetworks) {
                newSubNetwork.rebuildEndpoints(level);
                manager.addNetwork(newSubNetwork);
            }
        }
    }

    protected void setUUID(UUID uuid, Level level, BlockPos pos) {
        if (!level.isLoaded(pos)) return;

        if (level.getBlockEntity(pos) instanceof NetworkBlockEntity<?> networkBlockEntity) {
            @SuppressWarnings("unchecked")
            BE castBE = (BE) networkBlockEntity;
            castBE.setUuid(uuid);
        }
    }
}
