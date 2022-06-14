package redcoder.rctexteditor.model;

import redcoder.rctexteditor.event.TabOpenEventListener;

import java.io.File;

public interface EditorTabPaneModel {

    void newFileIndependentTab();

    void newFileIndependentTab(String tabTitle, String tabTextContent);

    void newFileDependentTab();

    void newFileDependentTab(File file);

    void saveSelectedTab();

    void saveSelectedTabAs();

    void saveAllTab();

    void closeSelectedTab();

    void closeAllTab();

    void addTabOpenListener(TabOpenEventListener eventListener);

    void removeTabOpenListener(TabOpenEventListener eventListener);

    void handleAction(Action action);

    enum Action {
        CUT, COPY, PASTE, UNDO, REDO, FIND, REPLACE, ZOOM_IN, ZOOM_OUT, AUTO_WRAP
    }
}
