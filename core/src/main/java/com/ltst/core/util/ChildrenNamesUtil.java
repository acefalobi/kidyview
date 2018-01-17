package com.ltst.core.util;

import com.livetyping.utils.utils.StringUtils;
import com.ltst.core.data.model.Child;

import java.util.List;

public class ChildrenNamesUtil {

    public static String formChildrenNamesString(List<Child> children) {
        if (children == null || children.size() < 1) {
            return StringUtils.EMPTY;
        }
        StringBuilder builder = new StringBuilder();
        Child lastChild = children.get(children.size() - 1);
        for (Child child : children) {
            builder.append(child.getFirstName())
                    .append(StringUtils.SPACE)
                    .append(child.getLastName());
            if (child != lastChild) {
                builder.append(StringUtils.COMMA)
                        .append(StringUtils.SPACE);
            }
        }
        return builder.toString();
    }
}
