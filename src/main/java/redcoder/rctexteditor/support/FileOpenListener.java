package redcoder.rctexteditor.support;

/**
 * 接收文件打开事件的监听器
 */
public interface FileOpenListener {

    /**
     * 响应文件打开事件
     *
     * @param e 件打开事件
     */
    void onFileOpen(FileOpenEvent e);
}
