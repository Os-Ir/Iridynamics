package com.atodium.iridynamics.api.gui.impl;

import com.atodium.iridynamics.api.gui.IModularGuiHolder;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IBlockEntityHolder<T extends BlockEntity> extends IModularGuiHolder<IBlockEntityHolder<T>> {

}