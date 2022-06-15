package redcoder.rctexteditor.support.tab;

import javafx.scene.control.Tab;

import java.io.File;

public class TabOpenEvent extends TabEvent {

    private TabType tabType;
    private Tab openedTab;
    private File relatedFile;

    public TabOpenEvent(Object source) {
        super(source);
    }

    public TabType getTabType() {
        return tabType;
    }

    public void setTabType(TabType tabType) {
        this.tabType = tabType;
    }

    public Tab getOpenedTab() {
        return openedTab;
    }

    public void setOpenedTab(Tab openedTab) {
        this.openedTab = openedTab;
    }

    public File getRelatedFile() {
        return relatedFile;
    }

    public void setRelatedFile(File relatedFile) {
        this.relatedFile = relatedFile;
    }
}
