package redcoder.rctexteditor.support;

import redcoder.rctexteditor.RcTabPane;
import redcoder.rctexteditor.RcTextTab;
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
 * 管理未保存的且新创建的文件
 */
public class UnsavedCreatedNewlyFiles {

    private static final Logger LOGGER = Logger.getLogger(UnsavedCreatedNewlyFiles.class.getName());

    private static final String DIR_NAME = "ucnf";
    private static final Map<String, RcTextTab> textPanes;
    private static final File targetDir;
    private static boolean loaded;

    static {
        textPanes = new HashMap<>();
        targetDir = new File(RcFileSupport.getParentDir(), DIR_NAME);
        if (!targetDir.exists()) {
            targetDir.mkdir();
        }
        ScheduledUtils.scheduleAtFixedRate(() -> {
            try {
                for (Map.Entry<String, RcTextTab> entry : textPanes.entrySet()) {
                    String filename = entry.getKey();
                    File f = new File(targetDir, filename);
                    FileUtils.writeFile(entry.getValue().getTextContent(), f);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "UnsavedCreatedNewlyFiles", e);
            }
        }, 1, 3, TimeUnit.MINUTES);
    }

    private UnsavedCreatedNewlyFiles() {
    }

    /**
     * 添加新创建的且未保存的文本tab
     *
     * @param tab 新创建的且未保存的文本tab
     */
    public static void addTextTab(RcTextTab tab) {
        textPanes.putIfAbsent(tab.getOriginalTitle(), tab);
    }

    /**
     * 移除tab
     *
     * @param tab 文本tab
     */
    public static void removeTextTab(RcTextTab tab) {
        String filename = tab.getOriginalTitle();
        textPanes.remove(filename);

        // delete file
        File f = new File(targetDir, filename);
        f.delete();
    }

    /**
     * 加载未保存的且新创建的文件
     *
     * @param tabPane 主窗格
     * @return 加载的文件数量
     */
    public static int load(RcTabPane tabPane) {
        if (loaded) {
            return 0;
        }
        loaded = true;
        File[] files = targetDir.listFiles(pathname -> !pathname.isDirectory());
        if (files == null || files.length == 0) {
            return 0;
        }
        for (File file : files) {
            FileProcessor.openFile(tabPane, file, true);
        }
        return files.length;
    }
}
