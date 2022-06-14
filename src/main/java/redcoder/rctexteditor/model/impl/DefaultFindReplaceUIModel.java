package redcoder.rctexteditor.model.impl;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import redcoder.rctexteditor.model.FindReplaceUIModel;
import redcoder.rctexteditor.ui.FindReplaceUI;
import redcoder.rctexteditor.utils.StringUtils;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultFindReplaceUIModel implements FindReplaceUIModel {

    private static final Logger LOGGER = Logger.getLogger(DefaultFindReplaceUIModel.class.getName());

    private final FindReplaceUI ui;
    private final TextArea textArea;

    private String findInput = null;
    private String replaceInput = null;
    private boolean replaceable = false;
    private int position = -1;
    private int cursor = 0;
    private int totalMatch = 0;

    public DefaultFindReplaceUIModel(FindReplaceUI ui, TextArea textArea) {
        this.ui = ui;
        this.textArea = textArea;
        this.ui.getMatchCaseCB().selectedProperty().addListener((observable, oldValue, newValue) -> reset());
        this.ui.getWholeWordsCB().selectedProperty().addListener((observable, oldValue, newValue) -> reset());
    }

    @Override
    public void handle(ActionEvent event) {
        try {
            if (!Objects.equals(ui.getFindInput().getText(), findInput)) {
                reset();
            }
            if (totalMatch == 0) {
                return;
            }

            int length = textArea.getLength();
            String text = textArea.getText();
            String pattern = findInput;
            if (!ui.getMatchCaseCB().isSelected()) {
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
            ui.getFindResultLabel().setText(String.format("%d/%d", cursor, totalMatch));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to handel action event.", e);
        }
    }

    private void reset() {
        findInput = ui.getFindInput().getText();
        position = -1;
        cursor = 0;
        totalMatch = countMatch();
        ui.getFindResultLabel().setText("0 results");
    }

    private int countMatch() {
        if (StringUtils.isEmpty(ui.getFindInput().getText())) {
            return 0;
        }

        int length = textArea.getLength();
        String text = textArea.getText();
        String pattern = findInput;
        if (!ui.getMatchCaseCB().isSelected()) {
            text = text.toLowerCase();
            pattern = pattern.toLowerCase();
        }

        int pos = 0;
        int count = 0;
        while ((pos = text.indexOf(pattern, pos)) >= 0) {
            if (ui.getWholeWordsCB().isSelected()) {
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
                if (ui.getWholeWordsCB().isSelected()) {
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
                if (ui.getWholeWordsCB().isSelected()) {
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
        replaceInput = ui.getReplaceInput().getText();
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
