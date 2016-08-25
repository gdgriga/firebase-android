package lv.gdgriga.firebase.user_management;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java8.util.Optional;
import lv.gdgriga.firebase.User;
import lv.gdgriga.firebase.database.FirebaseDb;

public class GoogleUser {
    static void saveIfNew() {
        FirebaseUser currentUser = getCurrentUser();
        FirebaseDb.getUserByKey(currentUser.getUid(), snapshot -> {
            if (snapshot.exists()) return;
            FirebaseDb.createUser(currentUser.getUid(), toDomainUser(currentUser));
        });
    }

    public static String getUserId() {
        return getCurrentUser().getUid();
    }

    public static User getSignedIn() {
        return toDomainUser(getCurrentUser());
    }

    private static FirebaseUser getCurrentUser() {
        return auth().getCurrentUser();
    }

    private static User toDomainUser(FirebaseUser user) {
        return new User(user.getDisplayName(), user.getEmail(), getAvatarFrom(user).orElse(null));
    }

    private static Optional<String> getAvatarFrom(FirebaseUser user) {
        return Optional.ofNullable(user.getPhotoUrl()).map(Uri::toString);
    }

    static void signOut() {
        auth().signOut();
    }

    private static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }
}
