package com.ltst.core.data.model;

public enum  ChildStateType {
    CHECKIN("checkin"),
    CHECKOUT("checkout");

    private String serverName;

    ChildStateType(String serverName) {
        this.serverName = serverName;
    }

    public static ChildStateType fromString(String type){
        for (ChildStateType stateType : values()){
            if (stateType.serverName.equals(type)){
                return stateType;
            }
        }
        throw new RuntimeException("ChildStateType: unknown type");
    }

    @Override
    public String toString() {
        return serverName;
    }
}
