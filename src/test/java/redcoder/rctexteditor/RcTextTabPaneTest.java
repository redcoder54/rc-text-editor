package redcoder.rctexteditor;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.Test;

public class RcTextTabPaneTest {

    @Test
    public void test() {
        RcApplication.launch(RcApplication.class);
    }

    public static class RcApplication extends Application {
        @Override
        public void start(Stage primaryStage) throws Exception {
            RcTabPane tabPane = new RcTabPane();
            Tab tab = new Tab("first", new TextArea());
            tabPane.getTabs().add(tab);

            VBox vBox = new VBox();
            vBox.getChildren().add(tabPane);

            Scene scene = new Scene(vBox, 400, 300);
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }
}
