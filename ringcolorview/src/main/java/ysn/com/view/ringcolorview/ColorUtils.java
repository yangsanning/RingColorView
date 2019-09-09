package ysn.com.view.ringcolorview;

import android.graphics.Color;

import java.util.Locale;

/**
 * @Author yangsanning
 * @ClassName ColorEnvelope
 * @Description 颜色工具
 * @Date 2019/8/24
 * @History 2019/8/24 author: description:
 */
public class ColorUtils {

    /**
     * @param color 颜色值
     * @param hex   十六进制透明度 #hexFF2020
     * @return
     */
    public static int convert(int color, String hex) {
        return Color.parseColor(("#" + hex.trim() + getHexCode(color).substring(2)));
    }

    /**
     * 转十六进制颜色
     */
    public static String getHexCode(int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return String.format(Locale.getDefault(), "%02X%02X%02X%02X", a, r, g, b);
    }
}
