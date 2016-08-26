package lv.gdgriga.firebase.notifications;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;

public class InstanceIdService extends FirebaseInstanceIdService {
    private static final String topic = "board_updates";

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("Token", token);
        FirebaseMessaging.getInstance().subscribeToTopic(topic);
    }
}
