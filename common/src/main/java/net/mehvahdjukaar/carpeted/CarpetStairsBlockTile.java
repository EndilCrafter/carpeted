package net.mehvahdjukaar.carpeted;

import net.mehvahdjukaar.moonlight.api.block.MimicBlockTile;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData;
import net.mehvahdjukaar.moonlight.api.client.model.ModelDataKey;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CarpetStairsBlockTile extends MimicBlockTile {

    public static final ModelDataKey<BlockState> CARPET = new ModelDataKey<>(BlockState.class);

    private BlockState carpet = Blocks.WHITE_CARPET.defaultBlockState();

    public CarpetStairsBlockTile(BlockPos pos, BlockState state) {
        super(Carpeted.CARPET_STAIRS_TILE.get(), pos, state);
    }

    @Override
    public ExtraModelData getExtraModelData() {
        return ExtraModelData.builder()
                .with(MIMIC, mimic)
                .with(CARPET, carpet)
                .build();
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.carpet = NbtUtils.readBlockState(compound.getCompound("Carpet"));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("Carpet", NbtUtils.writeBlockState(carpet));
    }

    @Override
    public BlockState getHeldBlock(int index) {
        if (index == 1) return carpet;
        return super.getHeldBlock(index);
    }

    @Override
    public boolean setHeldBlock(BlockState state, int index) {
        if (index == 0) {
            this.mimic = state;
            if (this.level instanceof ServerLevel) {
                this.setChanged();
                int newLight = this.getLightValue();
                this.level.setBlock(this.worldPosition, this.getBlockState()
                        .setValue(CarpetStairBlock.LIGHT_LEVEL, newLight), 3);
                this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_CLIENTS);
            } else {
                this.requestModelReload();
            }
            return true;
        } else if (index == 1) {
            this.carpet = state;
            return true;
        }
        return false;
    }

    public void initialize(BlockState stairs, BlockState carpet) {
        this.setHeldBlock(carpet, 1);
        this.setHeldBlock(stairs, 0);
    }

    public int getLightValue() {
        return this.getHeldBlock().getLightEmission();
    }
}

