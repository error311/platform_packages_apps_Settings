
package com.android.settings.samuele;

import android.os.Bundle;
import android.preference.ListPreference; 
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.samuele.SeekBarPreferenceGlow;

import net.margaritov.preference.colorpicker.ColorPickerPreference; 

public class NavigationBarSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String KEY_NAVIGATION_BAR_HEIGHT = "navigation_bar_height";
    private static final String NAVIGATION_BUTTON_COLOR = "navigation_button_color";
    private static final String NAVIGATION_BUTTON_GLOW_COLOR = "navigation_button_glow_color";
    private static final String NAVIGATION_BUTTON_GLOW_TIME = "navigation_button_glow_time"; 

    private ListPreference mNavigationBarHeight;
    private ColorPickerPreference mNavigationBarButtonColor;
    private ColorPickerPreference mNavigationButtonGlowColor; 
    private SeekBarPreferenceGlow mNavigationButtonGlowTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.navigation_bar_settings);

        mNavigationBarHeight = (ListPreference) findPreference(KEY_NAVIGATION_BAR_HEIGHT);
        mNavigationBarHeight.setOnPreferenceChangeListener(this);
        int statusNavigationBarHeight = Settings.System.getInt(getActivity().getApplicationContext()
                .getContentResolver(),
                Settings.System.NAVIGATION_BAR_HEIGHT, 48);
        mNavigationBarHeight.setValue(String.valueOf(statusNavigationBarHeight));
        mNavigationBarHeight.setSummary(mNavigationBarHeight.getEntry());

	mNavigationButtonGlowTime = (SeekBarPreferenceGlow) getPreferenceScreen().findPreference(NAVIGATION_BUTTON_GLOW_TIME);
        mNavigationButtonGlowTime.setDefault(Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                Settings.System.NAVIGATION_BUTTON_GLOW_TIME, 500));
        mNavigationButtonGlowTime.setOnPreferenceChangeListener(this); 

	mNavigationButtonGlowColor = (ColorPickerPreference) findPreference(NAVIGATION_BUTTON_GLOW_COLOR);
        mNavigationButtonGlowColor.setOnPreferenceChangeListener(this);
        int intColor1 = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BUTTON_GLOW_COLOR, 0x00000000);
        String hexColor1 = String.format("#%08x", (0xffffffff & intColor1));
        mNavigationButtonGlowColor.setNewPreviewColor(intColor1);  

	mNavigationBarButtonColor = (ColorPickerPreference) findPreference(NAVIGATION_BUTTON_COLOR);
        mNavigationBarButtonColor.setOnPreferenceChangeListener(this);
        int intColor = Settings.System.getInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BUTTON_COLOR, -2);
        if (intColor == -2) {
            intColor = getResources().getColor(
                    com.android.internal.R.color.white);
            mNavigationBarButtonColor.setSummary(getResources().getString(R.string.color_default));
        } else {
            String hexColor = String.format("#%08x", (0xffffffff & intColor));
            mNavigationBarButtonColor.setSummary(hexColor);
        }
        mNavigationBarButtonColor.setNewPreviewColor(intColor);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        if (preference == mNavigationBarHeight) {
            int statusNavigationBarHeight = Integer.valueOf((String) objValue);
            int index = mNavigationBarHeight.findIndexOfValue((String) objValue);
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAVIGATION_BAR_HEIGHT, statusNavigationBarHeight);
            mNavigationBarHeight.setSummary(mNavigationBarHeight.getEntries()[index]);
	    return true;
        } else if (preference == mNavigationBarButtonColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BUTTON_COLOR, intHex);
            return true;
	} else if (preference == mNavigationButtonGlowColor) {
            String hex = ColorPickerPreference.convertToARGB(
                    Integer.valueOf(String.valueOf(objValue)));
            preference.setSummary(hex);
            int intHex = ColorPickerPreference.convertToColorInt(hex);
            Settings.System.putInt(getActivity().getContentResolver(),
                    Settings.System.NAVIGATION_BUTTON_GLOW_COLOR, intHex);
	    return true;
	} else if (preference == mNavigationButtonGlowTime) {
            int value = (Integer) objValue;
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NAVIGATION_BUTTON_GLOW_TIME, value);
            return true;
	}
        return false; 
    }
}
