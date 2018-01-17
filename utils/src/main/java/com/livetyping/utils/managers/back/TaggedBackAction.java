package com.livetyping.utils.managers.back;

class TaggedBackAction {
    private String tag;
    private BackAction backAction;

    public TaggedBackAction(String tag, BackAction backAction) {
        this.tag = tag;
        this.backAction = backAction;
    }

    public String getTag() {
        return tag;
    }

    public BackAction getBackAction() {
        return backAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaggedBackAction that = (TaggedBackAction) o;

        return tag != null ? tag.equals(that.tag) : that.tag == null;

    }

    @Override
    public int hashCode() {
        return tag != null ? tag.hashCode() : 0;
    }
}