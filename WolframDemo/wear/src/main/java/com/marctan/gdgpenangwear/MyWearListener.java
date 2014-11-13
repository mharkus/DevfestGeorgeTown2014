package com.marctan.gdgpenangwear;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

public class MyWearListener extends WearableListenerService {

    private static final String TAG = MyWearListener.class.getName();

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String message = new String(messageEvent.getData());
        if(message.equals("magic")){
            Intent lollipopIntent = new Intent(this, LollipopActivity.class);
            lollipopIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(lollipopIntent);
            return;
        }

        Log.d(TAG, "Message received! " + message);
        Intent intent = new Intent("com.marctan.gdgpenangwear.RESULT");
        intent.putExtra("result", message);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals("/image")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset profileAsset = dataMapItem.getDataMap().getAsset("image");
                Intent intent = new Intent("com.marctan.gdgpenangwear.IMAGE_RESULT");
                intent.putExtra("result", profileAsset);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
        }
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
