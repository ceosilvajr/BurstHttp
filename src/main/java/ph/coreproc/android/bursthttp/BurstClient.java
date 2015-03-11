package ph.coreproc.android.bursthttp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chrisbjr on 10/1/14.
 */
public class BurstClient {

    private static final String TAG = "HttpClient";
    private static final int TIMEOUT = 100000;

    private Context mContext;
    private BurstCallback mBurstCallback;

    // Authorization headers
    private boolean mIsAuthorizationEnabled = false;
    private String mApiKey = "";
    private String mAuthorizationKey = "Authorization";
    private Future<Response<JsonObject>> mIonRequest;

    public BurstClient(Context context) {
        mContext = context;
    }

    public void get(String url, HashMap<String, String> params,
                    BurstCallback burstCallback) {

        mBurstCallback = burstCallback;

        mBurstCallback.onStart();

        // Build the URL
        if (params != null) {
            url += "?" + getQueryString(params);
        }

        // Let's validate
        if (!validate(url)) {
            return;
        }

        Log.i(TAG, "GET " + url);

        // Now we can get it
        if (mIsAuthorizationEnabled) {
            mIonRequest = Ion.with(mContext).load(url).setHeader(mAuthorizationKey, mApiKey)
                    .setTimeout(TIMEOUT).asJsonObject().withResponse()
                    .setCallback(new GenericFutureCallback());
        } else {
            mIonRequest = Ion.with(mContext).load(url).setTimeout(TIMEOUT).asJsonObject()
                    .withResponse().setCallback(new GenericFutureCallback());
        }

    }

    public void post(String url, HashMap<String, String> params,
                     BurstCallback burstCallback) {

        mBurstCallback = burstCallback;

        mBurstCallback.onStart();

        // Let's validate
        if (!validate(url)) {
            return;
        }

        Log.i(TAG, "POST " + url);

        // Modify params because koush is an ass :p
        // https://github.com/koush/ion/issues/200
        Map<String, List<String>> newParams = new HashMap<String, List<String>>();
        if (params != null) {
            for (ConcurrentHashMap.Entry<String, String> entry : params
                    .entrySet()) {
                newParams.put(entry.getKey(), Arrays.asList(entry.getValue()));
            }
        }

        if (mIsAuthorizationEnabled) {
            mIonRequest = Ion.with(mContext).load(url).setHeader(mAuthorizationKey, mApiKey)
                    .setTimeout(TIMEOUT).setBodyParameters(newParams)
                    .asJsonObject().withResponse()
                    .setCallback(new GenericFutureCallback());
        } else {
            mIonRequest = Ion.with(mContext).load(url).setTimeout(TIMEOUT)
                    .setBodyParameters(newParams).asJsonObject().withResponse()
                    .setCallback(new GenericFutureCallback());
        }

    }

    public void post(String url, JsonObject params,
                     BurstCallback burstCallback) {

        mBurstCallback = burstCallback;

        mBurstCallback.onStart();

        // Let's validate
        if (!validate(url)) {
            return;
        }

        Log.i(TAG, "POST " + url);

        if (mIsAuthorizationEnabled) {
            mIonRequest = Ion.with(mContext).load(url).setHeader(mAuthorizationKey, mApiKey)
                    .setTimeout(TIMEOUT).setJsonObjectBody(params)
                    .asJsonObject().withResponse()
                    .setCallback(new GenericFutureCallback());
        } else {
            mIonRequest = Ion.with(mContext).load(url).setTimeout(TIMEOUT)
                    .setJsonObjectBody(params).asJsonObject().withResponse()
                    .setCallback(new GenericFutureCallback());
        }
    }

    public void post(String url, BurstCallback burstCallback) {

        mBurstCallback = burstCallback;

        mBurstCallback.onStart();

        // Let's validate
        if (!validate(url)) {
            return;
        }

        Log.i(TAG, "POST " + url);

        if (mIsAuthorizationEnabled) {
            mIonRequest = Ion.with(mContext).load(url).setHeader(mAuthorizationKey, mApiKey)
                    .setTimeout(TIMEOUT).asJsonObject().withResponse()
                    .setCallback(new GenericFutureCallback());
        } else {
            mIonRequest = Ion.with(mContext).load(url).setTimeout(TIMEOUT).asJsonObject()
                    .withResponse().setCallback(new GenericFutureCallback());
        }
    }

