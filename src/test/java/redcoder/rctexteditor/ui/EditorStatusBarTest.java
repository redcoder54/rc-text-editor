package redcoder.rctexteditor.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import redcoder.rctexteditor.model.impl.DefaultEditorTabPaneModel;

public class EditorStatusBarTest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane root = new BorderPane();
        root.setCenter(new TextArea());
        root.setBottom(new EditorStatusBar(new DefaultEditorTabPaneModel(new EditorTabPane())));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
