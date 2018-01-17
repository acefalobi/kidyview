package com.ltst.core.data.model;

/**
 * Created by Danil on 26.09.2016.
 */

public enum AssetType {
    IMAGE("image"),
    DOC("document");

    private String type;

    AssetType(String type) {
        this.type = type;
    }


    @Override
    public String toString() {
        return type;
    }
}
