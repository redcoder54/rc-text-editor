package redcoder.rctexteditor.core.editoraction;

import javafx.scene.control.TabPane;
import redcoder.rctexteditor.core.editoraction.strategy.*;

import java.util.HashMap;
import java.util.Map;

import static redcoder.rctexteditor.core.editoraction.EditorAction.*;

public class DefaultEditorActionProcessor implements EditorActionProcessor {

    private final Map<EditorAction, EditorActionStrategy> actionStrategies = new HashMap<>();
    private final TabPane tabPane;

    public DefaultEditorActionProcessor(TabPane tabPane) {
        this.tabPane = tabPane;
        initActionStrategy();
    }

    private void initActionStrategy() {
        actionStrategies.put(NEW_FILE_INDEPENDENT_TAB, new NewFileIndependentTabActionStrategy());
        actionStrategies.put(NEW_FILE_DEPENDENT_TAB, new NewFileDependentTabActionStrategy());
        actionStrategies.put(SAVE_SELECTED_TAB, new SaveSelectedTabActionStrategy());
        actionStrategies.put(SAVE_SELECTED_TAB_AS, new SaveSelectedTabAsActionStrategy());
        actionStrategies.put(SAVE_ALL_TAB, new SaveAllActionStrategy());
        actionStrategies.put(CLOSE_SELECTED_TAB, new CloseSelectedTabActionStrategy());
        actionStrategies.put(CLOSE_ALL_TAB, new CloseAllTabActionStrategy());
        actionStrategies.put(CUT, new CutActionStrategy());
        actionStrategies.put(COPY, new CopyActionStrategy());
        actionStrategies.put(PASTE, new PasteActionStrategy());
        actionStrategies.put(UNDO, new UndoActionStrategy());
        actionStrategies.put(REDO, new RedoActionStrategy());
        actionStrategies.put(FIND, new FindActionStrategy());
        actionStrategies.put(REPLACE, new ReplaceActionStrategy());
        actionStrategies.put(ZOOM_IN, new ZoomInActionStrategy());
        actionStrategies.put(ZOOM_OUT, new ZoomOutActionStrategy());
        actionStrategies.put(AUTO_WRAP, new AutoWrapActionStrategy());
    }

    @Override
    public void handleEditorAction(EditorAction action) {
        EditorActionStrategy actionStrategy = actionStrategies.get(action);
        if (actionStrategy == null) {
            throw new RuntimeException(String.format("Unknown EditorAction: %s", action.name()));
        }
        actionStrategy.onAction(tabPane);
    }

}
