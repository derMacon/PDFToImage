package com.dermacon.app.worker;

import com.dermacon.app.Bookmark;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class Worker implements Runnable {

    private final Assignment assignment;

    public Worker(Assignment assignment) {
        this.assignment = assignment;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                render();
                // todo check
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
            } catch (IOException e) {
                System.err.println("image render error: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Renders an image of the given page given that the page is actually existent in the underlying pdf document
     *
     * @return an cliboard image which can be saved in the systems clipboard
     * @throws IOException Exception that will be thrown if the selected pdf document cannot be read
     */
    private void render() throws IOException {
        System.out.println(Thread.currentThread().getName() + " processes " +
                "page " + assignment.getBookmark().getPageNum());
//        Bookmark bm = assignment.getBookmark();
//        File file = bm.getFile();
//        PDDocument pdf = PDDocument.load(file);
//        PDFRenderer pdfRenderer = new PDFRenderer(pdf);
//        BufferedImage bim =
//                pdfRenderer.renderImageWithDPI(bm.getPageNum() - 1,
//                assignment.getDefaultDpi(),
//                ImageType.RGB);
//        File currPageImg = assignment.getCurrImgPath();
//
//        ImageIOUtil.writeImage(bim,
//                currPageImg.getPath(),
//                assignment.getDefaultDpi()
//        );
//
//        ImageResizer.resizeImage(
//                currPageImg.getPath(),
//                currPageImg.getPath(),
//                assignment.getDefaultWidth(),
//                assignment.getDefaultHeight()
//        );
    }
}