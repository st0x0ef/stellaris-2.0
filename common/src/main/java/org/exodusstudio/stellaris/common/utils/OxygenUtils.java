package org.exodusstudio.stellaris.common.utils;

import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.common.blocks.entities.machines.OxygenDistributorBlockEntity;
import org.exodusstudio.stellaris.common.blocks.entities.machines.OxygenPropagatorBlockEntity;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitHelmet;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;

import java.util.*;

public class OxygenUtils {

    private static final int MAX_BLOCKS = 50_000;

    public static Set<BlockPos> propagateOxygen(Level level, BlockPos distributorPos, Set<ChunkPos> allowedChunks) {
        Set<BlockPos> oxygenablePositions = new HashSet<>();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        visited.add(distributorPos);
        for (Direction direction : Direction.values()) {
            BlockPos adjacent = distributorPos.relative(direction);
            if (isBreathable(level, adjacent) && visited.add(adjacent)) {
                queue.add(adjacent);
            }
        }

        while (!queue.isEmpty()) {
            if (visited.size() > MAX_BLOCKS) {
                return Collections.emptySet();
            }

            BlockPos current = queue.poll().immutable();

            if (level.canSeeSky(current)) {
                return Collections.emptySet();
            }

            ChunkPos currentChunk = ChunkPos.containing(current);
            if (!allowedChunks.contains(currentChunk)) {
                return Collections.emptySet();
            }

            oxygenablePositions.add(current);

            for (Direction direction : Direction.values()) {
                BlockPos neighbor = current.relative(direction);
                if (visited.add(neighbor) && isBreathable(level, neighbor)) {
                    queue.add(neighbor);
                }
            }
        }

        return oxygenablePositions;
    }

    private static boolean isBreathable(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isAir() || !state.isCollisionShapeFullBlock(level, pos);
    }

    public static boolean isOxygenated(Level level, BlockPos entityPos) {
        Planet planet = PlanetsData.getPlanet(level.dimension());
        if (planet == null || planet.hasOxygen()) {
            return true;
        }

        Set<OxygenDistributorBlockEntity> checkedDistributors = new HashSet<>();
        Set<ChunkPos> visitedChunks = new HashSet<>();
        Queue<ChunkPos> chunksToSearch = new ArrayDeque<>();

        for (ChunkPos chunk : getBasicAllowedChunks(entityPos)) {
            if (visitedChunks.add(chunk)) {
                chunksToSearch.add(chunk);
            }
        }

        while (!chunksToSearch.isEmpty()) {
            ChunkPos chunkPos = chunksToSearch.poll();

            if (!level.hasChunk(chunkPos.x(), chunkPos.z())) {
                continue;
            }

            LevelChunk levelChunk = level.getChunk(chunkPos.x(), chunkPos.z());

            for (BlockEntity blockEntity : levelChunk.getBlockEntities().values()) {
                if (blockEntity instanceof OxygenDistributorBlockEntity distributor) {
                    if (checkedDistributors.add(distributor)) {
                        if (distributor.isOxygenated(entityPos.immutable())) {
                            return true;
                        }

                        Set<ChunkPos> coveredChunks = distributor.getCoveredChunks();
                        if (coveredChunks != null) {
                            for (ChunkPos covered : coveredChunks) {
                                if (visitedChunks.add(covered)) {
                                    chunksToSearch.add(covered);
                                }
                            }
                        }
                    }
                } else if (blockEntity instanceof OxygenPropagatorBlockEntity) {
                    for (ChunkPos extended : getBasicAllowedChunks(blockEntity.getBlockPos())) {
                        if (visitedChunks.add(extended)) {
                            chunksToSearch.add(extended);
                        }
                    }
                }
            }
        }

        return false;
    }

    public static Set<ChunkPos> getAllowedChunks(Level level, BlockPos entityPos) {
        Set<ChunkPos> allowedChunks = new HashSet<>(getBasicAllowedChunks(entityPos));
        Set<ChunkPos> toProcess = new HashSet<>(allowedChunks);

        while (!toProcess.isEmpty()) {
            Set<ChunkPos> newChunks = new HashSet<>();

            for (ChunkPos chunkPos : toProcess) {
                if (!level.hasChunk(chunkPos.x(), chunkPos.z())) {
                    continue;
                }

                LevelChunk levelChunk = level.getChunk(chunkPos.x(), chunkPos.z());
                for (BlockEntity blockEntity : levelChunk.getBlockEntities().values()) {
                    if (blockEntity instanceof OxygenPropagatorBlockEntity) {
                        for (ChunkPos extension : getBasicAllowedChunks(blockEntity.getBlockPos())) {
                            if (allowedChunks.add(extension)) {
                                newChunks.add(extension);
                            }
                        }
                    }
                }
            }

            toProcess = newChunks;
        }

        return allowedChunks;
    }

    private static final List<int[]> CHUNK_OFFSETS = List.of(
            new int[]{-1, -1}, new int[]{0, -1}, new int[]{1, -1},
            new int[]{-1, 0}, new int[]{0, 0}, new int[]{1, 0},
            new int[]{-1, 1}, new int[]{0, 1}, new int[]{1, 1}
    );

    public static List<ChunkPos> getBasicAllowedChunks(ChunkPos centerChunk) {
        List<ChunkPos> chunks = new ArrayList<>(9);
        for (int[] offset : CHUNK_OFFSETS) {
            chunks.add(new ChunkPos(centerChunk.x() + offset[0], centerChunk.z() + offset[1]));
        }
        return chunks;
    }

    public static List<ChunkPos> getBasicAllowedChunks(BlockPos pos) {
        return getBasicAllowedChunks(ChunkPos.containing(pos));
    }

    public static int getEntityWhoNeedsOxygen(Level level, Set<ChunkPos> chunks, Set<BlockPos> oxygenatedPositions) {
        Set<LivingEntity> counted = new HashSet<>();

        for (ChunkPos chunkPos : chunks) {
            AABB aabb = new AABB(
                    chunkPos.getMinBlockX(), level.getMinY(), chunkPos.getMinBlockZ(),
                    chunkPos.getMaxBlockX() + 1, level.getMaxY() + 1, chunkPos.getMaxBlockZ() + 1
            );

            for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
                if (entity.is(TagsRegistry.EntityTags.NO_OXYGEN_NEEDED)) continue;
                if (counted.contains(entity)) continue;
                if (!oxygenatedPositions.contains(entity.blockPosition())) continue;

                ItemStack headSlot = entity.getItemBySlot(EquipmentSlot.HEAD);
                if (Utils.isLivingInSpaceSuit(entity) && headSlot.getItem() instanceof SpaceSuitHelmet helmet) {
                    UniversalFluidItemStorage oxygenTank = helmet.getFluidTank(headSlot);
                    if (oxygenTank != null && !oxygenTank.getFluidInTank(0).isEmpty()) {
                        continue;
                    }
                }
                if (entity instanceof Player player) {
                    if (!player.isCreative() && !player.isSpectator()) {
                        counted.add(entity);
                    }
                } else {
                    counted.add(entity);
                }
            }
        }

        return counted.size();
    }
}
