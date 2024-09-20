package com.owen.cst2355finalproject;

import android.os.Build;
import android.text.style.TtsSpan;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class containing the list in memory for the ListView and image information, a DbOpener object
 * for getting the database.  Takes the Context of the opening activity when constructed.
 * The CopyOnWriteArrayList is static and thread safe so that all the activities can access the same one.
 */

public class ImageInfoWrapper {

    private final static CopyOnWriteArrayList<ImageEntry> images = new CopyOnWriteArrayList<>();
    private final static Comparator<ImageEntry> DATE_SORT = (o1, o2) -> {
        try {
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            final Date date1 = dateFormat.parse(o1.getDate());
            final Date date2 = dateFormat.parse(o2.getDate());
            return date1.compareTo(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };

    public static ImageEntry getImages(int position) {
        return images.get(position);
    }

    public static void setImages(ImageEntry image) {
        images.add(image);
    }

    public static void deleteImages(int position) {
        images.remove(position);
    }

    public static int listSize() {
        return images.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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

    /**
     * Checks if the CopyOnWriteArrayList contains the existing image by checking the dates.
     *
     * @param imageDate Date of the image to be checked.
     * @return true if the image is already in the database.
     */

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static boolean exists(String imageDate) {
        return images.stream().anyMatch(x -> x.getDate().contentEquals(imageDate));
    }
}
