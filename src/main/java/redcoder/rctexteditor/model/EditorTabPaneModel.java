package redcoder.rctexteditor.model;

import redcoder.rctexteditor.support.Lifecycle;
import redcoder.rctexteditor.support.tab.TabCloseListener;
import redcoder.rctexteditor.support.tab.TabContentChangeListener;
import redcoder.rctexteditor.support.tab.TabOpenListener;
import redcoder.rctexteditor.support.tab.TabSelectedListener;

import java.io.File;

public interface EditorTabPaneModel extends Lifecycle {

    void newFileIndependentTab();

    void newFileIndependentTab(String tabTitle, String tabTextContent);

    void newFileDependentTab();

    void newFileDependentTab(File file);

    void saveSelectedTab();

    void saveSelectedTabAs();

    void saveAllTab();

    void closeSelectedTab();

    void closeAllTab();

    void handleAction(Action action);

    void addTabOpenListener(TabOpenListener listener);

    void removeTabOpenListener(TabOpenListener listener);

    void addTabCloseListener(TabCloseListener listener);

    void removeTabCloseListener(TabCloseListener listener);

    void addTabContentChangeListener(TabContentChangeListener listener);

    void removeTabContentChangeListener(TabContentChangeListener listener);

    void addTabSelectedListener(TabSelectedListener listener);

    void removeTabSelectedListener(TabSelectedListener listener);

    enum Action {
        CUT, COPY, PASTE, UNDO, REDO, FIND, REPLACE, ZOOM_IN, ZOOM_OUT, AUTO_WRAP
    }
}
