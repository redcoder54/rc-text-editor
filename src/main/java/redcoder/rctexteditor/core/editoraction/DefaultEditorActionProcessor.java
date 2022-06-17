package redcoder.rctexteditor.core.editoraction;

import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import redcoder.rctexteditor.core.tab.TabType;
import redcoder.rctexteditor.support.FileChooserSupport;
import redcoder.rctexteditor.support.font.FontChangeProcessor;
import redcoder.rctexteditor.core.tab.NewTabId;
import redcoder.rctexteditor.ui.EditorTab;
import redcoder.rctexteditor.ui.FindReplaceDialog;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Logger;

import static redcoder.rctexteditor.core.editoraction.EditorAction.NEW_FILE_DEPENDENT_TAB;
import static redcoder.rctexteditor.core.editoraction.EditorAction.NEW_FILE_INDEPENDENT_TAB;

public class DefaultEditorActionProcessor implements EditorActionProcessor {

    private static final Logger LOGGER = Logger.getLogger(DefaultEditorActionProcessor.class.getName());

    private final TabPane tabPane;

    public DefaultEditorActionProcessor(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    @Override
    public void handleEditorAction(EditorAction action) {
        if (action == NEW_FILE_INDEPENDENT_TAB) {
            newFileIndependentTab();
            return;
        }
        if (action == NEW_FILE_DEPENDENT_TAB) {
            newFileDependentTab();
            return;
        }

        EditorTab tab = getSelectedTab();
        if (tab == null) {
            return;
        }
        switch (action) {
            case SAVE_SELECTED_TAB:
                saveSelectedTab();
                break;
            case SAVE_SELECTED_TAB_AS:
                saveSelectedTabAs();
                break;
            case SAVE_ALL_TAB:
                saveAllTab();
                break;
            case CLOSE_SELECTED_TAB:
                closeSelectedTab();
                break;
            case CLOSE_ALL_TAB:
                closeAllTab();
                break;
            case CUT:
                tab.getTabContent().cut();
                break;
            case COPY:
                tab.getTabContent().copy();
                break;
            case PASTE:
                tab.getTabContent().paste();
                break;
            case UNDO:
                tab.getTabContent().undo();
                break;
            case REDO:
                tab.getTabContent().redo();
                break;
            case FIND:
                FindReplaceDialog.showFindDialog(tab.getTabContent());
                break;
            case REPLACE:
                FindReplaceDialog.showReplaceDialog(tab.getTabContent());
                break;
            case ZOOM_IN:
                FontChangeProcessor.zoomIn(this);
                break;
            case ZOOM_OUT:
                FontChangeProcessor.zoomOut(this);
                break;
            case AUTO_WRAP:
                tab.getTabContent().setWrapText(!tab.getTabContent().isWrapText());
                break;
            default:
                LOGGER.warning(() -> String.format("Unknown action: %s", action.name()));
                break;
        }
    }

    private void newFileIndependentTab() {
        // add tab, then switch to it
        String tabTitle = "new-" + NewTabId.nextId();
        EditorTab editorTab = new EditorTab(tabTitle, TabType.FILE_INDEPENDENT);
        tabPane.getTabs().add(editorTab);
        tabPane.getSelectionModel().selectNext();
    }

    private void newFileDependentTab() {
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
        }
    }

    private void saveSelectedTab() {
        EditorTab editorTab = getSelectedTab();
        if (editorTab != null) {
            editorTab.save();
        }
    }

    private void saveSelectedTabAs() {
        EditorTab editorTab = getSelectedTab();
        if (editorTab != null) {
            editorTab.saveAs();
        }
    }

    private void saveAllTab() {
        for (Tab tab : tabPane.getTabs()) {
            EditorTab editorTab = (EditorTab) tab;
            if (!editorTab.save()) {
                return;
            }
        }
    }

    private void closeSelectedTab() {
        EditorTab editorTab = getSelectedTab();
        if (editorTab != null && editorTab.close()) {
            tabPane.getTabs().remove(editorTab);
        }
    }

    private void closeAllTab() {
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

    private EditorTab getSelectedTab() {
        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        return (EditorTab) selectionModel.getSelectedItem();
    }

}
