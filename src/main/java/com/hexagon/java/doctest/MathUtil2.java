package com.hexagon.java.doctest;

public abstract class MathUtil2 {

    private MathUtil2() {
    }

    /**
     * Adds 2 params
     *
     * @doctest
     * MathUtil2.add2(1,2) == 3
     * MathUtil2.add2(3,2) == 5
     * MathUtil2.add2(3,2) == 6
     */
    public static int add2(int a, int b) {
        return a + b;
    }

    /**
     * Multiplies 2 params
     *
     * @doctest
     * MathUtil2.mul2(1,2) == 3
     * MathUtil2.mul2(3,2) == 5
     */
    public static int mul2(int a, int b) {
        return a * b;
    }
}
