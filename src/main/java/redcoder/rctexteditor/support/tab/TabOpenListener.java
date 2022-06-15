package redcoder.rctexteditor.support.tab;

import java.util.EventListener;

public interface TabOpenListener extends EventListener {

    void onTabOpen(TabOpenEvent event);
}
