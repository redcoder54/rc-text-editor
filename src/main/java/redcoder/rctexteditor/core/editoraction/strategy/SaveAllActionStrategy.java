package redcoder.rctexteditor.core.editoraction.strategy;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import redcoder.rctexteditor.ui.EditorTab;

public class SaveAllActionStrategy implements EditorActionStrategy {
    @Override
    public void onAction(TabPane tabPane) {
        for (Tab tab : tabPane.getTabs()) {
            EditorTab editorTab = (EditorTab) tab;
            if (!editorTab.save()) {
                return;
            }
        }
    }
}
