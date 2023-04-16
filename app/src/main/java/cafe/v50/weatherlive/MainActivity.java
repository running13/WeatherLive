package cafe.v50.weatherlive;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerControlView;

import cafe.v50.weatherlive.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity  {
    private ActivityMainBinding binding;

    private ExoPlayer player;

    private final Handler locationUpdateTaskHandler = new Handler();

    private final Handler weatherUpdateUIHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            TextView view = findViewById(R.id.weather_textview);
            Log.d("weatherUpdateUIHandler", "handleMessage " + msg.obj);
            view.setText(String.valueOf(msg.obj));
        }
    };

    private final Handler batteryUpdateUIHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            TextView view = findViewById(R.id.battery_textview);
            Log.d("batteryUpdateUIHandler", "handleMessage " + msg.obj);
            view.setText(String.valueOf(msg.obj));
        }
    };

    private final BatteryReceiver batteryBroadcastReceiver = new BatteryReceiver(batteryUpdateUIHandler);;

    private final LocationListener locationListener = new LocationListener(weatherUpdateUIHandler);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set attr
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // init
        initPlayer();
        initLocationRequestTask();
        initBatteryReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
        unregisterReceiver(batteryBroadcastReceiver);
    }

    public void showControls(View view) {
        PlayerControlView controlView = findViewById(R.id.exo_controller);
        controlView.show();
    }

    private void initPlayer() {
        player = new ExoPlayer.Builder(this).build();
        PlayerControlView controlView = findViewById(R.id.exo_controller);
        controlView.setPlayer(player);
        SurfaceView surfaceView = findViewById(R.id.player_view);
        player.setVideoSurfaceView(surfaceView);
        MediaItem mediaItem = MediaItem.fromUri("");
        player.setMediaItem(mediaItem);
        player.setWakeMode(PowerManager.PARTIAL_WAKE_LOCK);
        player.setHandleAudioBecomingNoisy(false);
        player.prepare();
        player.play();
    }


    private void initLocationRequestTask() {
        LocationRequestTask locationRequestTask = new LocationRequestTask();
        locationRequestTask.handler = locationUpdateTaskHandler;
        locationRequestTask.activity = this;
        locationRequestTask.context = this;
        locationRequestTask.locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationRequestTask.listener = locationListener;
        locationUpdateTaskHandler.postDelayed(locationRequestTask, 10000);
    }

    private void initBatteryReceiver() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryBroadcastReceiver, intentFilter);
    }

    public void manualUpdateWeather(View view) {
        Log.d("MainActivity", "manualUpdateWeather: ");
        locationListener.update();
    }
}