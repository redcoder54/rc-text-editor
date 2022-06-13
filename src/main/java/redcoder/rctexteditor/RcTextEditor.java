package redcoder.rctexteditor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import redcoder.rctexteditor.log.LoggingUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RcTextEditor extends Application {

    private static final Logger LOGGER = Logger.getLogger(RcTextEditor.class.getName());
    public static final String TITLE = "Rc Text Editor";

    @Override
    public void start(Stage primaryStage) throws Exception {
        RcTabPane tabPane = new RcTabPane();
        RcMenuBar menuBar = new RcMenuBar(tabPane);

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(tabPane);

        Scene scene = new Scene(root, 900, 600);

        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        LoggingUtils.resetLogManager();
        LOGGER.log(Level.INFO, "LogManager has been reset.");
        launch(RcTextEditor.class, args);
    }
}
