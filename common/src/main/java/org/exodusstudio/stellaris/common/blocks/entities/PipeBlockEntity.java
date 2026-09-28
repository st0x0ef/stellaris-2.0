package org.exodusstudio.stellaris.common.blocks.entities;

import com.fej1fun.potentials.fluid.UniversalFluidStorage;
import com.fej1fun.potentials.providers.FluidProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.exodusstudio.stellaris.common.blocks.PipeBlock;
import org.exodusstudio.stellaris.common.registries.BlockEntitiesRegistry;
import org.exodusstudio.stellaris.common.transport.PassthroughFluidStorage;
import org.jetbrains.annotations.Nullable;

/**
 * A fluid pipe is a bufferless connector: it stores no fluid. Transport between Stellaris machines is
 * handled by {@link org.exodusstudio.stellaris.common.transport.TransportGraph}, which floods the
 * connected pipe network when a producer pushes into it. The pipe additionally exposes a stateless
 * passthrough fluid capability ({@link PassthroughFluidStorage}) so other mods' pipes/conduits can
 * push directly into a Stellaris line; that capability routes straight into the network and stores
 * nothing. The block entity itself neither ticks nor persists anything.
 */
public class PipeBlockEntity extends BlockEntity implements FluidProvider.BLOCK {

    public BlockState facadeState = null;

    public PipeBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntitiesRegistry.PIPE_ENTITY.get(), pos, state);
    }

    public static PipeBlockEntity create(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state);
    }

    @Override
    public @Nullable UniversalFluidStorage getFluidTank(@Nullable Direction direction) {
        if (level == null || !(getBlockState().getBlock() instanceof PipeBlock pipe)) {
            return null;
        }
        return new PassthroughFluidStorage(level, worldPosition, direction, pipe.maxIn);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if(facadeState != null) {
            output.store("facade", BlockState.CODEC, facadeState);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        facadeState = input.read("facade", BlockState.CODEC).orElse(null);
    }


    public void setFacadeState(@Nullable BlockState state) {

        this.facadeState = state;
        setChanged();

    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = this.saveWithoutMetadata(registries);

        if(facadeState != null) {
            tag.put("facade", BlockState.CODEC.encodeStart(NbtOps.INSTANCE, facadeState).result().orElseThrow(() -> new IllegalStateException("Failed to encode facade state")));
        }


        return tag;
    }

    // Return our packet here. This method returning a non-null result tells the game to use this packet for syncing.
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void setChanged() {
        super.setChanged();

        if (level == null) return;

        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition, state, state, Block.UPDATE_ALL);
    }



}
