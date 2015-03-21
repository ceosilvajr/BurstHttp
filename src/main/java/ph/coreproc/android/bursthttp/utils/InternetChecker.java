package ph.coreproc.android.bursthttp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ceosilvajr on 3/22/15.
 */
public class InternetChecker {

    private Context context;

    public InternetChecker(Context context) {
        this.context = context;
    }

    public boolean isConnected() {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null
                    && activeNetwork.isConnectedOrConnecting();
        } catch (NullPointerException e) {
            // nothing
        }
        return true;
    }
}
