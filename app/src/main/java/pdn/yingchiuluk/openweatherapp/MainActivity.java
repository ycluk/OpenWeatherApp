package pdn.yingchiuluk.openweatherapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    static final String KEY_ZIP_CODE = "ZIP_CODE";
    static final String PREFS_NAME = "MyPrefsFile";
    static WeatherData sWeatherData;

    private EditText mZipCodeEditText;
    private TextView mPlaceLabel;
    private TextView mWeatherLabel;
    private ImageView mWeatherIcon;
    private SharedPreferences mSharedPreferences;

    private QueryResultReceiver mQueryResultReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mZipCodeEditText = (EditText) findViewById(R.id.zip_code_edit_text);
        mPlaceLabel = (TextView) findViewById(R.id.place_text);
        mWeatherLabel = (TextView) findViewById(R.id.weather_text);
        mWeatherIcon = (ImageView) findViewById(R.id.weather_icon);
        mSharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        /**
         * check if savedInstance has save data before checking from shared preferences
         * and if there is saved zip code, put that into zip code edit text box
         */
        String userZipCode;
        if (savedInstanceState != null) {
            userZipCode = savedInstanceState.getString(KEY_ZIP_CODE, null);
        } else {
            userZipCode = mSharedPreferences.getString(KEY_ZIP_CODE, null);
        }
        if (userZipCode != null && userZipCode.length() > 0) {
            mZipCodeEditText.setText(userZipCode);
        }

        mQueryResultReceiver = new QueryResultReceiver();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            mZipCodeEditText.setText(savedInstanceState.getString(KEY_ZIP_CODE, ""));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mQueryResultReceiver, new IntentFilter(QueryIntentService.RESULT_INTENT_STRING));
    }


    /**
     * if data already exist, do not start new query to reduce the query count of the API key
     * otherwise check if zip code is entered before starting to retrieve data
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (sWeatherData != null) {
            showResults();
        } else {
            String zipCode = mZipCodeEditText.getText().toString();
            if (zipCode != null && zipCode.length() > 0) {
                Intent queryServiceIntent = new Intent(this, QueryIntentService.class);
                queryServiceIntent.putExtra(KEY_ZIP_CODE, zipCode);
                startService(queryServiceIntent);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String userZipCode = mZipCodeEditText.getText().toString();
        if (userZipCode != null && userZipCode.length() > 0) {
            outState.putString(KEY_ZIP_CODE, userZipCode);
        }
    }

    /**
     * unregister broadcast receiver and save entered zip code into sharded preference
     */
    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mQueryResultReceiver);

        String userZipCode = mZipCodeEditText.getText().toString();
        if (userZipCode != null && userZipCode.length() > 0) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(KEY_ZIP_CODE, userZipCode);
            editor.commit();
        }
        super.onStop();
    }

    /**
     * This is onClick listener for button that check if there is a zip code entered
     * before starting query service to retrieve data
     */
    public void queryData(View view) {
        String zipCode = mZipCodeEditText.getText().toString();
        if (zipCode != null && zipCode.length() > 0) {
            Intent queryServiceIntent = new Intent(this, QueryIntentService.class);
            queryServiceIntent.putExtra(KEY_ZIP_CODE, zipCode);
            startService(queryServiceIntent);
        } else {
            Toast.makeText(this, "Please enter zip code", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * show result of data on screen
     */
    private void showResults() {
        if (sWeatherData != null){
            Long time = sWeatherData.dt * 1000;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            String place = sWeatherData.name + " " + calendar.getTime();
            mPlaceLabel.setText(place);
            String weather = sWeatherData.weather[0].description + " " + sWeatherData.main.temp;
            mWeatherLabel.setText(weather);
            mWeatherIcon.setImageResource(
                    getResources().getIdentifier(sWeatherData.weather[0].icon, "drawable", getPackageName())
            );
        }
    }

    /**
     * if result broadcast intent is good (true), showResults
     * else create alert dialog to show error information
     */
    private class QueryResultReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(QueryIntentService.RESULT_INTENT_STRING, false)) {
                showResults();
            } else {
                QueryErrorDialog dialog = new QueryErrorDialog();
                Bundle errorResult = new Bundle();
                errorResult.putString(QueryIntentService.KEY_QUERY_ERROR,
                        intent.getStringExtra(QueryIntentService.KEY_QUERY_ERROR));
                dialog.setArguments(errorResult);
                dialog.show(getSupportFragmentManager(), "QueryErrorDialogFragment");
            }
        }
    }

}
