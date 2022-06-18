package redcoder.rctexteditor.core.editoraction.strategy;

import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import redcoder.rctexteditor.ui.EditorTab;
import redcoder.rctexteditor.ui.FindReplaceDialog;

public class FindActionStrategy implements EditorActionStrategy{
    @Override
    public void onAction(TabPane tabPane) {
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        EditorTab editorTab = (EditorTab) selectionModel.getSelectedItem();
        FindReplaceDialog.showFindDialog(editorTab.getTabContent());
    }
}
