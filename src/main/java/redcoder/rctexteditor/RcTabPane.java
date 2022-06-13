package redcoder.rctexteditor;

import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import redcoder.rctexteditor.support.UnsavedCreatedNewlyFiles;

import java.io.File;
import java.util.Iterator;

public class RcTabPane extends TabPane {

    public void newTab(String title) {
        RcTextTab tab = new RcTextTab(title, "");
        addTextTab(tab);
    }

    public void newTab(File file, boolean ucnf) {
        for (Tab tab : getTabs()) {
            RcTextTab rcTextTab = (RcTextTab) tab;
            File openedFile = rcTextTab.getFile();
            if (openedFile != null && openedFile == file) {
                getSelectionModel().select(rcTextTab);
                return;
            }
        }

        RcTextTab textTab = new RcTextTab(file);
        addTextTab(textTab);

        if (ucnf) {
            UnsavedCreatedNewlyFiles.addTextTab(textTab);
        }
    }

    private void addTextTab(RcTextTab textTab) {
        // add tab, then switch to it
        getTabs().add(textTab);
        getSelectionModel().selectNext();
    }

    public void saveCurrentTab() {
        SingleSelectionModel<Tab> selectionModel = getSelectionModel();
        RcTextTab textTab = (RcTextTab) selectionModel.getSelectedItem();
        textTab.save();
    }

    public void saveAllTab() {
        for (Tab tab : getTabs()) {
            RcTextTab textTab = (RcTextTab) tab;
            if (!textTab.save()) {
                return;
            }
        }
    }

    public void closeCurrentTab() {
        SingleSelectionModel<Tab> selectionModel = getSelectionModel();
        RcTextTab tab = (RcTextTab) selectionModel.getSelectedItem();
        if (tab.close()) {
            int index = selectionModel.getSelectedIndex();
            if (index > -1) {
                getTabs().remove(index);
            }
        }
    }

    public void closeAllTab() {
        Iterator<Tab> it = getTabs().iterator();
        while (it.hasNext()) {
            RcTextTab textTab = (RcTextTab) it.next();
            if (textTab.close()) {
                it.remove();
            } else {
                return;
            }
        }
    }

    public RcTextTab getCurrentTab() {
        SingleSelectionModel<Tab> selectionModel = getSelectionModel();
        return (RcTextTab) selectionModel.getSelectedItem();
    }
}
