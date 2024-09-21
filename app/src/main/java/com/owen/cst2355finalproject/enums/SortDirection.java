package com.owen.cst2355finalproject.enums;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum SortDirection {
    ASC("ASC"),
    DESC("DESC");

    private String direction;
    private SortDirection(String direction) {
        this.direction = direction;
    }

    private static final Map<String, SortDirection> DIRECTION_SORT_MAP = new HashMap<>();
            static {
                DIRECTION_SORT_MAP.put("ASC", ASC);
                DIRECTION_SORT_MAP.put("DESC", DESC);
            }

    public String toDirection() {
        return this.direction;
    }

    public static SortDirection fromDirection(final String direction) {
        return DIRECTION_SORT_MAP.get(direction);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> getDirectionSortMapKeys() {
        final List<String> sortMapKeys = new ArrayList<>(DIRECTION_SORT_MAP.keySet());
        sortMapKeys.sort(Comparator.naturalOrder());
        return sortMapKeys;
    }
}
