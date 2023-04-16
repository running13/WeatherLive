package cafe.v50.weatherlive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

public class BatteryReceiver extends BroadcastReceiver {

    private final Handler updateHandler;

    public BatteryReceiver(Handler updateHandler) {
        this.updateHandler = updateHandler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_BATTERY_CHANGED)) {
            Log.d("BatteryReceiver", "onReceive: ");
            // set value
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            int batteryPct = level * 100 / scale;
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                    || status == BatteryManager.BATTERY_STATUS_FULL; // 判断是否正在充电
            processUpdateView(batteryPct, isCharging);
        }
    }

    private void processUpdateView(int batteryPct, boolean isCharge) {
        // 处理电池变化事件
        Message message = updateHandler.obtainMessage();

        message.obj = "实景天气\n钱塘江\n" + batteryPct + "%" + (isCharge ? "+" : "");
        // send
        updateHandler.sendMessage(message);
    }
}
