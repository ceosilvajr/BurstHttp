package ph.coreproc.android.bursthttp;

import android.content.Context;
import android.util.Log;
import android.webkit.URLUtil;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.body.FilePart;
import com.koushikdutta.async.http.body.Part;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.Response;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import ph.coreproc.android.bursthttp.utils.InternetChecker;

/**
 * Created by chrisbjr on 10/1/14.
 */
public class BurstClient {

    private static final String TAG = "HttpClient";
    private static final int TIMEOUT = 100000;

    private InternetChecker internetChecker;

    private Context mContext;
    private BurstCallback mBurstCallback;
    private BurstFileCallBack mBurstFileCallBack;

    // Authorization headers
    private boolean mIsAuthorizationEnabled = false;
    private String mApiKey = "";
    private String mAuthorizationKey = "";
    private Future<Response<JsonObject>> mIonRequest;
    private Future<File> mIonFileRequest;

    public BurstClient(Context context) {
        mContext = context;
        internetChecker = new InternetChecker(context);
    }

    public void setAuthorization(String apiKey, String key) {
        mApiKey = apiKey;
        mAuthorizationKey = key;
        mIsAuthorizationEnabled = true;
    }

    public void get(String url, HashMap<String, String> params,
                    BurstCallback burstCallback) {

        mBurstCallback = burstCallback;

        mBurstCallback.onStart();

        if (!internetChecker.isConnected()) {
            Log.e(TAG, "The device is not connected to the internet");
            mBurstCallback.onFinish();
            mBurstCallback.onError(BurstError.noInternetConnection());
            return;
        }

        if (params != null) {
            url += "?" + getQueryString(params);
        }

        // Let's validate the url
        if (!isUrlValid(url)) {
            return;
        }

        Log.i(TAG, "GET " + url);

        if (mIsAuthorizationEnabled) {
            mIonRequest = Ion.with(mContext).load(url).setHeader(mAuthorizationKey, mApiKey)
                    .setTimeout(TIMEOUT).asJsonObject().withResponse()
                    .setCallback(new GenericFutureCallback());
        } else {
            mIonRequest = Ion.with(mContext).load(url).setTimeout(TIMEOUT).asJsonObject()
                    .withResponse().setCallback(new GenericFutureCallback());
        }

    }

    public void postWithFile(String url, HashMap<String, String> params, HashMap<String, File> files,
                             BurstCallback burstCallback) {

        mBurstCallback = burstCallback;

        mBurstCallback.onStart();

        if (!internetChecker.isConnected()) {
            Log.e(TAG, "The device is not connected to the internet");
            mBurstCallback.onFinish();
            mBurstCallback.onError(BurstError.noInternetConnection());
            return;
        }

        // Let's validate the url
        if (!isUrlValid(url)) {
            return;
        }

        Log.i(TAG, "POST " + url);

        Map<String, List<String>> newParams = new HashMap<>();
        if (params != null) {
            for (ConcurrentHashMap.Entry<String, String> entry : params
                    .entrySet()) {
                newParams.put(entry.getKey(), Arrays.asList(entry.getValue()));
            }
        }

        List<Part> data = new ArrayList<>();

        for (String s : files.keySet()) {
            if (files.get(s) != null) {
                FilePart filePart = new FilePart(s, files.get(s));
                data.add(filePart);
            }
        }
        if (mIsAuthorizationEnabled) {
            mIonRequest = Ion.with(mContext).load(url).setHeader(mAuthorizationKey, mApiKey)
                    .setTimeout(TIMEOUT).setMultipartParameters(newParams).addMultipartParts(data)
                    .asJsonObject().withResponse()
                    .setCallback(new GenericFutureCallback());
        } else {
            mIonRequest = Ion.with(mContext).load(url).setTimeout(TIMEOUT)
                    .setMultipartParameters(newParams).asJsonObject().withResponse()
                    .setCallback(new GenericFutureCallback());
        }
    }

