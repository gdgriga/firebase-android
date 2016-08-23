package lv.gdgriga.firebase;

public class Task {
    public String title;
    public String assignee;
    public String attachment;
    public String collection;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getCollection() {
        return collection;
    }

    public void setCollection(String collection) {
        this.collection = collection;
    }

    public Task copy() {
        Task copy = new Task();
        copy.title = title;
        copy.assignee = assignee;
        copy.attachment = attachment;
        copy.collection = collection;
        return copy;
    }
}
