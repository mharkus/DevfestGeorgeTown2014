package com.marctan.gdgpenangwear;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

public class MyListener extends WearableListenerService {

    private static final String TAG = MyListener.class.getName();


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String message = new String(messageEvent.getData());
        Log.d(TAG, "Message received!" + message);

        ExecuteQueryIntentService.startActionExecuteQuery(getApplicationContext(), message);
    }

    @Override
    public void onPeerConnected(Node peer) {
        Log.d(TAG, "Peer connected:" + peer.getId());
    }

    @Override
    public void onPeerDisconnected(Node peer) {
        Log.d(TAG, "Peer disconnected: " + peer.getId());
    }
}