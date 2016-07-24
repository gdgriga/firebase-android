package lv.gdgriga.firebase.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class PathFromUriResolver {
    private static final String[] projection = {MediaStore.Images.Media.DATA};
    private static final String selection = null;
    private static final String[] selectionArgs = null;
    private static final String sortOrder = null;

    private final Context context;

    private PathFromUriResolver(Context context) {
        this.context = context;
    }

    public static PathFromUriResolver fromContext(Context context) {
        return new PathFromUriResolver(context);
    }

    public String resolve(Uri uri) {
        Cursor cursor = context.getContentResolver()
                               .query(uri, projection, selection, selectionArgs, sortOrder);
        cursor.moveToFirst();
        try {
            return cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        } finally {
            cursor.close();
        }
    }
}

