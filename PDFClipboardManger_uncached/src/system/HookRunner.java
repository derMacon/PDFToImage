package system;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HookRunner implements Runnable {

    private static boolean running = true;
    private File selectedPdf;

    public HookRunner(File selectedPdf) {
        this.selectedPdf = selectedPdf;
    }

    @Override
    public void run() {
        try {
            initListener();
        } catch (NativeHookException e) {
            System.out.println("Error: Not possible to create system wide hook");
            e.printStackTrace();
        }
    }

    /**
     * Initializes the native hook listener to make it possible to listen for the key combinations to load up the
     * clipboard with an image.
     */
    private void initListener() throws NativeHookException {
        // turn of all notifications about which keys were pressed
        Logger l = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        l.setLevel(Level.OFF);

        GlobalScreen.registerNativeHook();
        HookListener hookListener = new HookListener(selectedPdf);
        GlobalScreen.addNativeKeyListener(hookListener);
        while (running) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        GlobalScreen.unregisterNativeHook();
    }

    /**
     * Sets the flag appropriately so that the thread will be terminated
     */
    public void stop() {
        this.running = false;
    }


}
