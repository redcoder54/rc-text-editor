package redcoder.rctexteditor.core.tab;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class NewTabId {

    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    public static int nextId() {
       return COUNTER.getAndIncrement();
    }
}
