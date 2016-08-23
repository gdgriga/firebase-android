package lv.gdgriga.firebase.util;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java8.util.Optional;

class LoadBitmapTask extends AsyncTask<String, Void, Optional<Bitmap>> {
    @Override
    protected Optional<Bitmap> doInBackground(String... url) {
        return BitmapLoader.loadFromUrl(url[0]);
    }
}