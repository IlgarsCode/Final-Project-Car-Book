package com.example.demo.util;

import java.text.Normalizer;

public final class SlugUtil {
    private SlugUtil() {}

    public static String slugify(String input) {
        if (input == null) return "";
        String now = input.trim().toLowerCase();
        now = Normalizer.normalize(now, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        now = now.replaceAll("[^a-z0-9\\s-]", "");
        now = now.replaceAll("\\s+", "-");
        now = now.replaceAll("-{2,}", "-");
        now = now.replaceAll("^-|-$", "");
        return now;
    }
}
