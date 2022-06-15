package redcoder.rctexteditor.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.tbee.javafx.scene.layout.MigPane;
import redcoder.rctexteditor.model.EditorTabPaneModel;
import redcoder.rctexteditor.support.ImageResources;
import redcoder.rctexteditor.support.Lifecycle;

public class EditorToolBar extends MigPane implements Lifecycle {

    private final EditorTabPaneModel editorTabPaneModel;

    public EditorToolBar(EditorTabPaneModel editorTabPaneModel) {
        super("gap 5!");
        this.editorTabPaneModel = editorTabPaneModel;
    }

    @Override
    public void start() {
        // new file
        Button newFileBtn = createButton("New File", "Add16.gif", event -> editorTabPaneModel.newFileIndependentTab());
        //open file
        Button openFileBtn = createButton("New File", "Open16.gif", event -> editorTabPaneModel.newFileDependentTab());
        // save file
        Button saveFileBtn = createButton("Save File", "Save16.gif", event -> editorTabPaneModel.saveSelectedTab());
        // save as
        Button saveAsBtn = createButton("Save As", "SaveAs16.gif", event -> editorTabPaneModel.saveSelectedTabAs());
        // save all
        Button saveAllBtn = createButton("Save All", "SaveAll16.gif", event -> editorTabPaneModel.saveAllTab());
        // cut
        Button cutBtn = createButton("Cut", "Cut16.gif", event -> editorTabPaneModel.handleAction(EditorTabPaneModel.Action.CUT));
        // copy
        Button copyBtn = createButton("Copy", "Copy16.gif", event -> editorTabPaneModel.handleAction(EditorTabPaneModel.Action.COPY));
        // paste
        Button pasteBtn = createButton("Paste", "Paste16.gif", event -> editorTabPaneModel.handleAction(EditorTabPaneModel.Action.PASTE));
        // undo
        Button undoBtn = createButton("Undo", "Undo16.gif", event -> editorTabPaneModel.handleAction(EditorTabPaneModel.Action.UNDO));
        // redo
        Button redoBtn = createButton("Redo", "Redo16.gif", event -> editorTabPaneModel.handleAction(EditorTabPaneModel.Action.REDO));
        // find
        Button findBtn = createButton("Find", "Find16.gif", event -> editorTabPaneModel.handleAction(EditorTabPaneModel.Action.FIND));
        // replace
        Button replaceBtn = createButton("Replace", "Replace16.gif", event -> editorTabPaneModel.handleAction(EditorTabPaneModel.Action.REPLACE));

        getChildren().addAll(newFileBtn, openFileBtn, new Separator(Orientation.VERTICAL),
                saveFileBtn, saveAsBtn, saveAllBtn, new Separator(Orientation.VERTICAL),
                cutBtn, copyBtn, pasteBtn, new Separator(Orientation.VERTICAL),
                undoBtn, redoBtn, new Separator(Orientation.VERTICAL),
                findBtn, replaceBtn);
        getStylesheets().add(getClass().getClassLoader().getResource("ui/EditorToolBar.css").toExternalForm());
    }

    private Button createButton(String text, String imageName, EventHandler<ActionEvent> handler) {
        Button button = new Button();
        Image image = ImageResources.getImage(imageName);
        if (image != null) {
            button.setGraphic(new ImageView(image));
        } else {
            button.setText(text);
        }
        button.setPadding(new Insets(3));
        button.setTooltip(new Tooltip(text));
        button.setOnAction(handler);
        return button;
    }
}
