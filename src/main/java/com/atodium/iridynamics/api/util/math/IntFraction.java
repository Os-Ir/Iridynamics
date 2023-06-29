package com.atodium.iridynamics.api.util.math;

import java.io.Serial;

public class IntFraction extends Number implements Comparable<IntFraction> {
    @Serial
    private static final long serialVersionUID = 2L;

    private final int numerator, denominator;

    public final static IntFraction ZERO = new IntFraction(0);
    public final static IntFraction ONE = new IntFraction(1);
    public final static IntFraction NEG_ONE = new IntFraction(-1);

    public IntFraction(int numerator, int denominator) {
        if (denominator == 0) throw new ArithmeticException("Divide by zero");
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
        int gcd = MathUtil.gcd(numerator, denominator);
        this.numerator = numerator / gcd;
        this.denominator = denominator / gcd;
    }

    public IntFraction(int numerator) {
        this(numerator, 1);
    }

    public IntFraction add(IntFraction f) {
        if (f == null) throw new IllegalArgumentException("Null argument");
        return new IntFraction(this.numerator * f.denominator + this.denominator * f.numerator, this.denominator * f.denominator);
    }

    public IntFraction add(int b) {
        return new IntFraction(this.numerator + this.denominator * b, this.denominator);
    }

    public IntFraction subtract(IntFraction f) {
        if (f == null) throw new IllegalArgumentException("Null argument");
        return new IntFraction(this.numerator * f.denominator - this.denominator * f.numerator, this.denominator * f.denominator);
    }

    public IntFraction subtract(int b) {
        return new IntFraction(this.numerator - this.denominator * b, this.denominator);
    }

    public IntFraction multiply(IntFraction f) {
        if (f == null) throw new IllegalArgumentException("Null argument");
        return new IntFraction(this.numerator * f.numerator, this.denominator * f.denominator);
    }

    public IntFraction multiply(int b) {
        return new IntFraction(this.numerator * b, this.denominator);
    }

    public IntFraction divide(IntFraction f) {
        if (f == null) throw new IllegalArgumentException("Null argument");
        return new IntFraction(this.numerator * f.denominator, this.denominator * f.numerator);
    }

    public IntFraction divide(int b) {
        return new IntFraction(this.numerator, this.denominator * b);
    }

    public IntFraction reciprocal() {
        if (this.numerator == 0) throw new ArithmeticException("Divide by zero");
        return new IntFraction(this.denominator, this.numerator);
    }

    public IntFraction complement() {
        return new IntFraction(this.denominator - this.numerator, this.denominator);
    }

    public IntFraction negate() {
        return new IntFraction(-this.numerator, this.denominator);
    }

    public int signum() {
        return Integer.compare(this.numerator, 0);
    }

    public IntFraction abs() {
        return this.signum() < 0 ? this.negate() : this;
    }

    @Override
    public String toString() {
        return this.numerator + "/" + this.denominator;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IntFraction f)) return false;
        return this.numerator == f.numerator && this.denominator == f.denominator;
    }

    @Override
    public int hashCode() {
        return (31 + Integer.hashCode(this.numerator)) * 31 + Integer.hashCode(this.denominator);
    }

    @Override
    public int compareTo(IntFraction f) {
        if (f == null) throw new IllegalArgumentException("Null argument");
        if (this.signum() != f.signum()) return this.signum() - f.signum();
        if (this.denominator == f.denominator) return Integer.compare(this.numerator, f.numerator);
        return Integer.compare(this.numerator * f.denominator, this.denominator * f.numerator);
    }

    public IntFraction min(IntFraction f) {
        if (f == null) throw new IllegalArgumentException("Null argument");
        return (this.compareTo(f) <= 0 ? this : f);
    }

    public IntFraction max(IntFraction f) {
        if (f == null) throw new IllegalArgumentException("Null argument");
        return (this.compareTo(f) >= 0 ? this : f);
    }

    public int getNumerator() {
        return this.numerator;
    }

    public int getDenominator() {
        return this.denominator;
    }

    @Override
    public byte byteValue() {
        return (byte) Math.max(Byte.MIN_VALUE, Math.min(Byte.MAX_VALUE, this.longValue()));
    }

    @Override
    public short shortValue() {
        return (short) Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, this.longValue()));
    }

    @Override
    public int intValue() {
        return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, this.longValue()));
    }

    @Override
    public long longValue() {
        return Math.round(this.doubleValue());
    }

    @Override
    public float floatValue() {
        return (float) this.doubleValue();
    }

    @Override
    public double doubleValue() {
        return ((double) this.numerator) / this.denominator;
    }
}