package redcoder.rctexteditor.core.editoraction.strategy;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import redcoder.rctexteditor.core.tab.TabType;
import redcoder.rctexteditor.support.FileChooserSupport;
import redcoder.rctexteditor.ui.EditorTab;

import java.io.File;

public class NewFileDependentTabActionStrategy implements EditorActionStrategy {
    
    @Override
    public void onAction(TabPane tabPane) {
        File file = FileChooserSupport.getFileChooser().showOpenDialog(null);
        if (file != null) {
            for (Tab tab : tabPane.getTabs()) {
                EditorTab editorTab = (EditorTab) tab;
                File openedFile = editorTab.getOpenedFile();
                if (openedFile != null && openedFile == file) {
                    tabPane.getSelectionModel().select(editorTab);
                    return;
                }
            }

            // add tab, then switch to it
            EditorTab editorTab = new EditorTab(file, TabType.FILE_DEPENDENT);
            tabPane.getTabs().add(editorTab);
            tabPane.getSelectionModel().selectNext();
            editorTab.getTabContent().requestFocus();
        }
    }
}
