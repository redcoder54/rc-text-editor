package redcoder.rctexteditor.support.tab;

import javafx.scene.Node;

public class TabContentChangeEvent extends TabEvent {

    private Node content;

    public TabContentChangeEvent(Object source) {
        super(source);
    }

    public Node getContent() {
        return content;
    }

    public void setContent(Node content) {
        this.content = content;
    }
}
