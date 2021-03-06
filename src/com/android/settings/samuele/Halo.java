/*
 * Copyright (C) 2012 The CyanogenMod Project
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

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.INotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.preference.Preference.OnPreferenceClickListener;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.IWindowManager; 

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.Utils;

import net.margaritov.preference.colorpicker.ColorPickerPreference;  

public class Halo extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String KEY_HALO_STATE = "halo_state";
    private static final String KEY_HALO_HIDE = "halo_hide";
    private static final String KEY_HALO_REVERSED = "halo_reversed";
    private static final String KEY_HALO_PAUSE = "halo_pause";
    private static final String KEY_HALO_ENABLED = "halo_enabled";
    private static final String KEY_HALO_SIZE = "halo_size"; 

    private CheckBoxPreference mHaloEnabled;
    private ListPreference mHaloState;
    private CheckBoxPreference mHaloHide;
    private CheckBoxPreference mHaloReversed;
    private CheckBoxPreference mHaloPause;
    private ListPreference mHaloSize; 

    private INotificationManager mNotificationManager; 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.prefs_halo);

        PreferenceScreen prefSet = getPreferenceScreen();

        mNotificationManager = INotificationManager.Stub.asInterface(
                ServiceManager.getService(Context.NOTIFICATION_SERVICE));

	mHaloEnabled = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_ENABLED);
        mHaloEnabled.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.HALO_ENABLED, 0) == 1); 

        mHaloState = (ListPreference) prefSet.findPreference(KEY_HALO_STATE);
        mHaloState.setValue(String.valueOf((isHaloPolicyBlack() ? "1" : "0")));
        mHaloState.setOnPreferenceChangeListener(this);

        mHaloHide = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_HIDE);
        mHaloHide.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.HALO_HIDE, 0) == 1);

        mHaloReversed = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_REVERSED);
        mHaloReversed.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.HALO_REVERSED, 1) == 1);

	int isLowRAM = (ActivityManager.isLargeRAM()) ? 0 : 1;
        mHaloPause = (CheckBoxPreference) prefSet.findPreference(KEY_HALO_PAUSE);
        mHaloPause.setChecked(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.HALO_PAUSE, isLowRAM) == 1); 

	mHaloSize = (ListPreference) prefSet.findPreference(KEY_HALO_SIZE);
        try {
            float haloSize = Settings.System.getFloat(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.HALO_SIZE, 1.0f);
            mHaloSize.setValue(String.valueOf(haloSize));  
        } catch(Exception ex) {
            // So what
        }
        mHaloSize.setOnPreferenceChangeListener(this); 
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
	if (preference == mHaloState) {
            boolean state = Integer.valueOf((String) newValue) == 1;
            try {
                mNotificationManager.setHaloPolicyBlack(state);
            } catch (android.os.RemoteException ex) {
                // System dead
            }          
            return true;
        } else if (preference == mHaloSize) {
            float haloSize = Float.valueOf((String) newValue);
            Settings.System.putFloat(getActivity().getContentResolver(),
                    Settings.System.HALO_SIZE, haloSize);
            return true;
	}
        return false;
    }

    private boolean isHaloPolicyBlack() {
        try {
            return mNotificationManager.isHaloPolicyBlack();
        } catch (android.os.RemoteException ex) {
                // System dead
        }
        return true;
    } 

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

        if (preference == mHaloHide) {  
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.HALO_HIDE, mHaloHide.isChecked()
                    ? 1 : 0);
	} else if  (preference == mHaloEnabled) {
             Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.HALO_ENABLED, mHaloEnabled.isChecked()
                    ? 1 : 0); 
	} else if (preference == mHaloPause) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.HALO_PAUSE, mHaloPause.isChecked()
                    ? 1 : 0); 
        } else if (preference == mHaloReversed) {  
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.HALO_REVERSED, mHaloReversed.isChecked()
                     ? 1 : 0);
	}

	return super.onPreferenceTreeClick(preferenceScreen, preference);   
    }
}

