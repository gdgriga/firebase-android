package lv.gdgriga.firebase.database;

import com.google.firebase.database.*;

import java8.util.function.Consumer;
import lv.gdgriga.firebase.Task;
import lv.gdgriga.firebase.User;

public final class FirebaseDb {
    private static final String tasks = "tasks";
    private static final String users = "users";
    private static final String collection = "collection";

    public static Query getTasksFor(String collectionName) {
        return db().child(tasks)
                   .orderByChild(collection).equalTo(collectionName);
    }

    public static void getUserByKey(String key, Consumer<DataSnapshot> onValue) {
        db().child(users).child(key).addListenerForSingleValueEvent(new OnSingleValue(onValue));
    }

    public static void createUser(String uid, User user) {
        db().child(users).child(uid).setValue(user);
    }

    public static void getAllUsers(Consumer<DataSnapshot> onValue) {
        db().child(users).orderByValue().addListenerForSingleValueEvent(new OnSingleValue(onValue));
    }

    public static void changeTaskColumn(String draggedTaskKey, String newColumn) {
        db().child(tasks).child(draggedTaskKey).child(collection).setValue(newColumn);
    }

    public static void createTask(Task task) {
        db().child(tasks).push().setValue(task);
    }

    public static void deleteTask(String taskKey) {
        db().child(tasks).child(taskKey).removeValue();
    }

    public static void updateTask(String taskKey, Task task) {
        db().child(tasks).child(taskKey).setValue(task);
    }

    public static void getTaskByKey(String taskKey, Consumer<DataSnapshot> onValue) {
        db().child(tasks).child(taskKey).addListenerForSingleValueEvent(new OnSingleValue(onValue));
    }

    public static void updateUser(String userKey, User user) {
        db().child(users).child(userKey).setValue(user);
    }

    private static DatabaseReference db() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
