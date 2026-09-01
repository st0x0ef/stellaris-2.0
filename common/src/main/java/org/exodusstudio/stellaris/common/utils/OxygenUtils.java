package org.exodusstudio.stellaris.common.utils;

import com.fej1fun.potentials.fluid.UniversalFluidItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.blocks.entities.machines.OxygenDistributorBlockEntity;
import org.exodusstudio.stellaris.common.blocks.entities.machines.OxygenPropagatorBlockEntity;
import org.exodusstudio.stellaris.common.config.CommonConfig;
import org.exodusstudio.stellaris.common.data.Planet;
import org.exodusstudio.stellaris.common.data.PlanetsData;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitBoots;
import org.exodusstudio.stellaris.common.items.space_suit.SpaceSuitHelmet;
import org.exodusstudio.stellaris.common.keybinds.KeyVariables;
import org.exodusstudio.stellaris.common.registries.TagsRegistry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class OxygenUtils {

    private static final int MAX_BLOCKS = 50_000;

    /// A room has to fit inside a 3 x 3 chunk area, but that area is fitted around the room rather
    /// than centred on the distributor, so the block may sit anywhere inside it - a corner
    /// included. Every 3 x 3 window containing the distributor's chunk lies within two chunks of
    /// it, so the fill is explored over a 5 x 5 box and the 3 x 3 budget is applied to the result.
    private static final int MAX_ROOM_CHUNK_SPAN = 2;
    private static final int EXPLORATION_CHUNK_RADIUS = 2;
    private static final int PROPAGATOR_CHUNK_RADIUS = 1;

    /// Bounds the propagator chain so a long line of them cannot grow the allowed area forever.
    private static final int MAX_ALLOWED_CHUNKS = 1024;

    /// Marks a column with nothing above it to hold oxygen in.
    private static final int NO_SEAL = Integer.MIN_VALUE;

    /// Distributors that are currently loaded, per dimension. `isOxygenated` runs on every entity
    /// tick as well as on fire ticks, crop ticks and block placement, so it must not scan chunks.
    private static final Map<ResourceKey<Level>, Set<OxygenDistributorBlockEntity>> ACTIVE_DISTRIBUTORS =
            new ConcurrentHashMap<>();

    /// Why a distributor is or is not filling its room. Surfaced in the machine screen, because
    /// every failure otherwise looks the same from the outside: the block simply goes dark.
    public enum OxygenStatus {
        OK,
        BREATHABLE_ATMOSPHERE,
        NO_ENERGY,
        NO_OXYGEN,
        NOT_ENOUGH_OXYGEN,
        SKY_LEAK,
        ROOM_TOO_LARGE,
        TOO_MANY_BLOCKS;

        public String translationKey() {
            return "gui.stellaris.oxygen_distributor.status." + name().toLowerCase(Locale.ROOT);
        }
    }

    public record OxygenResult(Set<BlockPos> positions, OxygenStatus status) {
        public static OxygenResult failure(OxygenStatus status) {
            return new OxygenResult(Collections.emptySet(), status);
        }
    }

    /// @param allowedChunks    every chunk the fill is allowed to enter
    /// @param propagatorChunks the subset covered by a powered propagator, exempt from the 3 x 3 budget
    public record AllowedArea(Set<ChunkPos> allowedChunks, Set<ChunkPos> propagatorChunks) {
    }

    public static int getOxygenDrain(LivingEntity entity) {
        CommonConfig.OxygenConfig config = Stellaris.CONFIG.oxygenConfig;
        int drain = config.baseOxygenDrain;

        if (entity instanceof Player player && isFiringJet(player)) {
            drain *= config.jetOxygenDrainMultiplier;
        } else if (entity.isSprinting()) {
            drain *= config.sprintOxygenDrainMultiplier;
        }

        return Math.max(1, drain);
    }

    private static boolean isFiringJet(Player player) {
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        if (!(boots.getItem() instanceof SpaceSuitBoots)) {
            return false;
        }

        return SpaceSuitBoots.getModeType(boots) != SpaceSuitBoots.ModeType.DISABLED
                && KeyVariables.isHoldingJump(player)
                && !player.onGround();
    }

    public static void registerDistributor(OxygenDistributorBlockEntity distributor) {
        Level level = distributor.getLevel();
        if (level == null || level.isClientSide()) {
            return;
        }

        ACTIVE_DISTRIBUTORS.computeIfAbsent(level.dimension(), key -> ConcurrentHashMap.newKeySet()).add(distributor);
    }

    public static void unregisterDistributor(OxygenDistributorBlockEntity distributor) {
        Level level = distributor.getLevel();
        if (level == null) {
            return;
        }

        Set<OxygenDistributorBlockEntity> distributors = ACTIVE_DISTRIBUTORS.get(level.dimension());
        if (distributors != null) {
            distributors.remove(distributor);
        }
    }

    public static Collection<OxygenDistributorBlockEntity> getDistributors(Level level) {
        Set<OxygenDistributorBlockEntity> distributors = ACTIVE_DISTRIBUTORS.get(level.dimension());
        if (distributors == null) {
            return List.of();
        }

        return distributors;
    }

    public static AllowedArea getAllowedArea(Level level, BlockPos distributorPos) {
        Set<ChunkPos> propagatorChunks = new HashSet<>();

        Set<ChunkPos> toProcess = new HashSet<>(
                chunksAround(ChunkPos.containing(distributorPos), EXPLORATION_CHUNK_RADIUS));
        Set<ChunkPos> allowedChunks = new HashSet<>(toProcess);

        while (!toProcess.isEmpty() && allowedChunks.size() < MAX_ALLOWED_CHUNKS) {
            Set<ChunkPos> newChunks = new HashSet<>();

            for (ChunkPos chunkPos : toProcess) {
                if (!level.hasChunk(chunkPos.x(), chunkPos.z())) {
                    continue;
                }

                LevelChunk levelChunk = level.getChunk(chunkPos.x(), chunkPos.z());
                for (BlockEntity blockEntity : levelChunk.getBlockEntities().values()) {
                    if (!(blockEntity instanceof OxygenPropagatorBlockEntity propagator) || !isPowered(propagator)) {
                        continue;
                    }

                    ChunkPos propagatorChunk = ChunkPos.containing(propagator.getBlockPos());
                    for (ChunkPos extension : chunksAround(propagatorChunk, PROPAGATOR_CHUNK_RADIUS)) {
                        propagatorChunks.add(extension);
                        if (allowedChunks.add(extension)) {
                            newChunks.add(extension);
                        }
                    }
                }
            }

            toProcess = newChunks;
        }

        return new AllowedArea(allowedChunks, propagatorChunks);
    }

    private static boolean isPowered(OxygenPropagatorBlockEntity propagator) {
        return propagator.getEnergy(null) != null && propagator.getEnergy(null).getEnergy() > 0;
    }

    private static List<ChunkPos> chunksAround(ChunkPos center, int radius) {
        int width = radius * 2 + 1;
        List<ChunkPos> chunks = new ArrayList<>(width * width);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                chunks.add(new ChunkPos(center.x() + x, center.z() + z));
            }
        }

        return chunks;
    }

    public static OxygenResult propagateOxygen(Level level, BlockPos distributorPos, AllowedArea area) {
        Set<BlockPos> oxygenablePositions = new HashSet<>();
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        Map<Long, Integer> lowestSealAbove = new HashMap<>();

        int minChunkX = Integer.MAX_VALUE;
        int maxChunkX = Integer.MIN_VALUE;
        int minChunkZ = Integer.MAX_VALUE;
        int maxChunkZ = Integer.MIN_VALUE;

        visited.add(distributorPos);
        queue.add(distributorPos);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            if (!current.equals(distributorPos)) {
                if (isOpenToSky(level, current, lowestSealAbove)) {
                    return OxygenResult.failure(OxygenStatus.SKY_LEAK);
                }

                oxygenablePositions.add(current);
                if (oxygenablePositions.size() > MAX_BLOCKS) {
                    return OxygenResult.failure(OxygenStatus.TOO_MANY_BLOCKS);
                }

                ChunkPos currentChunk = ChunkPos.containing(current);
                if (!area.propagatorChunks().contains(currentChunk)) {
                    minChunkX = Math.min(minChunkX, currentChunk.x());
                    maxChunkX = Math.max(maxChunkX, currentChunk.x());
                    minChunkZ = Math.min(minChunkZ, currentChunk.z());
                    maxChunkZ = Math.max(maxChunkZ, currentChunk.z());

                    if (maxChunkX - minChunkX > MAX_ROOM_CHUNK_SPAN || maxChunkZ - minChunkZ > MAX_ROOM_CHUNK_SPAN) {
                        return OxygenResult.failure(OxygenStatus.ROOM_TOO_LARGE);
                    }
                }
            }

            for (Direction direction : Direction.values()) {
                BlockPos neighbor = current.relative(direction);
                if (!visited.add(neighbor)) {
                    continue;
                }

                Cell cell = classify(level, neighbor);
                if (cell == Cell.WALL) {
                    continue;
                }

                boolean allowed = area.allowedChunks().contains(ChunkPos.containing(neighbor));
                if (cell == Cell.OCCUPIABLE_SEAL) {
                    if (allowed) {
                        oxygenablePositions.add(neighbor);
                        if (oxygenablePositions.size() > MAX_BLOCKS) {
                            return OxygenResult.failure(OxygenStatus.TOO_MANY_BLOCKS);
                        }
                    }
                    continue;
                }

                if (!allowed) {
                    return OxygenResult.failure(OxygenStatus.ROOM_TOO_LARGE);
                }

                queue.add(neighbor);
            }
        }

        return new OxygenResult(oxygenablePositions, OxygenStatus.OK);
    }

    private enum Cell {
        OPEN,
        OCCUPIABLE_SEAL,
        WALL
    }

    private static Cell classify(Level level, BlockPos pos) {
        if (level.isOutsideBuildHeight(pos)
                || !level.hasChunk(SectionPos.blockToSectionCoord(pos.getX()), SectionPos.blockToSectionCoord(pos.getZ()))) {
            return Cell.WALL;
        }

        BlockState state = level.getBlockState(pos);
        if (!sealsRoom(state)) {
            return Cell.OPEN;
        }

        return state.blocksMotion() ? Cell.WALL : Cell.OCCUPIABLE_SEAL;
    }

    private static boolean sealsRoom(BlockState state) {
        if (state.isAir()) {
            return false;
        }

        if (state.is(TagsRegistry.BlockTags.OXYGEN_PERMEABLE)) {
            return !state.getOptionalValue(BlockStateProperties.OPEN).orElse(true);
        }

        return true;
    }

    private static boolean isOpenToSky(Level level, BlockPos pos, Map<Long, Integer> lowestSealAbove) {
        LevelChunk chunk = level.getChunk(SectionPos.blockToSectionCoord(pos.getX()),
                SectionPos.blockToSectionCoord(pos.getZ()));

        int height = chunk.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
        if (pos.getY() <= height) {
            return false;
        }

        int seal = lowestSealAbove.computeIfAbsent(BlockPos.asLong(pos.getX(), 0, pos.getZ()),
                key -> findSealAbove(level, chunk, pos.getX(), height, pos.getZ()));
        if (seal == NO_SEAL) {
            return true;
        }
        if (pos.getY() < seal) {
            return false;
        }

        return findSealAbove(level, chunk, pos.getX(), pos.getY(), pos.getZ()) == NO_SEAL;
    }

    private static int findSealAbove(Level level, LevelChunk chunk, int x, int fromY, int z) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int y = fromY + 1; y <= level.getMaxY(); y++) {
            if (sealsRoom(chunk.getBlockState(cursor.set(x, y, z)))) {
                return y;
            }
        }

        return NO_SEAL;
    }

    /// True when the dimension's own air is breathable, so a distributor has nothing to do there.
    /// An unknown dimension counts as breathable: nothing tracks its oxygen either way.
    public static boolean hasBreathableAtmosphere(Level level) {
        Planet planet = PlanetsData.getPlanet(level.dimension());
        return planet == null || planet.hasOxygen();
    }

    public static boolean isOxygenated(Level level, BlockPos entityPos) {
        if (hasBreathableAtmosphere(level)) {
            return true;
        }

        Set<OxygenDistributorBlockEntity> distributors = ACTIVE_DISTRIBUTORS.get(level.dimension());
        if (distributors == null || distributors.isEmpty()) {
            return false;
        }

        BlockPos pos = entityPos.immutable();
        for (OxygenDistributorBlockEntity distributor : distributors) {
            if (distributor.isRemoved() || distributor.getLevel() != level) {
                if (distributor.isRemoved()) {
                    distributors.remove(distributor);
                }
                continue;
            }

            if (distributor.isOxygenated(pos)) {
                return true;
            }
        }

        return false;
    }

    public static int getEntityWhoNeedsOxygen(Level level, Set<BlockPos> oxygenatedPositions) {
        if (oxygenatedPositions.isEmpty()) {
            return 0;
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (BlockPos pos : oxygenatedPositions) {
            minX = Math.min(minX, pos.getX());
            maxX = Math.max(maxX, pos.getX());
            minY = Math.min(minY, pos.getY());
            maxY = Math.max(maxY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxZ = Math.max(maxZ, pos.getZ());
        }

        AABB aabb = new AABB(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);

        int count = 0;
        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, aabb)) {
            if (entity.is(TagsRegistry.EntityTags.NO_OXYGEN_NEEDED)) continue;
            if (!oxygenatedPositions.contains(entity.blockPosition())) continue;

            ItemStack headSlot = entity.getItemBySlot(EquipmentSlot.HEAD);
            if (Utils.isLivingInSpaceSuit(entity) && headSlot.getItem() instanceof SpaceSuitHelmet helmet) {
                UniversalFluidItemStorage oxygenTank = helmet.getFluidTank(headSlot);
                if (oxygenTank != null && !oxygenTank.getFluidInTank(0).isEmpty()) {
                    continue;
                }
            }

            if (entity instanceof Player player && (player.isCreative() || player.isSpectator())) {
                continue;
            }

            count++;
        }

        return count;
    }
}
