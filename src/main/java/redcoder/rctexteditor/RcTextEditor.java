package redcoder.rctexteditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import redcoder.rctexteditor.log.LoggingUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RcTextEditor extends Application {

    public static final String TITLE = "Rc Text Editor";
    private static final Logger LOGGER = Logger.getLogger(RcTextEditor.class.getName());

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            Pane root = FXMLLoader.load(getClass().getResource("/fxml/rc-text-editor.fxml"));
            Scene scene = new Scene(root, 900, 600);
            primaryStage.setScene(scene);
            primaryStage.setTitle(TITLE);
            primaryStage.show();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load rc-text-editor.fxml", e);
            throw e;
        }
    }

    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        LoggingUtils.resetLogManager();
        LOGGER.log(Level.INFO, "LogManager has been reset.");
        launch(RcTextEditor.class, args);
    }
}
