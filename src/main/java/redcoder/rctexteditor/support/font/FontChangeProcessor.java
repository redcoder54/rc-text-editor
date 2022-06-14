package redcoder.rctexteditor.support.font;

import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class FontChangeProcessor {

    private static final Font DEFAULT_FONT = Font.getDefault();
    private static final List<FontChangeListener> LISTENERS = new ArrayList<>();
    private static final int FONT_SIZE_MINIMUM = 10;
    private static final int FONT_SIZE_MAXIMUM = 100;
    private static Font font = DEFAULT_FONT;

    private FontChangeProcessor() {
    }

    public static void addListener(FontChangeListener listener) {
        LISTENERS.add(listener);
    }

    public static void removeListener(FontChangeListener listener) {
        LISTENERS.remove(listener);
    }

    public static Font getFont() {
        return font;
    }

    public static void zoomIn(Object source) {
        double fontSize = Math.min(font.getSize() + 2, FONT_SIZE_MAXIMUM);
        font = Font.font(font.getFamily(), fontSize);
        fireChangeEvent(source);
    }

    public static void zoomOut(Object source) {
        double fontSize = Math.max(font.getSize() - 2, FONT_SIZE_MINIMUM);
        font = Font.font(font.getFamily(), fontSize);
        fireChangeEvent(source);
    }

    private static void fireChangeEvent(Object source) {
        FontChangeEvent event = new FontChangeEvent(source, font);
        for (FontChangeListener listener : LISTENERS) {
            listener.onChange(event);
        }
    }
}
