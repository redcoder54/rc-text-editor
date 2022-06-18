package redcoder.rctexteditor.core.editoraction.strategy;

import javafx.scene.control.TabPane;
import redcoder.rctexteditor.core.tab.NewTabId;
import redcoder.rctexteditor.core.tab.TabType;
import redcoder.rctexteditor.ui.EditorTab;

public class NewFileIndependentTabActionStrategy implements EditorActionStrategy {

    @Override
    public void onAction(TabPane tabPane) {
        // add tab, then switch to it
        String tabTitle = "new-" + NewTabId.nextId();
        EditorTab editorTab = new EditorTab(tabTitle, TabType.FILE_INDEPENDENT);
        tabPane.getTabs().add(editorTab);
        tabPane.getSelectionModel().selectNext();
        editorTab.getTabContent().requestFocus();
    }
}
