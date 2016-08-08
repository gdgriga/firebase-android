package lv.gdgriga.firebase.util;

import android.graphics.Bitmap;
import android.util.Log;

import java8.util.Optional;

public class BitmapLoader {
    public static Optional<Bitmap> loadFromUrl(String bitmapUrl) {
        try {
            return new LoadBitmapTask().execute(bitmapUrl).get();
        } catch (Exception e) {
            Log.e(BitmapLoader.class.getCanonicalName(), e.getMessage());
            return Optional.empty();
        }
    }
}
