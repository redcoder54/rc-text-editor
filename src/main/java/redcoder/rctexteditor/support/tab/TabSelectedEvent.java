package redcoder.rctexteditor.support.tab;

import javafx.scene.control.Tab;

public class TabSelectedEvent extends TabEvent {

    private Tab selectedTab;

    public TabSelectedEvent(Object source) {
        super(source);
    }

    public Tab getSelectedTab() {
        return selectedTab;
    }

    public void setSelectedTab(Tab selectedTab) {
        this.selectedTab = selectedTab;
    }
}
