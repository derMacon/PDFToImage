package com.dermacon.app.worker;

import com.dermacon.app.dataStructures.Bookmark;

import java.io.File;

public class Assignment {

    private final Bookmark bookmark;

    /**
     * True: States if the assigned (rendered) page from the bookmark should
     * actually be displayed on the gui
     * False: run as a simple background assignment (buffer for feature renders)
     */
    private boolean displayed_on_gui = true;

    private final String imgPath;

    public Assignment(Bookmark bookmark, String imgPath) {
        this.bookmark = bookmark.copy();
        this.imgPath = imgPath;
    }

    public File translateCurrImgPath() {
        return new File(imgPath + File.separator
                + removeExtension(bookmark.getFile().getName()) + "_"
                + ((bookmark.getPageIdx() + 1) + ".png"));
    }

    public Bookmark getBookmark() {
        return bookmark;
    }

    public Assignment displayGui(boolean displayed_on_gui) {
        this.displayed_on_gui = displayed_on_gui;
        return this;
    }

    public boolean shouldDisplayGui() {
        return displayed_on_gui;
    }

    /**
     * Removes the extension from a given full qualified file.
     * @param fullFileName full qualified file name
     * @return name without extension
     */
    protected static String removeExtension(String fullFileName) {
        int idx = fullFileName.lastIndexOf('.');
        if (idx > 0) {
            fullFileName = fullFileName.substring(0, idx);
        }
        return fullFileName;
    }

    public Assignment prev() {
        Bookmark newCopy = bookmark.copy();
        // following function is bound safe, no need to check if copy is
        // valid afterwards
        newCopy.decPageIdx();
        return new Assignment(newCopy, imgPath);
    }

    public Assignment next() {
        Bookmark newCopy = bookmark.copy();
        // following function is bound safe, no need to check if copy is
        // valid afterwards
        newCopy.incPageIdx();
        return new Assignment(newCopy, imgPath);
    }

    @Override
    public String toString() {
        return "Assignment: " + bookmark.toString() + ",  displayed: " + this.displayed_on_gui;
    }

}
