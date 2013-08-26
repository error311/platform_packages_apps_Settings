/*
 * Copyright (C) 2012 SuperNova Team
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

import android.content.Intent;
import android.net.Uri;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;

public class AboutUs extends SettingsPreferenceFragment {

    Preference mFbPage;
    Preference mImage;
    Preference mXdaToro;
    Preference mXdaMaguro;
    Preference mContacts;
    Preference mWebSite;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.about);

	mFbPage = findPreference("supernova_on_fb");
	mImage = findPreference("image");
	mXdaToro = findPreference("xda_toro");
        mXdaMaguro = findPreference("xda_maguro");
	mContacts = findPreference("contacts");
	mWebSite = findPreference("web_site");
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        boolean value;

	if (preference == mXdaMaguro) {
	    launchUrl("http://forum.xda-developers.com/showthread.php?t=2290812");
	} else if (preference == mFbPage) {
	    launchUrl("https://www.facebook.com/SuperNovaDevTeam");
	} else if (preference == mXdaToro) {
	    launchUrl("http://forum.xda-developers.com/showthread.php?t=2413236");
	} else if (preference == mContacts) {
	    Intent email = new Intent(Intent.ACTION_SEND);
	    email.setType("plain/text");
	    email.putExtra(Intent.EXTRA_EMAIL, new String[] {
		"SuperNovaDevTeam@gmail.com" });
	    startActivity(Intent.createChooser(email, ""));
	} else if (preference == mWebSite) {
	    launchUrl("http://supernovadevteam.altervista.org");
	} 
	return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

     private void launchUrl(String url) {
	Intent web = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
	web.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	getActivity().getApplicationContext().startActivity(web);
    }
}
