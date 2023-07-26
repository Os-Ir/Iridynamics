package com.atodium.iridynamics.api.gui.impl;

import com.atodium.iridynamics.api.gui.IModularGuiHolder;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IBlockEntityGuiHolder<T extends BlockEntity> extends IModularGuiHolder<IBlockEntityGuiHolder<T>> {

}