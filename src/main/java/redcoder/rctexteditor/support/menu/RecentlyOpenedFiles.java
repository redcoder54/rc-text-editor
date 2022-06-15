package redcoder.rctexteditor.support.menu;

import redcoder.rctexteditor.utils.FileUtils;
import redcoder.rctexteditor.utils.RcFileSupport;
import redcoder.rctexteditor.utils.ScheduledUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecentlyOpenedFiles {

    private static final Logger LOGGER = Logger.getLogger(RecentlyOpenedFiles.class.getName());

    private static final String FILENAME = "rof.rc";
    private static final List<File> recentlyFiles = new ArrayList<>();
    private static final File target;

    static {
        target = new File(RcFileSupport.getParentDir(), FILENAME);
        init();
    }

    private RecentlyOpenedFiles() {
    }

    /**
     * add the recently opened file
     *
     * @param file the recently opened file
     */
    public static synchronized void addFile(File file) {
        if (recentlyFiles.contains(file)) {
            // exist, move it to the head
            recentlyFiles.remove(file);
            recentlyFiles.add(0, file);
        } else {
            // insert it to the head
            recentlyFiles.add(0, file);
        }
    }

    /**
     * get all recently opened files.
     */
    public static List<File> getRecentlyFile() {
        return recentlyFiles;
    }

    private static void init() {
        loadRecentFilesFromLocal();

        ScheduledUtils.scheduleAtFixedRate(() -> {
            try {
                if (!recentlyFiles.isEmpty()) {
                    ArrayList<File> copyList = new ArrayList<>(recentlyFiles);
                    recentlyFiles.clear();
                    String content = extract(copyList);
                    FileUtils.writeFile(content, target, false);
                }
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "RecentlyOpenedFiles", e);
            }
        }, 1, 5, TimeUnit.MINUTES);
    }

    private static void loadRecentFilesFromLocal() {
        try (BufferedReader reader = new BufferedReader(new FileReader(target))) {
            String filepath = reader.readLine();
            while (filepath != null) {
                File file = new File(filepath);
                if (file.exists()) {
                    recentlyFiles.add(file);
                }
                filepath = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            // ignore
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "RecentlyOpenedFiles.loadRecentFilesFromLocal", e);
        }
    }

    private static String extract(List<File> files) {
        StringBuilder tmp = new StringBuilder();
        for (File file : files) {
            tmp.append(file.getAbsolutePath()).append("\n");
        }
        return tmp.toString();
    }
}
