package com.marctan.gdgpenangwear;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.marctan.gdgpenangwear.wolfram.APIHelper;
import com.marctan.gdgpenangwear.wolfram.ExecuteQueryResponseCallback;
import com.marctan.gdgpenangwear.wolfram.Pod;

public class ExecuteQueryIntentService extends IntentService {
    private static final String TAG = ExecuteQueryIntentService.class.getName();
    private static final String ACTION_EXECUTE_QUERY = "com.marctan.gdgpenangwear.action.EXECUTE_QUERY";
    private static final String EXTRA_QUERY = "com.marctan.gdgpenangwear.extra.QUERY";
    public static final String ACTION_RESULT = "com.marctan.gdgpenangwear.RESULT";

    public static void startActionExecuteQuery(Context context, String param1) {
        Intent intent = new Intent(context, ExecuteQueryIntentService.class);
        intent.setAction(ACTION_EXECUTE_QUERY);
        intent.putExtra(EXTRA_QUERY, param1);
        context.startService(intent);
    }


    public ExecuteQueryIntentService() {
        super("ExecuteQueryIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_EXECUTE_QUERY.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_QUERY);
                handleActionExecuteQuery(param1);
            }
        }
    }

    private void handleActionExecuteQuery(String param1) {
        final Context context = this;
        APIHelper.executeQuery(this, param1, new ExecuteQueryResponseCallback() {
            @Override
            public void onSuccess(Pod pod) {
                Intent intent = new Intent(ACTION_RESULT);
                intent.putExtra("result", pod);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.d(TAG, errorMessage);
                Intent intent = new Intent(ACTION_RESULT);
                intent.putExtra("error", errorMessage);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });

    }


}
