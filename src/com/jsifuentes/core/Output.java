package com.jsifuentes.core;

/**
 * Created by Jacob on 11/18/2014.
 */
public class Output {
    public static void raw(String input) { System.out.println(input); }

    public static void debug(String input) {
        System.out.println("[DEBUG]: " + input);
    }

    public static void warning(String input) {
        System.out.println("[WARNING]: " + input);
    }

    public static void error(String input) {
        System.out.println("[ERROR]: " + input);
    }

    public static void kill() {
        System.exit(0);
    }
}
