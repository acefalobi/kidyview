package com.ltst.core.util.validator;

import android.support.annotation.NonNull;

import com.livetyping.utils.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;

public class FieldsValidator {

    private FieldsValidator() {
        //no instance
    }

    public static Observable<Map<ValidateType, String>> validate(@NonNull Map<ValidateType, String> needCheckMap) {
        Map<ValidateType, String> forSuccess = new HashMap<>();
        forSuccess.putAll(needCheckMap);
        String email = needCheckMap.get(ValidateType.PERSONAL_EMAIL);
        if (email != null) {
            boolean emailValidated = checkEmail(email);
            if (emailValidated) {
                needCheckMap.remove(ValidateType.PERSONAL_EMAIL);
            }
        }
        String oldPassword = needCheckMap.get(ValidateType.OLD_PASSWORD);
        if (oldPassword != null) {
            boolean passValidated = checkPassword(oldPassword);
            if (passValidated) {
                needCheckMap.remove(ValidateType.OLD_PASSWORD);
            }
        }
        String password = needCheckMap.get(ValidateType.PASSWORD);
        if (password != null) {
            boolean passValidated = checkPassword(password);
            if (passValidated) {
                needCheckMap.remove(ValidateType.PASSWORD);
            }
        }
        String confirm = needCheckMap.get(ValidateType.CONFIRM);
        if (confirm != null) {
            boolean confirmValidated = checConfirmPassword(confirm, password);
            if (confirmValidated) {
                needCheckMap.remove(ValidateType.CONFIRM);
            }
        }
        String code = needCheckMap.get(ValidateType.CODE);
        if (code != null) {
            boolean codeValidated = checkCode(code);
            if (codeValidated) {
                needCheckMap.remove(ValidateType.CODE);
            }
        }
        String name = needCheckMap.get(ValidateType.NAME);
        if (name != null) {
            boolean nameValidated = checkEmpty(name);
            if (nameValidated) {
                needCheckMap.remove(ValidateType.NAME);
            }
        }
        String lastName = needCheckMap.get(ValidateType.LAST_NAME);
        if (lastName != null) {
            if (checkEmpty(lastName)) {
                needCheckMap.remove(ValidateType.LAST_NAME);
            }
        }

        String personalPhone = needCheckMap.get(ValidateType.PERSONAL_PHONE);
        if (personalPhone != null) {
            if (checkPhone(personalPhone)) {
                needCheckMap.remove(ValidateType.PERSONAL_PHONE);
            }
        }
        String schoolTitle = needCheckMap.get(ValidateType.SCHOOL_TITLE);
        if (schoolTitle != null) {
            if (checkEmpty(schoolTitle)) {
                needCheckMap.remove(ValidateType.SCHOOL_TITLE);
            }
        }
        String schoolAddress = needCheckMap.get(ValidateType.SCHOOL_ADDRESS);
        if (schoolAddress != null) {
            if (checkEmpty(schoolAddress)) {
                needCheckMap.remove(ValidateType.SCHOOL_ADDRESS);
            }
        }
        String schoolPhone = needCheckMap.get(ValidateType.SCHOOL_PHONE);
        if (schoolPhone != null) {
            if (checkPhone(schoolPhone)) {
                needCheckMap.remove(ValidateType.SCHOOL_PHONE);
            }
        }
        String schoolAdditionalPhone = needCheckMap.get(ValidateType.SCHOOL_ADDITIONAL_PHONE);
        if (schoolAdditionalPhone != null) {
            if (checkPhone(schoolAdditionalPhone)) {
                needCheckMap.remove(ValidateType.SCHOOL_ADDITIONAL_PHONE);
            }
        }
        String schoolEmail = needCheckMap.get(ValidateType.SCHOOL_EMAIL);
        if (schoolEmail != null) {
            if (checkEmail(schoolEmail)) {
                needCheckMap.remove(ValidateType.SCHOOL_EMAIL);
            }
        }
        String secondPhone = needCheckMap.get(ValidateType.SECOND_PHONE);
        if (secondPhone != null) {
            if (checkPhone(secondPhone)) {
                needCheckMap.remove(ValidateType.SECOND_PHONE);
            }
        }
        String familyMember = needCheckMap.get(ValidateType.STATUS);
        if (!StringUtils.isBlank(familyMember)) {
            needCheckMap.remove(ValidateType.STATUS);
        }
        if (needCheckMap.values().size() == 0) {
            return Observable.just(forSuccess);
        } else {
            return Observable.error(new ValidationThrowable(needCheckMap));
        }


    }

    public static final int MIN_NUMBER_COUNT = 2;

    private static boolean checkPhone(String personalPhone) {
        return personalPhone.trim().length() >= MIN_NUMBER_COUNT;
    }

    private static final int MIN_CHAR_COUNT = 2;

    private static boolean checkEmpty(String string) {
        String trim = string.trim();
        if (trim.length() > MIN_CHAR_COUNT)
            return true;
        else return false;
    }

    private static final String EMAIL_PATTERN =
//            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
//                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

            "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
                    "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")" +
                    "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|" +
                    "\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]" +
                    ":(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    private static boolean checkEmail(String email) {
        if (StringUtils.isBlank(email)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(email);
            if (!matcher.matches()) {
                return false;
            }
        }
        return true;
    }

    private static final String PASSWORD_PATTERN =
            "^((?=\\S*?[A-Z])(?=\\S*?[a-z])(?=\\S*?[0-9]).{6,})\\S$";

    private static boolean checkPassword(String password) {
        if (StringUtils.isBlank(password)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
            Matcher matcher = pattern.matcher(password);
            if (!matcher.matches()) {
                return false;
            }
        }
        return true;
    }

    private static boolean checConfirmPassword(String confirmPassword, String password) {
        if (StringUtils.isBlank(confirmPassword)) {
            return false;
        } else {
            if (!confirmPassword.equals(password)) {
                return false;
            }
        }
        return true;
    }

    private static final int MIN_CODE_LENGTH = 6;
    private static final String CODE_PATTERN = "^[0-9]+$";

    private static boolean checkCode(String code) {
        if (code.length() < MIN_CODE_LENGTH) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(CODE_PATTERN);
            Matcher matcher = pattern.matcher(code);
            if (!matcher.matches()) {
                return false;
            }
        }
        return true;
    }
}
