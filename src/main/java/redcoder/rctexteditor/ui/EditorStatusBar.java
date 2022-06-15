package redcoder.rctexteditor.ui;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import org.tbee.javafx.scene.layout.MigPane;
import redcoder.rctexteditor.model.EditorTabPaneModel;
import redcoder.rctexteditor.support.Lifecycle;
import redcoder.rctexteditor.support.font.FontChangeEvent;
import redcoder.rctexteditor.support.font.FontChangeListener;
import redcoder.rctexteditor.support.font.FontChangeProcessor;
import redcoder.rctexteditor.support.tab.*;
import redcoder.rctexteditor.utils.SystemUtils;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EditorStatusBar extends MigPane implements Lifecycle {

    private static final Logger LOGGER = Logger.getLogger(EditorStatusBar.class.getName());

    private final EditorTabPaneModel editorTabPaneModel;
    private final EditorStatusBarListener listener;
    private final Label lengthLabel = new Label("length: 0, lines: 1");
    // private final Label rowColLabel = new Label("row: 1, col: 1");
    private final Label caretPositionLabel = new Label("caret position: 1");
    private final Label lineSeparatorLabel = new Label("Windows(CR LF)");
    private final Label fontLabel = new Label("Font Size: 12");

    public EditorStatusBar(EditorTabPaneModel editorTabPaneModel) {
        super("fill", "[fill][grow 0]");
        this.editorTabPaneModel = editorTabPaneModel;
        this.listener = new EditorStatusBarListener();

        // setBackground();
        add(lengthLabel);
        add(new Separator(Orientation.VERTICAL), "right, split 6, gapafter 3");
        add(caretPositionLabel, "gapafter 30");
        add(new Separator(Orientation.VERTICAL), "gapafter 3");
        add(lineSeparatorLabel, "gapafter 30");
        add(new Separator(Orientation.VERTICAL), "gapafter 3");
        add(fontLabel, "gapafter 20");

        // init text
        if (!SystemUtils.isWindowsOS()) {
            lineSeparatorLabel.setText("Unix(LF)");
        }
        fontLabel.setText("Font Size: " + Double.valueOf(FontChangeProcessor.getFont().getSize()).intValue());
    }

    @Override
    public void start() {
        // register listener
        editorTabPaneModel.addTabOpenListener(listener);
        editorTabPaneModel.addTabCloseListener(listener);
        editorTabPaneModel.addTabContentChangeListener(listener);
        editorTabPaneModel.addTabSelectedListener(listener);
        FontChangeProcessor.addListener(listener);
    }

    @Override
    public void stop() {
        // unregister listener
        editorTabPaneModel.removeTabOpenListener(listener);
        editorTabPaneModel.removeTabCloseListener(listener);
        editorTabPaneModel.removeTabContentChangeListener(listener);
        editorTabPaneModel.removeTabSelectedListener(listener);
        FontChangeProcessor.removeListener(listener);
    }

    private void updateLabelText(TextArea textArea) {
        int length = textArea.getLength();
        int lines = textArea.getText().split("\n").length;
        int caretPosition = textArea.getCaretPosition();
        lengthLabel.setText(String.format("length: %d, lines: %d", length, lines));
        caretPositionLabel.setText(String.format("caret position: %d", caretPosition));
    }

    private void show() {
        for (Node child : getChildren()) {
            child.setVisible(true);
        }
    }

    private void hide() {
        for (Node child : getChildren()) {
            child.setVisible(false);
        }
    }

    private class EditorStatusBarListener implements FontChangeListener, TabOpenListener, TabCloseListener,
            TabContentChangeListener, TabSelectedListener {

        private boolean isHidden;

        @Override
        public void onChange(FontChangeEvent e) {
            fontLabel.setText("Font Size: " + Double.valueOf(e.getNewFont().getSize()).intValue());
        }

        @Override
        public void onTabOpen(TabOpenEvent event) {
            if (isHidden) {
                show();
                isHidden = false;
            }
            updateStatusBar(event.getOpenedTab().getContent());
        }

        @Override
        public void onTabClose(TabCloseEvent event) {
            if (event.getEditorTabPane().getTabs().isEmpty()) {
                hide();
                isHidden = true;
            }
        }

        @Override
        public void onTabContentChange(TabContentChangeEvent event) {
            updateStatusBar(event.getContent());
        }

        @Override
        public void onTabSelected(TabSelectedEvent event) {
            updateStatusBar(event.getSelectedTab().getContent());
        }

        private void updateStatusBar(Node content) {
            try {
                TextArea textArea = (TextArea) content;
                updateLabelText(textArea);
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to update status bar.", e);
            }
        }
    }
}
