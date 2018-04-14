package org.inspirecenter.indoorpositioningsystem.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;

@Entity
public class Parameter implements Serializable {

    @Id
    private String name;

    private String value;

    private String createdBy;

    private long createdOn;

    public Parameter() { /* empty constructor needed by Objectify */ }

    public Parameter(final String name, final String value, final String createdBy, final long createdOn) {
        this.name = name;
        this.value = value;
        this.createdBy = createdBy;
        this.createdOn = createdOn;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    @Override
    public String toString() {
        return "[" + name + "->" + value + "]";
    }
}