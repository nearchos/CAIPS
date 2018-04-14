package org.inspirecenter.indoorpositioningsystem.model;

import java.io.Serializable;

/**
 * @author Nearchos
 * Created: 11-Apr-18
 */
public class ContextEntry implements Serializable {

    private String name; // e.g. acceleration
    private ContextValue [] contextValues; // e.g. ["X" -> 0.1, "Y" -> 0.2, "Z" -> 0.05]

    public ContextEntry() { /* empty constructor needed by Objectify */ }

    public ContextEntry(String name, ContextValue [] contextValues) {
        this.name = name;
        this.contextValues = contextValues;
    }

    public String getName() {
        return name;
    }

    public ContextValue [] getContextValues() {
        return contextValues;
    }

    public int getNumOfContextValues() {
        return contextValues.length;
    }

    public ContextValue getContextValue(final int index) {
        return contextValues[index];
    }
}