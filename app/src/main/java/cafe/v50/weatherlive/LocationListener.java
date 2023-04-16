package cafe.v50.weatherlive;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationListener implements android.location.LocationListener {

    private final Handler weatherUpdateUIHandler;

    private double latitude;

    private double longitude;

    public LocationListener(Handler weatherUpdateUIHandler) {
        this.weatherUpdateUIHandler = weatherUpdateUIHandler;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        // 在这里处理获取到的经纬度信息
        Log.d("LocationListener", "Latitude: " + latitude + ", Longitude: " + longitude + ", Provider:" + location.getProvider());
        update();
    }

    public void update() {
        String latlon = String.format("%s,%s", longitude, latitude);
        String url = String.format("", latlon);
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                Log.d("LocationListener", "url " + url);
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("LocationListener", "resp " + responseBody);
                    JSONObject jsonObject = JSON.parseObject(responseBody);
                    JSONObject result = jsonObject.getJSONObject("result");
                    JSONObject realtime = result.getJSONObject("realtime");
                    long temp = realtime.getLong("temperature");
                    String skycon = realtime.getString("skycon");
                    String serverTime = DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()).toString();
                    String text = "" + skycon + "\n" + temp + "℃ \n" + serverTime;
                    Log.d("LocationListener", "txt " + responseBody);
                    Message message = weatherUpdateUIHandler.obtainMessage();
                    message.obj = text;
                    weatherUpdateUIHandler.sendMessage(message);
                    // 处理响应数据
                } else {
                    // 处理响应错误
                }
                response.close();
            } catch (Exception e) {
                Log.e("LocationListener", "okhttp: " + e);
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        android.location.LocationListener.super.onProviderEnabled(provider);
        Log.e("LocationListener", "Provider, enable" + provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Log.e("LocationListener", "Provider, disabled" + provider);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        android.location.LocationListener.super.onStatusChanged(provider, status, extras);
        Log.e("LocationListener", "onStatusChanged " + provider + " " + status + " " + extras);
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {
        android.location.LocationListener.super.onLocationChanged(locations);
        Log.e("LocationListener", "onLocationChanged " + JSON.toJSONString(locations));
    }
}
