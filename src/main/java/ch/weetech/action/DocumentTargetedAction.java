package ch.weetech.action;

import ch.weetech.client.JwResult;

public interface DocumentTargetedAction<T extends JwResult> extends Action<T> {
    String getIndex();

    String getType();

    String getId();
}
