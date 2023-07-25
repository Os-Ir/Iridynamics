package com.atodium.iridynamics.api.material.alloy;

import com.atodium.iridynamics.api.heat.IHeat;
import com.atodium.iridynamics.api.heat.liquid.ILiquidContainer;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.util.data.UnorderedRegistry;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;

public class AlloyModule {
    public static final UnorderedRegistry<MaterialBase, Alloy> ALLOYS = new UnorderedRegistry<>();

    public static void registerAlloy(Alloy alloy) {
        ALLOYS.register(alloy.material(), alloy);
    }

    public static void registerAlloy(MaterialBase material, Object2IntMap<MaterialBase> composition) {
        ALLOYS.register(material, new Alloy(material, composition));
    }

    public static Alloy materialAlloy(MaterialBase material) {
        return ALLOYS.get(material);
    }

    public static Optional<Alloy> validate(ILiquidContainer container, double temperature) {
        for (Alloy alloy : ALLOYS.values()) if (alloy.validate(container, temperature)) return Optional.of(alloy);
        return Optional.empty();
    }

    public static int maxAlloyUnits(MaterialBase alloy, ILiquidContainer container, IHeat heat, double temperature, boolean consume) {
        if (ALLOYS.containsKey(alloy)) return ALLOYS.get(alloy).maxAlloyUnits(container, heat, temperature, consume);
        return 0;
    }

    public static int maxAlloyUnits(Alloy alloy, ILiquidContainer container, IHeat heat, double temperature, boolean consume) {
        return alloy.maxAlloyUnits(container, heat, temperature, consume);
    }

    public static Optional<Pair<Alloy, Integer>> maxAlloyUnits(ILiquidContainer container, IHeat heat, double temperature, boolean consume) {
        for (Alloy alloy : ALLOYS.values()) {
            int unit = alloy.maxAlloyUnits(container, heat, temperature, consume);
            if (unit != 0) return Optional.of(Pair.of(alloy, unit));
        }
        return Optional.empty();
    }
}