package lv.gdgriga.firebase.remote_config;

import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.util.HashMap;

import java8.util.function.Consumer;

public final class RemoteConfig {
    private final FirebaseRemoteConfig config;

    private RemoteConfig() {
        this.config = FirebaseRemoteConfig.getInstance();
        setup();
    }

    public static void fetchConfig(Consumer<FirebaseRemoteConfig> onSuccess) {
        new RemoteConfig().fetch(onSuccess);
    }

    private void setup() {
        FirebaseRemoteConfigSettings settings = new FirebaseRemoteConfigSettings.Builder()
            .setDeveloperModeEnabled(true)
            .build();

        config.setConfigSettings(settings);
        config.setDefaults(new HashMap<String, Object>() {{
            put("toolbar_color", "#303F9F");
        }});
    }

    private void fetch(Consumer<FirebaseRemoteConfig> onSuccess) {
        config.fetch(cacheExpiration())
              .addOnSuccessListener(nothing -> {
                  config.activateFetched();
                  onSuccess.accept(config);
              })
              .addOnFailureListener(error -> {
                  Log.e(getClass().getName(), error.getMessage());
                  onSuccess.accept(config);
              });
    }

    private long cacheExpiration() {
        return config.getInfo().getConfigSettings().isDeveloperModeEnabled() ? 0 : 3600;
    }
}
