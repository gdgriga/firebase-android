package lv.gdgriga.firebase.board;

import lv.gdgriga.firebase.User;
import lv.gdgriga.firebase.database.FirebaseDb;

final class KarmaManager {
    static void updateUserKarma(String userKey, int diff) {
        FirebaseDb.getUserByKey(userKey, snapshot -> {
            User user = snapshot.getValue(User.class);
            int newValue = user.getKarma() + diff;
            if (newValue < 0) newValue = -1;
            if (newValue > 10) newValue = 10;
            user.setKarma(newValue);
            FirebaseDb.updateUser(userKey, user);
        });
    }
}
