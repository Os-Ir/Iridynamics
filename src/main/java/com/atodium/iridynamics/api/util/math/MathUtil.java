package com.atodium.iridynamics.api.util.math;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Random;

public class MathUtil {
    public static double positiveGaussian(Random random, double stddev) {
        return Math.abs(random.nextGaussian(0.0, stddev));
    }

    public static boolean isEquals(float a, float b) {
        return isEquals(a, b, 1e-5f);
    }

    public static boolean isEquals(double a, double b) {
        return isEquals(a, b, 1e-5);
    }

    public static boolean isEquals(float a, float b, float err) {
        return Math.abs(a - b) <= a * err;
    }

    public static boolean isEquals(double a, double b, double err) {
        return Math.abs(a - b) <= a * err;
    }

    public static Vec3 minus(Vec3 a, BlockPos b) {
        return a.add(-b.getX(), -b.getY(), -b.getZ());
    }

    public static Vec3 minus(Vec3 a, Vec3 b) {
        return a.add(-b.x, -b.y, -b.z);
    }

    public static Vec3 transformPosition(Vec3 origin, Direction to) {
        return transformPosition(origin, Direction.SOUTH, to);
    }

    public static Vec3 transformPosition(Vec3 origin, Direction from, Direction to) {
        int change = to.get2DDataValue() - from.get2DDataValue();
        if (change < 0) change += 4;
        switch (change) {
            case 1 -> {
                return new Vec3(origin.z, origin.y, 1.0 - origin.x);
            }
            case 2 -> {
                return new Vec3(1.0 - origin.x, origin.y, 1.0 - origin.z);
            }
            case 3 -> {
                return new Vec3(1.0 - origin.z, origin.y, origin.x);
            }
        }
        return origin;
    }

    public static boolean between(int value, int min, int max) {
        return value >= min && value <= max;
    }

    public static boolean between(long value, long min, long max) {
        return value >= min && value <= max;
    }

    public static boolean between(float value, float min, float max) {
        return value >= min && value <= max;
    }

    public static boolean between(double value, double min, double max) {
        return value >= min && value <= max;
    }

    public static int sum(int... values) {
        int ans = 0;
        for (int value : values) {
            ans += value;
        }
        return ans;
    }

    public static long sum(long... values) {
        long ans = 0;
        for (long value : values) {
            ans += value;
        }
        return ans;
    }

    public static float sum(float... values) {
        float ans = 0;
        for (float value : values) {
            ans += value;
        }
        return ans;
    }

    public static double sum(double... values) {
        double ans = 0;
        for (double value : values) {
            ans += value;
        }
        return ans;
    }

    public static int average(int... values) {
        return sum(values) / values.length;
    }

    public static long average(long... values) {
        return sum(values) / values.length;
    }

    public static float average(float... values) {
        return sum(values) / values.length;
    }

    public static double average(double... values) {
        return sum(values) / values.length;
    }

    public static int getTrueBits(int num) {
        int cnt = 0;
        while (num != 0) {
            num &= (num - 1);
            cnt++;
        }
        return cnt;
    }

    public static int getTrueBits(long num) {
        int cnt = 0;
        while (num != 0) {
            num &= (num - 1);
            cnt++;
        }
        return cnt;
    }

    public static int gcd(int a, int b) {
        int t;
        while (b > 0) {
            t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    public static int lcm(int a, int b) {
        return a * b / gcd(a, b);
    }

    public static long gcd(long a, long b) {
        long t;
        while (b > 0) {
            t = b;
            b = a % b;
            a = t;
        }
        return a;
    }

    public static long lcm(long a, long b) {
        return a * b / gcd(a, b);
    }

    public static int[] getRandomSortedArray(int length) {
        int[] copy = new int[length];
        for (int i = 0; i < length; i++) {
            copy[i] = i;
        }
        Random rand = new Random();
        for (int i = length - 1; i >= 0; i--) {
            int idx = rand.nextInt(i + 1);
            int val = copy[idx];
            copy[idx] = copy[i];
            copy[i] = val;
        }
        return copy;
    }

    public static int[] getRandomSortedArray(int[] array) {
        int[] copy = Arrays.copyOf(array, array.length);
        Random rand = new Random();
        for (int i = copy.length - 1; i >= 0; i--) {
            int idx = rand.nextInt(i + 1);
            int val = copy[idx];
            copy[idx] = copy[i];
            copy[i] = val;
        }
        return copy;
    }

    public static double getTriangularWave(int period, double min, double max) {
        double x = ((double) (System.currentTimeMillis() % period)) / period;
        return Mth.lerp((x > 0.5 ? 1 - x : x) * 2, min, max);
    }
}