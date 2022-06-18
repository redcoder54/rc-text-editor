package redcoder.rctexteditor.core.editoraction.strategy;

import javafx.scene.control.TabPane;
import redcoder.rctexteditor.support.font.FontChangeProcessor;

public class ZoomInActionStrategy implements EditorActionStrategy {
    @Override
    public void onAction(TabPane tabPane) {
        FontChangeProcessor.zoomIn(this);
    }
}
