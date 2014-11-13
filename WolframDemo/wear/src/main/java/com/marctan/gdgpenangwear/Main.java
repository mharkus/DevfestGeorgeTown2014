package com.marctan.gdgpenangwear;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Main extends Activity {

    private static final String TAG = Main.class.getName();
    public static final String ACTION_TEXT_RESULT = "com.marctan.gdgpenangwear.RESULT";
    public static final String ACTION_IMAGE_RESULT = "com.marctan.gdgpenangwear.IMAGE_RESULT";
    private GoogleApiClient mGoogleApiClient;
    private CountDownLatch latch = new CountDownLatch(1);
    private Node parentNode;
    private ResultReceiver resultReceiver;
    private Button askButton;
    private AnimatorSet animSet;
    private boolean isQuerying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                askButton = (Button) stub.findViewById(R.id.askButton);
                setupButtonAnimation();
            }
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        latch.countDown();
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                        latch = new CountDownLatch(1);
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

        IntentFilter filter = new IntentFilter(ACTION_TEXT_RESULT);
        filter.addAction(ACTION_IMAGE_RESULT);
        resultReceiver = new ResultReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(resultReceiver, filter);

    }

    private void setupButtonAnimation() {
        final ObjectAnimator scaleX = ObjectAnimator.ofFloat(askButton, "scaleX", 1, 1.2f);
        final ObjectAnimator scaleY = ObjectAnimator.ofFloat(askButton, "scaleY", 1, 1.2f);

        scaleX.setDuration(600);
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.setRepeatMode(ObjectAnimator.REVERSE);

        scaleY.setDuration(600);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatMode(ObjectAnimator.REVERSE);

        animSet = new AnimatorSet();
        animSet.play(scaleX).with(scaleY);
    }


    class ResultReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(isQuerying){
                String action = intent.getAction();

                if (action.equals(ACTION_TEXT_RESULT)) {
                    String result = intent.getStringExtra("result");
                    Intent resultIntent = new Intent(Main.this, ResultActivity.class);
                    resultIntent.putExtra("result", result);
                    startActivity(resultIntent);

                } else if (action.equals(ACTION_IMAGE_RESULT)) {
                    Asset asset = intent.getParcelableExtra("result");
                    new LoadBitmapFromAssetTask().execute(asset);
                }

                askButton.setText("Ask");
                askButton.setEnabled(true);
                animSet.cancel();
                isQuerying = false;
            }
        }
    }

    class LoadBitmapFromAssetTask extends AsyncTask<Asset, Void, Bitmap>{

        public LoadBitmapFromAssetTask() {

        }

        @Override
        protected Bitmap doInBackground(Asset... assets) {
            Asset asset = assets[0];
            return loadBitmapFromAsset(asset);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Intent resultIntent = new Intent(Main.this, ResultActivity.class);
            resultIntent.putExtra("image", bitmap);
            startActivity(resultIntent);

            //Wearable.DataApi.deleteDataItems(mGoogleApiClient, Uri.parse("/image"));
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();

        new CheckConnectedWearables().execute();
    }

    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, 0);
    }


    class CheckConnectedWearables extends AsyncTask<Void, Void, Node> {
        @Override
        protected Node doInBackground(Void... voids) {

            try {
                latch.await();

                NodeApi.GetConnectedNodesResult nodes =
                        Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                if (nodes.getNodes().size() > 0) {
                    return nodes.getNodes().get(0);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Node node) {
            super.onPostExecute(node);
            parentNode = node;
        }
    }

    public void saySomething(View v) {
        if(isQuerying){
            isQuerying = false;
            askButton.setText("Ask");
            animSet.cancel();
        }else{
            displaySpeechRecognizer();
        }

    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            sendMessage(spokenText);

            isQuerying = true;
            askButton.setText("Cancel");
            animSet.start();
        }
    }

    private void sendMessage(String message) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                String message = params[0];
                Log.d(TAG, "sending message to " + parentNode.getId());
                final PendingResult<MessageApi.SendMessageResult> pendingResult = Wearable.MessageApi
                        .sendMessage(mGoogleApiClient, parentNode.getId(), "/start/MainActivity/", message.getBytes());

                pendingResult.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                    @Override
                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                        if (!sendMessageResult.getStatus().isSuccess()) {
                            Log.e(TAG, "ERROR: failed to send Message: " + sendMessageResult.getStatus());
                        }else{
                            Log.v(TAG, sendMessageResult.getStatus().toString());
                        }
                    }
                });
                return null;
            }
        }.execute(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mGoogleApiClient.disconnect();

        if (resultReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(resultReceiver);
        }
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mGoogleApiClient.blockingConnect(10000, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }

        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }

        return BitmapFactory.decodeStream(assetInputStream);
    }

}
