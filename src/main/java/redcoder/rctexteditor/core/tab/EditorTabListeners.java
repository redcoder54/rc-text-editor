package redcoder.rctexteditor.core.tab;

import redcoder.rctexteditor.core.tab.event.EditorTabListener;
import redcoder.rctexteditor.core.tab.event.TabEvent;

import javax.swing.event.EventListenerList;

public class EditorTabListeners {

    private static final EventListenerList EVENT_LISTENER_LIST = new EventListenerList();

    public static void addEditorTabListener(EditorTabListener listener) {
        EVENT_LISTENER_LIST.add(EditorTabListener.class, listener);
    }

    public static void removeEditorTabListener(EditorTabListener listener) {
        EVENT_LISTENER_LIST.remove(EditorTabListener.class, listener);
    }

    public static void dispatchTabEvent(TabEvent event) {
        EditorTabListener[] listeners = EVENT_LISTENER_LIST.getListeners(EditorTabListener.class);
        if (listeners.length == 0) {
            return;
        }
        switch (event.getEventType()) {
            case TAB_OPEN:
                onTabOpen(listeners, event);
                break;
            case TAB_CLOSE:
                onTabClose(listeners, event);
                break;
            case TAB_SELECTED:
                onTabSelected(listeners, event);
                break;
            case TAB_CONTENT_CHANGE:
                onTabContentChange(listeners, event);
                break;
            default:
                throw new RuntimeException("Unknown TabEventType: " + event.getEventType());
        }
    }

    private static void onTabOpen(EditorTabListener[] listeners, TabEvent event) {
        for (EditorTabListener listener : listeners) {
            listener.onTabOpen(event);
        }
    }

    private static void onTabClose(EditorTabListener[] listeners, TabEvent event) {
        for (EditorTabListener listener : listeners) {
            listener.onTabClose(event);
        }
    }

    private static void onTabSelected(EditorTabListener[] listeners, TabEvent event) {
        for (EditorTabListener listener : listeners) {
            listener.onTabSelected(event);
        }
    }

    private static void onTabContentChange(EditorTabListener[] listeners, TabEvent event) {
        for (EditorTabListener listener : listeners) {
            listener.onTabContentChange(event);
        }
    }
}
