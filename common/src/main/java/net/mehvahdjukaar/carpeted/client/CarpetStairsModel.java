package net.mehvahdjukaar.carpeted.client;

import net.mehvahdjukaar.carpeted.CarpetedBlockTile;
import net.mehvahdjukaar.carpeted.CarpetedPlatformStuff;
import net.mehvahdjukaar.moonlight.api.client.model.CustomBakedModel;
import net.mehvahdjukaar.moonlight.api.client.model.ExtraModelData;
import net.mehvahdjukaar.moonlight.api.client.util.VertexUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class CarpetStairsModel implements CustomBakedModel {
    private final BakedModel carpet;
    private final BlockModelShaper blockModelShaper;

    public CarpetStairsModel(BakedModel carpet) {
        this.carpet = carpet;
        this.blockModelShaper = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
    }

    @Override
    public List<BakedQuad> getBlockQuads(BlockState state, Direction side, RandomSource rand, RenderType renderType, ExtraModelData data) {
        List<BakedQuad> quads = new ArrayList<>();

        if (state != null) {
            try {
                BlockState mimic = data.get(CarpetedBlockTile.MIMIC);

                if (mimic != null) {
                    BakedModel model = blockModelShaper.getBlockModel(mimic);
                        quads.addAll(model.getQuads(mimic, side, rand));
                }
            } catch (Exception ignored) {
            }

            try {
                BlockState carpetBlock = data.get(CarpetedBlockTile.CARPET_KEY);
                var supportQuads = carpet.getQuads(state, side, rand);

                if (!supportQuads.isEmpty()) {
                    if (carpetBlock != null) {
                        TextureAtlasSprite sprite = getCarpetSprite(carpetBlock);
                        if (sprite != null) {
                            supportQuads = VertexUtil.swapSprite(supportQuads, sprite);
                            CarpetedPlatformStuff.removeAmbientOcclusion(supportQuads);
                        }
                    }
                    quads.addAll(supportQuads);
                }
            } catch (Exception ignored) {
            }
        }
        return quads;
    }

    private TextureAtlasSprite getCarpetSprite(BlockState carpetBlock) {
        return blockModelShaper.getBlockModel(carpetBlock).getParticleIcon();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getBlockParticle(ExtraModelData data) {
        BlockState mimic = data.get(CarpetedBlockTile.MIMIC);
        if (mimic != null && !mimic.isAir()) {
            BakedModel model = blockModelShaper.getBlockModel(mimic);
            try {
                return model.getParticleIcon();
            } catch (Exception ignored) {
            }
        }
        return carpet.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

}
