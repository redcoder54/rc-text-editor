package redcoder.rctexteditor.support;

import java.io.File;
import java.util.EventObject;

/**
 * 文件打开事件
 */
public class FileOpenEvent extends EventObject {

    private boolean unsavedNew;
    private File openedFile;

    /**
     * 创建事件对象
     *
     * @param source     触发事件的对象
     * @param unsavedNew 是否是新创建的且未保存的文件
     * @param openedFile 打开的文件
     * @throws IllegalArgumentException if source is null.
     */
    public FileOpenEvent(Object source, boolean unsavedNew, File openedFile) {
        super(source);
        this.unsavedNew = unsavedNew;
        this.openedFile = openedFile;
    }


    /**
     * 是否是新创建的且未保存的文件
     */
    public boolean isUnSavedNew() {
        return unsavedNew;
    }

    /**
     * 打开的文件
     */
    public File getOpenedFile() {
        return openedFile;
    }
}
