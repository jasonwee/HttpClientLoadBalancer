package ch.weetech.action;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.base.Joiner;

public abstract class AbstractMultiTypeActionBuilder<T extends Action, K> extends AbstractMultiIndexActionBuilder<T, K> {
	
	private Set<String> indexTypes = new LinkedHashSet<String>();
	
	
    public String getJoinedTypes() {
        return Joiner.on(',').join(indexTypes);
    }

}
