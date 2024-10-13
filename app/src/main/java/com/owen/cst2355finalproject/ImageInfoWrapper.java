package com.owen.cst2355finalproject;

import com.owen.cst2355finalproject.enums.BooleanKeyword;
import com.owen.cst2355finalproject.enums.SortDirection;
import com.owen.cst2355finalproject.enums.ViewAllFilterField;
import com.owen.cst2355finalproject.enums.ViewAllSortField;
import com.owen.cst2355finalproject.pojos.ImageEntry;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Class containing the list in memory for the ListView and image information, a DbOpener object
 * for getting the database.  Takes the Context of the opening activity when constructed.
 * The CopyOnWriteArrayList is static and thread safe so that all the activities can access the same one.
 */

public class ImageInfoWrapper {

    private final static CopyOnWriteArrayList<ImageEntry> images = new CopyOnWriteArrayList<>();
    private static List<ImageEntry> FILTERED_OUT_IMAGES = new ArrayList<>();
    private final static Comparator<ImageEntry> DATE_SORT = (o1, o2) -> {
        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN);
            final Date date1 = dateFormat.parse(o1.getDate());
            final Date date2 = dateFormat.parse(o2.getDate());
            return date1.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };

    public static ImageEntry getImage(final int position) {
        return images.get(position);
    }
    public static void addImage(final ImageEntry image) {
        images.add(image);
    }
    public static void deleteImage(final int position) {
        images.remove(position);
    }
    public static int listSize() {
        return images.size();
    }
    public static int getTotalImageCount(){return (images.size() + FILTERED_OUT_IMAGES.size());}
    public static List<ImageEntry> getImages() { return images; }

    public static void sortList(final ViewAllSortField field, final SortDirection direction) {
        if (images.isEmpty()) {
            return;
        }
        final boolean isForward = direction == SortDirection.ASC;
        switch (field) {
            case TITLE:
                if (isForward) {
                    images.sort(Comparator.comparing(ImageEntry::getTitle));
                } else {
                    images.sort(Comparator.comparing(ImageEntry::getTitle).reversed());
                }
                break;
            case IMAGE_DATE:
                if (isForward) {
                    images.sort(DATE_SORT);
                } else {
                    images.sort(DATE_SORT.reversed());
                }
                break;
            case SAVED_DATE:
                if (isForward) {
                    images.sort(Comparator.comparingLong(ImageEntry::getId));
                } else {
                    images.sort(Comparator.comparingLong(ImageEntry::getId).reversed());
                }
                break;
        }
    }

    public static void filterByKeywords(
            final String keywords,
            final SortDirection direction,
            final ViewAllSortField sortField,
            final ViewAllFilterField filterField) {
        images.addAll(FILTERED_OUT_IMAGES);
        if (StringUtils.isEmpty(keywords)) {
            FILTERED_OUT_IMAGES.clear();
        } else {
            runKeywordFilter(keywords, filterField);
        }
        sortList(sortField, direction);
    }

    private static void runKeywordFilter(
            final String keywords,
            final ViewAllFilterField filterField) {
        final Map<BooleanKeyword, List<String>> keywordsMap =
                populateKeywordsMap(populateAllTokens(keywords));
        switch (filterField) {
            case TITLE:
                FILTERED_OUT_IMAGES = images.stream()
                        .filter(image -> !filterTitle(image, keywordsMap))
                        .collect(Collectors.toList());
                break;
            case EXPLANATION:
                FILTERED_OUT_IMAGES = images.stream()
                        .filter(image -> !filterExplanation(image, keywordsMap))
                        .collect(Collectors.toList());
                break;
            case ALL:
                FILTERED_OUT_IMAGES = images.stream()
                        .filter(image -> !filterAll(image, keywordsMap))
                        .collect(Collectors.toList());
                images.removeAll(FILTERED_OUT_IMAGES);
                break;
        }
        images.removeAll(FILTERED_OUT_IMAGES);
    }

    private static boolean filterTitle(final ImageEntry image, final Map<BooleanKeyword, List<String>> keywordsMap) {
       return filterAnyTerms(image.getTitle(), keywordsMap.get(BooleanKeyword.OR))
                && filterMustTerms(image.getTitle(), keywordsMap.get(BooleanKeyword.AND))
                && filterNotTerms(image.getTitle(), keywordsMap.get(BooleanKeyword.NOT));
    }

    private static boolean filterExplanation(final ImageEntry image, final Map<BooleanKeyword, List<String>> keywordsMap) {
        return filterAnyTerms(image.getExplanation(), keywordsMap.get(BooleanKeyword.OR))
                && filterMustTerms(image.getExplanation(), keywordsMap.get(BooleanKeyword.AND))
                && filterNotTerms(image.getExplanation(), keywordsMap.get(BooleanKeyword.NOT));
    }

    private static boolean filterAll(final ImageEntry image, final Map<BooleanKeyword, List<String>> keywordsMap) {
        return filterTitle(image, keywordsMap) || filterExplanation(image, keywordsMap);
    }

    private static boolean filterMustTerms (final String toCheck, final List<String> keywords) {
        for (final String keyword : keywords) {
            if(!StringUtils.containsIgnoreCase(toCheck, keyword)) {
                return false;
            }
        }
        return true;
    }

    private static boolean filterAnyTerms(final String toCheck, final List<String> keywords) {
        if (keywords.isEmpty())
            return true;
        for (final String keyword : keywords) {
            if(StringUtils.containsIgnoreCase(toCheck, keyword)) {
                return true;
            }
        }
        return false;
    }

    private static boolean filterNotTerms(final String toCheck, final List<String> keywords) {
        for (final String keyword : keywords) {
            if(StringUtils.containsIgnoreCase(toCheck, keyword)) {
                return false;
            }
        }
        return true;
    }

    private static List<String> populateAllTokens(final String keywords) {
        final StringTokenizer tokenizer = new StringTokenizer(keywords);
        final List<String> allTokens = new ArrayList<>();
        boolean openParenthesis = false;
        final List<String> withinParenthesisToken = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            final String token = StringUtils.trim(tokenizer.nextToken());
            if (token.startsWith("\"")) {
                openParenthesis = true;
                withinParenthesisToken.add(StringUtils.stripStart(token,"\""));
            } else if (token.endsWith("\"")) {
                openParenthesis = false;
                withinParenthesisToken.add(StringUtils.stripEnd(token,"\""));
                allTokens.add(String.join(StringUtils.SPACE, withinParenthesisToken));
                withinParenthesisToken.clear();
            } else if (openParenthesis && !StringUtils.contains(token,"\"")) {
                withinParenthesisToken.add(token);
            } else {
                allTokens.add(token);
            }
        }
        return allTokens;
    }

    private static Map<BooleanKeyword, List<String>> populateKeywordsMap(
            final List<String> allTokens) {
        BooleanKeyword booleanWord = BooleanKeyword.OR;
        final Map<BooleanKeyword, List<String>> keywordsMap = new HashMap<>();
        keywordsMap.put(BooleanKeyword.AND, new ArrayList<>());
        keywordsMap.put(BooleanKeyword.NOT, new ArrayList<>());
        keywordsMap.put(BooleanKeyword.OR, new ArrayList<>());

        for (final String token : allTokens) {
            final BooleanKeyword tempBooleanWord = BooleanKeyword.getBooleanKeywordByString(token);
            if (tempBooleanWord != null) {
                booleanWord = tempBooleanWord;
                continue;
            }
            keywordsMap.get(booleanWord).add(token);
        }
        return keywordsMap;
    }

    /**
     * Checks if the CopyOnWriteArrayList contains the existing image by checking the dates.
     *
     * @param imageDate Date of the image to be checked.
     * @return true if the image is already in the database.
     */

    public static boolean exists(String imageDate) {
        return (images.stream().anyMatch(x -> x.getDate().contentEquals(imageDate))
                || FILTERED_OUT_IMAGES.stream().anyMatch(x ->x.getDate().contentEquals(imageDate)));
    }
}
