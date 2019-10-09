package com.hexagon.java.doctest;

public abstract class MathUtil {

    private MathUtil() {
    }

    /**
     * Adds 2 params
     *
     * @doctest
     * MathUtil.add(1,2) == 3
     * com.hexagon.java.doctest.MathUtil.add(3,2) == 5
     * com.hexagon.java.doctest.MathUtil.add(3,2) == 6
     */
    public static int add(int a, int b) {
        return a + b;
    }

//    /**
//     * Multiplies 2 params
//     *
//     * @doctest
//     * MathUtil.mul(1,2) == 3
//     * MathUtil.mul(3,2) == 5
//     */
//    public static int mul(int a, int b) {
//        return a * b;
//    }
}
