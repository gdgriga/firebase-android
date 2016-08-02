package lv.gdgriga.firebase;

public class Task {
    public String title;
    public User assignee;
    public String attachment;
    public Column column;

    public Task() {
    }

    Task(String title, User assignee, Column column) {
        this.title = title;
        this.assignee = assignee;
        this.column = column;
    }
}
