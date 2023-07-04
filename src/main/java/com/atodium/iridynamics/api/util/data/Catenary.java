package com.atodium.iridynamics.api.util.data;

import com.atodium.iridynamics.api.util.math.MathUtil;
import net.minecraft.world.phys.Vec3;

public class Catenary {
    private final double xl, zl, dx, dz, dp, zx, zy, a;

    public Catenary(Vec3 p1, Vec3 p2, double l) {
        this.xl = p1.x;
        this.zl = p1.z;
        this.dx = p2.x - p1.x;
        double dy = p2.y - p1.y;
        this.dz = p2.z - p1.z;
        this.dp = Math.sqrt(this.dx * this.dx + this.dz * this.dz);
        double sqrDistance = this.dx * this.dx + dy * dy + this.dz * this.dz;
        double sqrLength = sqrDistance * Math.max(l * l, 1.01);
        double length = Math.sqrt(sqrLength);
        double coefficient = Math.sqrt(sqrLength - dy * dy);
        this.a = 0.5 / MathUtil.newton((a) -> Math.sinh(this.dp * a) / a - coefficient, 1.0, 0.0, 100);
        this.zx = MathUtil.newton((x) -> this.a * (Math.sinh((this.dx - x) / this.a) + Math.sinh(x / this.a)) - length, 0.0, 0.0, 100);
        this.zy = -this.a * Math.cosh(this.zx / this.a);
    }

    public double lerpX(double t) {
        return this.xl + t * this.dx;
    }

    public double lerpY(double t) {
        return this.zy + this.a * Math.cosh((t * this.dp - this.zx) / this.a);
    }

    public double lerpZ(double t) {
        return this.zl + t * this.dz;
    }

    public Vec3 lerp(double t) {
        return new Vec3(this.lerpX(t), this.lerpY(t), this.lerpZ(t));
    }
}