package com.eveningoutpost.dexdrip.watch.miband;

import android.content.SharedPreferences;

import com.eveningoutpost.dexdrip.Models.JoH;
import com.eveningoutpost.dexdrip.UtilityModels.Inevitable;
import com.eveningoutpost.dexdrip.UtilityModels.Pref;

import java.util.Date;
// very lightweight entry point class to avoid loader overhead when not in use

public class MiBandEntry {
    public static final String PREF_MIBAND_ENABLED = "miband_enabled";
    public static final String MIBAND_SEND_READINGS = "miband_send_readings";
    public static final String PREF_SEND_ALARMS = "miband_send_alarms";
    public static final String PREF_CALL_ALERTS = "miband_option_call_notifications";
    public static final String PREF_MIBAND_SETTINGS = "miband_settings";
    public static final String PREF_MIBAND_PREFERENCES = "miband_preferences";
    public static final String PREF_MIBAND_INSTALL_WATCHFACE = "install_miband_watchface";
    public static final String PREF_MIBAND_NIGHTMODE_ENABLED = "miband_nightmode_enabled";
    public static final String PREF_MIBAND_NIGHTMODE_START = "miband_nightmode_start";
    public static final String PREF_MIBAND_NIGHTMODE_END = "miband_nightmode_end";

    public static final String MIBAND_SEND_READINGS_AS_NOTIFICATION = "miband_send_readings_as_notification";

    public static final String PREF_VIBRATE_ON_READINGS = "miband_vibrate_on_readings";

    public static boolean isEnabled() {
        return Pref.getBooleanDefaultFalse(PREF_MIBAND_ENABLED);
    }

    public static boolean areAlertsEnabled() {
        return isEnabled() && Pref.getBooleanDefaultFalse(PREF_SEND_ALARMS);
    }

    public static boolean isVibrateOnReadings() {
        return Pref.getBooleanDefaultFalse(PREF_VIBRATE_ON_READINGS);
    }

    public static boolean isNeedSendReading() {
        return isEnabled() && Pref.getBooleanDefaultFalse(MIBAND_SEND_READINGS);
    }

    public static boolean isNeedSendReadingAsNotification() {
        return isEnabled() && Pref.getBooleanDefaultFalse(MIBAND_SEND_READINGS_AS_NOTIFICATION);
    }

    public static boolean areCallAlertsEnabled() {
        return isEnabled() && Pref.getBooleanDefaultFalse(PREF_CALL_ALERTS);
    }

    public static boolean isNightModeEnabled() {
        return Pref.getBooleanDefaultFalse(PREF_MIBAND_NIGHTMODE_ENABLED);
    }

    public static Date getNightModeStart() {
        return new Date(Pref.getLong(PREF_MIBAND_NIGHTMODE_START, 0));
    }

    public static Date getNightModeEnd() {
        return new Date(Pref.getLong(PREF_MIBAND_NIGHTMODE_END, 0));
    }

    public static void initialStartIfEnabled() {
        if (isEnabled()) {
            Inevitable.task("mb-full-initial-start", 500, new Runnable() {
                @Override
                public void run() {
                    showLatestBG();
                }
            });
        }
    }

    public static SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.startsWith("miband")) {
                android.util.Log.d("miband", "Preference key: " + key);
                refresh();
            }
        }
    };

    static void refresh() {
        Inevitable.task("miband-preference-changed", 1000, () -> JoH.startService(MiBandService.class, "function", "refresh"));
    }

    public static void showLatestBG() {
        if (isNeedSendReading()) {
            JoH.startService(MiBandService.class, "function", "update_bg");
        }
    }
}
