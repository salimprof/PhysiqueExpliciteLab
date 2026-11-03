/*
 * Physique Explicite Lab ("Edited by Salimprof")
 * Copyright (C) 2026 Salimprof (Salim El Mokhtar) — Local favorites storage
 * Based on phyphox (https://phyphox.org / https://github.com/phyphox/phyphox-android)
 * Copyright (C) 2017 RWTH Aachen University, 2nd Institute of Physics (Sebastian Staacks et al.)
 *
 * MODIFICATION NOTICE (GNU GPL v3 Section 5a):
 * Added on 2026-11-02 by Salimprof (Salim El Mokhtar) for "Physique Explicite Lab, Edited by Salimprof".
 * This is an independent customized version and is NOT an official RWTH Aachen University or phyphox product.
 * Distributed under the GNU General Public License v3.0 (see assets/gpl-3.0.txt).
 */
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
