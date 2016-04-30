package com.tastingroomdelmar.TastingRoomDelMar.Activities;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;

import com.parse.ParseUser;
import com.tastingroomdelmar.TastingRoomDelMar.R;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    static Context mContext;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            ParseUser user = ParseUser.getCurrentUser();

            if(user == null) return false;

            if (preference instanceof CheckBoxPreference) {
                String key = preference.getKey();
                boolean booleanValue = ((CheckBoxPreference) preference).isChecked();

                switch (key) {
                    case "push":
                        user.put("pushAllowed",!booleanValue);
                        user.saveInBackground();
                        break;
                    case "newsletter":
                        user.put("marketingAllowed",!booleanValue);
                        user.saveInBackground();
                        break;
                }
            } else {
                String stringValue = value.toString();

                preference.setSummary(stringValue);
                String key = preference.getKey();

                switch (key) {
                    case "firstname":
                        user.put("firstName",stringValue);
                        user.saveInBackground();
                        break;
                    case "lastname":
                        user.put("lastName",stringValue);
                        user.saveInBackground();
                        break;
                    case "mobile":
                        user.put("mobileNumber",stringValue);
                        user.saveInBackground();
                        break;
                    case "email":
                        user.put("email",stringValue);
                        user.saveInBackground();
                        break;
                    case "password":
                        user.put("password",stringValue);
                        user.saveInBackground();
                        break;
                }
            }
            return true;
        }
    };

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        if (preference instanceof CheckBoxPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getBoolean(preference.getKey(), false));
        } else {
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setupActionBar();
        getFragmentManager().beginTransaction().replace(android.R.id.content, new GeneralPreferenceFragment()).commit();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        Log.d("isValidFragment", "fragmentName: " + fragmentName);
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            Log.d("PREFERENCE", "onCreate called");
            setHasOptionsMenu(false);

            EditTextPreference firstnamePref = (EditTextPreference) findPreference("firstname");
            EditTextPreference lastnamePref = (EditTextPreference) findPreference("lastname");
            EditTextPreference mobilePref = (EditTextPreference) findPreference("mobile");
            EditTextPreference emailPref = (EditTextPreference) findPreference("email");
            EditTextPreference passwordPref = (EditTextPreference) findPreference("password");
            CheckBoxPreference pushPref = (CheckBoxPreference) findPreference("push");
            CheckBoxPreference newsletterPref = (CheckBoxPreference) findPreference("newsletter");
            Preference logoutPref = findPreference("logout");

            if (ParseUser.getCurrentUser() == null) {
                firstnamePref.setEnabled(false);
                lastnamePref.setEnabled(false);
                mobilePref.setEnabled(false);
                emailPref.setEnabled(false);
                passwordPref.setEnabled(false);
                pushPref.setEnabled(false);
                newsletterPref.setEnabled(false);
                logoutPref.setEnabled(false);
            }

            firstnamePref.setSummary(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("firstname",""));
            lastnamePref.setSummary(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("lastname",""));
            mobilePref.setSummary(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("mobile",""));
            emailPref.setSummary(PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("email",""));
            pushPref.setChecked(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("push",false));
            newsletterPref.setChecked(PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("newsletter",false));

            bindPreferenceSummaryToValue(firstnamePref);
            bindPreferenceSummaryToValue(lastnamePref);
            bindPreferenceSummaryToValue(mobilePref);
            bindPreferenceSummaryToValue(emailPref);
            bindPreferenceSummaryToValue(passwordPref);
            bindPreferenceSummaryToValue(pushPref);
            bindPreferenceSummaryToValue(newsletterPref);

            findPreference("privacypolicy").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(getActivity(), WebViewActivity.class);
                    i.putExtra("TARGET","Privacy Policy");
                    startActivity(i);
                    return true;
                }
            });

            findPreference("termsofuse").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(getActivity(), WebViewActivity.class);
                    i.putExtra("TARGET","Terms & Conditions");
                    startActivity(i);
                    return true;
                }
            });

            logoutPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("LOGOUT");
                    alertDialogBuilder.setMessage("Are you sure?");

                    alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ParseUser.logOutInBackground();

                            PreferenceManager.getDefaultSharedPreferences(getActivity()).edit().clear().commit();

                            Intent newStackIntent = new Intent(getActivity(), LoginActivity.class);
                            newStackIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(newStackIntent);


                        }
                    });

                    alertDialogBuilder.setNegativeButton("NO", null);

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    return true;
                }
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
