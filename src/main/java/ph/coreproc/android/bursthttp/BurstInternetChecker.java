package ph.coreproc.android.bursthttp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by ceosilvajr on 7/21/15.
 */
public class BurstInternetChecker {

    private static final String TAG = "BurstInternetChecker";
    private Context mContext;

    public BurstInternetChecker(Context mContext) {
        this.mContext = mContext;
    }

    public boolean isConnectedToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
            }
        }
        Log.e(TAG, "The device is not connected to the internet");
        return false;
    }

}
