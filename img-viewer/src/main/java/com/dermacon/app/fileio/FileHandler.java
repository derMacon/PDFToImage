package com.dermacon.app.fileio;

import com.dermacon.app.dataStructures.PropertyValues;
import com.dermacon.app.dataStructures.Bookmark;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.swing.*;
import java.awt.print.Book;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileHandler {

    private static final String PROPERTIES_DEFAULT_PATH = "config.properties";
    private static final String HISTORY_DEFAULT_PATH = "pdf2img_history.csv";
    private static final String HISTORY_HEADING = "pdf,page\n";

    private static final String PDF_CSV_CAPTION = "pdf";
    private static final String PAGENUM_CSV_CAPTION = "page";

    private List<Bookmark> bookmarks = new LinkedList<>();
    private PropertyValues props;

    public FileHandler() throws IOException, CSVException {
        this(PROPERTIES_DEFAULT_PATH);
    }

    public FileHandler(String... args) throws IOException, CSVException {
        this(args != null && args.length == 1 ?
                args[0] : PROPERTIES_DEFAULT_PATH);
    }


    public FileHandler(String propertiesPath) throws IOException, CSVException {
        File f = new File(propertiesPath);
        if (!f.exists() || f.isDirectory()) {
            f = copyConfigResource();
        }
        props = new PropertyValues(f);

        initBookmarks();
    }

    public void initBookmarks() throws IOException, CSVException {
        Map<String, List<String>> map = CSVReader.readCSV(props.getHistoryCSV());

        if (map == null || map.size() != 2) {
            throw new CSVException("csv file - wrong column names, please " +
                    "delete csv file and let program reload.");
        }

        List<File> pdfs = map.get(PDF_CSV_CAPTION).stream()
                        .map(e -> new File(e)).collect(Collectors.toList());
        List<Integer> pageNums = map.get(PAGENUM_CSV_CAPTION).stream()
                .map(e -> Integer.parseInt(e)).collect(Collectors.toList());

        if (pdfs.size() != pageNums.size()) {
            throw new CSVException("csv file - column lengths vary, please " +
                    "delete csv file and let program reload.");
        }

        for (int i = 0; i < pdfs.size(); i++) {
            bookmarks.add(new Bookmark(pdfs.get(i), pageNums.get(i)));
        }
    }

    public List<Bookmark> getBookmarks() {
        return bookmarks;
    }

    public PropertyValues getProps() {
        return props;
    }

    private File copyConfigResource() {
        System.out.println("copying default config properties to: "
                + System.getProperty("user.dir"));
        File d = new File(PROPERTIES_DEFAULT_PATH);
        try {
            File r = MyFileUtils.findResourceFile(PROPERTIES_DEFAULT_PATH);
            System.out.println("exists:");
            System.out.println(r.exists());
            FileUtils.copyFile(r, d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return d;
    }

    public void appendHistory(Bookmark bookmark) throws IOException {
        // todo
        File history = props.getHistoryCSV();
        String input = "";
        if (history == null || !history.exists() || history.isDirectory()) {
            history = new File(HISTORY_DEFAULT_PATH);
            input = HISTORY_HEADING;
        }
        input += bookmark.getFile().getPath() + ',' + bookmark.getPageNum();
        FileUtils.writeStringToFile(history, input);
    }

    public Bookmark openNewBookmark() {
        System.out.println("todo open file explorer");
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        int opt = chooser.showOpenDialog(null);
        File out = null;
        if (opt == JFileChooser.APPROVE_OPTION) {
            out = chooser.getSelectedFile();
            System.out.println("User selected: " + out.getPath());
        }
        return new Bookmark(out, 0);
    }

}
