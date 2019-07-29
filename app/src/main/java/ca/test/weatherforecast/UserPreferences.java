package ca.test.weatherforecast;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {
    private static final String USER_PREFERENCES_FILE = "USER_PREFERENCES_FILE";

    private static final String DEFAULT_CITY = "DEFAULT_CITY";
    private static final String DEFAULT_COUNTRY = "DEFAULT_COUNTRY";
    private static final String TEMPERATURE_UNIT = "TEMPERATURE_UNIT";

    public static String getDefaultCity(Context context) {
        SharedPreferences userPref = context.getSharedPreferences(USER_PREFERENCES_FILE, Context.MODE_PRIVATE);
        return userPref.getString(DEFAULT_CITY, context.getString(R.string.default_city));
    }

    public static void setDefaultCity(Context context, String city) {
        SharedPreferences userPref = context.getSharedPreferences(USER_PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putString(DEFAULT_CITY, city);
        editor.apply();
    }

    public static String getDefaultCountry(Context context) {
        SharedPreferences userPref = context.getSharedPreferences(USER_PREFERENCES_FILE, Context.MODE_PRIVATE);
        return userPref.getString(DEFAULT_COUNTRY, context.getString(R.string.default_country));
    }

    public static void setDefaultCountry(Context context, String country) {
        SharedPreferences userPref = context.getSharedPreferences(USER_PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putString(DEFAULT_COUNTRY, country);
        editor.apply();
    }

    public static TemperatureUnit getTemperatureUnit(Context context) {
        SharedPreferences userPref = context.getSharedPreferences(USER_PREFERENCES_FILE, Context.MODE_PRIVATE);
        String unitName = userPref.getString(TEMPERATURE_UNIT, TemperatureUnit.Celsius.name());
        return TemperatureUnit.valueOf(unitName);
    }

    public static void setTemperatureUnit(Context context, TemperatureUnit unit) {
        SharedPreferences userPref = context.getSharedPreferences(USER_PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();
        editor.putString(TEMPERATURE_UNIT, unit.name());
        editor.apply();
    }
}
