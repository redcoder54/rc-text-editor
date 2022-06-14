package redcoder.rctexteditor.support.font;

import javafx.scene.text.Font;

import java.util.EventObject;

/**
 * 字体变化事件
 */
public class FontChangeEvent extends EventObject {

    private final Font newFont;

    public FontChangeEvent(Object source, Font newFont) {
        super(source);
        this.newFont = newFont;
    }

    /**
     * 新的字体
     */
    public Font getNewFont() {
        return newFont;
    }
}
