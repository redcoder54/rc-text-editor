package redcoder.rctexteditor.ui;

import javafx.scene.control.TabPane;
import redcoder.rctexteditor.model.DefaultEditorTabPaneModel;
import redcoder.rctexteditor.model.EditorTabPaneModel;

public class EditorTabPane extends TabPane {

    private final EditorTabPaneModel editorTabPaneModel;

    public EditorTabPane() {
        this.editorTabPaneModel = new DefaultEditorTabPaneModel(this);
    }

    public EditorTabPaneModel getEditorTabPaneModel() {
        return editorTabPaneModel;
    }
}
