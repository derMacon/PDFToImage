package com.dermacon.app.dataStructures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * https://crunchify.com/java-properties-file-how-to-read-config-properties-values-in-java/
 *
 * @author Crunchify.com
 */

public class PropertyValues {

    public static final String NEXT_PAGE = "nextPage";
    public static final String PREV_PAGE = "prevPage";
    public static final String GOTO_PAGE = "gotoPage";
    public static final String IMG_PATH = "imgPath";
    public static final String HISTORY_CSV = "historyCSV";

    public static final String DISPLAY_DPI = "display_dpi";
    public static final String DISPLAY_HEIGHT = "display_height";
    public static final String DISPLAY_WIDTH = "display_width";

    public static final String CLIPBOARD_DPI = "clipboard_dpi";
    public static final String CLIPBOARD_HEIGHT = "clipboard_height";
    public static final String CLIPBOARD_WIDTH = "clipboard_width";

    private int display_height = 0;
    private int display_width = 0;
    private int display_dpi = 0;

    private int clipboard_height = 0;
    private int clipboard_width = 0;
    private int clipboard_dpi = 0;

    private String imgPath = null;
    private File historyCSV = null;
    private int next_command;
    private int prev_command;
    private int goto_command;

    private File propertiesFile;

    public PropertyValues(File file) throws IOException {
        this.propertiesFile = file;
        initPropValues();

    }

    private void initPropValues() throws IOException {

        assert propertiesFile != null;
        InputStream inputStream = null;

        try {
            Properties prop = new Properties();
            inputStream = new FileInputStream(propertiesFile);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propertiesFile.getName() +
                        "' not found in the classpath");
            }

            display_width = Integer.parseInt(prop.getProperty(DISPLAY_WIDTH));
            display_height = Integer.parseInt(prop.getProperty(DISPLAY_HEIGHT));
            display_dpi = Integer.parseInt(prop.getProperty(DISPLAY_DPI));

            clipboard_width = Integer.parseInt(prop.getProperty(CLIPBOARD_WIDTH));
            clipboard_height = Integer.parseInt(prop.getProperty(CLIPBOARD_HEIGHT));
            clipboard_dpi = Integer.parseInt(prop.getProperty(CLIPBOARD_DPI));

            prev_command = Integer.parseInt(prop.getProperty(NEXT_PAGE));
            next_command = Integer.parseInt(prop.getProperty(PREV_PAGE));
            goto_command = Integer.parseInt(prop.getProperty(GOTO_PAGE));
            imgPath = prop.getProperty(IMG_PATH);
            historyCSV = new File(prop.getProperty(HISTORY_CSV));

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
    }


    public int getDisplay_height() {
        return display_height;
    }

    public int getDisplay_width() {
        return display_width;
    }

    public int getDisplay_dpi() {
        return display_dpi;
    }

    public int getClipboard_height() {
        return clipboard_height;
    }

    public int getClipboard_width() {
        return clipboard_width;
    }

    public int getClipboard_dpi() {
        return clipboard_dpi;
    }

    public String getImgPath() {
        return imgPath;
    }

    public File getHistoryCSV() {
        return historyCSV;
    }

    public int getNext_command() {
        return next_command;
    }

    public int getPrev_command() {
        return prev_command;
    }

    public int getGoto_command() {
        return goto_command;
    }

    private void setNext_command(int next_command) throws IOException {
        this.next_command = next_command;
        updatePropertiesFile(PropertyValues.NEXT_PAGE,
                String.valueOf(next_command));
    }

    private void updatePropertiesFile(String key, String value) throws IOException {
        InputStream inputStream = null;
        try {
            Properties prop = new Properties();
            inputStream = new FileInputStream(propertiesFile);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propertiesFile.getName() +
                        "' not found in the classpath");
            }
//            System.out.println("old value: " + prop.getProperty(key));
//            System.out.println("setting: " + key + ", " + value + ", " + propertiesFile);
            prop.setProperty(key, value);

            try {
                prop.store(new FileOutputStream(propertiesFile.getName()), null);
            } catch (IOException ex) {
                System.out.println(ex);
            }


        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }

    }

    private void setPrev_command(int prev_command) throws IOException {
        this.prev_command = prev_command;
        updatePropertiesFile(PropertyValues.PREV_PAGE,
                String.valueOf(next_command));
    }

    private void setGoto_command(int goto_command) throws IOException {
        this.goto_command = goto_command;
        updatePropertiesFile(PropertyValues.GOTO_PAGE,
                String.valueOf(next_command));
    }

    public void setMovementRawCode(String key, int rawCode) {
        try {
            switch (key) {
                case PropertyValues.PREV_PAGE:
                    setPrev_command(rawCode);
                    break;
                case PropertyValues.NEXT_PAGE:
                    setNext_command(rawCode);
                    break;
                case PropertyValues.GOTO_PAGE:
                    setGoto_command(rawCode);
                    break;
                default:
                    // todo
            }
        } catch (IOException e) {
            // todo
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return "props: {"
                + "\n\theight: " + display_height
                + "\n\twidth: " + display_width
                + "\n\tdpi: " + display_dpi
                + "\n\timgPath: " + imgPath
                + "\n\thistoryCSV: " + historyCSV
                + "\n\tnext_rawCode: " + next_command
                + "\n\tprev_rawCode: " + prev_command
                + "\n\tgoto_rawCode: " + goto_command
                + "\n}";
    }

}