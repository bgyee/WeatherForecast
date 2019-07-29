package ca.test.weatherforecast;

import android.util.Log;

import java.util.Locale;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    private static final String CONDITION_01d = "01d";
    private static final String CONDITION_01n = "01n";
    private static final String CONDITION_02d = "02d";
    private static final String CONDITION_02n = "02n";
    private static final String CONDITION_03d = "03d";
    private static final String CONDITION_03n = "03n";
    private static final String CONDITION_04d = "04d";
    private static final String CONDITION_04n = "04n";
    private static final String CONDITION_09d = "09d";
    private static final String CONDITION_09n = "09n";
    private static final String CONDITION_10d = "10d";
    private static final String CONDITION_10n = "10n";
    private static final String CONDITION_11d = "11d";
    private static final String CONDITION_11n = "11n";
    private static final String CONDITION_13d = "13d";
    private static final String CONDITION_13n = "13n";
    private static final String CONDITION_50d = "50d";
    private static final String CONDITION_50n = "50n";

    public static int getConditionResIdFromCode(String conditionCode) {
        switch (conditionCode) {
            case CONDITION_01d:
            case CONDITION_01n:
                return R.drawable.condition_01d;
            case CONDITION_02d:
            case CONDITION_02n:
                return R.drawable.condition_02d;
            case CONDITION_03d:
            case CONDITION_03n:
                return R.drawable.condition_03d;
            case CONDITION_04d:
            case CONDITION_04n:
                return R.drawable.condition_04d;
            case CONDITION_09d:
            case CONDITION_09n:
                return R.drawable.condition_09d;
            case CONDITION_10d:
            case CONDITION_10n:
                return R.drawable.condition_10d;
            case CONDITION_11d:
            case CONDITION_11n:
                return R.drawable.condition_11d;
            case CONDITION_13d:
            case CONDITION_13n:
                return R.drawable.condition_13d;
            case CONDITION_50d:
            case CONDITION_50n:
                return R.drawable.condition_50d;
            default:
                // no-op
                Log.e(TAG, "Unknown condition code: " + conditionCode);
                return 0;
        }
    }

    public static String getCountryCodeFromName(String countryName) {
        String[] countryCodes = Locale.getISOCountries();
        Locale locale;

        for (String countryCode : countryCodes) {
            locale = new Locale("", countryCode);
            if (countryName.equalsIgnoreCase(locale.getDisplayCountry())) {
                return countryCode;
            }
        }

        Log.e(TAG, "Could not find country code for " + countryName);
        return "??";
    }

    public static double convertKelvinToCelsius(double temperatureInK) {
        return temperatureInK - 273.15;
    }

    public static double convertCelsiusToKelvin(double temperatureInC) {
        return temperatureInC + 273.15;
    }

    public static double convertKelvinToFahrenheit(double temperatureInK) {
        return convertCelsiusToFahrenheit(convertKelvinToCelsius(temperatureInK));
    }

    public static double convertFarhrenheitToKelvin(double temperatureInF) {
        return convertCelsiusToKelvin(convertFahrenheitToCelsius(temperatureInF));
    }

    public static double convertCelsiusToFahrenheit(double temperatureInC) {
        return temperatureInC * 9 / 5 + 32;
    }

    public static double convertFahrenheitToCelsius(double temperatureInF) {
        return (temperatureInF - 32) * 5 / 9;
    }
}
