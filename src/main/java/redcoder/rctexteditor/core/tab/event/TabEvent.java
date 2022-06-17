package redcoder.rctexteditor.core.tab.event;

import javafx.scene.control.TabPane;
import redcoder.rctexteditor.ui.EditorTab;

import java.util.EventObject;

public class TabEvent extends EventObject {

    private TabPane tabPane;
    private EditorTab editorTab;
    private TabEventType eventType;

    public TabEvent(Object source) {
        super(source);
    }

    public TabPane getTabPane() {
        return tabPane;
    }

    public void setTabPane(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public EditorTab getEditorTab() {
        return editorTab;
    }

    public void setEditorTab(EditorTab editorTab) {
        this.editorTab = editorTab;
    }

    public TabEventType getEventType() {
        return eventType;
    }

    public void setEventType(TabEventType eventType) {
        this.eventType = eventType;
    }

    public enum TabEventType {
        TAB_OPEN,
        TAB_CLOSE,
        TAB_SELECTED,
        TAB_CONTENT_CHANGE
    }
}
