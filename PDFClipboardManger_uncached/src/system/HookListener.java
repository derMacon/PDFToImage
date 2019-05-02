package system;

import logic.Organizer;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.io.File;

/**
 * Class that listens for a given shortcut combination and delegates the user input to an appropriate parser.
 */
public class HookListener implements NativeKeyListener {

    private static final int F7_RAW_CODE = 118;
    private static final int ZERO_RAW_CODE = 48;
    private static final int NINE_RAW_CODE = 57;

    private boolean normalMode = false;
    private StringBuilder userInput = new StringBuilder();
    private Organizer organizer;

    /**
     * Constructor initializing an organizer object with a given path
     * @param doc pdf document that will be converted
     */
    public HookListener(File doc) {
        this.organizer = new Organizer(doc);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        // needs to stay empty
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {
        // needs to stay empty
    }

    /**
     * Similar to vim the user can change into normal mode with a specific key (currently it's the f8 key). Every
     * following number represents a digit of the page num from the pdf file that should be copied to the systems
     * clipboard. The normal mode can be exited by pressing any other kay than a num key.
     *
     * @param event event called by the user and intercepted by the NativeKeyListener in the main class (using this
     *              implementation)
     */
    @Override
    public void nativeKeyPressed(NativeKeyEvent event) {
        Integer pageNum = translateToPageNum(event);
        boolean successfulCopy = this.organizer.copyToClipboard(pageNum);
        printState(pageNum, successfulCopy);
    }

    /**
     * Translates a given NativeKeyEvent to the appropriate page number.
     * @param event event triggered by the system wide hook
     * @return Page number that should be copied.
     */
    private Integer translateToPageNum(NativeKeyEvent event) {
        Integer output = null;
        if (normalMode) {
            if (isNum(event)) {
                userInput.append(event.getRawCode() - ZERO_RAW_CODE);
            } else {
                output = Integer.parseInt(this.userInput.toString());
                this.normalMode = false;
                userInput = new StringBuilder();
            }
        } else {
            normalMode = F7_RAW_CODE == event.getRawCode();
        }
        return output;
    }

    /**
     * Checks whether a given event represents a num key on the users keyboard.
     *
     * @param event event called by the user and intercepted by the NativeKeyListener in the main class (using this
     *              implementation)
     * @return true if the key corresponding to the given event represents a number (excluding the numpad).
     */
    private boolean isNum(NativeKeyEvent event) {
        int eventRawCode = event.getRawCode();
        return ZERO_RAW_CODE <= eventRawCode && NINE_RAW_CODE >= eventRawCode;
    }

    /**
     * Prints the current state of the system.
     *
     * @param pageNum           Page number that should be copied to the clipboard
     * @param copiedToClipboard flag determining if the copy process to the clipboard was successful or not.
     */
    private void printState(Integer pageNum, boolean copiedToClipboard) {
        if (null != pageNum && copiedToClipboard) {
            System.out.println("Page " + pageNum + " copied to the clipboard. Puffer cleared.");
        } else if (null != pageNum && !copiedToClipboard) {
            System.out.println("Error: Page " + pageNum + " cannot be copied to clipboard.");
        } else if (normalMode && this.userInput.length() == 0) {
            System.out.println("User entered normal mode");
        } else if (this.normalMode && this.userInput.length() > 0) {
            System.out.println("Current digit puffer: " + this.userInput);
        }
    }

}
