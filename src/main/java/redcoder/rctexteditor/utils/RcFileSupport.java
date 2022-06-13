package redcoder.rctexteditor.utils;

import java.io.File;

public abstract class RcFileSupport {

    private static final String dir = "redcoder54/rc-text-editor";

    public static File getParentDir() {
        File file = new File(SystemUtils.getUserHome(), dir);
        FileUtils.ensureDirExist(file);
        return file;
    }
}
