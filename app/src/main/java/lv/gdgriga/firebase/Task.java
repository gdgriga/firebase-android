package lv.gdgriga.firebase;

class Task {
    final String title;
    final User assignee;
    final String attachment;
    final Column column;

    Task(String title, User assignee, String attachment, Column column) {
        this.title = title;
        this.assignee = assignee;
        this.attachment = attachment;
        this.column = column;
    }
}
