package ph.coreproc.android.bursthttp;

import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

/**
 * Created by ceosilvajr on 7/12/15.
 */
public class BurtstImageLoader {
    public static void loadImage(String imageUrl, ImageView mIVHatchImage, int logo) {
        Ion.with(mIVHatchImage).placeholder(logo).load(imageUrl);
    }
}
