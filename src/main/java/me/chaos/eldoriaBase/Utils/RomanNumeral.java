package me.chaos.eldoriaBase.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class RomanNumeral {

    private static final Map<Integer, String> VALUES = new LinkedHashMap<>();

    static {
        VALUES.put(1000, "M");
        VALUES.put(900, "CM");
        VALUES.put(500, "D");
        VALUES.put(400, "CD");
        VALUES.put(100, "C");
        VALUES.put(90, "XC");
        VALUES.put(50, "L");
        VALUES.put(40, "XL");
        VALUES.put(10, "X");
        VALUES.put(9, "IX");
        VALUES.put(5, "V");
        VALUES.put(4, "IV");
        VALUES.put(1, "I");
    }

    private RomanNumeral() {}

    public static String toRoman(int number) {
        if (number <= 0) return String.valueOf(number);

        StringBuilder sb = new StringBuilder();
        int remaining = number;
        for (Map.Entry<Integer, String> entry : VALUES.entrySet()) {
            while (remaining >= entry.getKey()) {
                sb.append(entry.getValue());
                remaining -= entry.getKey();
            }
        }
        return sb.toString();
    }
}
