package lv.gdgriga.firebase.util;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

import static android.content.Context.WINDOW_SERVICE;

public final class Display {
    public static int getWidth(Context context) {
        WindowManager window = (WindowManager) context.getSystemService(WINDOW_SERVICE);
        Point dimensions = new Point();
        window.getDefaultDisplay().getSize(dimensions);
        return dimensions.x;
    }
}
