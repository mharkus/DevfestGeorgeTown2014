package com.marctan.hellowatchface;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

public class ClockManager {
    private static final String TAG = ClockManager.class.getName();
    private static ClockManager instance;
    private TextView second;
    private TextView minutes;
    private TextView hour;
    private ViewGroup viewGroup;
    private Context context;
    private TextView divider;
    private boolean ambientMode;


    public static ClockManager getInstance(){
        if(instance == null){
            instance = new ClockManager();
        }

        return instance;
    }


    public void init(Context context, ViewGroup viewGroup){
        this.viewGroup = viewGroup;
        this.context = context;
        setupHour(viewGroup);
        setupMinutes(viewGroup);
        setupSeconds(viewGroup);
        setupDivider(viewGroup);

    }

    public void updateTime(){
        Calendar cal = Calendar.getInstance();
        updateHour(cal);
        updateMinutes(cal);

        if(!isAmbientMode()){
            updateSeconds(cal);
        }
    }

    private void updateSeconds(Calendar date) {
        int val = date.get(Calendar.SECOND);
        second.setText(padValue(val));

    }

    private void updateMinutes(Calendar date) {
        int val = date.get(Calendar.MINUTE);
        minutes.setText(padValue(val));

    }

    private void updateHour(Calendar date) {
        int val = date.get(Calendar.HOUR);
        hour.setText(String.valueOf(val));

    }

    private String padValue(int val){
        return val < 10 ? "0" + val : String.valueOf(val);
    }

    private void setupDivider(ViewGroup viewGroup) {
        divider = (TextView)viewGroup.findViewById(R.id.divider);
    }

    private void setupSeconds(ViewGroup viewGroup) {
        second = (TextView)viewGroup.findViewById(R.id.second);
    }

    private void setupMinutes(ViewGroup viewGroup) {
        minutes = (TextView)viewGroup.findViewById(R.id.minute);
    }

    private void setupHour(ViewGroup viewGroup) {
        hour = (TextView)viewGroup.findViewById(R.id.hour);
    }

    public void setAmbientMode(boolean ambientMode) {
        this.ambientMode = ambientMode;

        if(second != null){
            if(ambientMode){
                ObjectAnimator.ofFloat(second, "alpha", 1, 0).setDuration(300).start();
                viewGroup.setBackground(null);
                minutes.setTextColor(Color.LTGRAY);
                hour.setTextColor(Color.LTGRAY);
                divider.setTextColor(Color.LTGRAY);
            }else{
                ObjectAnimator.ofFloat(second, "alpha", 0, 1).setDuration(300).start();
                viewGroup.setBackgroundResource(R.drawable.fields);
                minutes.setTextColor(context.getResources().getColor(R.color.yellow));
                hour.setTextColor(context.getResources().getColor(R.color.red));
                divider.setTextColor(context.getResources().getColor(R.color.green));
            }
        }

    }

    public boolean isAmbientMode() {
        return ambientMode;
    }
}
