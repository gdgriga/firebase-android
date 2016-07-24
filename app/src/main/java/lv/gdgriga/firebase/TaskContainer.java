package lv.gdgriga.firebase;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static lv.gdgriga.firebase.Column.*;
import static lv.gdgriga.firebase.UserContainer.users;

public final class TaskContainer {
    public static final List<Task> tasks = new ArrayList<>(asList(
        new Task("A Task", users.get(0), Backlog),
        new Task("B Task", users.get(2), Sprint),
        new Task("C Task", users.get(3), InProgress),
        new Task("Z Task", users.get(1), Done)));
}
