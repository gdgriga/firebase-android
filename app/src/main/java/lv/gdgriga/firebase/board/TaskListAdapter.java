package lv.gdgriga.firebase.board;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import lv.gdgriga.firebase.Task;

class TaskListAdapter extends ArrayAdapter<Task> {
    private List<Task> tasks;

    TaskListAdapter(Context context, int resource, List<Task> tasks) {
        super(context, resource, tasks);
        this.tasks = tasks;
        setNotifyOnChange(true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return new TaskViewBuilder(getContext(), parent, tasks.get(position)).buildView();
    }

    void refresh(List<Task> tasks) {
        clear();
        addAll(tasks);
    }
}
