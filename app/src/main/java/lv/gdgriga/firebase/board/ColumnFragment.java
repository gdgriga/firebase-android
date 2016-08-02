package lv.gdgriga.firebase.board;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lv.gdgriga.firebase.Column;
import lv.gdgriga.firebase.Task;

import static java8.util.stream.Collectors.toList;
import static java8.util.stream.StreamSupport.stream;
import static lv.gdgriga.firebase.R.id.column_label;
import static lv.gdgriga.firebase.R.id.task_list;
import static lv.gdgriga.firebase.R.layout.fragment_board;
import static lv.gdgriga.firebase.R.layout.view_task;
import static lv.gdgriga.firebase.TaskContainer.tasks;

public class ColumnFragment extends Fragment {
    private static final String ARG_COLUMN = "column";

    @BindView(column_label) TextView columnLabel;
    @BindView(task_list) ListView taskList;

    private Column column;

    static ColumnFragment newInstance(Column column) {
        ColumnFragment fragment = new ColumnFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN, column.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(fragment_board, container, false);
        ButterKnife.bind(this, rootView);
        column = Column.fromInt(getArguments().getInt(ARG_COLUMN));
        taskList.setAdapter(new TaskListAdapter(container.getContext(), view_task, thisColumnTasks()));
        columnLabel.setText(column.toString());
        return rootView;
    }

    private List<Task> thisColumnTasks() {
        return stream(tasks).filter(task -> task.column == column)
                            .collect(toList());
    }
}

