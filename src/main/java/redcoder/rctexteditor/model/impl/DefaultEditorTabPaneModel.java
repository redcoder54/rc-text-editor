package redcoder.rctexteditor.model.impl;

import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import redcoder.rctexteditor.RcTextEditor;
import redcoder.rctexteditor.model.EditorTabPaneModel;
import redcoder.rctexteditor.support.font.FontChangeEvent;
import redcoder.rctexteditor.support.font.FontChangeListener;
import redcoder.rctexteditor.support.font.FontChangeProcessor;
import redcoder.rctexteditor.support.tab.*;
import redcoder.rctexteditor.ui.EditorTabPane;
import redcoder.rctexteditor.ui.FindReplaceUI;
import redcoder.rctexteditor.utils.FileUtils;
import redcoder.rctexteditor.utils.SystemUtils;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultEditorTabPaneModel implements EditorTabPaneModel {

    private static final Logger LOGGER = Logger.getLogger(DefaultEditorTabPaneModel.class.getName());
    private static final Object[] CLOSE_OPTIONS = {"Save", "Don't Save", "Cancel"};
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    private static final FileChooser FILE_CHOOSER = new FileChooser();
    private static final EventListenerList EVENT_LISTENER_LIST = new EventListenerList();

    static {
        FILE_CHOOSER.setTitle(RcTextEditor.TITLE);
        FILE_CHOOSER.setInitialDirectory(new File(SystemUtils.getUserHome()));
        FILE_CHOOSER.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Text Files", "*.txt", "*.text"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
    }

    private final EditorTabPane editorTabPane;

    public DefaultEditorTabPaneModel(EditorTabPane editorTabPane) {
        this.editorTabPane = editorTabPane;
        this.editorTabPane.getTabs().addListener((ListChangeListener<Tab>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    List<? extends Tab> list = c.getAddedSubList();
                    for (Tab tab : list) {
                        EditorTab editorTab = (EditorTab) tab;
                        if (editorTab.tabType == TabEvent.TabType.FILE_INDEPENDENT) {
                            FileIndependentTabs.addTab(editorTab.tabTitle, editorTab);
                        }
                        fireTabOpenEvent(editorTab, editorTab.openedFile);
                    }
                } else if (c.wasRemoved()) {
                    List<? extends Tab> list = c.getRemoved();
                    for (Tab tab : list) {
                        EditorTab editorTab = (EditorTab) tab;
                        FileIndependentTabs.removeTab(editorTab.tabTitle, editorTab);
                        fireTabCloseEvent(editorTab);
                    }
                }
            }
        });
    }

    @Override
    public void start() {
        try {
            int i = FileIndependentTabs.load(this);
            COUNTER.set(i);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load file independent tabs.", e);
        }
    }

    @Override
    public void newFileIndependentTab() {
        String tabTitle = "new-" + COUNTER.getAndIncrement();
        newFileIndependentTab(tabTitle, "");
    }

    @Override
    public void newFileIndependentTab(String tabTitle, String tabTextContent) {
        EditorTab editorTab = new EditorTab(tabTitle, TabOpenEvent.TabType.FILE_INDEPENDENT);
        editorTab.textArea.appendText(tabTextContent);
        addEditorTab(editorTab);
    }

    @Override
    public void newFileDependentTab() {
        File file = FILE_CHOOSER.showOpenDialog(null);
        if (file != null) {
            newFileDependentTab(file);
        }
    }

    @Override
    public void newFileDependentTab(File file) {
        for (Tab tab : editorTabPane.getTabs()) {
            EditorTab editorTab = (EditorTab) tab;
            File openedFile = editorTab.openedFile;
            if (openedFile != null && openedFile == file) {
                editorTabPane.getSelectionModel().select(editorTab);
                return;
            }
        }

        EditorTab editorTab = new EditorTab(file, TabOpenEvent.TabType.FILE_DEPENDENT);
        addEditorTab(editorTab);
    }

    private void addEditorTab(EditorTab editorTab) {
        // add tab, then switch to it
        editorTabPane.getTabs().add(editorTab);
        editorTabPane.getSelectionModel().selectNext();
    }

    @Override
    public void saveSelectedTab() {
        EditorTab textTab = getSelectedTab();
        if (textTab != null) {
            save(textTab);
        }
    }

    @Override
    public void saveSelectedTabAs() {
        EditorTab editorTab = getSelectedTab();
        if (editorTab != null) {
            File file = saveAs(editorTab);
            if (file != null) {
                editorTab.replaceWith(file);
            }
        }
    }

    @Override
    public void saveAllTab() {
        for (Tab tab : editorTabPane.getTabs()) {
            EditorTab editorTab = (EditorTab) tab;
            if (!save(editorTab)) {
                return;
            }
        }
    }

    @Override
    public void closeSelectedTab() {
        EditorTab editorTab = getSelectedTab();
        if (close(editorTab)) {
            editorTabPane.getTabs().remove(editorTab);
        }
    }

    @Override
    public void closeAllTab() {
        Iterator<Tab> it = editorTabPane.getTabs().iterator();
        while (it.hasNext()) {
            EditorTab editorTab = (EditorTab) it.next();
            if (close(editorTab)) {
                it.remove();
            } else {
                return;
            }
        }
    }

    @Override
    public void handleAction(Action action) {
        EditorTab tab = getSelectedTab();
        if (tab == null) {
            return;
        }
        switch (action) {
            case CUT:
                tab.textArea.cut();
                break;
            case COPY:
                tab.textArea.copy();
                break;
            case PASTE:
                tab.textArea.paste();
                break;
            case UNDO:
                tab.textArea.undo();
                break;
            case REDO:
                tab.textArea.redo();
                break;
            case FIND:
            case REPLACE:
                FindReplaceUI.showFindUI(tab.textArea);
                break;
            case ZOOM_IN:
                FontChangeProcessor.zoomIn(this);
                break;
            case ZOOM_OUT:
                FontChangeProcessor.zoomOut(this);
                break;
            case AUTO_WRAP:
                tab.textArea.setWrapText(!tab.textArea.isWrapText());
                break;
            default:
                LOGGER.warning(() -> String.format("Unknown action: %s", action.name()));
                break;
        }
    }

    @Override
    public void addTabOpenListener(TabOpenListener listener) {
        EVENT_LISTENER_LIST.add(TabOpenListener.class, listener);
    }

    @Override
    public void removeTabOpenListener(TabOpenListener listener) {
        EVENT_LISTENER_LIST.remove(TabOpenListener.class, listener);
    }

    @Override
    public void addTabCloseListener(TabCloseListener listener) {
        EVENT_LISTENER_LIST.add(TabCloseListener.class, listener);
    }

    @Override
    public void removeTabCloseListener(TabCloseListener listener) {
        EVENT_LISTENER_LIST.remove(TabCloseListener.class, listener);
    }

    @Override
    public void addTabContentChangeListener(TabContentChangeListener listener) {
        EVENT_LISTENER_LIST.add(TabContentChangeListener.class, listener);
    }

    @Override
    public void removeTabContentChangeListener(TabContentChangeListener listener) {
        EVENT_LISTENER_LIST.remove(TabContentChangeListener.class, listener);
    }

    @Override
    public void addTabSelectedListener(TabSelectedListener listener) {
        EVENT_LISTENER_LIST.add(TabSelectedListener.class, listener);
    }

    @Override
    public void removeTabSelectedListener(TabSelectedListener listener) {
        EVENT_LISTENER_LIST.remove(TabSelectedListener.class, listener);
    }

    private void fireTabOpenEvent(EditorTab editorTab, File relatedFile) {
        TabOpenEvent event = new TabOpenEvent(this);
        event.setEditorTabPane(editorTabPane);
        event.setTabType(editorTab.tabType);
        event.setOpenedTab(editorTab);
        event.setRelatedFile(relatedFile);
        for (TabOpenListener listener : EVENT_LISTENER_LIST.getListeners(TabOpenListener.class)) {
            listener.onTabOpen(event);
        }
    }

    private void fireTabCloseEvent(EditorTab editorTab) {
        TabCloseEvent event = new TabCloseEvent(this);
        event.setEditorTabPane(editorTabPane);
        event.setTabType(editorTab.tabType);
        event.setClosedTab(editorTab);
        for (TabCloseListener listener : EVENT_LISTENER_LIST.getListeners(TabCloseListener.class)) {
            listener.onTabClose(event);
        }
    }

    private void fireTabContentChangeEvent(Node node) {
        TabContentChangeEvent event = new TabContentChangeEvent(this);
        event.setEditorTabPane(editorTabPane);
        event.setContent(node);
        for (TabContentChangeListener listener : EVENT_LISTENER_LIST.getListeners(TabContentChangeListener.class)) {
            listener.onTabContentChange(event);
        }
    }

    private void fireTabSelectedEvent(Tab tab) {
        TabSelectedEvent event = new TabSelectedEvent(this);
        event.setEditorTabPane(editorTabPane);
        event.setSelectedTab(tab);
        for (TabSelectedListener listener : EVENT_LISTENER_LIST.getListeners(TabSelectedListener.class)) {
            listener.onTabSelected(event);
        }
    }

    private EditorTab getSelectedTab() {
        SingleSelectionModel<Tab> selectionModel = editorTabPane.getSelectionModel();
        return (EditorTab) selectionModel.getSelectedItem();
    }

    private boolean save(EditorTab editorTab) {
        File openedFile = editorTab.openedFile;
        String text = editorTab.textArea.getText();
        if (openedFile != null) {
            FileUtils.writeFile(text, openedFile);
            return true;
        } else {
            openedFile = saveAs(editorTab);
            if (openedFile != null) {
                editorTab.updateTabTitle(openedFile.getName());
                editorTab.textArea.change = false;
                return true;
            }
        }
        return false;
    }

    private File saveAs(EditorTab editorTab) {
        File saveFile = FILE_CHOOSER.showSaveDialog(null);
        if (saveFile != null) {
            String text = editorTab.textArea.getText();
            if (saveFile.exists()) {
                String message = String.format("%s already exist, would you like overwriting it?", saveFile.getName());
                int n = JOptionPane.showConfirmDialog(null, message, RcTextEditor.TITLE, JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    FileUtils.writeFile(text, saveFile);
                    editorTab.openedFile = saveFile;
                    return saveFile;
                }
            } else {
                FileUtils.writeFile(text, saveFile);
                editorTab.openedFile = saveFile;
                return saveFile;
            }
        }
        return null;
    }

    private boolean close(EditorTab editorTab) {
        boolean closed = false;
        if (editorTab.textArea.change) {
            // FIXME: 2022/6/13 customize dialog?
            String message = String.format("Do you want to save the changes you made to %s?\n"
                    + "Your changes will be lost if you don't save them.", editorTab.tabTitle);
            int state = JOptionPane.showOptionDialog(null, message, RcTextEditor.TITLE, JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, CLOSE_OPTIONS, CLOSE_OPTIONS[0]);
            if (state == JOptionPane.YES_OPTION) {
                // save file firstly, then close it.
                if (save(editorTab)) {
                    closed = true;
                }
            } else if (state == JOptionPane.NO_OPTION) {
                // close file directly
                closed = true;
            }
            // user cancel operation, don't close it.
        } else {
            closed = true;
        }
        return closed;
    }

    private class EditorTab extends Tab {

        private String tabTitle;
        private File openedFile;
        private EditorTextArea textArea;
        private TabOpenEvent.TabType tabType;

        public EditorTab(File openedFile, TabOpenEvent.TabType tabType) {
            this(openedFile.getName(), FileUtils.readFile(openedFile), openedFile, tabType);
        }

        public EditorTab(String tabTitle, TabOpenEvent.TabType tabType) {
            this(tabTitle, "", null, tabType);
        }

        private EditorTab(String tabTitle, String tabTextContent, File openedFile, TabOpenEvent.TabType tabType) {
            this.tabTitle = tabTitle;
            this.openedFile = openedFile;
            this.textArea = new EditorTextArea(tabTextContent);
            this.tabType = tabType;

            setText(tabTitle);
            setContent(textArea);
            setOnCloseRequest(event -> {
                if (!close(this)) {
                    event.consume();
                }
            });
            selectedProperty().addListener((observable, oldValue, newValue) -> fireTabSelectedEvent(EditorTab.this));
        }

        public void updateTabTitle(String newTabTitle) {
            this.tabTitle = newTabTitle;
            setText(newTabTitle);
        }

        public void replaceWith(File file) {
            openedFile = file;
            textArea = new EditorTab.EditorTextArea(FileUtils.readFile(file));
            updateTabTitle(file.getName());
            setContent(textArea);
        }

        private class EditorTextArea extends TextArea implements FontChangeListener {

            // Indicates whether the text content has changed
            public boolean change;

            public EditorTextArea(String text) {
                setPrefColumnCount(Integer.MAX_VALUE);
                setPrefRowCount(Integer.MAX_VALUE);
                setFont(FontChangeProcessor.getFont());
                setText(text);
                getContent().addListener((observable, oldValue, newValue) -> {
                    if (!change) {
                        change = true;
                        EditorTab.this.setText("* " + EditorTab.this.tabTitle);
                    }
                    fireTabContentChangeEvent(this);
                });
                caretPositionProperty().addListener((observable, oldValue, newValue) -> {
                    fireTabContentChangeEvent(this);
                });

                FontChangeProcessor.addListener(this);
            }

            @Override
            public void onChange(FontChangeEvent e) {
                Font font = e.getNewFont();
                setFont(font);
            }
        }
    }
}