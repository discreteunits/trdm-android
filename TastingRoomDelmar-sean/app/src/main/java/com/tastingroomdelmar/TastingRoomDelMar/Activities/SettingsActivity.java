package com.tastingroomdelmar.TastingRoomDelMar.Activities;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.tastingroomdelmar.TastingRoomDelMar.BuildConfig;
import com.tastingroomdelmar.TastingRoomDelMar.R;
import com.tastingroomdelmar.TastingRoomDelMar.utils.CategoryManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OIDManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.OrderManager;
import com.tastingroomdelmar.TastingRoomDelMar.utils.PaymentManager;

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

    static Dialog loadingDialog;

    static Dialog alertDialog;
    static TextView alertTitle;
    static TextView alertMsg;
    static Button alertBtn;

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            ParseUser user = ParseUser.getCurrentUser();

            if(user == null) return false;

            final String savedEmail = user.getEmail();

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
                        if (stringValue.isEmpty() || stringValue.length() == 0) {
                            user.setEmail(savedEmail);
                            preference.setSummary(savedEmail);

                            alertMsg.setText("You must specify an email.");
                            alertDialog.show();
                            break;
                        }

                        user.put("email",stringValue);
                        loadingDialog.show();
                        try {
                            user.save();
                        } catch (ParseException e) {
                            user.setEmail(savedEmail);
                            preference.setSummary(savedEmail);

                            if (e.getCode() == ParseException.EMAIL_MISSING) {
                                alertMsg.setText("You must specify an email.");
                            } else if (e.getCode() == ParseException.EMAIL_TAKEN) {
                                alertMsg.setText("Email address is already taken.");
                            } else if (e.getCode() == ParseException.INVALID_EMAIL_ADDRESS) {
                                alertMsg.setText("Email address is invalid. Please try again.");
                            } else {
                                alertMsg.setText("There was an error. Please try again. Error["+e.getCode()+"]");
                            }
                            alertDialog.show();
                        }
                        loadingDialog.dismiss();
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

        // TODO add action bar. it's not showing up
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

            loadingDialog = new Dialog(mContext);
            loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            loadingDialog.setContentView(R.layout.layout_loading_dialog);
            loadingDialog.setCancelable(false);
            loadingDialog.setCanceledOnTouchOutside(false);
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            alertDialog = new Dialog(mContext);
            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialog.setContentView(R.layout.layout_general_alert);
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertTitle = (TextView) alertDialog.findViewById(R.id.tv_general_title);
            alertMsg = (TextView) alertDialog.findViewById(R.id.tv_general_msg);
            alertBtn = (Button) alertDialog.findViewById(R.id.btn_general_ok);
            alertBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

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

            findPreference("appinfo").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setTitle("APP INFO");
                    alertDialogBuilder.setMessage("Tasting Room Del Mar\nVersion: v" + BuildConfig.VERSION_NAME +"\nMADE IN SAN DIEGO");

                    alertDialogBuilder.setNegativeButton("Okay", null);

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
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

                            OrderManager.clearOrders();
                            OIDManager.popAll();
                            CategoryManager.popAll();
                            PaymentManager.getSingleton().clearPaymentMethod();
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
