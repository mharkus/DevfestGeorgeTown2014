package com.marctan.gdgpenangwear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;


public class Main extends Activity {

    private static final String TAG = Main.class.getName();
    public static final String ACTION_RESULT = "com.marctan.gdgpenangwear.RESULT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(this, WolframAPIQueryService.class));
    }

    public void showNotification(View b){
        Intent intent = new Intent(ACTION_RESULT);
        intent.putExtra("error", "magic");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


}
