package redcoder.rctexteditor.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.tbee.javafx.scene.layout.MigPane;

public class MigPaneTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        MigPane pane = new MigPane("debug");
        // pane.add(new Button("b1"),"span 2 2");
        // pane.add(new Button("b2"));
        // pane.add(new Button("b3"));
        // pane.add(new Button("b4"),"newline");
        // pane.add(new Button("b5"));

        // pane.add(new Button("b1"),"dock north");
        // pane.add(new Button("b2"),"dock north");
        // pane.add(new Button("b3"),"dock center");
        // pane.add(new Button("b4"),"dock south");
        // pane.add(new Button("b5"),"dock south");

        pane.add(new Button("ok"), "tag ok");
        pane.add(new Button("cancel"), "tag cancel, wrap");
        pane.add(new Button("help"), "tag right");
        pane.add(new Button("help2"), "tag help");

        Scene scene = new Scene(pane, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("MigPaneTest");
        primaryStage.show();
    }
}
