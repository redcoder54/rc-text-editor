package redcoder.rctexteditor;

import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import redcoder.rctexteditor.support.UnsavedCreatedNewlyFiles;

import java.io.File;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RcTabPane extends TabPane {

    private static final Logger LOGGER = Logger.getLogger(RcTabPane.class.getName());
    private final AtomicInteger counter = new AtomicInteger(0);

    public void newTab() {
        String title = "new-" + counter.getAndIncrement();
        RcTextTab textTab = new RcTextTab(title, "");
        addTextTab(textTab);
        UnsavedCreatedNewlyFiles.addTextTab(textTab);
    }

    public void newTab(File file, boolean ucnf) {
        for (Tab tab : getTabs()) {
            RcTextTab rcTextTab = (RcTextTab) tab;
            File openedFile = rcTextTab.getOpenedFile();
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

    /**
     * 加载新创建的且未保存的文件
     */
    public void loadUCNF() {
        try {
            int i = UnsavedCreatedNewlyFiles.load(this);
            counter.set(i);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load unsaved created file newly", e);
        }
    }
}
