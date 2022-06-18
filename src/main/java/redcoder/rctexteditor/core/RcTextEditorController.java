package redcoder.rctexteditor.core;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.tbee.javafx.scene.layout.fxml.MigPane;
import redcoder.rctexteditor.core.editoraction.DefaultEditorActionProcessor;
import redcoder.rctexteditor.core.editoraction.EditorAction;
import redcoder.rctexteditor.core.editoraction.EditorActionProcessor;
import redcoder.rctexteditor.core.tab.EditorTabListeners;
import redcoder.rctexteditor.core.tab.TabType;
import redcoder.rctexteditor.core.tab.event.EditorTabListener;
import redcoder.rctexteditor.core.tab.event.TabEvent;
import redcoder.rctexteditor.core.tab.event.TabEvent.TabEventType;
import redcoder.rctexteditor.support.font.FontChangeEvent;
import redcoder.rctexteditor.support.font.FontChangeListener;
import redcoder.rctexteditor.support.font.FontChangeProcessor;
import redcoder.rctexteditor.support.menu.RecentlyOpenedFiles;
import redcoder.rctexteditor.core.tab.FileIndependentTabs;
import redcoder.rctexteditor.core.tab.NewTabId;
import redcoder.rctexteditor.ui.EditorTab;
import redcoder.rctexteditor.utils.FileUtils;
import redcoder.rctexteditor.utils.ScheduledUtils;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RcTextEditorController {

    private static final Logger LOGGER = Logger.getLogger(RcTextEditorController.class.getName());

    private EditorActionProcessor processor;
    @FXML
    private TabPane tabPane;
    @FXML
    private Menu recentlyOpenedMenu;
    @FXML
    private MigPane statusBar;
    @FXML
    private Label lengthLabel;
    @FXML
    private Label caretPositionLabel;
    @FXML
    private Label fontLabel;

    @FXML
    private void initialize() {
        this.processor = new DefaultEditorActionProcessor(tabPane);

        this.tabPane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            if ((code == KeyCode.EQUALS || code == KeyCode.ADD) && event.isControlDown()) {
                this.processor.handleEditorAction(EditorAction.ZOOM_IN);
                event.consume();
            } else if ((code == KeyCode.MINUS || code == KeyCode.SUBTRACT) && event.isControlDown()) {
                this.processor.handleEditorAction(EditorAction.ZOOM_OUT);
                event.consume();
            }
        });

        // load saved file independent tab
        for (File file : FileIndependentTabs.getFileIndependentTabs()) {
            EditorTab editorTab = new EditorTab(file.getName(), TabType.FILE_INDEPENDENT);
            editorTab.getTabContent().appendText(FileUtils.readFile(file));
            // add tab, then switch to it
            tabPane.getTabs().add(editorTab);
            tabPane.getSelectionModel().selectNext();
            editorTab.getTabContent().requestFocus();
            NewTabId.nextId();
        }

        // init recently opened file menu
        initRecentlyOpenedFileMenu();

        // for status bar
        EditorStatusBarListener listener = new EditorStatusBarListener();
        EditorTabListeners.addEditorTabListener(listener);
        FontChangeProcessor.addListener(listener);
    }

    @FXML
    private void handleAction(ActionEvent event) {
        try {
            Object source = event.getSource();
            Object userData;
            if (source instanceof MenuItem) {
                userData = ((MenuItem) source).getUserData();
            } else if (source instanceof Node) {
                userData = ((Node) source).getUserData();
            } else {
                LOGGER.warning(() -> String.format("The type of source is '%s', we cannot get UserData from it.", source.getClass().getName()));
                return;
            }

            if (userData instanceof EditorAction) {
                processor.handleEditorAction((EditorAction) userData);
            } else {
                LOGGER.warning(() -> "Unknown userData: " + userData);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to handle action.", e);
        }
    }

    @FXML
    private void handleTabPaneChange(ListChangeListener.Change<Tab> c) {
        while (c.next()) {
            if (c.wasAdded()) {
                List<? extends Tab> list = c.getAddedSubList();
                for (Tab tab : list) {
                    EditorTab editorTab = (EditorTab) tab;
                    if (editorTab.getTabType() == TabType.FILE_INDEPENDENT) {
                        FileIndependentTabs.addTab(editorTab.getTabTitle(), editorTab);
                    }
                    fireTabEvent(TabEventType.TAB_OPEN, editorTab);
                }
            } else if (c.wasRemoved()) {
                List<? extends Tab> list = c.getRemoved();
                for (Tab tab : list) {
                    EditorTab editorTab = (EditorTab) tab;
                    FileIndependentTabs.removeTab(editorTab.getTabTitle(), editorTab);
                    fireTabEvent(TabEventType.TAB_CLOSE, editorTab);
                }
            }
        }
    }

    private void fireTabEvent(TabEventType eventType, EditorTab editorTab) {
        TabEvent tabEvent = new TabEvent(this);
        tabEvent.setTabPane(tabPane);
        tabEvent.setEditorTab(editorTab);
        tabEvent.setEventType(eventType);
        EditorTabListeners.dispatchTabEvent(tabEvent);
    }

    private void initRecentlyOpenedFileMenu() {
        EditorTabListeners.addEditorTabListener(new EditorTabListener() {
            @Override
            public void onTabOpen(TabEvent event) {
                if (TabType.FILE_DEPENDENT == event.getEditorTab().getTabType()) {
                    addRecentlyOpenedFile(event.getEditorTab().getOpenedFile());
                }
            }
        });
        ScheduledUtils.schedule(() -> {
            for (File file : RecentlyOpenedFiles.getRecentlyFile()) {
                addMenuItem(file);
            }
        }, 0, TimeUnit.SECONDS);
    }

    private void addMenuItem(File file) {
        MenuItem menuItem = new MenuItem(file.getAbsolutePath());
        menuItem.setOnAction(event -> {
            // add tab, then switch to it
            EditorTab editorTab = new EditorTab(file, TabType.FILE_DEPENDENT);
            tabPane.getTabs().add(editorTab);
            tabPane.getSelectionModel().selectNext();

            addRecentlyOpenedFile(file);
        });
        recentlyOpenedMenu.getItems().add(menuItem);
    }

    private void addRecentlyOpenedFile(File file) {
        RecentlyOpenedFiles.addFile(file);
        String filepath = file.getAbsolutePath();

        // if menuitem exist, move it to first
        ObservableList<MenuItem> items = recentlyOpenedMenu.getItems();
        Iterator<MenuItem> it = items.iterator();
        while (it.hasNext()) {
            MenuItem menuItem = it.next();
            if (Objects.equals(menuItem.getText(), filepath)) {
                // move to first
                it.remove();
                items.add(0, menuItem);
                return;
            }
        }

        // not exist, insert to first
        addMenuItem(file);
    }

    private class EditorStatusBarListener implements FontChangeListener, EditorTabListener {

        private boolean isHidden;

        @Override
        public void onChange(FontChangeEvent e) {
            fontLabel.setText("Font Size: " + Double.valueOf(e.getNewFont().getSize()).intValue());
        }

        @Override
        public void onTabOpen(TabEvent event) {
            if (isHidden) {
                show();
                isHidden = false;
            }
            updateStatusBar(event.getEditorTab().getTabContent());
        }

        @Override
        public void onTabClose(TabEvent event) {
            if (event.getTabPane().getTabs().isEmpty()) {
                hide();
                isHidden = true;
            }
        }

        @Override
        public void onTabContentChange(TabEvent event) {
            updateStatusBar(event.getEditorTab().getTabContent());
        }

        @Override
        public void onTabSelected(TabEvent event) {
            updateStatusBar(event.getEditorTab().getTabContent());
        }

        private void show() {
            for (Node child : statusBar.getChildren()) {
                child.setVisible(true);
            }
        }

        private void hide() {
            for (Node child : statusBar.getChildren()) {
                child.setVisible(false);
            }
        }

        private void updateStatusBar(Node content) {
            try {
                TextArea textArea = (TextArea) content;
                int length = textArea.getLength();
                int lines = textArea.getText().split("\n").length;
                int caretPosition = textArea.getCaretPosition();
                lengthLabel.setText(String.format("length: %d, lines: %d", length, lines));
                caretPositionLabel.setText(String.format("caret position: %d", caretPosition));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to update status bar.", e);
            }
        }
    }
}
