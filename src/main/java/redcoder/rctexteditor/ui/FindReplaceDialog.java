package redcoder.rctexteditor.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import org.tbee.javafx.scene.layout.MigPane;
import redcoder.rctexteditor.utils.StringUtils;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FindReplaceDialog extends BorderPane {

    private static final Logger LOGGER = Logger.getLogger(FindReplaceDialog.class.getName());

    // replace dialog required
    private final Label replaceLabel = new Label("Replace With:");
    private final TextField replaceInputTextField = new TextField();
    private final Button replaceBtn = new Button("Replace");
    private final Button replaceAllBtn = new Button("Replace All");
    // replace&find dialog required
    private final Label findLabel = new Label("Find What:");
    private final TextField findInputTextField = new TextField();
    private final Label findResultLabel = new Label("0 result");
    private final Button prevBtn = new Button("Prev");
    private final Button nextBtn = new Button("Next");
    private final CheckBox matchCaseCB = new CheckBox("Match Case");
    private final CheckBox wholeWordsCB = new CheckBox("Whole words");

    private static final ThreadLocal<FindReplaceDialog> LOCAL = new ThreadLocal<>();

    public static void showFindDialog(TextArea textArea) {
        if (exist()) {
            return;
        }
        FindReplaceDialog findReplaceDialog = new FindReplaceDialog(textArea);
        findReplaceDialog.initFindUI();
        findReplaceDialog.showDialog("Find", textArea);
    }

    public static void showReplaceDialog(TextArea textArea) {
        if (exist()) {
            return;
        }
        FindReplaceDialog findReplaceDialog = new FindReplaceDialog(textArea);
        findReplaceDialog.initReplaceUI();
        findReplaceDialog.showDialog("Replace", textArea);
    }

    private static boolean exist() {
        return LOCAL.get() != null;
    }

    private FindReplaceDialog(TextArea textArea) {
        FindReplaceEventHandler eventHandler = new FindReplaceEventHandler(textArea);

        this.prevBtn.setUserData(USER_DATA_PREV_BUTTON);
        this.prevBtn.setOnAction(eventHandler);

        this.nextBtn.setUserData(USER_DATA_NEXT_BUTTON);
        this.nextBtn.setOnAction(eventHandler);

        this.replaceBtn.setUserData(USER_DATA_REPLACE_BUTTON);
        this.replaceBtn.setOnAction(eventHandler);

        this.replaceAllBtn.setUserData(USER_DATA_REPLACE_ALL_BUTTON);
        this.replaceAllBtn.setOnAction(eventHandler);

        this.findResultLabel.setAlignment(Pos.CENTER);
    }

    private void initFindUI() {
        MigPane migPane = new MigPane("fillx", "[60!][]");
        migPane.add(findLabel);
        migPane.add(findInputTextField, "growx, split 4");
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
        migPane.add(findInputTextField, "growx, split 4");
        migPane.add(findResultLabel, "w 60!");
        migPane.add(prevBtn);
        migPane.add(nextBtn, "wrap");
        migPane.add(replaceLabel, "right");
        migPane.add(replaceInputTextField, "growx, split 3");
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

    private static final String USER_DATA_PREV_BUTTON = "Prev";
    private static final String USER_DATA_NEXT_BUTTON = "Next";
    private static final String USER_DATA_REPLACE_BUTTON = "Replace";
    private static final String USER_DATA_REPLACE_ALL_BUTTON = "Replace All";

    private class FindReplaceEventHandler implements EventHandler<ActionEvent> {

        private TextArea textArea;
        private String findInput = null;
        private String replaceInput = null;
        private boolean replaceable = false;
        private int position = -1;
        private int cursor = 0;
        private int totalMatch = 0;

        public FindReplaceEventHandler(TextArea textArea) {
            this.textArea = textArea;

            matchCaseCB.selectedProperty().addListener((observable, oldValue, newValue) -> reset());
            wholeWordsCB.selectedProperty().addListener((observable, oldValue, newValue) -> reset());
        }

        @Override
        public void handle(ActionEvent event) {
            try {
                if (!Objects.equals(findInputTextField.getText(), findInput)) {
                    reset();
                }
                if (totalMatch == 0) {
                    return;
                }

                int length = textArea.getLength();
                String text = textArea.getText();
                String pattern = findInput;
                if (!matchCaseCB.isSelected()) {
                    text = text.toLowerCase();
                    pattern = pattern.toLowerCase();
                }

                Node node = (Node) event.getSource();
                Object userData = node.getUserData();
                if (Objects.equals(USER_DATA_PREV_BUTTON, userData)) {
                    findPrev(length, text, pattern);
                } else if (Objects.equals(USER_DATA_NEXT_BUTTON, userData)) {
                    findNext(length, text, pattern);
                } else if (Objects.equals(USER_DATA_REPLACE_BUTTON, userData)) {
                    updateReplaceInput();
                    replace(length, text, pattern);
                } else if (Objects.equals(USER_DATA_REPLACE_ALL_BUTTON, userData)) {
                    updateReplaceInput();
                    replaceAll(length, text, pattern);
                }
                findResultLabel.setText(String.format("%d/%d", cursor, totalMatch));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Failed to handel action event.", e);
            }
        }

        private void reset() {
            findInput = findInputTextField.getText();
            position = -1;
            cursor = 0;
            totalMatch = countMatch();
            findResultLabel.setText("0 results");
        }

        private int countMatch() {
            if (StringUtils.isEmpty(findInputTextField.getText())) {
                return 0;
            }

            int length = textArea.getLength();
            String text = textArea.getText();
            String pattern = findInput;
            if (!matchCaseCB.isSelected()) {
                text = text.toLowerCase();
                pattern = pattern.toLowerCase();
            }

            int pos = 0;
            int count = 0;
            while ((pos = text.indexOf(pattern, pos)) >= 0) {
                if (wholeWordsCB.isSelected()) {
                    if (isWord(pos, pattern, length, text)) {
                        count++;
                    }
                } else {
                    count++;
                }
                pos += pattern.length();
            }
            return count;
        }

        private void findPrev(int length, String text, String pattern) {
            while (true) {
                position--;
                if (position < 0) {
                    position = length - 1;
                    cursor = totalMatch + 1;
                }
                String subText = text.substring(0, position + 1);
                int matchIndex = subText.lastIndexOf(pattern);
                if (matchIndex >= 0) {
                    if (wholeWordsCB.isSelected()) {
                        if (isWord(matchIndex, pattern, length, text)) {
                            position = matchIndex;
                            cursor--;
                            replaceable = true;
                            highlight(position, position + pattern.length());
                            return;
                        }
                    } else {
                        position = matchIndex;
                        cursor--;
                        replaceable = true;
                        highlight(position, position + pattern.length());
                        return;
                    }
                }
            }
        }

        private void findNext(int length, String text, String pattern) {
            while (true) {
                position++;
                if (position >= length) {
                    position = 0;
                    cursor = 0;
                }
                int matchIndex = text.indexOf(pattern, position);
                if (matchIndex >= 0) {
                    if (wholeWordsCB.isSelected()) {
                        if (isWord(position, pattern, length, text)) {
                            position = matchIndex;
                            cursor++;
                            replaceable = true;
                            highlight(position, position + pattern.length());
                            return;
                        }
                    } else {
                        position = matchIndex;
                        cursor++;
                        replaceable = true;
                        highlight(position, position + pattern.length());
                        return;
                    }
                }
            }
        }

        private void highlight(int startOffset, int endOffset) {
            textArea.selectRange(startOffset, endOffset);
        }

        private void updateReplaceInput() {
            replaceInput = replaceInputTextField.getText();
            if (replaceInput == null) {
                replaceInput = "";
            }
        }

        private void replace(int length, String text, String pattern) {
            if (!replaceable) {
                findNext(length, text, pattern);
            }
            textArea.replaceText(position, position + pattern.length(), replaceInput);
            totalMatch--;
            cursor--;
            replaceable = false;
            if (totalMatch > 0) {
                findNext(length, text, pattern);
            }
        }

        private void replaceAll(int length, String text, String pattern) {
            while (totalMatch > 0) {
                replace(length, text, pattern);
            }
        }

        private boolean isWord(int startIndex, String pattern, int length, String text) {
            if (StringUtils.isContainNonEnglishLetter(pattern)) {
                return true;
            }

            boolean isWord1, isWord2;
            if (startIndex > 0) {
                isWord1 = !Character.isLetterOrDigit(text.charAt(startIndex - 1));
            } else {
                isWord1 = true;
            }

            if ((startIndex + pattern.length()) < length) {
                isWord2 = !Character.isLetterOrDigit(text.charAt(startIndex + pattern.length()));
            } else {
                isWord2 = true;
            }

            return isWord1 && isWord2;
        }
    }
}
