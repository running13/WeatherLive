package cafe.v50.weatherlive;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class LocationRequestTask implements Runnable {

    public LocationManager locationManager;

    public LocationListener listener;

    public Context context;

    public Activity activity;

    public Handler handler;

    @Override
    public void run() {
        // 检查是否有权限访问位置信息
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 请求位置更新
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 10.0f, listener);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000L, 10.0f, listener);
        } else {
            // 没有权限，请求权限
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        Log.e("LocationRequestTask", "run: ");
        // set next
        handler.postDelayed(this, 1800000);
    }
}
