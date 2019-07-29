package ca.test.weatherforecast;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class WeatherParser {
    private static final String TAG = WeatherParser.class.getSimpleName();

    private static final String CNT = "cnt";
    private static final String CITY = "city";
    private static final String TIMEZONE = "timezone";
    private static final String LIST = "list";
    private static final String DT = "dt";
    private static final String MAIN = "main";
    private static final String TEMP = "temp";
    private static final String WEATHER = "weather";
    private static final String ICON = "icon";

    private static final int MORNING_UPPER_BOUND = 11;
    private static final int MID_DAY_LOWER_BOUND = 12;
    private static final int MID_DAY_UPPER_BOUND = 14;

    public static List<WeatherModel> parseFiveDayForecast(Context context, JSONObject jsonString) throws JSONException {
        int count = jsonString.getInt(CNT);

        // Get time difference to account for different time zones
        JSONObject city = jsonString.getJSONObject(CITY);
        long timeDiffInMillis = city.getLong(TIMEZONE) * 1000;

        // Convert list of weather json objects into list of weather models
        JSONArray list = jsonString.getJSONArray(LIST);
        List<WeatherModel> weatherList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            JSONObject weatherDatum = list.getJSONObject(i);
            weatherList.add(parseWeatherModel(weatherDatum, timeDiffInMillis));
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(context.getString(R.string.day_of_week_format));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        String today = simpleDateFormat.format(new Date(calendar.getTimeInMillis() + timeDiffInMillis));

        simpleDateFormat = new SimpleDateFormat(context.getString(R.string.hour_of_day_format));
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        int currHour = Integer.valueOf(simpleDateFormat.format(new Date(calendar.getTimeInMillis() + timeDiffInMillis)));

        // Due to limitation of weather API, we get list of weather data at 3 hour intervals instead of weather at day intervals
        // Therefore, we must filter out the data we do not want to use
        List<WeatherModel> fiveDayForecast = new ArrayList<>();
        for (WeatherModel weatherModel : weatherList) {
            String dayOfWeek = weatherModel.getDayOfWeek(context);
            int hourOfDay = weatherModel.getHourOfDay(context);

            // Weather at mid-day is used as the weather for the day
            if (hourOfDay >= MID_DAY_LOWER_BOUND && hourOfDay <= MID_DAY_UPPER_BOUND) {
                // Only use today's forecast if morning
                if (!dayOfWeek.equals(today) || currHour <= MORNING_UPPER_BOUND) {
                    fiveDayForecast.add(weatherModel);
                }
            }
        }

        // Due to limitation of weather API, we may not have weather at mid-day for day 5, so take latest weather
        if (fiveDayForecast.size() != 5) {
            fiveDayForecast.add(weatherList.get(weatherList.size() - 1));
        }

        return fiveDayForecast;
    }

    public static WeatherModel parseWeatherModel(JSONObject weatherDatum, long timeDiffInMillis) throws JSONException {
        long epochTimeInMillis = weatherDatum.getLong(DT) * 1000;

        JSONObject main = weatherDatum.getJSONObject(MAIN);
        double temperature = main.getDouble(TEMP);

        JSONArray weather = weatherDatum.getJSONArray(WEATHER);
        JSONObject weather0 = weather.getJSONObject(0);
        String iconCode = weather0.getString(ICON);
        int iconResId = Utils.getConditionResIdFromCode(iconCode);

        return new WeatherModel(epochTimeInMillis, timeDiffInMillis, iconResId, temperature);
    }
}
