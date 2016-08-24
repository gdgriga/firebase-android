package lv.gdgriga.firebase.util;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;

public interface ConnectionCallback extends GoogleApiClient.ConnectionCallbacks {
    @Override
    default void onConnectionSuspended(int i) {
        Log.e("Google Api Connection", "suspended.");
    }
}
