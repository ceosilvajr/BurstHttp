package ph.coreproc.android.bursthttp;

import java.io.File;

/**
 * Created by ceosilvajr on 3/22/15.
 */
public interface BurstFileCallBack {

    public void onStart();

    public void onProgress(long downloaded, long total);

    public void onSuccess(File result);

    public void onError(BurstError burstError);

    public void onFinish();
}
