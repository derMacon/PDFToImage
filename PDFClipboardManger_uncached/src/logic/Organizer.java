package logic;

import clipboard.ClipboardImage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Class to process the commands given by the user.
 */
public class Organizer {

    private static final int DEFAULT_WIDTH = 930;
    private static final int DEFAULT_HEIGHT = 650;
    public static String TEMP_FILE_TYPE = "tempImg.png";
    public static String FILE_TEMPLATE = "tempImg%s.png";

    /**
     * Default output resolution of the images (in dots per inch)
     */
    public static int DEFAULT_DPI = 150;

    /**
     * Images representing each page of a given pdf document
     */
    private PDDocument selectedPdf;
    private File currPageImg;

    /**
     * Constructor setting the path for the pdf document that will be processed to the list of images.
     *
     * @param doc pdf document to process
     */
    public Organizer(File doc) {
        try {
            this.selectedPdf = PDDocument.load(doc);
            this.currPageImg = new File(TEMP_FILE_TYPE);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: cannot load the pdf document [" + doc.getName() + "]");
        }
    }

    /**
     * Most important method of this class. It copies a screenshot of the given page number from the specified pdf.
     *
     * @param pageNum number of the page that should be copied to the clipboard
     * @return true if the process ran successful else false
     */
    public boolean copyToClipboard(Integer pageNum) {
        assert null != this.selectedPdf;
        if (null != pageNum && 1 <= pageNum && this.selectedPdf.getNumberOfPages() >= pageNum) {
            try {
                ClipboardImage clipboardImage  = renderImageInTemp(pageNum);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(clipboardImage, clipboardImage);
            } catch (IOException e) {
                System.out.println("Error: Page cannot be rendered");
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Renders an image of the given page given that the page is actually existent in the underlying pdf document
     * @param pageNum page num which the user selected (page to copy to the clipboard)
     * @return an cliboard image which can be saved in the systems clipboard
     * @throws IOException Exception that will be thrown if the selected pdf document cannot be read
     */
    private ClipboardImage renderImageInTemp(Integer pageNum) throws IOException {
        PDFRenderer pdfRenderer = new PDFRenderer(this.selectedPdf);
        BufferedImage bim = pdfRenderer.renderImageWithDPI(pageNum, DEFAULT_DPI, ImageType.RGB);
//        System.out.println(String.format(FILE_TEMPLATE, pageNum));
        this.currPageImg = new File(String.format(FILE_TEMPLATE, pageNum.toString()));
        ImageIOUtil.writeImage(bim, this.currPageImg.getPath(), DEFAULT_DPI);
        ImageResizer.resizeImage(this.currPageImg.getPath(), this.currPageImg.getPath(), DEFAULT_WIDTH, DEFAULT_HEIGHT);

        ImageIcon icon = new ImageIcon(this.currPageImg.getPath());
        return new ClipboardImage(icon.getImage());
    }

}
