package com.owen.cst2355finalproject;

import com.owen.cst2355finalproject.enums.SortDirection;
import com.owen.cst2355finalproject.enums.ViewAllSortField;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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

    public static void sortList(final ViewAllSortField field, final SortDirection direction) {
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
            final ViewAllSortField field) {

        images.addAll(FILTERED_OUT_IMAGES);
        if (StringUtils.isEmpty(keywords)) {
            FILTERED_OUT_IMAGES.clear();
        } else {
            FILTERED_OUT_IMAGES = images.stream()
                    .filter(image ->
                            !(StringUtils.containsIgnoreCase(image.getTitle(), keywords)
                                    || StringUtils.containsIgnoreCase(image.getExplanation(), keywords)))
                    .collect(Collectors.toList());
            images.removeAll(FILTERED_OUT_IMAGES);
        }
        sortList(field, direction);
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
