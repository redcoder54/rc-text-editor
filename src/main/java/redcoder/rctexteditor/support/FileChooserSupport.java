package redcoder.rctexteditor.support;

import javafx.stage.FileChooser;
import redcoder.rctexteditor.RcTextEditor;
import redcoder.rctexteditor.utils.SystemUtils;

import java.io.File;

public abstract class FileChooserSupport {

    private static final FileChooser FILE_CHOOSER = new FileChooser();

    static {
        FILE_CHOOSER.setTitle(RcTextEditor.TITLE);
        FILE_CHOOSER.setInitialDirectory(new File(SystemUtils.getUserHome()));
        FILE_CHOOSER.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Text Files", "*.txt", "*.text"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
    }

    public static FileChooser getFileChooser() {
        return FILE_CHOOSER;
    }
}
