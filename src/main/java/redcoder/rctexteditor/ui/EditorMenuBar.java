package redcoder.rctexteditor.ui;

import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.*;
import redcoder.rctexteditor.support.Lifecycle;
import redcoder.rctexteditor.support.tab.TabOpenEvent;
import redcoder.rctexteditor.support.tab.TabOpenListener;
import redcoder.rctexteditor.model.EditorTabPaneModel;
import redcoder.rctexteditor.model.EditorTabPaneModel.Action;
import redcoder.rctexteditor.support.menu.RecentlyOpenedFiles;
import redcoder.rctexteditor.utils.ScheduledUtils;

import java.io.File;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class EditorMenuBar extends MenuBar implements Lifecycle {

    private final EditorTabPaneModel editorTabPaneModel;
    private final OpenRecentlyFileMenu orfMenu;

    public EditorMenuBar(EditorTabPaneModel editorTabPaneModel) {
        this.editorTabPaneModel = editorTabPaneModel;
        this.orfMenu = new OpenRecentlyFileMenu(editorTabPaneModel);
    }

    @Override
    public void start() {
        initMenu();
    }

    @Override
    public void stop() {
        editorTabPaneModel.removeTabOpenListener(orfMenu);
    }

    private void initMenu() {
        Menu fileMenu = createFileMenu();
        Menu editMenu = createEditMenu();
        Menu viewMenu = createViewMenu();
        getMenus().addAll(fileMenu, editMenu, viewMenu);
    }

    private Menu createFileMenu() {
        Menu fileMenu = new Menu("File");
        ObservableList<MenuItem> items = fileMenu.getItems();

        // new file
        MenuItem newFileItem = new MenuItem("New File");
        newFileItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        newFileItem.setOnAction(event -> editorTabPaneModel.newFileIndependentTab());

        items.add(newFileItem);
        items.add(new SeparatorMenuItem());

        // open file
        MenuItem openFileItem = new MenuItem("Open File");
        openFileItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        openFileItem.setOnAction(event -> editorTabPaneModel.newFileDependentTab());
        items.add(openFileItem);
        // open recently file
        items.add(orfMenu);
        items.add(new SeparatorMenuItem());

        // save file
        MenuItem saveFileItem = new MenuItem("Save File");
        saveFileItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveFileItem.setOnAction(event -> editorTabPaneModel.saveSelectedTab());
        items.add(saveFileItem);
        // save as
        MenuItem saveAsItem = new MenuItem("Save As");
        saveAsItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
        saveAsItem.setOnAction(event -> editorTabPaneModel.saveSelectedTabAs());
        items.add(saveAsItem);
        // save all
        MenuItem saveAllItem = new MenuItem("Save All");
        saveAllItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        saveAllItem.setOnAction(event -> editorTabPaneModel.saveAllTab());
        items.add(saveAllItem);
        items.add(new SeparatorMenuItem());

        // close file
        MenuItem closeFileItem = new MenuItem("Close File");
        closeFileItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));
        closeFileItem.setOnAction(event -> editorTabPaneModel.closeSelectedTab());
        items.add(closeFileItem);
        // close all
        MenuItem closeAllItem = new MenuItem("Close All");
        closeAllItem.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));
        closeAllItem.setOnAction(event -> editorTabPaneModel.closeAllTab());
        items.add(closeAllItem);
        items.add(new SeparatorMenuItem());

        // exit
        MenuItem exitItem = new MenuItem("Exit");
        // exitItem.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.CONTROL_DOWN));
        exitItem.setOnAction(event -> System.exit(0));
        items.add(exitItem);

        return fileMenu;
    }

    private Menu createEditMenu() {
        Menu editMenu = new Menu("Edit");
        ObservableList<MenuItem> items = editMenu.getItems();
        // cut
        MenuItem cutItem = new MenuItem("Cut");
        cutItem.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
        cutItem.setOnAction(event -> editorTabPaneModel.handleAction(Action.CUT));
        items.add(cutItem);
        // copy
        MenuItem copyItem = new MenuItem("Copy");
        copyItem.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
        copyItem.setOnAction(event -> editorTabPaneModel.handleAction(Action.COPY));
        items.add(copyItem);
        // paste
        MenuItem pasteItem = new MenuItem("Paste");
        pasteItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        pasteItem.setOnAction(event -> editorTabPaneModel.handleAction(Action.PASTE));
        items.add(pasteItem);
        items.add(new SeparatorMenuItem());

        // undo
        MenuItem undoItem = new MenuItem("Undo");
        undoItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        undoItem.setOnAction(event -> editorTabPaneModel.handleAction(Action.UNDO));
        items.add(undoItem);
        // redo
        MenuItem redoItem = new MenuItem("Redo");
        redoItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
        redoItem.setOnAction(event -> editorTabPaneModel.handleAction(Action.REDO));
        items.add(redoItem);
        items.add(new SeparatorMenuItem());

        // find
        MenuItem findItem = new MenuItem("Find");
        findItem.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
        findItem.setOnAction(event -> editorTabPaneModel.handleAction(Action.FIND));
        items.add(findItem);
        // replace
        MenuItem replaceItem = new MenuItem("Replace");
        replaceItem.setAccelerator(new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN));
        replaceItem.setOnAction(event -> editorTabPaneModel.handleAction(Action.REPLACE));
        items.add(replaceItem);
        items.add(new SeparatorMenuItem());

        return editMenu;
    }

    private Menu createViewMenu() {
        Menu editMenu = new Menu("View");
        ObservableList<MenuItem> items = editMenu.getItems();

        // zoom in
        MenuItem zoomInItem = new MenuItem("Zoom In");
        zoomInItem.setAccelerator(new KeyCharacterCombination("=", KeyCombination.CONTROL_DOWN));
        zoomInItem.setOnAction(event -> editorTabPaneModel.handleAction(Action.ZOOM_IN));
        items.add(zoomInItem);
        // zoom out
        MenuItem zoomOutItem = new MenuItem("Zoom Out");
        zoomOutItem.setAccelerator(new KeyCharacterCombination("-", KeyCombination.CONTROL_DOWN));
        zoomOutItem.setOnAction(event -> editorTabPaneModel.handleAction(Action.ZOOM_OUT));
        items.add(zoomOutItem);
        items.add(new SeparatorMenuItem());

        // auto wrap
        MenuItem autoWrapItem = new MenuItem("Auto Wrap");
        autoWrapItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.ALT_DOWN));
        autoWrapItem.setOnAction(event -> editorTabPaneModel.handleAction(Action.AUTO_WRAP));
        items.add(autoWrapItem);

        return editMenu;
    }

    private class OpenRecentlyFileMenu extends Menu implements TabOpenListener {

        public OpenRecentlyFileMenu(EditorTabPaneModel editorTabPaneModel) {
            super("Open Recently");
            editorTabPaneModel.addTabOpenListener(this);
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
                editorTabPaneModel.newFileDependentTab(file);
                addRecentlyOpenedFile(file);
            });
            getItems().add(menuItem);
        }

        private void addRecentlyOpenedFile(File file) {
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

        @Override
        public void onTabOpen(TabOpenEvent event) {
            if (TabOpenEvent.TabType.FILE_DEPENDENT == event.getTabType()) {
                File relatedFile = event.getRelatedFile();
                addRecentlyOpenedFile(relatedFile);
            }
        }
    }
}
