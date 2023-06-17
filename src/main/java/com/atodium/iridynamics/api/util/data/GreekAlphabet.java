package com.atodium.iridynamics.api.util.data;

public class GreekAlphabet {
    private static final String[] UPPERCASE = {"", "Α", "Β", "Γ", "Δ", "Ε", "Ζ", "Η", "Θ", "Ι", "Κ", "Λ", "Μ", "Ν", "Ξ", "Ο", "Π", "Ρ", "Σ", "Τ", "Υ", "Φ", "Χ", "Ψ", "Ω"};

    private static final String[] LOWERCASE = {"", "α", "β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", "κ", "λ", "μ", "ν", "ξ", "ο", "π", "ρ", "σ", "τ", "υ", "φ", "χ", "ψ", "ω"};

    public static String getUppercase(int index) {
        return index >= 1 && index <= 24 ? UPPERCASE[index] : String.valueOf(index);
    }

    public static String getLowercase(int index) {
        return index >= 1 && index <= 24 ? LOWERCASE[index] : String.valueOf(index);
    }
}