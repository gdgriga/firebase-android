package lv.gdgriga.firebase;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import static lv.gdgriga.firebase.R.id.task_title;
import static lv.gdgriga.firebase.R.layout.fragment_task;

class TaskListAdapter extends ArrayAdapter<Task> {
    private final LayoutInflater layout;
    private List<Task> tasks;

    TaskListAdapter(Context context, int resource, List<Task> tasks) {
        super(context, resource, tasks);
        layout = ((Activity) context).getLayoutInflater();
        this.tasks = tasks;
        setNotifyOnChange(true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View taskView = layout.inflate(fragment_task, parent, false);
        TextView taskTitle = (TextView) taskView.findViewById(task_title);
        taskTitle.setText(tasks.get(position).title);
        return taskView;
    }

    void refresh(List<Task> tasks) {
        clear();
        addAll(tasks);
    }
}
