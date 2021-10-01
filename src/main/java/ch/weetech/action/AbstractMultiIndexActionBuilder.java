package ch.weetech.action;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.Joiner;

public abstract class AbstractMultiIndexActionBuilder<T extends Action, K> extends AbstractAction.Builder<T, K> {

    protected Set<String> indexNames = new LinkedHashSet<String>();

    public String getJoinedIndices() {
        if (indexNames.size() > 0) {
            return Joiner.on(',').join(indexNames);
        } else {
            return "_all";
        }
    }

}
