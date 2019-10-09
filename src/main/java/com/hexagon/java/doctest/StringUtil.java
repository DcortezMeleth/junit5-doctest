package com.hexagon.java.doctest;

public abstract class StringUtil {

    private StringUtil() {
    }

    /**
     * Adds cool text
     *
     * @doctest
     * StringUtil.addCoolText("Random text") == "Random text.coolText"
     * StringUtil.addCoolText("blabla") == "blabla.coolText"
     */
    String addCoolText(final String text) {
        return text + ".coolText";
    }
}
