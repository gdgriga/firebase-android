package lv.gdgriga.firebase.database;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

import lv.gdgriga.firebase.User;

public final class Snapshot {
    public static List<User> toUsers(DataSnapshot dataSnapshot) {
        ArrayList<User> users = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            User user = snapshot.getValue(User.class);
            user.setKey(snapshot.getKey());
            users.add(user);
        }
        return users;
    }
}
