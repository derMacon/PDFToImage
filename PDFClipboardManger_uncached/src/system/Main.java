package system;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Main method calling all the methods and starting the application.
 */
public class Main extends Application {

    private static final String EXIT_KEYWORD = "exit";
    private static final String IMPORT_KEYWORD = "imp";
    private static final String DEFAULT_DIR_FC = "./";
    private static final String USAGE = "usage\n" +
            "\t- EXIT to exit the program\n" +
            "\t- IMP  to import a new pdf file\n";
    public static final String HISTORY_FILE_DIR = "history";

    private static final File HISTORY = new File("./" + HISTORY_FILE_DIR + "/.history");

    private File selectedPdf;
    private String fc_openingDir;

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
        refreshSelectedFile(primaryStage);
        System.out.println("PDFClipboardManager: " + this.selectedPdf);

        HookRunner runner = new HookRunner(this.selectedPdf);
        Thread thread = new Thread(runner);
        thread.start();

        listenToTerminalInput(scanner, primaryStage);
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
     * Starts up the initial file chooser to select a pdf document that should be processed
     * @param primaryStage stage to show the file chooser on
     */
    private void startFileChooser(Stage primaryStage) {
        final FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF", "*.pdf"));
        File historyParentDir = createOpeningDir();
        fileChooser.setInitialDirectory(historyParentDir);
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

    /**
     * If the the opening directory for the file chooser is not defined the
     * default directory will be returned otherwise the parent directory of
     * the given file chooser file will be selected.
     * @return initial directory of the file
     */
    private File createOpeningDir() {
        String path = null == this.fc_openingDir ? DEFAULT_DIR_FC :
                new File(this.fc_openingDir).getParent();
        return new File(path);
    }

    /*
     * Reads the HISTORY .txt file which contains only one path to the pdf-document which was opened the last time
     * the program was running.
     */
    private void setSelectedPdf() {
        BufferedReader brTest = null;
        try {
            brTest = new BufferedReader(new FileReader(HISTORY));
            String line = brTest.readLine();
            this.selectedPdf = new File(line);
            this.fc_openingDir = line;
        } catch (FileNotFoundException e) {
            System.out.println("Could not read history file");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Could not read history file");
            e.printStackTrace();
        }
    }

    /**
     * Listens to the terminal input via the given scanner instance. The user has the choice to select a new pdf file
     * or terminate the running programm completely
     * @param scanner scanner instance which will be used to listen to the user input from the terminal
     * @param primaryStage stage to display the filechooser if needed
     */
    private void listenToTerminalInput(Scanner scanner, Stage primaryStage) {
        String userInput = null;
        while (this.selectedPdf != null && !EXIT_KEYWORD.equals(userInput)) {
            System.out.print(USAGE + "user input: ");
            userInput = scanner.next().toLowerCase();
            if(userInput.equals(IMPORT_KEYWORD)) {
                startFileChooser(primaryStage);
            }
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

            System.out.println(this.selectedPdf.getPath());
            writer.close();
        } catch (IOException e) {
            System.out.println("Could not write the history file");
            e.printStackTrace();
        }
    }

}

