package com.atodium.iridynamics.api.blockEntity;

import com.atodium.iridynamics.api.pipe.LiquidPipeSavedData;
import com.atodium.iridynamics.api.rotate.RotateSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.function.Function;

public class SavedDataTickManager {
    public static final List<Function<ServerLevel, TickableSavedData>> TICKABLE_SAVED_DATA = Lists.newArrayList();

    public static void init() {
        registerSavedData(RotateSavedData::get);
        registerSavedData(LiquidPipeSavedData::get);
    }

    public static void registerSavedData(Function<ServerLevel, TickableSavedData> function) {
        TICKABLE_SAVED_DATA.add(function);
    }

    public static void tick(ServerLevel level, BlockPos pos) {
        TICKABLE_SAVED_DATA.forEach((function) -> function.apply(level).tryTick(level, pos, level.getGameTime()));
    }

    public static abstract class TickableSavedData extends SavedData {
        public abstract void tryTick(ServerLevel level, BlockPos pos, long time);
    }
}