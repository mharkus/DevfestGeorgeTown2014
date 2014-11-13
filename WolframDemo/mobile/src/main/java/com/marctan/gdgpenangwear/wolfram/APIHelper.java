package com.marctan.gdgpenangwear.wolfram;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.turbomanage.httpclient.AsyncCallback;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;
import com.turbomanage.httpclient.android.AndroidHttpClient;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class APIHelper {
    private static final String TAG = APIHelper.class.getName();

    public static void executeQuery(Context ctx, String query, final ExecuteQueryResponseCallback callback) {

        if (!isNetworkConnected(ctx)) {
            callback.onFailure("No Internet connection");
            return;
        }

        AndroidHttpClient httpClient = getHttpClient();
        ParameterMap params = httpClient.newParams()
                .add("input", query)
                .add("appid", APIConstants.APP_ID)
                .add("excludepodid", "Input");


        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(File.separator);
        urlBuilder.append(APIConstants.VERSION);
        urlBuilder.append(File.separator);
        urlBuilder.append(APIConstants.QUERY_COMMAND);
        urlBuilder.append("?");
        try {
            urlBuilder.append("input=" + URLEncoder.encode(query, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlBuilder.append("&appid=" + APIConstants.APP_ID);
        urlBuilder.append("&excludepodid=Input");
        urlBuilder.append("&excludepodid=InputValue");

        String url = urlBuilder.toString();

        httpClient.get(url, null, new AsyncCallback() {
            @Override
            public void onComplete(HttpResponse httpResponse) {
                if (httpResponse != null) {
                    try {
                        final List<Pod> pods = new WolframAPIParser().parse(new StringReader(httpResponse.getBodyAsString()));
                        if (pods.size() > 0) {
                            final Pod pod = pods.get(0);
                            callback.onSuccess(pod);

                            return;
                        }
                    } catch (XmlPullParserException e) {
                        Log.e(TAG, e.getMessage(), e);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }

                callback.onFailure("I cannot understand that statement.");
            }


            @Override
            public void onError(Exception e) {
                callback.onFailure(e.getMessage());
                Log.e(TAG, "error: " + e.getMessage(), e);

            }
        });
    }

    public static void fetchBitmapFromURL(String url, final AsyncCallback callback) {
        AndroidHttpClient httpClient = getHttpClient(url);
        httpClient.get("/", null, new AsyncCallback() {
            @Override
            public void onComplete(HttpResponse httpResponse) {
                callback.onComplete(httpResponse);
            }
        });
    }


    protected static AndroidHttpClient getHttpClient() {
        return getHttpClient(APIConstants.SERVER_URL);
    }

    protected static AndroidHttpClient getHttpClient(String url) {
        AndroidHttpClient httpClient = new AndroidHttpClient(url);
        httpClient.setConnectionTimeout(NetworkConstants.DEFAULT_NETWORK_TIMEOUT); // 2s
        return httpClient;
    }

    protected static boolean isNetworkConnected(Context ctx) {
        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

}
