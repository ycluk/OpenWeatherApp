package pdn.yingchiuluk.openweatherapp;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class QueryIntentService extends IntentService {
    static final String RESULT_INTENT_STRING = "open_weather_result";
    static final String KEY_QUERY_ERROR = "key_query_error_text";

    private final String errorString = "Unable to retrieve data. Try to use different zip code";
    private final String baseUrl = "http://api.openweathermap.org/data/2.5/weather?";
    private final String units = "&units=imperial";
    private final String appId = "&APPID=0a35ac2343f1bd49c8d5adb7c1d065c6";

    private String zip;

    public QueryIntentService() {
        super("QueryIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        zip = "zip=" + intent.getExtras().getString(MainActivity.KEY_ZIP_CODE, "") + ",us";

        StringBuilder sb = new StringBuilder(baseUrl);
        sb.append(zip).append(units).append(appId);

        BufferedReader bufferedReader = null;
        HttpURLConnection httpURLConnection;
        InputStream inputStream = null;
        URL openWeatherApiUrl;

        try {
            openWeatherApiUrl = new URL(sb.toString());
            httpURLConnection = (HttpURLConnection) openWeatherApiUrl.openConnection();
            int connectionStatus = httpURLConnection.getResponseCode();
            Intent resultIntent = new Intent(RESULT_INTENT_STRING);

            /**
             * if connection status is 400 or higher, there is error for the connection
             * else the connection is good and query should return result
             */
            if (connectionStatus > 399) {
                resultIntent.putExtra(RESULT_INTENT_STRING, false);
                resultIntent.putExtra(KEY_QUERY_ERROR, errorString);
            } else {
                inputStream = httpURLConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                DataParser dataParser = new DataParser();
                if ( dataParser.parseStreamData(bufferedReader) ) {
                    resultIntent.putExtra(RESULT_INTENT_STRING, true);
                } else {
                    resultIntent.putExtra(RESULT_INTENT_STRING, false);
                }
            }
            LocalBroadcastManager.getInstance(this).sendBroadcast(resultIntent);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) inputStream.close();
                if (bufferedReader != null) bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
