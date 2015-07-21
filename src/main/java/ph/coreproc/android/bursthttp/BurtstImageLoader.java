package ph.coreproc.android.bursthttp;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by ceosilvajr on 7/12/15.
 */
public class BurtstImageLoader {
    public static void loadImage(String imageUrl, Context context, ImageView mIVHatchImage, int logo, int logoError) {
        if (imageUrl.isEmpty()) {
            return;
        }
        Picasso.with(context).load(imageUrl).placeholder(logo).error(logoError).into(mIVHatchImage);
    }
}
