package lv.gdgriga.firebase;

import java.util.UUID;

public class Task {
    public final String id;
    public String title;
    public User assignee;
    public String attachment;
    public Column column;

    public Task() {
        id = UUID.randomUUID().toString();
    }

    Task(String title, User assignee, Column column) {
        this();
        this.title = title;
        this.assignee = assignee;
        this.column = column;
    }
}
