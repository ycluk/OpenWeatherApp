package pdn.yingchiuluk.openweatherapp;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.BufferedReader;

public class DataParser {

   boolean parseStreamData(BufferedReader reader) {
       boolean isSuccessful = false;
       Gson gson = new Gson();
       try {
           /**
            * Parse returned JSON string object, and add "ic_" prefix to icon string name
            */
           MainActivity.sWeatherData = gson.fromJson(reader, WeatherData.class);
           String icon = MainActivity.sWeatherData.weather[0].icon;
           MainActivity.sWeatherData.weather[0].icon = "ic_" + icon;
           isSuccessful = true;
       } catch (JsonIOException e) {
           e.printStackTrace();
       } catch (JsonSyntaxException e) {
           e.printStackTrace();
       } catch (NullPointerException e) {
           e.printStackTrace();
       } catch (Exception e) {
           e.printStackTrace();
       }

       return isSuccessful;
   }

}
