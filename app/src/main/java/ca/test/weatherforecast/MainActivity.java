package ca.test.weatherforecast;

import android.app.SearchManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private List<WeatherModel> mFiveDayForecast;

    private String mCity;
    private String mCountry;
    private String mCityCountryApiFormat;

    private Button mKelvinButton;
    private Button mCelsiusButton;
    private Button mFahrenheitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCity = UserPreferences.getDefaultCity(this);
        mCountry = UserPreferences.getDefaultCountry(this);
        mCityCountryApiFormat = mCity + "," + Utils.getCountryCodeFromName(mCountry);

        refresh();

        mKelvinButton = findViewById(R.id.kelvin_button);
        mKelvinButton.setOnClickListener(this);

        mCelsiusButton = findViewById(R.id.celsius_button);
        mCelsiusButton.setOnClickListener(this);

        mFahrenheitButton = findViewById(R.id.fahrenheit_button);
        mFahrenheitButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu_main, menu);

        final MenuItem searchMenuItem = menu.findItem(R.id.search);

        final SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.search_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                updateCityCountry(query);
                searchMenuItem.collapseActionView();
                searchView.setQuery("", false);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String cityCountry = intent.getStringExtra(SearchManager.QUERY);
            updateCityCountry(cityCountry);
        }
    }

    private void refresh() {
        requestFiveDayForecast();
    }

    private void reloadUI() {
        setTitle(mCity + ", " + mCountry);

        ViewGroup day0 = findViewById(R.id.day0);
        loadWeatherModelIntoView(mFiveDayForecast.get(0), day0);

        ViewGroup day1 = findViewById(R.id.day1);
        loadWeatherModelIntoView(mFiveDayForecast.get(1), day1);

        ViewGroup day2 = findViewById(R.id.day2);
        loadWeatherModelIntoView(mFiveDayForecast.get(2), day2);

        ViewGroup day3 = findViewById(R.id.day3);
        loadWeatherModelIntoView(mFiveDayForecast.get(3), day3);

        ViewGroup day4 = findViewById(R.id.day4);
        loadWeatherModelIntoView(mFiveDayForecast.get(4), day4);

        TemperatureUnit temperatureUnit = UserPreferences.getTemperatureUnit(this);
        mKelvinButton.setEnabled(temperatureUnit != TemperatureUnit.Kelvin);
        mCelsiusButton.setEnabled(temperatureUnit != TemperatureUnit.Celsius);
        mFahrenheitButton.setEnabled(temperatureUnit != TemperatureUnit.Fahrenheit);
    }

    private void requestFiveDayForecast() {
        String url = getString(R.string.five_day_forecast_base_url, mCityCountryApiFormat, getString(R.string.api_key));
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Successful response, so city/country are valid
                        UserPreferences.setDefaultCity(MainActivity.this, mCity);
                        UserPreferences.setDefaultCountry(MainActivity.this, mCountry);

                        try {
                            mFiveDayForecast = WeatherParser.parseFiveDayForecast(MainActivity.this, response);
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "Unable to retrieve weather data for " + mCity + ", " + mCountry,
                                    Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Unexpected JSON format: " + response.toString(), e);
                        }

                        reloadUI();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Unable to retrieve weather data for " + mCity + ", " + mCountry,
                                Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Error response: " + error.networkResponse);
                    }
                });

        RequestQueueManager.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    private void loadWeatherModelIntoView(WeatherModel weather, ViewGroup fiveDayForecastItem) {
        TextView dayText = fiveDayForecastItem.findViewById(R.id.text_day);
        ImageView conditionIcon = fiveDayForecastItem.findViewById(R.id.condition_icon);
        TextView temperatureText = fiveDayForecastItem.findViewById(R.id.text_temperature);

        dayText.setText(weather.getDayOfWeek(this));
        conditionIcon.setImageResource(weather.getConditionIconResId());
        long temperature = Math.round(weather.getTemperatureInUserPreferredUnit(this));
        temperatureText.setText(getString(R.string.temperature_degrees, Long.toString(temperature)));
    }

    private void updateCityCountry(String cityCountry) {
        String[] cityCountryArray = cityCountry.split(",");

        mCity = cityCountryArray[0].trim();
        mCity = mCity.substring(0, 1).toUpperCase() + mCity.substring(1);

        // If specified, use new default country. Else, use old default country
        if (cityCountryArray.length == 2) {
            mCountry = cityCountryArray[1].trim();
            mCountry = mCountry.substring(0, 1).toUpperCase() + mCountry.substring(1);
        } else {
            mCountry = UserPreferences.getDefaultCountry(this);
        }

        mCityCountryApiFormat = mCity + "," + Utils.getCountryCodeFromName(mCountry);

        refresh();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.kelvin_button:
                UserPreferences.setTemperatureUnit(this, TemperatureUnit.Kelvin);
                reloadUI();
                break;
            case R.id.celsius_button:
                UserPreferences.setTemperatureUnit(this, TemperatureUnit.Celsius);
                reloadUI();
                break;
            case R.id.fahrenheit_button:
                UserPreferences.setTemperatureUnit(this, TemperatureUnit.Fahrenheit);
                reloadUI();
                break;
            default:
                // no-op
        }
    }
}
