package redcoder.rctexteditor.support;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import redcoder.rctexteditor.model.EditorTabPaneModel;
import redcoder.rctexteditor.utils.FileUtils;
import redcoder.rctexteditor.utils.RcFileSupport;
import redcoder.rctexteditor.utils.ScheduledUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 管理与文件无关的tab
 */
public class FileIndependentTabs {

    private static final Logger LOGGER = Logger.getLogger(FileIndependentTabs.class.getName());

    private static final String DIR_NAME = "ucnf";
    private static final Map<String, Tab> textTabs;
    private static final File targetDir;
    private static boolean loaded;

    static {
        textTabs = new HashMap<>();
        targetDir = new File(RcFileSupport.getParentDir(), DIR_NAME);
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        ScheduledUtils.scheduleAtFixedRate(() -> {
            try {
                for (Map.Entry<String, Tab> entry : textTabs.entrySet()) {
                    String filename = entry.getKey();
                    File f = new File(targetDir, filename);
                    Node content = entry.getValue().getContent();
                    if (content instanceof TextArea) {
                        FileUtils.writeFile(((TextArea) content).getText(), f);
                    } else {
                        LOGGER.log(Level.WARNING, "Cannot save tab, because the content of tab is not a TextArea.");
                    }
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "UnsavedCreatedNewlyFiles", e);
            }
        }, 1, 3, TimeUnit.MINUTES);
    }

    private FileIndependentTabs() {
    }

    /**
     * 添加新创建的且未保存的文本tab
     *
     * @param tab 新创建的且未保存的文本tab
     */
    public static void addTab(Tab tab) {
        textTabs.putIfAbsent(tab.getText(), tab);
    }

    /**
     * 移除tab
     *
     * @param tab 文本tab
     */
    public static void removeTab(Tab tab) {
        String filename = tab.getText();
        textTabs.remove(filename);

        // delete file
        File f = new File(targetDir, filename);
        f.delete();
    }

    /**
     * 加载与文件无关的tab
     *
     * @return 加载的文件数量
     */
    public static int load(EditorTabPaneModel editorTabPaneModel) {
        if (loaded) {
            return 0;
        }
        loaded = true;
        File[] files = targetDir.listFiles(pathname -> !pathname.isDirectory());
        if (files == null || files.length == 0) {
            return 0;
        }
        for (File file : files) {
            editorTabPaneModel.newFileIndependentTab(file.getName(), FileUtils.readFile(file));
        }
        return files.length;
    }
}
