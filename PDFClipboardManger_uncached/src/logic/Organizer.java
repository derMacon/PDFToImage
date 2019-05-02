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
    /**
     * Default output directory
     */
    public static String OUTPUT_DIR_TEMPLATE = "%s" + File.separator + "%s_img" + File.separator;
    public static String TEMP_FILE_TYPE = "./.tempImg";

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
    public Organizer(File doc) throws IOException {
        this.selectedPdf = PDDocument.load(doc);
        this.currPageImg = new File(doc.getName() + TEMP_FILE_TYPE);
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
            System.out.println("Error: Page number out of bound");
            return false;
        }
    }

    private ClipboardImage renderImageInTemp(Integer pageNum) throws IOException {
        PDFRenderer pdfRenderer = new PDFRenderer(this.selectedPdf);
        BufferedImage bim = pdfRenderer.renderImageWithDPI(pageNum, DEFAULT_DPI, ImageType.RGB);
        ImageIOUtil.writeImage(bim, this.currPageImg.getName(), DEFAULT_DPI);
        ImageResizer.resizeImage(this.currPageImg.getPath(), this.currPageImg.getPath(), DEFAULT_WIDTH, DEFAULT_HEIGHT);

        ImageIcon icon = new ImageIcon(this.currPageImg.getPath());
        return new ClipboardImage(icon.getImage());
    }

}
