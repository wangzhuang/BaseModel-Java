package com.king.basemodel.utils;

import android.text.TextUtils;

import java.util.Collection;
import java.util.Map;

/**
 * 判断
 */
public class Judge {

    /**
     * 正面的(positive)，包括非空、正确、大于0
     *
     * @param obj
     * @return
     */
    public static boolean p(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof String) {
            return !TextUtils.isEmpty((String) obj) && !((String) obj).equalsIgnoreCase("null");
        }
        if (obj instanceof Boolean) {
            return (boolean) obj;
        }
        if (obj instanceof Collection) {
            return !((Collection) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return !((Map) obj).isEmpty();
        }
        if (obj instanceof String[]) {
            return ((String[]) obj).length > 0;
        }
        if (obj instanceof Integer) {
            return ((int) obj) > 0;
        }
        if (obj instanceof Short) {
            return ((int) obj) > 0;
        }
        if (obj instanceof Long) {
            return ((long) obj) > 0L;
        }
        if (obj instanceof Float) {
            return ((Float) obj) > 0f;
        }
        if (obj instanceof Double) {
            return ((double) obj) > 0.0;
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() > 0;
        }
        return true;
    }

    /**
     * 反面的(negative)，包括空、错误、小于等于0
     *
     * @param obj
     * @return
     */
    public static boolean n(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj instanceof String) {
            return TextUtils.isEmpty((String) obj) || ((String) obj).equalsIgnoreCase("null");
        }
        if (obj instanceof Boolean) {
            return !(boolean) obj;
        }
        if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }
        if (obj instanceof String[]) {
            return ((String[]) obj).length <= 0;
        }
        if (obj instanceof Integer) {
            return ((int) obj) <= 0;
        }
        if (obj instanceof Short) {
            return ((int) obj) <= 0;
        }
        if (obj instanceof Long) {
            return ((long) obj) <= 0L;
        }
        if (obj instanceof Float) {
            return ((Float) obj) <= 0f;
        }
        if (obj instanceof Double) {
            return ((double) obj) <= 0.0;
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() <= 0;
        }
        return false;
    }

}
