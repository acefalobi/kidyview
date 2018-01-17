package com.ltst.core.util.validator;

import java.util.Map;
import java.util.Set;

public class ValidationThrowable extends Throwable {
    public final Map<ValidateType, String> notValidatedParams;

    public ValidationThrowable(Map<ValidateType, String> notValidatedParams) {
        this.notValidatedParams = notValidatedParams;
    }

    public Map<ValidateType, String> getParams() {
        return notValidatedParams;
    }

    public Set<ValidateType> keySet() {
        return getParams().keySet();
    }
}
