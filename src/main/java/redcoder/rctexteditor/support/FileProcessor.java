package redcoder.rctexteditor.support;

import javafx.stage.FileChooser;
import redcoder.rctexteditor.RcTabPane;
import redcoder.rctexteditor.RcTextEditor;
import redcoder.rctexteditor.RcTextTab;
import redcoder.rctexteditor.utils.FileUtils;
import redcoder.rctexteditor.utils.SystemUtils;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {

    private static final FileChooser fileChooser;
    private static final List<FileOpenListener> listenerList;

    static {
        fileChooser = new FileChooser();
        fileChooser.setTitle(RcTextEditor.TITLE);
        fileChooser.setInitialDirectory(new File(SystemUtils.getUserHome()));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Text Files", "*.txt", "*.text"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        listenerList = new ArrayList<>();
    }

    private FileProcessor() {
    }

    /**
     * 打开文件，由用户选择打开哪个文件
     */
    public static void openFile(RcTabPane tabPane) {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            openFile(tabPane, file, false);
        }
    }

    /**
     * 打开指定的文件
     *
     * @param tabPane tab pane
     * @param file    要打开的文件
     * @param ucnf    是否是新创建的且未保存的文件
     */
    public static void openFile(RcTabPane tabPane, File file, boolean ucnf) {
        tabPane.newTab(file, ucnf);
        fireFileOpenEvent(tabPane, file, ucnf);
    }

    /**
     * 将文本tab中的内容保存到文件中
     *
     * @param textTab 文本tab
     * @return true：保存成功，false：保存失败
     */
    public static boolean saveTextTabToFile(RcTextTab textTab) {
        File file = textTab.getOpenedFile();
        String text = textTab.getTextContent();
        if (file != null) {
            FileUtils.writeFile(text, file);
            fireFileOpenEvent(textTab, file, false);
            return true;
        } else {
            return saveTextTabToAnotherFile(textTab) != null;
        }
    }

    /**
     * 将文本tab中的内容保存到另外一个文件中
     *
     * @param textTab 文本tab
     * @return 返回保存的文件，如果保存失败，返回null
     */
    public static File saveTextTabToAnotherFile(RcTextTab textTab) {
        File saveFile = fileChooser.showSaveDialog(null);
        if (saveFile != null) {
            String text = textTab.getTextContent();
            if (saveFile.exists()) {
                String message = String.format("%s already exist, would you like overwriting it?", saveFile.getName());
                int n = JOptionPane.showConfirmDialog(null, message, RcTextEditor.TITLE, JOptionPane.YES_NO_OPTION);
                if (n == JOptionPane.YES_OPTION) {
                    FileUtils.writeFile(text, saveFile);
                    textTab.setOpenedFile(saveFile);

                    fireFileOpenEvent(textTab, saveFile, false);
                    return saveFile;
                }
            } else {
                FileUtils.writeFile(text, saveFile);
                textTab.setOpenedFile(saveFile);

                fireFileOpenEvent(textTab, saveFile, false);
                return saveFile;
            }
        }
        return null;
    }

    /**
     * 添加文件打开监听器
     *
     * @param listener 监听器
     */
    public static void addFileOpenListener(FileOpenListener listener) {
        listenerList.add(listener);
    }

    private static void fireFileOpenEvent(Object source, File file, boolean ucnf) {
        FileOpenEvent e = new FileOpenEvent(source, ucnf, file);
        for (FileOpenListener listener : listenerList) {
            listener.onFileOpen(e);
        }
    }

}
