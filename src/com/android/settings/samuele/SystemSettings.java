/*
 * Copyright (C) 2012 The CyanogenMod project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.samuele;

import android.app.ActivityManagerNative;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver; 
import android.os.Bundle;
import android.os.Handler; 
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory; 
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.view.VolumePanel;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SystemSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "SystemSettings";
    private static final String KEY_WAKEUP_CATEGORY = "category_wakeup_options";
    private static final String KEY_VOLUME_WAKE = "pref_volume_wake"; 
    private static final String KEY_VOLUME_OVERLAY = "volume_overlay"; 
    private static final String KEY_VOLBTN_MUSIC_CTRL = "volbtn_music_controls"; 
    private static final String KILL_APP_LONGPRESS_BACK = "kill_app_longpress_back";
    private static final String KEY_BATTERY_LIGHT = "battery_light";
    private static final String KEY_NOTIFICATION_PULSE = "notification_pulse";

    private CheckBoxPreference mVolumeWake; 
    private ListPreference mVolumeOverlay; 
    private CheckBoxPreference mVolBtnMusicCtrl; 
    private CheckBoxPreference mKillAppLongpressBack;
    private PreferenceScreen mNotificationPulse;
    private PreferenceScreen mBatteryPulse; 

    private final ArrayList<Preference> mAllPrefs = new ArrayList<Preference>();
    private final ArrayList<CheckBoxPreference> mResetCbPrefs
            = new ArrayList<CheckBoxPreference>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	ContentResolver resolver = getContentResolver();

        addPreferencesFromResource(R.xml.system_settings);

	mVolumeWake = (CheckBoxPreference) findPreference(KEY_VOLUME_WAKE);
        if (mVolumeWake != null) {
            if (!getResources().getBoolean(R.bool.config_show_volumeRockerWake)) {
                getPreferenceScreen().removePreference(mVolumeWake);
                getPreferenceScreen().removePreference((PreferenceCategory) findPreference(KEY_WAKEUP_CATEGORY));
            } else {
                mVolumeWake.setChecked(Settings.System.getInt(resolver,
                        Settings.System.VOLUME_WAKE_SCREEN, 0) == 1);
            }
        }  

        mVolumeOverlay = (ListPreference) findPreference(KEY_VOLUME_OVERLAY);
        mVolumeOverlay.setOnPreferenceChangeListener(this);
        int volumeOverlay = Settings.System.getInt(getContentResolver(),
                Settings.System.MODE_VOLUME_OVERLAY,
                VolumePanel.VOLUME_OVERLAY_EXPANDABLE);
        mVolumeOverlay.setValue(Integer.toString(volumeOverlay));
        mVolumeOverlay.setSummary(mVolumeOverlay.getEntry()); 

        mVolBtnMusicCtrl = (CheckBoxPreference) findPreference(KEY_VOLBTN_MUSIC_CTRL);
        mVolBtnMusicCtrl.setChecked(Settings.System.getInt(resolver,
                Settings.System.VOLBTN_MUSIC_CONTROLS, 1) != 0); 

	mKillAppLongpressBack = findAndInitCheckboxPref(KILL_APP_LONGPRESS_BACK);

	mNotificationPulse = (PreferenceScreen) findPreference(KEY_NOTIFICATION_PULSE);
        if (mNotificationPulse != null) {
            if (!getResources().getBoolean(com.android.internal.R.bool.config_intrusiveNotificationLed)) {
                getPreferenceScreen().removePreference(mNotificationPulse);
            } else {
                updateLightPulseDescription();
              }
         } 

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mVolumeWake) {
            Settings.System.putInt(getContentResolver(), Settings.System.VOLUME_WAKE_SCREEN,
                    mVolumeWake.isChecked() ? 1 : 0);
	} else if (preference == mVolBtnMusicCtrl) {
            Settings.System.putInt(getContentResolver(), Settings.System.VOLBTN_MUSIC_CONTROLS,
                    mVolBtnMusicCtrl.isChecked() ? 1 : 0); 
	} else if (preference == mKillAppLongpressBack) {
	    writeKillAppLongpressBackOptions(); 
	} else {
	    // If we didn't handle it, let preferences handle it.
	    return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	return true;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
	if (preference == mVolumeOverlay) {
            final int value = Integer.valueOf((String) objValue);
            final int index = mVolumeOverlay.findIndexOfValue((String) objValue);
            Settings.System.putInt(getContentResolver(),
                    Settings.System.MODE_VOLUME_OVERLAY, value);
            mVolumeOverlay.setSummary(mVolumeOverlay.getEntries()[index]);
	    return true;
        } 
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
	updateLightPulseDescription(); 
    }

    @Override
    public void onPause() {
        super.onPause();

	updateKillAppLongpressBackOptions(); 
    }

    private void updateLightPulseDescription() {
        if (Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.NOTIFICATION_LIGHT_PULSE, 0) == 1) {
            mNotificationPulse.setSummary(getString(R.string.notification_light_enabled));
        } else {
            mNotificationPulse.setSummary(getString(R.string.notification_light_disabled));
        }
    } 

    private CheckBoxPreference findAndInitCheckboxPref(String key) {
        CheckBoxPreference pref = (CheckBoxPreference) findPreference(key);
        if (pref == null) {
            throw new IllegalArgumentException("Cannot find preference with key = " + key);
        }
        mAllPrefs.add(pref);
        mResetCbPrefs.add(pref);
        return pref;
    }

    private void writeKillAppLongpressBackOptions() {
        Settings.Secure.putInt(getActivity().getContentResolver(),
                Settings.Secure.KILL_APP_LONGPRESS_BACK,
                mKillAppLongpressBack.isChecked() ? 1 : 0);
    }

    private void updateKillAppLongpressBackOptions() {
        mKillAppLongpressBack.setChecked(Settings.Secure.getInt(
            getActivity().getContentResolver(), Settings.Secure.KILL_APP_LONGPRESS_BACK, 0) != 0);
    } 
}

