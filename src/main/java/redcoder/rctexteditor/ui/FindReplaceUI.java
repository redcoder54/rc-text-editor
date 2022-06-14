package redcoder.rctexteditor.ui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import org.tbee.javafx.scene.layout.MigPane;
import redcoder.rctexteditor.model.FindReplaceUIModel;
import redcoder.rctexteditor.model.impl.DefaultFindReplaceUIModel;

public class FindReplaceUI extends BorderPane {

    // replace dialog required
    private final Label replaceLabel = new Label("Replace With:");
    private final TextField replaceInput = new TextField();
    private final Button replaceBtn = new Button("Replace");
    private final Button replaceAllBtn = new Button("Replace All");
    // replace&find dialog required
    private final Label findLabel = new Label("Find What:");
    private final TextField findInput = new TextField();
    private final Label findResultLabel = new Label("0 result");
    private final Button prevBtn = new Button("Prev");
    private final Button nextBtn = new Button("Next");
    private final CheckBox matchCaseCB = new CheckBox("Match Case");
    private final CheckBox wholeWordsCB = new CheckBox("Whole words");

    private static final ThreadLocal<FindReplaceUI> LOCAL = new ThreadLocal<>();

    public static void showFindUI(TextArea textArea) {
        if (exist()) {
            return;
        }
        FindReplaceUI findReplaceUI = new FindReplaceUI(textArea);
        findReplaceUI.initFindUI();
        findReplaceUI.showDialog("Find", textArea);
    }

    public static void showReplaceUI(TextArea textArea) {
        if (exist()) {
            return;
        }
        FindReplaceUI findReplaceUI = new FindReplaceUI(textArea);
        findReplaceUI.initReplaceUI();
        findReplaceUI.showDialog("Replace", textArea);
    }

    private static boolean exist() {
        return LOCAL.get() != null;
    }

    private FindReplaceUI(TextArea textArea) {
        FindReplaceUIModel model = new DefaultFindReplaceUIModel(this, textArea);

        this.prevBtn.setUserData(FindReplaceUIModel.USER_DATA_PREV_BUTTON);
        this.prevBtn.setOnAction(model);

        this.nextBtn.setUserData(FindReplaceUIModel.USER_DATA_NEXT_BUTTON);
        this.nextBtn.setOnAction(model);

        this.replaceBtn.setUserData(FindReplaceUIModel.USER_DATA_REPLACE_BUTTON);
        this.replaceBtn.setOnAction(model);

        this.replaceAllBtn.setUserData(FindReplaceUIModel.USER_DATA_REPLACE_ALL_BUTTON);
        this.replaceAllBtn.setOnAction(model);

        this.findResultLabel.setAlignment(Pos.CENTER);
    }

    private void initFindUI() {
        MigPane migPane = new MigPane("fillx", "[60!][]");
        migPane.add(findLabel);
        migPane.add(findInput, "growx, split 4");
        migPane.add(findResultLabel, "w 80!");
        migPane.add(prevBtn);
        migPane.add(nextBtn, "wrap");
        migPane.add(matchCaseCB, "cell 1 1, split 2");
        migPane.add(wholeWordsCB);
        setCenter(migPane);
    }

    private void initReplaceUI() {
        MigPane migPane = new MigPane("fillx", "[80!][]");
        migPane.add(findLabel, "right");
        migPane.add(findInput, "growx, split 4");
        migPane.add(findResultLabel, "w 60!");
        migPane.add(prevBtn);
        migPane.add(nextBtn, "wrap");
        migPane.add(replaceLabel, "right");
        migPane.add(replaceInput, "growx, split 3");
        migPane.add(replaceBtn);
        migPane.add(replaceAllBtn, "wrap");
        migPane.add(matchCaseCB, "cell 1 2, split 2");
        migPane.add(wholeWordsCB);
        setCenter(migPane);
    }

    private void showDialog(String title, TextArea textArea) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initModality(Modality.NONE);
        dialog.getDialogPane().setContent(this);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.setOnCloseRequest(event -> {
            textArea.selectRange(0, 0);
            LOCAL.remove();
        });
        dialog.show();
        LOCAL.set(this);
    }

    public TextField getReplaceInput() {
        return replaceInput;
    }

    public TextField getFindInput() {
        return findInput;
    }

    public CheckBox getMatchCaseCB() {
        return matchCaseCB;
    }

    public CheckBox getWholeWordsCB() {
        return wholeWordsCB;
    }

    public Label getFindResultLabel() {
        return findResultLabel;
    }
}
