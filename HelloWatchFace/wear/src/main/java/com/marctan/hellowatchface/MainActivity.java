package com.marctan.hellowatchface;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.Display;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements DisplayManager.DisplayListener{

    private static final String TAG = MainActivity.class.getName();
    private TimeTickReceiver timeTickReceiver;
    private ScheduledExecutorService scheduleTaskExecutor;
    private ScheduledFuture<?> scheduledFuture;
    private DisplayManager displayManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        displayManager.registerDisplayListener(this, null);

        timeTickReceiver = new TimeTickReceiver();
        scheduleTaskExecutor = Executors.newSingleThreadScheduledExecutor();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        final Context context = this;
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                ClockManager.getInstance().init(context, stub);
            }
        });

        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(timeTickReceiver, filter);
    }


    private void updateEverySecond() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }

        ClockManager.getInstance().setAmbientMode(false);

        scheduledFuture = scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {

            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ClockManager.getInstance().updateTime();
                    }
                });

            }
        }, 0, 1, TimeUnit.SECONDS);
    }


    private void updateEveryMinute() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }

        ClockManager.getInstance().setAmbientMode(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduleTaskExecutor.shutdown();
        unregisterReceiver(timeTickReceiver);
        displayManager.unregisterDisplayListener(this);


    }

    @Override
    public void onDisplayAdded(int i) {

    }

    @Override
    public void onDisplayRemoved(int i) {

    }

    @Override
    public void onDisplayChanged(int displayId) {
        switch(displayManager.getDisplay(displayId).getState()){
            case Display.STATE_OFF:
            case Display.STATE_DOZING:
                updateEveryMinute();
                break;
            default:
                updateEverySecond();
                break;
        }
    }
}
