package lv.gdgriga.firebase.user_management;

import com.google.firebase.auth.FirebaseAuth;

import lv.gdgriga.firebase.User;

public class GoogleUser {
    static void saveIfNew() {
        // TODO: persist user
    }

    public static String getUserId() {
        // TODO: return user id
        return null;
    }

    public static User getSignedIn() {
        // TODO: map google user to domain user
        return null;
    }

    static void signOut() {
        auth().signOut();
    }

    private static FirebaseAuth auth() {
        return FirebaseAuth.getInstance();
    }
}
