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

    public static void getUserByKey(String key, Consumer<Object /*TODO: Replace with DataSnapshot*/> onValue) {
        // TODO: Get User by given key
    }

    public static void createUser(String uid, User user) {
        // TODO: Create a new user
    }

    public static void getAllUsers(Consumer<Object /*TODO: Replace with DataSnapshot*/> onValue) {
        // TODO: Get all users
    }

    public static void changeTaskColumn(String draggedTaskKey, String newCollection) {
        // TODO: Update task's collection field
    }

    public static void createTask(Task task) {
        // TODO: Create a new task
    }

    public static void deleteTask(String taskKey) {
        // TODO: Delete task with given key
    }

    public static void updateTask(String taskKey, Task task) {
        // TODO: Update task with given key
    }

    public static void getTaskByKey(String taskKey, Consumer<Object /*TODO: Replace with DataSnapshot*/> onValue) {
        // TODO: Get task with given key
    }

    public static void updateUser(String userKey, User user) {
        // TODO: Update User
    }

    private static DatabaseReference db() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
