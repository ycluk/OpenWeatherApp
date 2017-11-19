package pdn.yingchiuluk.openweatherapp;

/**
 * Data model below is base on return JSON format from openweathermap.org
 */
public class WeatherData {

    public MainData main;
    public Weather[] weather;
    public long dt;
    public String name;

    public class Weather {
        public String description;
        public String icon;
    }

    public class MainData {
        public double temp;
    }
}
