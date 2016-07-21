package lv.gdgriga.firebase;

class Task {
    final String title;
    final String summary;
    final Column column;

    public Task(String title, String summary, Column column) {
        this.title = title;
        this.summary = summary;
        this.column = column;
    }
}
