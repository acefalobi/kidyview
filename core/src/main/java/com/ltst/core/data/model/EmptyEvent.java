package com.ltst.core.data.model;

public class EmptyEvent extends Event {

    private String emptyEventString;

    public EmptyEvent(String emptyString) {
        super(0L);
        this.emptyEventString = emptyString;
    }

    public String getEmptyEventString() {
        return emptyEventString;
    }
}
