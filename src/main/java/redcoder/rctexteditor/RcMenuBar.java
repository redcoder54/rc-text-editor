package redcoder.rctexteditor;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import redcoder.rctexteditor.support.FileOpenEvent;
import redcoder.rctexteditor.support.FileOpenListener;
import redcoder.rctexteditor.support.FileProcessor;
import redcoder.rctexteditor.support.RecentlyOpenedFiles;
import redcoder.rctexteditor.utils.ScheduledUtils;

import java.io.File;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class RcMenuBar extends MenuBar {

    private final RcTabPane tabPane;
    private final AtomicInteger counter = new AtomicInteger(0);

    public RcMenuBar(RcTabPane tabPane) {
        this.tabPane = tabPane;
        init();
    }

    private void init() {
        Menu fileMenu = createFileMenu();
        getMenus().add(fileMenu);
    }

    private Menu createFileMenu() {
        Menu fileMenu = new Menu("File");
        ObservableList<MenuItem> items = fileMenu.getItems();

        // new file
        MenuItem newFileItem = new MenuItem("New File");
        newFileItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        newFileItem.setOnAction(event -> tabPane.newTab("new-" + counter.getAndIncrement()));

        items.add(newFileItem);
        items.add(new SeparatorMenuItem());

        // open file
        MenuItem openFileItem = new MenuItem("Open File");
        openFileItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        openFileItem.setOnAction(event -> FileProcessor.openFile(tabPane));
        items.add(openFileItem);
        // open recently file
        items.add(new OpenRecentlyFileMenu());
        items.add(new SeparatorMenuItem());

        // save file
        MenuItem saveFileItem = new MenuItem("Save File");
        saveFileItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveFileItem.setOnAction(event -> tabPane.saveCurrentTab());
        items.add(saveFileItem);
        // save as
        MenuItem saveAsItem = new MenuItem("Save As");
        saveAsItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
        saveAsItem.setOnAction(event -> {
            RcTextTab textTab = tabPane.getCurrentTab();
            if (textTab != null) {
                FileProcessor.saveTextTabToAnotherFile(textTab);
            }
        });
        items.add(saveAsItem);
        // save all
        MenuItem saveAllItem = new MenuItem("Save All");
        saveAllItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        saveAllItem.setOnAction(event -> tabPane.saveAllTab());
        items.add(saveAllItem);
        items.add(new SeparatorMenuItem());

        // close file
        MenuItem closeFileItem = new MenuItem("Close File");
        closeFileItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));
        closeFileItem.setOnAction(event -> tabPane.closeCurrentTab());
        items.add(closeFileItem);
        // close all
        MenuItem closeAllItem = new MenuItem("Close All");
        closeAllItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        closeAllItem.setOnAction(event -> tabPane.closeAllTab());
        items.add(closeAllItem);

        return fileMenu;
    }

    private class OpenRecentlyFileMenu extends Menu implements FileOpenListener {

        public OpenRecentlyFileMenu() {
            super("Open Recently");
            FileProcessor.addFileOpenListener(this);
            ScheduledUtils.schedule(this::initMenuItem, 0, TimeUnit.SECONDS);
        }

        private void initMenuItem() {
            for (File file : RecentlyOpenedFiles.getRecentlyFile()) {
                addMenuItem(file);
            }
        }

        private void addMenuItem(File file) {
            MenuItem menuItem = new MenuItem(file.getAbsolutePath());
            menuItem.setOnAction(event -> {
                tabPane.newTab(file, false);
                addOpenedFileRecently(file);
            });
            getItems().add(menuItem);
        }

        @Override
        public void onFileOpen(FileOpenEvent e) {
            if (!e.isUnSavedNew()) {
                File file = e.getOpenedFile();
                addOpenedFileRecently(file);
            }
        }

        private void addOpenedFileRecently(File file) {
            RecentlyOpenedFiles.addFile(file);
            String filepath = file.getAbsolutePath();

            // if menuitem exist, move it to first
            ObservableList<MenuItem> items = getItems();
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
    }
}
