package com.floodalert.app.UI.weather;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherData {

    private String mTemperature, micon, mcity, mWeatherType;
    private int mCondition;

    public static WeatherData fromJson(JSONObject jsonObject)
    {
        try {
            WeatherData weatherD = new WeatherData();
            weatherD.mcity = jsonObject.getString("name");
            weatherD.mCondition = jsonObject.getJSONArray("weather").getJSONObject(0).getInt("id");
            weatherD.mWeatherType = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
            weatherD.micon = updateWeatherIcon(weatherD.mCondition);
            double tempResult = jsonObject.getJSONObject("main").getDouble("temp") - 273.15;
            int roundedValue = (int) Math.rint(tempResult);
            weatherD.mTemperature = Integer.toString(roundedValue);
            return weatherD;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }


    private static String updateWeatherIcon(int condition)
    {
        if (condition >= 0 && condition <= 300) {
            return "pcp_thunderstrom1";
        } else if (condition >= 300 && condition <= 500) {
            return "pcp_lightrain";
        } else if (condition >= 500 && condition <= 600) {
            return "pcp_shower";
        } else if (condition >= 600 && condition <= 700) {
            return "pcp_snow2";
        } else if (condition >= 701 && condition <= 771) {
            return "pcp_fog";
        } else if (condition >= 772 && condition <= 800) {
            return "pcp_overcast";
        } else if (condition == 800) {
            return "pcp_sunny";
        } else if (condition >= 801 && condition <= 804) {
            return "pcp_cloudy";
        } else if (condition >= 900 && condition <= 902) {
            return "pcp_thunderstrom1";
        }
        if (condition == 903) {
            return "pcp_snow1";
        }
        if (condition == 904) {
            return "pcp_sunny";
        }
        if (condition >= 905 && condition <= 1000) {
            return "pcp_thunderstrom2";
        }

        return "dunno";
    }

    public String getmTemperature() {
        return mTemperature + "°C";
    }

    public String getMicon() {
        return micon;
    }

    public String getMcity() {
        return mcity;
    }

    public String getmWeatherType() {
        return mWeatherType;
    }
}
