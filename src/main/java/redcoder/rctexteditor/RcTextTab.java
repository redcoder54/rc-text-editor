package redcoder.rctexteditor;

import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import redcoder.rctexteditor.support.FileProcessor;
import redcoder.rctexteditor.support.UnsavedCreatedNewlyFiles;
import redcoder.rctexteditor.utils.FileUtils;

import javax.swing.*;
import java.io.File;

public class RcTextTab extends Tab {

    private static final Object[] CLOSE_OPTIONS = {"Save", "Don't Save", "Cancel"};
    private String originalTitle;
    private File openedFile;
    private RcTextArea textArea;

    public RcTextTab(File openedFile) {
        this(openedFile.getName(), FileUtils.readFile(openedFile));
        this.openedFile = openedFile;
    }

    public RcTextTab(String originalTitle, String text) {
        super(originalTitle);

        this.originalTitle = originalTitle;
        this.textArea = new RcTextArea(text);

        this.setContent(textArea);
        this.setOnCloseRequest(event -> {
            if (!close()) {
                event.consume();
            }
        });
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getTextContent() {
        return textArea.getText();
    }

    public File getOpenedFile() {
        return openedFile;
    }

    public void setOpenedFile(File openedFile) {
        this.openedFile = openedFile;
    }

    public boolean save() {
        if (FileProcessor.saveTextTabToFile(this)) {
            originalTitle = openedFile.getName();
            setText(originalTitle);
            textArea.setChange(false);

            UnsavedCreatedNewlyFiles.removeTextTab(this);
            return true;
        }
        return false;
    }

    public boolean close() {
        boolean closed = false;
        if (textArea.isChange()) {
            // FIXME: 2022/6/13 customize dialog?
            String message = String.format("Do you want to save the changes you made to %s?\n"
                    + "Your changes will be lost if you don't save them.", originalTitle);
            int state = JOptionPane.showOptionDialog(null, message, RcTextEditor.TITLE, JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, CLOSE_OPTIONS, CLOSE_OPTIONS[0]);
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

        if (closed) {
            UnsavedCreatedNewlyFiles.removeTextTab(this);
        }

        return closed;
    }

    public void replaceWith(File file) {
        originalTitle = file.getName();
        openedFile = file;
        textArea = new RcTextArea(FileUtils.readFile(file));
        setText(originalTitle);
        setContent(textArea);
    }

    private class RcTextArea extends TextArea {

        // Indicates whether the text content has changed
        private boolean change;

        public RcTextArea(String text) {
            setPrefColumnCount(Integer.MAX_VALUE);
            setPrefRowCount(Integer.MAX_VALUE);
            setText(text);
            getContent().addListener((observable, oldValue, newValue) -> {
                if (!change) {
                    change = true;
                    RcTextTab.this.setText("* " + RcTextTab.this.getOriginalTitle());
                }
            });
        }

        public boolean isChange() {
            return change;
        }

        public void setChange(boolean change) {
            this.change = change;
        }
    }
}
