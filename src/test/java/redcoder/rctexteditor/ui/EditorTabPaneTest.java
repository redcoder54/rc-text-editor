package redcoder.rctexteditor.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EditorTabPaneTest extends Application {

    public static void main(String[] args) {
        EditorTabPaneTest.launch(EditorTabPaneTest.class);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        EditorTabPane tabPane = new EditorTabPane();
        Tab tab = new Tab("first", new TextArea());
        tabPane.getTabs().add(tab);

        VBox vBox = new VBox();
        vBox.getChildren().add(tabPane);

        Scene scene = new Scene(vBox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
