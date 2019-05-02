package system;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private static final String EXIT_KEYWORD = "exit";
    private static final String IMPORT_KEYWORD = "imp";
    private static final String DEFAULT_DIR_FC = "./";
    private static final String USAGE = "usage\n" +
            "\t- EXIT to exit the program\n" +
            "\t- IMP  to import a new pdf file\n";
    private static final File HISTORY = new File("./.history");

    private File selectedPdf;

    /**
     * Main method calling the file chooser
     * @param args command line args
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println(HISTORY.getPath());
        refreshSelectedFile(primaryStage);

        System.out.println("PDFClipboardManager: " + this.selectedPdf);

        HookRunner runner = new HookRunner(this.selectedPdf);
        Thread thread = new Thread(runner);
        thread.start();

        String userInput = null;
        while (this.selectedPdf != null && !EXIT_KEYWORD.equals(userInput)) {
            System.out.print(USAGE + "user input: ");
            userInput = scanner.next().toLowerCase();
            if(userInput.equals(IMPORT_KEYWORD)) {
                startFileChooser(primaryStage);
            }
        }

        runner.stop();
        thread.join();
        setHistoryFile();
        System.out.println("User terminated the programm");
        System.exit(0);
    }

    /**
     * Asks the user if he wants to load up the last used file specified in the history file.
     */
    private void refreshSelectedFile(Stage primaryStage) {
        Scanner scanner = new Scanner(System.in);
        String userInput = null;
        if(HISTORY.exists()) {
            setSelectedPdf();
            System.out.print("Do you want to:\n1. reload the following file: " + this.selectedPdf.getName()
                    + "\n2. select a new one\nuser input: ");
            userInput = scanner.next().toLowerCase();
        }
        if(null == userInput || userInput.equals("2") || userInput.equals("2.")) {
            startFileChooser(primaryStage);
        }
    }

    /**
     * Writes the current selected pdf document into the history file to load up instantly next time the programm
     * will be started.
     */
    private void setHistoryFile() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY));
            writer.write(this.selectedPdf.getPath());

            writer.close();
        } catch (IOException e) {
            System.out.println("Could not write the history file");
            e.printStackTrace();
        }
    }


    /**
     * Reads the HISTORY .txt file which contains only one path to the pdf-document which was opened the last time
     * the program was running.
     */
    private void setSelectedPdf() {
        BufferedReader brTest = null;
        try {
            brTest = new BufferedReader(new FileReader(HISTORY));
            this.selectedPdf = new File(brTest.readLine());
        } catch (FileNotFoundException e) {
            System.out.println("Could not read history file");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Could not read history file");
            e.printStackTrace();
        }
    }

    /**
     * Starts up the initial file chooser to select a pdf document that should be processed
     * @param primaryStage stage to show the file chooser on
     */
    private void startFileChooser(Stage primaryStage) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        fileChooser.setInitialDirectory(new File(DEFAULT_DIR_FC));
        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            System.out.println("User selected file: " + file.getName());
            selectedPdf = file;
        } else {
            System.out.println("User has not selected a valid pdf document, program terminated.");
            System.exit(0);
        }
        primaryStage.setTitle("Choose Pdf-Document");
    }
}

