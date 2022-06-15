package redcoder.rctexteditor.support.tab;

import java.util.EventListener;

public interface TabContentChangeListener extends EventListener {

    void onTabContentChange(TabContentChangeEvent event);
}
