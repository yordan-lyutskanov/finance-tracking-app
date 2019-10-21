package com.yordan.finance.view.ui.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.yordan.finance.R;
import com.yordan.finance.view.ui.activities.ActivitySettings;

import static com.yordan.finance.view.ui.activities.ActivitySettings.KEY_BUDGETING_ENABLED;

public class SettingsFragmentBudgeting extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.budgeting_settings, rootKey);

        findPreference(KEY_BUDGETING_ENABLED).setSummaryProvider(new Preference.SummaryProvider<SwitchPreferenceCompat>() {
            @Override
            public CharSequence provideSummary(SwitchPreferenceCompat preference) {
                if(preference.isChecked()){
                    return "Budgeting is enabled.";
                }else{
                    return "Tap to enable budgeting.";
                }
            }
        });

        for(String key : ActivitySettings.KEYS_FOR_BUDGET){
            EditTextPreference preference = findPreference(key);

            preference.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
                @Override
                public void onBindEditText(@NonNull EditText editText) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
                }
            });

            preference.setSummaryProvider(new Preference.SummaryProvider<EditTextPreference>(){
                @Override
                public CharSequence provideSummary(EditTextPreference preference) {
                    String text = preference.getText();
                    if(text.isEmpty() || text.equals("0")){
                        return "No monthly budget set";
                    }else{
                        return "Monthly budget: " + text + " лв.";
                    }
                }
            });
        }
    }
}
