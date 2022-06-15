package redcoder.rctexteditor.support.tab;

import java.util.EventListener;

public interface TabSelectedListener extends EventListener {

    void onTabSelected(TabSelectedEvent event);
}
