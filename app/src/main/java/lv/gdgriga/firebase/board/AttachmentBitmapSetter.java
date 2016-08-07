package lv.gdgriga.firebase.board;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import java8.util.Optional;

import static lv.gdgriga.firebase.util.AttachmentDecoder.decodeBitmap;

class AttachmentBitmapSetter extends AsyncTask<String, Void, Bitmap> {
    private final WeakReference<Resources> resourceRef;
    private final WeakReference<ImageView> imageViewRef;

    AttachmentBitmapSetter(Resources resources, ImageView imageView) {
        resourceRef = new WeakReference<>(resources);
        imageViewRef = new WeakReference<>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... attachment) {
        return decodeBitmap(attachment[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        Optional.ofNullable(imageViewRef.get()).ifPresent(view ->
            Optional.ofNullable(resourceRef.get()).ifPresent(resources ->
                view.setImageDrawable(new BitmapDrawable(resources, bitmap))));
    }
}
