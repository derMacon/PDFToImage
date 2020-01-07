package com.dermacon.app;

import com.dermacon.app.dataStructures.Bookmark;
import com.dermacon.app.dataStructures.MockBookmark;
import com.dermacon.app.fileio.FileHandler;
import com.dermacon.app.hook.HookRunner;
import com.dermacon.app.hook.MyListener;
import com.dermacon.app.logik.MockTerminalUI;
import com.dermacon.app.logik.TerminalUI;
import com.dermacon.app.logik.UserInterface;
import com.dermacon.app.worker.MockRenderer;
import com.dermacon.app.worker.RenderManager;
import com.dermacon.app.worker.Renderer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FXApp extends Application {

    private Bookmark bookmark;

    private boolean running = true;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader =
                new FXMLLoader(FXApp.class.getResource("viewer.fxml"));
//        fxmlLoader.setController(new FXMLController());
        Parent parent = fxmlLoader.load();

//        FXMLController controller = fxmlLoader.getController();
//        controller.setProjectController(this.projectController);
//        SpringApiController.setJFXController(controller);

        Scene scene = new Scene(parent);
        stage.setScene(scene);
//        controller.setImgVwResponsive(stage);
        stage.show();
    }

    /**
     * Main method of the whole application.
     * @param args command line args, the user may specify a separate config
     *             file other than the default config.properties.
     */
    public static void main(String[] args) {
        try {
//            FileHandler fileHandler = new FileHandler(args);
//            UserInterface ui = new TerminalUI(fileHandler.getBookmarks(),
            UserInterface ui = new MockTerminalUI();
//            fileHandler);

//            Bookmark user_select = ui.waitForUserSelection();

//            Renderer renderer = new RenderManager(user_select, fileHandler.getProps());
            Renderer renderer = new MockRenderer();
            Thread runner = new Thread(new HookRunner(renderer));
            runner.start();

            ui.waitForExit();
            runner.join();
            // todo set history csv file
            System.out.println("user terminated program");
            System.exit(0);

//            Renderer renderer = new RenderManager(user_select, fileHandler.getProps());
//            renderer.renderPageIntervall();

        } catch (InterruptedException e) {
//             todo
            e.printStackTrace();
        }

        System.out.println("launch");
        launch(args);
    }

    // todo
    private void launchListener() {
        Logger l = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        l.setLevel(Level.OFF);

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            System.out.println("error");
            e.printStackTrace();
        }

        MyListener list = new MyListener();
        GlobalScreen.addNativeKeyListener(list);

        while(running) {
            try {
                Thread.sleep(300);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            System.out.println("error");
            e.printStackTrace();
        }
    }
}
