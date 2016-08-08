package lv.gdgriga.firebase.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import java8.util.Optional;

import static android.graphics.BitmapFactory.decodeStream;

class LoadBitmapTask extends AsyncTask<String, Void, Optional<Bitmap>> {
    @Override
    protected Optional<Bitmap> doInBackground(String... url) {
        try {
            return bitmapFromUrl(url[0]);
        } catch (Exception e) {
            Log.e(getClass().getCanonicalName(), String.valueOf(e.getMessage()));
            return Optional.empty();
        }
    }

    private Optional<Bitmap> bitmapFromUrl(String spec) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(spec).openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        Optional<Bitmap> bitmap = Optional.ofNullable(decodeStream(input));
        input.close();
        connection.disconnect();
        return bitmap;
    }
}