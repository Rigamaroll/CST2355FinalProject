package com.owen.cst2355finalproject.enums;

import java.util.Map;

public enum BooleanKeyword {
    AND,
    OR,
    NOT;

    private static final Map<String, BooleanKeyword> BOOLEAN_WORD_BY_STRING =
            Map.of("AND", AND, "OR", OR, "NOT", NOT);

    public static BooleanKeyword getBooleanKeywordByString(final String string) {
        if (BOOLEAN_WORD_BY_STRING.containsKey(string)) {
            return BOOLEAN_WORD_BY_STRING.get(string);
        }
        return null;
    }
}
