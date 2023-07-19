package com.atodium.iridynamics.api.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

import java.util.Map;

public class StructureLayer {
    private final int dx, dz;
    private final Block[][] blocks;

    public StructureLayer(Map<BlockPos, Block> blocks, int layer, int dx, int dz) {
        this.dx = dx;
        this.dz = dz;
        this.blocks = new Block[dz][dx];
        blocks.forEach((pos, block) -> {
            if (pos.getY() == layer) this.blocks[pos.getZ()][pos.getX()] = block;
        });
    }

    public StructureLayer(Block[][] blocks, int dx, int dz) {
        this.dx = dx;
        this.dz = dz;
        this.blocks = new Block[dz][dx];
        for (int z = 0; z < dz; z++) System.arraycopy(blocks[z], 0, this.blocks[z], 0, dx);
    }

    public boolean isBound(int x, int z) {
        return x == 0 || z == 0 || x == this.dx - 1 || z == this.dz - 1;
    }

    public boolean checkBlock(int x, int z, Block... optional) {
        for (Block b : optional) if (this.blocks[z][x] == b) return true;
        return false;
    }

    public boolean checkBlock(Block block, Block... optional) {
        for (Block b : optional) if (block == b) return true;
        return false;
    }

    public boolean isFilled(Block... optional) {
        for (Block[] l : this.blocks) for (Block b : l) if (!this.checkBlock(b, optional)) return false;
        return true;
    }

    public boolean isSurrounded(Block... optional) {
        for (int z = 0; z < this.dz; z++)
            for (int x = 0; x < this.dx; x++)
                if ((this.isBound(x, z) && !this.checkBlock(x, z, optional)) || (!this.isBound(x, z) && this.blocks[z][x] != null))
                    return false;
        return true;
    }
}