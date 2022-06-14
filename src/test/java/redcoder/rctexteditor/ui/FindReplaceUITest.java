package redcoder.rctexteditor.ui;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class FindReplaceUITest extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        TextArea textArea = new TextArea("hello world");
        Button button1 = new Button("Show Find");
        button1.setOnAction(event -> {
            FindReplaceUI.showFindUI(textArea);
        });

        Button button2 = new Button("Show Replace");
        button2.setOnAction(event -> {
            FindReplaceUI.showReplaceUI(textArea);
        });


        HBox hBox = new HBox(10, button1, button2);
        hBox.setAlignment(Pos.CENTER);

        BorderPane root = new BorderPane();
        root.setCenter(textArea);
        root.setBottom(hBox);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(FindReplaceUITest.class, args);
    }
}
