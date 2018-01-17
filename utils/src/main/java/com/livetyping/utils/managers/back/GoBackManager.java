package com.livetyping.utils.managers.back;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Danil on 04.08.2016.
 */
public class GoBackManager {
    private static final String EMPTY_TAG = "empty.tag";
    private static final int EMPTY_POSITION = -1;

    private List<TaggedBackAction> backActions = new ArrayList<>();
    private boolean isBlockGoBack;

    public GoBackManager() {
    }

    public void addGoBackAction(BackAction backAction) {
        addGoBackAction(EMPTY_POSITION, EMPTY_TAG, backAction);
    }

    public void addGoBackAction(int position, BackAction backAction) {
        addGoBackAction(position, EMPTY_TAG, backAction);
    }

    public void addGoBackAction(String tag, BackAction backAction) {
        addGoBackAction(EMPTY_POSITION, tag, backAction);
    }

    public void addGoBackAction(int position, String tag, BackAction backAction) {
        if (position != EMPTY_POSITION) {
            backActions.add(position, new TaggedBackAction(tag, backAction));
        } else {
            TaggedBackAction action = new TaggedBackAction(tag, backAction);
            if (backActions.contains(action)) {
                return;
            }
            backActions.add(action);
        }
    }

    public void removeGoBackAction(BackAction backAction) {
        for (TaggedBackAction taggedBackAction : backActions) {
            if (backAction.equals(taggedBackAction.getBackAction())) {
                backActions.remove(taggedBackAction);
                break;
            }
        }
    }

    public void removeGoBackActionByTag(String tag) {
        for (TaggedBackAction taggedBackAction : backActions) {
            if (tag.equals(taggedBackAction.getTag())) {
                backActions.remove(taggedBackAction);
                break;
            }
        }
    }

    public boolean isBlockGoBack() {
        return isBlockGoBack;
    }

    public void setBlockGoBack(boolean blockGoBack) {
        isBlockGoBack = blockGoBack;
    }

    public boolean enabledGoBackAction() {
        return backActions.size() > 0 && !isBlockGoBack;
    }

    public void startLastGoBackAction() {
        startLastGoBackAction(EMPTY_TAG);
    }

    public void startLastGoBackAction(String tag) {
        int index;
        if (tag.equals(EMPTY_TAG)) {
            index = backActions.size() - 1;
        } else {
            index = backActions.lastIndexOf(new TaggedBackAction(tag, null));
        }
        if (index < 0) return;
        TaggedBackAction backAction = backActions.get(index);
        backAction.getBackAction().onGoBack();
        backActions.remove(backAction);
    }

    public void removeAllGoBackActions() {
        backActions.clear();
    }

    public void removeLastGoBackAction() {
        if (backActions.size() == 0) return;
        backActions.remove(backActions.size() - 1);
    }
}