    public void post(String url, HashMap<String, String> params,
                     BurstCallback burstCallback) {

        mBurstCallback = burstCallback;

        mBurstCallback.onStart();

        if (!internetChecker.isConnected()) {
            Log.e(TAG, "The device is not connected to the internet");
            mBurstCallback.onFinish();
            mBurstCallback.onError(BurstError.noInternetConnection());
            return;
        }

        // Let's validate the url
        if (!isUrlValid(url)) {
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

        if (!internetChecker.isConnected()) {
            Log.e(TAG, "The device is not connected to the internet");
            mBurstCallback.onFinish();
            mBurstCallback.onError(BurstError.noInternetConnection());
            return;
        }

        // Let's validate the url
        if (!isUrlValid(url)) {
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

        if (!internetChecker.isConnected()) {
            Log.e(TAG, "The device is not connected to the internet");
            mBurstCallback.onFinish();
            mBurstCallback.onError(BurstError.noInternetConnection());
            return;
        }

        // Let's validate the url
        if (!isUrlValid(url)) {
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

    public void put(String url, JsonObject params,
                    BurstCallback burstCallback) {

        mBurstCallback = burstCallback;

        mBurstCallback.onStart();

        if (!internetChecker.isConnected()) {
            Log.e(TAG, "The device is not connected to the internet");
            mBurstCallback.onFinish();
            mBurstCallback.onError(BurstError.noInternetConnection());
            return;
        }

        // Let's validate the url
        if (!isUrlValid(url)) {
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

        if (!internetChecker.isConnected()) {
            Log.e(TAG, "The device is not connected to the internet");
            mBurstCallback.onFinish();
            mBurstCallback.onError(BurstError.noInternetConnection());
            return;
        }

        // Let's validate the url
        if (!isUrlValid(url)) {
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

    public void downloadFile(String url, final File zipFile, BurstFileCallBack burstFileCallBack) {

        mBurstFileCallBack = burstFileCallBack;

        mBurstFileCallBack.onStart();

        if (!internetChecker.isConnected()) {
            Log.e(TAG, "The device is not connected to the internet");
            mBurstFileCallBack.onFinish();
            mBurstFileCallBack.onError(BurstError.noInternetConnection());
            return;
        }

        // Let's validate the url
        if (!isUrlValid(url)) {
            return;
        }

        Log.i(TAG, "DOWNLOAD FILE " + url);

        if (mIsAuthorizationEnabled) {
            mIonFileRequest = Ion.with(mContext).load(url)
                    .setHeader(mAuthorizationKey, mApiKey).setTimeout(TIMEOUT)
                    .progress(new ProgressCallback() {
                        @Override
                        public void onProgress(long downloaded,
                                               long total) {
                            mBurstFileCallBack.onProgress(downloaded, total);
                        }
                    }).write(zipFile)
                    .setCallback(new FileFutureCallback());
        } else {
            mIonFileRequest = Ion.with(mContext).load(url)
                    .setHeader(mAuthorizationKey, mApiKey).setTimeout(TIMEOUT)
                    .progress(new ProgressCallback() {
                        @Override
                        public void onProgress(long downloaded,
                                               long total) {
                            mBurstFileCallBack.onProgress(downloaded, total);
                        }
                    }).write(zipFile)
                    .setCallback(new FileFutureCallback());
        }

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
        if (!URLUtil.isValidUrl(url)) {
            Log.e(TAG, "Invalid URL: " + url);
            mBurstCallback.onFinish();
            mBurstCallback.onError(BurstError.invalidUrl());
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

    private class FileFutureCallback implements FutureCallback<File> {

        @Override
        public void onCompleted(Exception e, File result) {
            if (e != null) {
                Log.e(TAG, "Error: " + e.getMessage());
                BurstError burstError = new BurstError();
                burstError.setMessage(e.getMessage());
                burstError.setCode("ION-EXCEPTION");
                mBurstFileCallBack.onError(burstError);
                mBurstFileCallBack.onFinish();
                return;
            }
            if (result == null) {
                mBurstFileCallBack.onError(BurstError.failedToDownloadFile());
                mBurstFileCallBack.onFinish();
                return;
            }
            Log.d(TAG, "File Size " + result.getTotalSpace() + " File name " + result.getName());
            mBurstFileCallBack.onSuccess(result);
            mBurstFileCallBack.onFinish();
        }
    }


    public void cancel() {
        mIonRequest.cancel();
    }

}
