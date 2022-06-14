package redcoder.rctexteditor.event;

import java.util.EventListener;

public interface TabOpenEventListener extends EventListener {

    void onTabOpen(TabOpenEvent event);
}
