package de.rwth_aachen.phyphox.pelab;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Local, private storage for the user's favourite experiments.
 */
public final class PEFavoritesManager {

    private static final String PREFS_FILE = "pe_lab_prefs";
    private static final String KEY_FAVORITES = "favorite_experiments";

    private PEFavoritesManager() {
    }

    public static Set<String> getFavorites(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        Set<String> stored = prefs.getStringSet(KEY_FAVORITES, Collections.emptySet());
        return new HashSet<>(stored);
    }

    public static boolean isFavorite(Context context, String xmlFile) {
        if (xmlFile == null) return false;
        return getFavorites(context).contains(xmlFile);
    }

    public static boolean toggleFavorite(Context context, String xmlFile) {
        if (xmlFile == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        Set<String> next = new HashSet<>(prefs.getStringSet(KEY_FAVORITES, Collections.emptySet()));
        boolean added;
        if (next.contains(xmlFile)) {
            next.remove(xmlFile);
            added = false;
        } else {
            next.add(xmlFile);
            added = true;
        }
        prefs.edit().putStringSet(KEY_FAVORITES, next).apply();
        return added;
    }
}
