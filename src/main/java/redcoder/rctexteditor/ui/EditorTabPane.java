package redcoder.rctexteditor.ui;

import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import redcoder.rctexteditor.model.DefaultEditorTabPaneModel;
import redcoder.rctexteditor.model.EditorTabPaneModel;

public class EditorTabPane extends TabPane {

    private final EditorTabPaneModel editorTabPaneModel;

    public EditorTabPane() {
        this.editorTabPaneModel = new DefaultEditorTabPaneModel(this);

        addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            if ((code == KeyCode.EQUALS || code == KeyCode.ADD) && event.isControlDown()) {
                editorTabPaneModel.handleAction(EditorTabPaneModel.Action.ZOOM_IN);
                event.consume();
            } else if ((code == KeyCode.MINUS || code == KeyCode.SUBTRACT) && event.isControlDown()) {
                editorTabPaneModel.handleAction(EditorTabPaneModel.Action.ZOOM_OUT);
                event.consume();
            }
        });
    }

    public EditorTabPaneModel getEditorTabPaneModel() {
        return editorTabPaneModel;
    }
}
