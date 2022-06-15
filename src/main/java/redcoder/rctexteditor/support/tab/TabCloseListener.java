package redcoder.rctexteditor.support.tab;

import java.util.EventListener;

public interface TabCloseListener extends EventListener {

    void onTabClose(TabCloseEvent event);
}
