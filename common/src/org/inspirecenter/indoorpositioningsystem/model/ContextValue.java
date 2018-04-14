package org.inspirecenter.indoorpositioningsystem.model;

import java.io.Serializable;

public class ContextValue implements Serializable {
    private String label;
    private ContextValueType type;
    private Serializable value;

    public ContextValue() { /* empty constructor needed by Objectify */ }

    public ContextValue(final String label, final Boolean value) {
        this.label = label;
        this.type = ContextValueType.BOOLEAN;
        this.value = value;
    }

    public ContextValue(final String label, final Long value) {
        this.label = label;
        this.type = ContextValueType.LONG;
        this.value = value;
    }

    public ContextValue(final String label, final Double value) {
        this.label = label;
        this.type = ContextValueType.DOUBLE;
        this.value = value;
    }

    public ContextValue(final String label, final String value) {
        this.label = label;
        this.type = ContextValueType.STRING;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public ContextValueType getType() {
        return type;
    }

    public Serializable getValue() {
        return value;
    }
}