    public void uploadRecording(HashMap<String, String> params, File file,
                                BurstCallback burstCallback) {

        mBurstCallback = burstCallback;

        mBurstCallback.onStart();

        String url = "http://api.pinoykaraokestar.com/v1/recordings";

        mIonRequest = Ion.with(mContext).load(url).setHeader(mAuthorizationKey, mApiKey)
                .setTimeout(TIMEOUT)
                .setMultipartParameter("song_id", params.get("song_id"))
                .setMultipartParameter("score", params.get("score"))
                .setMultipartFile("recording", file).asJsonObject()
                .withResponse().setCallback(new GenericFutureCallback());

    }

    public void put(String url, JsonObject params,
                    BurstCallback burstCallback) {

        mBurstCallback = burstCallback;

        mBurstCallback.onStart();

        // Let's validate
        if (!validate(url)) {
            return;
        }

        Log.i(TAG, "PUT " + url);

        if (mIsAuthorizationEnabled) {
            mIonRequest = Ion.with(mContext).load("PUT", url)
                    .setHeader(mAuthorizationKey, mApiKey).setTimeout(TIMEOUT)
                    .setJsonObjectBody(params).asJsonObject().withResponse()
                    .setCallback(new GenericFutureCallback());
        } else {
            mIonRequest = Ion.with(mContext).load(url).setTimeout(TIMEOUT)
                    .setJsonObjectBody(params).asJsonObject().withResponse()
                    .setCallback(new GenericFutureCallback());
        }
    }

    public void delete(String url, BurstCallback burstCallback) {

        mBurstCallback = burstCallback;

        mBurstCallback.onStart();

        // Let's validate
        if (!validate(url)) {
            return;
        }

        Log.i(TAG, "DELETE " + url);

        if (mIsAuthorizationEnabled) {
            mIonRequest = Ion.with(mContext).load("DELETE", url)
                    .setHeader(mAuthorizationKey, mApiKey).setTimeout(TIMEOUT)
                    .asJsonObject().withResponse()
                    .setCallback(new GenericFutureCallback());
        } else {
            mIonRequest = Ion.with(mContext).load(url).setTimeout(TIMEOUT).asJsonObject()
                    .withResponse().setCallback(new GenericFutureCallback());
        }
    }

    public void setAuthorization(String apiKey) {
        mApiKey = apiKey;
        mIsAuthorizationEnabled = true;
    }

    public void setAuthorization(String apiKey, String key) {
        mApiKey = apiKey;
        mAuthorizationKey = key;
        mIsAuthorizationEnabled = true;
    }

    private boolean validate(String url) {
        if (!isConnected()) {
            Log.e(TAG, "The device is not connected to the internet");
            mBurstCallback.onFinish();
            mBurstCallback.onError(BurstError.noInternetConnection());
            return false;
        }

        // Validate url
        if (!isUrlValid(url)) {
            Log.e(TAG, "The URL provided is invalid");
            mBurstCallback.onFinish();
            mBurstCallback.onError(BurstError.invalidUrl());
            return false;
        }

        return true;
    }

    private boolean isConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null
                    && activeNetwork.isConnectedOrConnecting();
        } catch (NullPointerException e) {
            // nothing
        }
        return true;
    }

    private String getQueryString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : params.entrySet()) {
            if (result.length() > 0)
                result.append("&");
            result.append(entry.getKey());
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue()).replace("+",
                    "%20"));
        }

        return result.toString();
    }

    private boolean isUrlValid(String url) {
        try {
            URL assignUrl = new URL(url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Invalid URL: " + url);
            return false;
        }
        return true;
    }

    private class GenericFutureCallback implements
            FutureCallback<Response<JsonObject>> {

        @Override
        public void onCompleted(Exception e, Response<JsonObject> result) {

            if (e != null) {
                Log.e(TAG, "Error: " + e.getMessage());
                BurstError burstError = new BurstError();
                burstError.setMessage(e.getMessage());
                if (result != null) {
                    burstError.setHttpCode(result.getHeaders().code());
                } else {
                    burstError
                            .setHttpCode(BurstError.INTERNET_UNAVAILABLE);
                }
                burstError.setCode("ION-EXCEPTION");
                mBurstCallback.onError(burstError);
                mBurstCallback.onFinish();
                return;
            }

            int responseCode = result.getHeaders().code();

            if (responseCode < 200 || responseCode > 299) {
                // response error
                Log.e(TAG, "Unsuccessful HTTP call: " + responseCode);
                // Log.d(TAG, result.getResult().toString());
                mBurstCallback.onFinish();
                mBurstCallback.onError(new BurstError(result));
                return;
            }

            // Now we have a success
            Log.i(TAG, "Successful HTTP call: " + responseCode);

            mBurstCallback.onSuccess(result);

            mBurstCallback.onFinish();
        }

    }

    public void cancel() {
        mIonRequest.cancel();
    }

}
