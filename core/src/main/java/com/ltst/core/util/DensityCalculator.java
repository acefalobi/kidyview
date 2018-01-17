package com.ltst.core.util;

import android.util.DisplayMetrics;

public class DensityCalculator {

    public static final String XXXHDPI = "xxxhdpi";
    public static final String XXHDPI = "xxhdpi";
    public static final String XHDPI = "xhdpi";
    public static final String HDPI = "hdpi";
    public static final String MDPI = "mdpi";

    public static String calculate(int density) {
        String result = null;
        if (density >= DisplayMetrics.DENSITY_XXXHIGH) {
            result = XXXHDPI;
        } else if (DisplayMetrics.DENSITY_XXXHIGH - density <= density - DisplayMetrics.DENSITY_XXHIGH) {
            result = XXXHDPI;
        } else if (DisplayMetrics.DENSITY_XXXHIGH - density > density - DisplayMetrics.DENSITY_XXHIGH) {
            result = XXHDPI;
        } else if (density == DisplayMetrics.DENSITY_XXHIGH) {
            result = XXHDPI;
        } else if (DisplayMetrics.DENSITY_XXHIGH - density <= density - DisplayMetrics.DENSITY_XHIGH) {
            result = XXHDPI;
        } else if (DisplayMetrics.DENSITY_XXHIGH - density > density - DisplayMetrics.DENSITY_XHIGH) {
            result = XHDPI;
        } else if (density == DisplayMetrics.DENSITY_XHIGH) {
            result = XHDPI;
        } else if (DisplayMetrics.DENSITY_HIGH - density <= density - DisplayMetrics.DENSITY_MEDIUM) {
            result = HDPI;
        } else result = MDPI;
        return result;
    }
}
