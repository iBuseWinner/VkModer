package ru.ibusewinner.fundaily.vkmoder.utils;

public class TextNormalizer {

    public static String replaceAll(String text) {
        return text.toLowerCase().replace("?", "")
                .replace("!", "")
                .replace(".", "")
                .replace("@", "а")
                .replace("#", "")
                .replace("$", "")
                .replace("%", "")
                .replace("^", "")
                .replace("&", "")
                .replace("*", "")
                .replace("(", "")
                .replace(")", "")
                .replace("\"", "")
                .replace("№", "")
                .replace(";", "")
                .replace(":", "")
                .replace("-", "")
                .replace("+", "")
                .replace("=", "")
                .replace("[", "")
                .replace("]", "")
                .replace("{", "")
                .replace("}", "")
                .replace("6", "б")
                .replace("b", "б")
                .replace("ё", "е")
                .replace("e", "е");
    }
}
