package redcoder.rctexteditor.model;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public interface FindReplaceUIModel extends EventHandler<ActionEvent> {

    String USER_DATA_PREV_BUTTON = "Prev";
    String USER_DATA_NEXT_BUTTON = "Next";
    String USER_DATA_REPLACE_BUTTON = "Replace";
    String USER_DATA_REPLACE_ALL_BUTTON = "Replace All";
}
