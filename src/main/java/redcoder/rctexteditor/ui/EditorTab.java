package redcoder.rctexteditor.ui;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import redcoder.rctexteditor.RcTextEditor;
import redcoder.rctexteditor.core.tab.EditorTabListeners;
import redcoder.rctexteditor.core.tab.TabType;
import redcoder.rctexteditor.core.tab.event.TabEvent;
import redcoder.rctexteditor.core.tab.event.TabEvent.TabEventType;
import redcoder.rctexteditor.support.FileChooserSupport;
import redcoder.rctexteditor.support.font.FontChangeEvent;
import redcoder.rctexteditor.support.font.FontChangeListener;
import redcoder.rctexteditor.support.font.FontChangeProcessor;
import redcoder.rctexteditor.utils.FileUtils;

import javax.swing.*;
import java.io.File;

public class EditorTab extends Tab {

    private static final Object[] CLOSE_OPTIONS = {"Save", "Don't Save", "Cancel"};

    private String tabTitle;
    private File openedFile;
    private EditorTextArea tabContent;
    private final TabType tabType;

    public EditorTab(File openedFile, TabType tabType) {
        this(openedFile.getName(), FileUtils.readFile(openedFile), openedFile, tabType);
    }

    public EditorTab(String tabTitle, TabType tabType) {
        this(tabTitle, "", null, tabType);
    }

    private EditorTab(String tabTitle, String tabTextContent, File openedFile, TabType tabType) {
        this.tabTitle = tabTitle;
        this.openedFile = openedFile;
        this.tabContent = new EditorTextArea(tabTextContent);
        this.tabType = tabType;

        setText(tabTitle);
        setContent(tabContent);
        setOnCloseRequest(event -> {
            if (!close()) {
                event.consume();
            }
        });
        selectedProperty().addListener((observable, oldValue, newValue) -> {
            fireTabEvent(TabEventType.TAB_SELECTED);
        });
    }

    public String getTabTitle() {
        return tabTitle;
    }

    public TextArea getTabContent() {
        return tabContent;
    }

    public File getOpenedFile() {
        return openedFile;
    }

    public TabType getTabType() {
        return tabType;
    }

    public boolean save() {
        String text = tabContent.getText();
        if (openedFile != null) {
            FileUtils.writeFile(text, openedFile);
        } else {
            saveAs();
        }
        return true;
    }

    public void saveAs() {
        boolean success = false;
        File savedFile = FileChooserSupport.getFileChooser().showSaveDialog(null);
        if (savedFile != null) {
            String text = tabContent.getText();
            if (savedFile.exists()) {
                String message = String.format("%s already exist, would you like overwriting it?", savedFile.getName());
                int n = JOptionPane.showConfirmDialog(null, message, RcTextEditor.TITLE, JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    FileUtils.writeFile(text, savedFile);
                    success = true;
                }
            } else {
                FileUtils.writeFile(text, savedFile);
                success = true;
            }
        }

        if (success) {
            openedFile = savedFile;
            // replace current tab
            tabContent = new EditorTextArea(FileUtils.readFile(openedFile));
            updateTabTitle(openedFile.getName());
            setContent(tabContent);
        }
    }

    public boolean close() {
        boolean closed = false;
        if (tabContent.change) {
            // TODO: 2022/6/13 customize dialog?
            String message = String.format("Do you want to save the changes you made to %s?\n"
                    + "Your changes will be lost if you don't save them.", tabTitle);
            int state = JOptionPane.showOptionDialog(null, message, RcTextEditor.TITLE,
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, CLOSE_OPTIONS, CLOSE_OPTIONS[0]);
            if (state == JOptionPane.YES_OPTION) {
                // save file firstly, then close it.
                if (save()) {
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

    private void updateTabTitle(String newTabTitle) {
        this.tabTitle = newTabTitle;
        setText(newTabTitle);
    }

    private void fireTabEvent(TabEventType eventType) {
        TabEvent tabEvent = new TabEvent(EditorTab.this);
        tabEvent.setTabPane(getTabPane());
        tabEvent.setEditorTab(EditorTab.this);
        tabEvent.setEventType(eventType);
        EditorTabListeners.dispatchTabEvent(tabEvent);
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
                fireTabEvent(TabEventType.TAB_CONTENT_CHANGE);
            });
            caretPositionProperty().addListener((observable, oldValue, newValue) -> {
                fireTabEvent(TabEventType.TAB_CONTENT_CHANGE);
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
