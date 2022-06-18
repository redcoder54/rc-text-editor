package redcoder.rctexteditor.core.editoraction.strategy;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import redcoder.rctexteditor.ui.EditorTab;

import java.util.Iterator;

public class CloseAllTabActionStrategy implements EditorActionStrategy {
    @Override
    public void onAction(TabPane tabPane) {
        Iterator<Tab> it = tabPane.getTabs().iterator();
        while (it.hasNext()) {
            EditorTab editorTab = (EditorTab) it.next();
            if (editorTab.close()) {
                it.remove();
            } else {
                return;
            }
        }
    }
}
