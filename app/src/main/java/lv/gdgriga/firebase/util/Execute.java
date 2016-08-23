package lv.gdgriga.firebase.util;

import android.content.Context;
import android.os.Handler;

public final class Execute {
    private final Context context;

    private Execute(Context context) {
        this.context = context;
    }

    public static Execute using(Context context) {
        return new Execute(context);
    }

    public void onUiThread(Runnable callback) {
        new Handler(context.getMainLooper()).post(callback);
    }
}
