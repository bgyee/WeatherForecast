package ca.test.weatherforecast;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class WeatherModel {

    private long mEpochTime; // in milliseconds
    private long mTimeDifference; // in milliseconds; Needed to account for different time zones
    private int mConditionIconResId;
    private double mTemperature;

    public WeatherModel(long epochTimeInMillis, long timeDifferenceInMillis, int conditionIconResId, double temperature) {
        mEpochTime = epochTimeInMillis;
        mTimeDifference = timeDifferenceInMillis;
        mConditionIconResId = conditionIconResId;
        mTemperature = temperature;
    }

    public String getDayOfWeek(Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.day_of_week_format));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(new Date(mEpochTime + mTimeDifference));
    }

    public int getHourOfDay(Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.hour_of_day_format));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return Integer.valueOf(simpleDateFormat.format(new Date(mEpochTime + mTimeDifference)));
    }

    public int getConditionIconResId() {
        return mConditionIconResId;
    }

    public double getTemperature() {
        return mTemperature;
    }

    public double getTemperatureInUserPreferredUnit(Context context) {
        switch (UserPreferences.getTemperatureUnit(context)) {
            case Celsius:
                return Utils.convertKelvinToCelsius(mTemperature);
            case Fahrenheit:
                return Utils.convertKelvinToFahrenheit(mTemperature);
            case Kelvin:
            default:
                return mTemperature;
        }
    }
}
