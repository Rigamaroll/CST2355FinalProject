package com.owen.cst2355finalproject;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class containing the list in memory for the ListView and image information, a DbOpener object
 * for getting the database.  Takes the Context of the opening activity when constructed.
 * The CopyOnWriteArrayList is static and thread safe so that all the activities can access the same one.
 */

public class ImageInfoWrapper {

    private final static CopyOnWriteArrayList<ImageEntry> images = new CopyOnWriteArrayList<>();

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

    /**
     * Checks if the CopyOnWriteArrayList contains the existing image by checking the dates.
     *
     * @param imageDate Date of the image to be checked.
     * @return true if the image is already in the database.
     */

    public static boolean exists(String imageDate) {

        for (ImageEntry dates : images) {
            if (dates.getDate().contentEquals(imageDate)) {
                return true;
            }
        }
        return false;
    }
}
