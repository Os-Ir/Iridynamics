package com.atodium.iridynamics.api.material.alloy;

import com.atodium.iridynamics.api.liquid.ILiquidContainer;
import com.atodium.iridynamics.api.material.type.MaterialBase;
import com.atodium.iridynamics.api.util.math.MathUtil;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

public class Alloy {
    private final MaterialBase material;
    private final Object2IntMap<MaterialBase> composition;
    private final int sumUnits;

    public Alloy(MaterialBase alloy, Object2IntMap<MaterialBase> composition) {
        this.material = alloy;
        this.composition = new Object2IntOpenHashMap<>();
        int sum = 0;
        int gcd = 0;
        for (Object2IntMap.Entry<MaterialBase> entry : composition.object2IntEntrySet()) {
            int unit = Math.max(entry.getIntValue(), 0);
            if (unit == 0) continue;
            sum += unit;
            if (gcd == 0) gcd = unit;
            else gcd = MathUtil.gcd(gcd, unit);
        }
        for (Object2IntMap.Entry<MaterialBase> entry : composition.object2IntEntrySet())
            this.composition.put(entry.getKey(), Math.max(entry.getIntValue(), 0) / gcd);
        this.sumUnits = sum / gcd;
    }

    public MaterialBase material() {
        return this.material;
    }

    public Object2IntMap<MaterialBase> composition() {
        return this.composition;
    }

    public int sumUnits() {
        return this.sumUnits;
    }

    public int maxAlloyUnits(ILiquidContainer container, double temperature, boolean consume) {
        int k = Integer.MAX_VALUE;
        for (MaterialBase material : this.composition().keySet())
            if (!container.hasLiquidMaterial(material, temperature)) return 0;
        for (Object2IntMap.Entry<MaterialBase> entry : this.composition.object2IntEntrySet())
            k = Math.min(k, container.getLiquidMaterialUnit(entry.getKey(), temperature) / entry.getIntValue());
        if (consume) {
            for (Object2IntMap.Entry<MaterialBase> entry : this.composition.object2IntEntrySet())
                container.addMaterial(entry.getKey(), -k * entry.getIntValue());
            container.addMaterial(this.material, k * this.sumUnits);
        }
        return k * this.sumUnits;
    }

    public boolean validate(ILiquidContainer container, double temperature) {
        if (this.composition.size() != container.getMaterialTypes()) return false;
        for (MaterialBase material : this.composition().keySet())
            if (!container.hasLiquidMaterial(material, temperature)) return false;
        int k = 0;
        for (Object2IntMap.Entry<MaterialBase> entry : this.composition().object2IntEntrySet()) {
            if (k == 0) k = container.getLiquidMaterialUnit(entry.getKey(), temperature) / entry.getIntValue();
            else if (k != container.getLiquidMaterialUnit(entry.getKey(), temperature) / entry.getIntValue())
                return false;
        }
        return true;
    }
}