package lv.gdgriga.firebase.remote_config;

import java8.util.function.Consumer;

public final class RemoteConfig {
    private RemoteConfig() {
        // TODO: Save FirebaseRemoteConfig instance to an instance field
        setup();
    }

    public static void fetchConfig(Consumer</*TODO: Replace with FirebaseRemoteConfig*/Object> onSuccess) {
        // TODO: Fetch config
    }

    private void setup() {
        // TODO: Perform setup
    }

    private void fetch(Consumer</*TODO: Replace with FirebaseRemoteConfig*/Object> onSuccess) {
        // TODO: Fetch the config
    }

    private long cacheExpiration() {
        // TODO: Define cache expiration time
        return 0L;
    }
}
