package ph.coreproc.android.bursthttp;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Response;

/**
 * Interface for HTTP requests
 *
 * @author chrisbjr
 */
public interface BurstCallback {
    public void onStart();

    public void onFinish();

    public void onError(BurstError burstError);

    public void onSuccess(Response<JsonObject> response);
}
