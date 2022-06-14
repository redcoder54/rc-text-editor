package redcoder.rctexteditor.support.font;

/**
 * 字体变化监听器
 */
public interface FontChangeListener {

    /**
     * 处理字体变化事件
     *
     * @param e 字体变化事件
     */
    void onChange(FontChangeEvent e);
}
