package redcoder.rctexteditor.event;

import java.io.File;
import java.util.EventObject;

public class TabOpenEvent extends EventObject {

    private TabType tabType;
    private File relatedFile;

    public TabOpenEvent(Object source, TabType tabType) {
        super(source);
        this.tabType = tabType;
    }

    public TabType getTabType() {
        return tabType;
    }

    public void setTabType(TabType tabType) {
        this.tabType = tabType;
    }

    public File getRelatedFile() {
        return relatedFile;
    }

    public void setRelatedFile(File relatedFile) {
        this.relatedFile = relatedFile;
    }

    public enum TabType {
        FILE_INDEPENDENT, FILE_DEPENDENT
    }
}
