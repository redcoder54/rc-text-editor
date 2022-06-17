package redcoder.rctexteditor.core.tab.event;

import java.util.EventListener;

public interface EditorTabListener extends EventListener {

    default void onTabOpen(TabEvent event) {
    }

    default void onTabClose(TabEvent event){
    }

    default void onTabSelected(TabEvent event){
    }

    default void onTabContentChange(TabEvent event) {
    }
}
