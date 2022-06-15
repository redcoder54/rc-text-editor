package redcoder.rctexteditor.support.tab;

import javafx.scene.control.Tab;

public class TabCloseEvent extends TabEvent {

    private Tab closedTab;
    private TabOpenEvent.TabType tabType;

    public TabCloseEvent(Object source) {
        super(source);
    }

    public Tab getClosedTab() {
        return closedTab;
    }

    public void setClosedTab(Tab closedTab) {
        this.closedTab = closedTab;
    }

    public TabOpenEvent.TabType getTabType() {
        return tabType;
    }

    public void setTabType(TabOpenEvent.TabType tabType) {
        this.tabType = tabType;
    }
}
