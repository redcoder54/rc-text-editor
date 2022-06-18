package redcoder.rctexteditor.core.editoraction.strategy;

import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import redcoder.rctexteditor.ui.EditorTab;

public class SaveSelectedTabAsActionStrategy implements EditorActionStrategy {
    @Override
    public void onAction(TabPane tabPane) {
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        EditorTab editorTab = (EditorTab) selectionModel.getSelectedItem();
        if (editorTab != null) {
            editorTab.saveAs();
        }
    }
}
