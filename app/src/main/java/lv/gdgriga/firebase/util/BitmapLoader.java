package lv.gdgriga.firebase.util;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import java8.util.Optional;

import static android.graphics.BitmapFactory.decodeStream;

class BitmapLoader {
    static Optional<Bitmap> loadFromUrl(String url) {
        InputStream stream = null;
        try {
            stream = new URL(url).openStream();
            return Optional.ofNullable(decodeStream(stream));
        } catch (Exception e) {
            logError(e);
            return Optional.empty();
        } finally {
            try {
                if (stream != null) stream.close();
            } catch (IOException e) {
                logError(e);
            }
        }
    }

    private static void logError(Exception e) {
        Log.e(BitmapLoader.class.getCanonicalName(), String.valueOf(e.getMessage()));
    }
}
