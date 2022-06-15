package redcoder.rctexteditor.support.tab;

import redcoder.rctexteditor.ui.EditorTabPane;

import java.util.EventObject;

public abstract class TabEvent extends EventObject {

    protected EditorTabPane editorTabPane;

    public TabEvent(Object source) {
        super(source);
    }

    public EditorTabPane getEditorTabPane() {
        return editorTabPane;
    }

    public void setEditorTabPane(EditorTabPane editorTabPane) {
        this.editorTabPane = editorTabPane;
    }

    public enum TabType {
        FILE_INDEPENDENT, FILE_DEPENDENT
    }
}
