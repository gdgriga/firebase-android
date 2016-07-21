package lv.gdgriga.firebase;

class Task {
    final String title;
    final String summary;
    final int column;

    public Task(String title, String summary, int column) {
        this.title = title;
        this.summary = summary;
        this.column = column;
    }
}
