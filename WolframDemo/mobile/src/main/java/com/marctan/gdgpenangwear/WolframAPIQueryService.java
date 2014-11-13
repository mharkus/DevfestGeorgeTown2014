package com.marctan.gdgpenangwear;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.marctan.gdgpenangwear.wolfram.APIHelper;
import com.marctan.gdgpenangwear.wolfram.Pod;
import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WolframAPIQueryService extends Service {
    private static final String TAG = WolframAPIQueryService.class.getName();
    private GoogleApiClient mGoogleApiClient;
    private ResultReceiver resultReceiver;
    private Node childNode;
    private CountDownLatch latch = new CountDownLatch(1);

    public WolframAPIQueryService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "service running....");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        // Now you can use the data layer API
                        latch.countDown();
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();

        resultReceiver = new ResultReceiver();
        IntentFilter filter = new IntentFilter("com.marctan.gdgpenangwear.RESULT");
        LocalBroadcastManager.getInstance(this).registerReceiver(resultReceiver, filter);

        mGoogleApiClient.connect();
        new CheckConnectedWearables().execute();

        return START_STICKY;
    }

    class CheckConnectedWearables extends AsyncTask<Void, Void, Node> {
        @Override
        protected Node doInBackground(Void... voids) {

            try {
                latch.await();

                NodeApi.GetConnectedNodesResult nodes =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                final List<Node> nodeList = nodes.getNodes();
                if (nodeList.size() > 0) {
                    return nodeList.get(0);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Node node) {
            super.onPostExecute(node);
            childNode = node;
        }
    }

    class ResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.hasExtra("error")){
                String errorMessage = intent.getStringExtra("error");
                final com.google.android.gms.common.api.PendingResult<MessageApi.SendMessageResult> pendingResult = Wearable.MessageApi.sendMessage(mGoogleApiClient, childNode.getId(), "/start/MainActivity", errorMessage.getBytes());
                pendingResult.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "ERROR: failed to send Message: " + sendMessageResult.getStatus());
                        }
                    }
                });
                return;
            }
            Pod pod = intent.getParcelableExtra("result");
            Log.d(TAG, "Got result from API call: " + pod.getTitle());


            if(pod.getData() != null && pod.getData().length() > 0){
                Log.d(TAG, "sending data");
                final com.google.android.gms.common.api.PendingResult<MessageApi.SendMessageResult> pendingResult = Wearable.MessageApi.sendMessage(mGoogleApiClient, childNode.getId(), "/start/MainActivity", pod.getData().getBytes());
                pendingResult.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "ERROR: failed to send Message: " + sendMessageResult.getStatus());
                        }
                    }
                });
            }else if(pod.getImage() != null){
                Log.d(TAG, "sending image");

                APIHelper.fetchBitmapFromURL(pod.getImage(), new AsyncCallback() {
                    @Override
                    public void onComplete(HttpResponse httpResponse) {
                        byte[] imageBytes = httpResponse.getBody();
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                        final Asset assetFromBitmap = createAssetFromBitmap(bitmap);

                        PutDataMapRequest map = PutDataMapRequest.create("/image");
                        map.getDataMap().putAsset("image", assetFromBitmap);
                        PutDataRequest request = map.asPutDataRequest();
                        final com.google.android.gms.common.api.PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(mGoogleApiClient, request);
                        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                            @Override
                            public void onResult(DataApi.DataItemResult dataItemResult) {
                                Log.d(TAG, "Sending image: " + dataItemResult.getStatus().toString());
                            }
                        });
                    }
                });
            }




        }
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(resultReceiver);
    }
}
