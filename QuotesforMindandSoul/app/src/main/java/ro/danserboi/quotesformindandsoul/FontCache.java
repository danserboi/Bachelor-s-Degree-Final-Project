package ro.danserboi.quotesformindandsoul;

import android.graphics.Typeface;

import java.util.Hashtable;

public class FontCache {

    private static final Hashtable<String, Typeface> fontCache = new Hashtable<>();

    public static Typeface get(String name) {
        Typeface tf = fontCache.get(name);
        if (tf == null) {
            try {
                tf = Typeface.create(name, Typeface.NORMAL);
            } catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }

}