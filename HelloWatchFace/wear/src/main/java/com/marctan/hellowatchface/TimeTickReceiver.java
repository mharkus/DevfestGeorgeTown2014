package com.marctan.hellowatchface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeTickReceiver extends BroadcastReceiver {
    private static final String TAG = TimeTickReceiver.class.getName();

    public TimeTickReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ClockManager.getInstance().updateTime();
    }
}
