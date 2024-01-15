import com.atodium.iridynamics.api.util.math.BigFraction;

import java.math.BigInteger;

public class Test {
    public static void main(String[] args) {
        BigFraction x = new BigFraction(1, 2022);
        for (int i = 0; i <= 2020; i++) x = x.add(new BigFraction(c(i, 2021).multiply(BigInteger.valueOf((i % 2 == 1) ? 1 : -1)), BigInteger.valueOf(i + 2)));
        System.out.println(x);
    }

    private static BigInteger c(int m, int n) {
        BigInteger result = BigInteger.ONE;
        for (int i = 0; i < m; i++) result = result.multiply(BigInteger.valueOf(n - i));
        for (int i = 1; i <= m; i++) result = result.divide(BigInteger.valueOf(i));
        return result;
    }
}