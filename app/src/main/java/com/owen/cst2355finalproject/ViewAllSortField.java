package com.owen.cst2355finalproject;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum ViewAllSortField {
    IMAGE_DATE("Image Date"),
    SAVED_DATE("Saved Date"),
    TITLE("Title");

    private String sortName;

    private ViewAllSortField(final String sortName){
        this.sortName = sortName;
    }

    private final static Map<String, ViewAllSortField> SORT_FIELD_MAP = new HashMap<>();
    static {
        SORT_FIELD_MAP.put("Saved Date", SAVED_DATE);
        SORT_FIELD_MAP.put("Image Date", IMAGE_DATE);
        SORT_FIELD_MAP.put("Title", TITLE);
    }

    public String toSortName(){
        return this.sortName;
    }

    public static ViewAllSortField fromSortName(final String sortName) {
        return SORT_FIELD_MAP.get(sortName);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<String> getSortFieldKeys() {
        final List<String> sortFieldKeys = new ArrayList<String>(SORT_FIELD_MAP.keySet());
        sortFieldKeys.sort(Comparator.naturalOrder());
        return sortFieldKeys;
    }
}
