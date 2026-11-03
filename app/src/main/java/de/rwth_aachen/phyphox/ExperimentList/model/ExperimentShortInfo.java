/*
 * Original file from phyphox (https://github.com/phyphox/phyphox-android)
 * Copyright (C) RWTH Aachen University / Dr. Sebastian Staacks and contributors.
 *
 * Modified by Salimprof (Salim El Mokhtar) on 2026-11-02 for "Physique Explicite Lab":
 * - Added sensorTypes field to classify experiments into learning categories.
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License version 3 as published by the Free Software Foundation.
 */
package de.rwth_aachen.phyphox.ExperimentList.model;

import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;

import de.rwth_aachen.phyphox.helper.RGB;

public class ExperimentShortInfo {
    public RGB color;
    public Drawable icon;
    public String title;
    public String description;
    public String fullDescription;
    public Set<String> resources;
    public String xmlFile;
    public String isTemp;
    public boolean isAsset;
    public int unavailableSensor;
    public String isLink;
    public Map<String, String> links;
    public String categoryName;
    public boolean selected = false;

    public final Set<String> bluetoothDeviceNames = new HashSet<>();
    //Physique Explicite Lab: sensor/input types used by the experiment (lower case sensor type
    //names plus "audio", "location", "camera"). Used to sort experiments into learning categories.
    public final Set<String> sensorTypes = new HashSet<>();
    public final Set<UUID> bluetoothDeviceUUIDs = new HashSet<>();
}
