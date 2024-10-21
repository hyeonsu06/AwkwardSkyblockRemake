package io.hyonsu06.core.functions;

public class NumberTweaks {
    public static double flip(double value) {
        return value + (value * -2);
    }
    public static double asPercentageMultiplier(double value) {
        return 1 + (value / 100);
    }

    public static String addPlusIfPositive(double value) {
        if (value > 0) return "+" + value;
        return String.valueOf(value);
    }
    public static int getDenominator(double input) {
        return (int) Math.round(1 / input);
    }

    // This method is based on generation of ChatGPT
    public static String numberFormat(double number) {
        // Check if the number is a whole number (no decimal part)
        if (number == Math.floor(number)) {
            // Format as an integer
            return String.format("%,d", (long) number);
        } else {
            // Format with decimal points (if needed)
            return String.format("%,1f", number);
        }
    }

    public static String shortNumber(double value) {
        if (value >= 1_000_000_000_000_000_000d) {
            return String.format("%.0fQi", value / 1_000_000_000_000_000_000d);
        } else if (value >= 1_000_000_000_000_000d) {
            return String.format("%.0fQa", value / 1_000_000_000_000_000d);
        } else if (value >= 1_000_000_000_000d) {
            return String.format("%.0fT", value / 1_000_000_000_000d);
        } else if (value >= 1_000_000_000) {
            return String.format("%.0fB", value / 1_000_000_000);
        } else if (value >= 1_000_000) {
            return String.format("%.0fM", value / 1_000_000);
        } else if (value >= 1_000) {
            return String.format("%.0fk", value / 1_000);
        } else {
            return String.valueOf((long) value); // No formatting for small numbers
        }
    }
}
