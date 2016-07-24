package lv.gdgriga.firebase.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;

import static android.graphics.BitmapFactory.decodeFile;
import static java.lang.Math.ceil;
import static java.lang.Math.max;

public class AttachmentDecoder {
    private static final int MAX_WIDTH = 640;
    private static final int MAX_HEIGHT = 480;

    public static Bitmap decodeBitmap(String filePath) {
        Options dimensions = getBitmapDimensions(filePath);
        int samples = (int) max(ceil((double) dimensions.outWidth / MAX_WIDTH), ceil((double) dimensions.outHeight / MAX_HEIGHT));
        return reducedBySamples(filePath, samples);
    }

    private static Options getBitmapDimensions(String filePath) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        decodeFile(filePath, options);
        return options;
    }

    private static Bitmap reducedBySamples(String filePath, int samples) {
        Options options = new Options();
        options.inSampleSize = samples;
        return decodeFile(filePath, options);
    }
}
