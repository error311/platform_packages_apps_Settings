/*
 * Copyright (C) 2012 The CyanogenMod Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.samuele;

import static com.android.internal.util.cm.QSConstants.TILES_DEFAULT;
import static com.android.internal.util.cm.QSConstants.TILE_AIRPLANE;
import static com.android.internal.util.cm.QSConstants.TILE_AUTOROTATE;
import static com.android.internal.util.cm.QSConstants.TILE_BATTERY;
import static com.android.internal.util.cm.QSConstants.TILE_BLUETOOTH;
import static com.android.internal.util.cm.QSConstants.TILE_BRIGHTNESS;
import static com.android.internal.util.cm.QSConstants.TILE_CAMERA;
import static com.android.internal.util.cm.QSConstants.TILE_DELIMITER;
import static com.android.internal.util.cm.QSConstants.TILE_GPS;
import static com.android.internal.util.cm.QSConstants.TILE_LOCKSCREEN;
import static com.android.internal.util.cm.QSConstants.TILE_MOBILEDATA;
import static com.android.internal.util.cm.QSConstants.TILE_NFC;
import static com.android.internal.util.cm.QSConstants.TILE_SETTINGS;
import static com.android.internal.util.cm.QSConstants.TILE_SLEEP;
import static com.android.internal.util.cm.QSConstants.TILE_SYNC;
import static com.android.internal.util.cm.QSConstants.TILE_TORCH;
import static com.android.internal.util.cm.QSConstants.TILE_USER;
import static com.android.internal.util.cm.QSConstants.TILE_VOLUME;
import static com.android.internal.util.cm.QSConstants.TILE_WIFI;
import static com.android.internal.util.cm.QSConstants.TILE_WIFIAP;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.telephony.Phone;
import com.android.internal.util.cm.QSUtils;
import com.android.settings.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QuickSettingsUtil {
    private static final String TAG = "QuickSettingsUtil";

    public static final Map<String, TileInfo> TILES;

    private static final Map<String, TileInfo> ENABLED_TILES = new HashMap<String, TileInfo>();
    private static final Map<String, TileInfo> DISABLED_TILES = new HashMap<String, TileInfo>();

    static {
        TILES = Collections.unmodifiableMap(ENABLED_TILES);
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_AIRPLANE, R.string.title_tile_airplane,
                "com.android.systemui:drawable/ic_qs_airplane_off"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_BATTERY, R.string.title_tile_battery,
                "com.android.systemui:drawable/ic_qs_battery_neutral"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_BLUETOOTH, R.string.title_tile_bluetooth,
                "com.android.systemui:drawable/ic_qs_bluetooth_neutral"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_BRIGHTNESS, R.string.title_tile_brightness,
                "com.android.systemui:drawable/ic_qs_brightness_auto_off"));
        registerTile(new QuickSettingsUtil.TileInfo(
                 TILE_CAMERA, R.string.title_tile_camera,
                "com.android.systemui:drawable/ic_qs_camera"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_SLEEP, R.string.title_tile_sleep,
                "com.android.systemui:drawable/ic_qs_sleep"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_GPS, R.string.title_tile_gps,
                "com.android.systemui:drawable/ic_qs_gps_neutral"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_LOCKSCREEN, R.string.title_tile_lockscreen,
                "com.android.systemui:drawable/ic_qs_lock_screen_off"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_MOBILEDATA, R.string.title_tile_mobiledata,
                "com.android.systemui:drawable/ic_qs_signal_4"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_NFC, R.string.title_tile_nfc,
                "com.android.systemui:drawable/ic_qs_nfc_off"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_AUTOROTATE, R.string.title_tile_autorotate,
                "com.android.systemui:drawable/ic_qs_auto_rotate"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_SETTINGS, R.string.title_tile_settings,
                "com.android.systemui:drawable/ic_qs_settings"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_SYNC, R.string.title_tile_sync,
                "com.android.systemui:drawable/ic_qs_sync_off"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_TORCH, R.string.title_tile_torch,
                "com.android.systemui:drawable/ic_qs_torch_off"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_USER, R.string.title_tile_user,
                "com.android.systemui:drawable/ic_qs_default_user"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_VOLUME, R.string.title_tile_volume,
                "com.android.systemui:drawable/ic_qs_volume"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_WIFI, R.string.title_tile_wifi,
                "com.android.systemui:drawable/ic_qs_wifi_4"));
        registerTile(new QuickSettingsUtil.TileInfo(
                TILE_WIFIAP, R.string.title_tile_wifiap,
                "com.android.systemui:drawable/ic_qs_wifi_ap_neutral"));
    }

    private static void registerTile(QuickSettingsUtil.TileInfo info) {
        ENABLED_TILES.put(info.getId(), info);
    }

    private static void removeTile(String id) {
        ENABLED_TILES.remove(id);
        DISABLED_TILES.remove(id);
        TILES_DEFAULT.remove(id);
    }

    private static void disableTile(String id) {
        if (ENABLED_TILES.containsKey(id)) {
            DISABLED_TILES.put(id, ENABLED_TILES.remove(id));
        }
    }

    private static void enableTile(String id) {
        if (DISABLED_TILES.containsKey(id)) {
            ENABLED_TILES.put(id, DISABLED_TILES.remove(id));
        }
    }

    private static boolean sUnsupportedRemoved = false;

    private static synchronized void removeUnsupportedTiles(Context context) {
        if (sUnsupportedRemoved) {
            return;
        }

        // Don't show mobile data options if not supported
        if (!QSUtils.deviceSupportsMobileData(context)) {
            removeTile(TILE_MOBILEDATA);
            removeTile(TILE_WIFIAP);
        }

        // Don't show the bluetooth options if not supported
        if (!QSUtils.deviceSupportsBluetooth()) {
            removeTile(TILE_BLUETOOTH);
        }

        // Don't show the NFC tile if not supported
        if (!QSUtils.deviceSupportsNfc(context)) {
            removeTile(TILE_NFC);
        }

        // Don't show the Torch tile if not supported
        if (!context.getResources().getBoolean(R.bool.has_led_flash)) {
            removeTile(TILE_TORCH);
        }

        sUnsupportedRemoved = true;
    }

    private static synchronized void refreshAvailableTiles(Context context) {
        ContentResolver resolver = context.getContentResolver();

    }

    public static synchronized void updateAvailableTiles(Context context) {
        removeUnsupportedTiles(context);
        refreshAvailableTiles(context);
    }

    public static boolean isTileAvailable(String id) {
        return ENABLED_TILES.containsKey(id);
    }

    public static String getCurrentTiles(Context context) {
        String tiles = Settings.System.getString(context.getContentResolver(),
                Settings.System.QUICK_SETTINGS_TILES);
        if (tiles == null) {
            tiles = getDefaultTiles(context);
        }
        return tiles;
    }

    public static void saveCurrentTiles(Context context, String tiles) {
        Settings.System.putString(context.getContentResolver(),
                Settings.System.QUICK_SETTINGS_TILES, tiles);
    }

    public static void resetTiles(Context context) {
        String defaultTiles = getDefaultTiles(context);
        Settings.System.putString(context.getContentResolver(),
                Settings.System.QUICK_SETTINGS_TILES, defaultTiles);
    }

    public static String mergeInNewTileString(String oldString, String newString) {
        ArrayList<String> oldList = getTileListFromString(oldString);
        ArrayList<String> newList = getTileListFromString(newString);
        ArrayList<String> mergedList = new ArrayList<String>();

        // add any items from oldlist that are in new list
        for (String tile : oldList) {
            if (newList.contains(tile)) {
                mergedList.add(tile);
            }
        }

        // append anything in newlist that isn't already in the merged list to
        // the end of the list
        for (String tile : newList) {
            if (!mergedList.contains(tile)) {
                mergedList.add(tile);
            }
        }

        // return merged list
        return getTileStringFromList(mergedList);
    }

    public static ArrayList<String> getTileListFromString(String tiles) {
        return new ArrayList<String>(Arrays.asList(tiles.split("\\|")));
    }

    public static String getTileStringFromList(ArrayList<String> tiles) {
        if (tiles == null || tiles.size() <= 0) {
            return "";
        } else {
            String s = tiles.get(0);
            for (int i = 1; i < tiles.size(); i++) {
                s += TILE_DELIMITER + tiles.get(i);
            }
            return s;
        }
    }

    public static String getDefaultTiles(Context context) {
        removeUnsupportedTiles(context);
        return TextUtils.join(TILE_DELIMITER, TILES_DEFAULT);
    }

    public static class TileInfo {
        private String mId;
        private int mTitleResId;
        private String mIcon;

        public TileInfo(String id, int titleResId, String icon) {
            mId = id;
            mTitleResId = titleResId;
            mIcon = icon;
        }

        public String getId() {
            return mId;
        }

        public int getTitleResId() {
            return mTitleResId;
        }

        public String getIcon() {
            return mIcon;
        }
    }
}

