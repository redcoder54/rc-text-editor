package redcoder.rctexteditor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.tbee.javafx.scene.layout.MigPane;
import redcoder.rctexteditor.ui.EditorStatusBar;
import redcoder.rctexteditor.ui.EditorTabPane;
import redcoder.rctexteditor.ui.EditorMenuBar;
import redcoder.rctexteditor.log.LoggingUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class RcTextEditor extends Application {

    public static final String TITLE = "Rc Text Editor";
    private static final Logger LOGGER = Logger.getLogger(RcTextEditor.class.getName());

    private final EditorTabPane tabPane;
    private final EditorMenuBar menuBar;
    private final EditorStatusBar statusBar;

    public RcTextEditor() {
        tabPane = new EditorTabPane();
        menuBar = new EditorMenuBar(tabPane.getEditorTabPaneModel());
        statusBar = new EditorStatusBar(tabPane.getEditorTabPaneModel());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        menuBar.start();
        statusBar.start();
        tabPane.start();

        MigPane root = new MigPane();
        root.add(menuBar, "north");
        root.add(tabPane, "dock center");
        root.add(statusBar, "south");

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle(TITLE);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        tabPane.stop();
        menuBar.stop();
        statusBar.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        LoggingUtils.resetLogManager();
        LOGGER.log(Level.INFO, "LogManager has been reset.");
        launch(RcTextEditor.class, args);
    }
}
