package lv.gdgriga.firebase.board;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lv.gdgriga.firebase.Column;
import lv.gdgriga.firebase.Task;
import lv.gdgriga.firebase.util.Display;

import static android.view.DragEvent.ACTION_DRAG_LOCATION;
import static android.view.DragEvent.ACTION_DROP;
import static java8.util.stream.Collectors.toList;
import static java8.util.stream.StreamSupport.stream;
import static lv.gdgriga.firebase.R.id.column_label;
import static lv.gdgriga.firebase.R.id.task_list;
import static lv.gdgriga.firebase.R.layout.fragment_board;
import static lv.gdgriga.firebase.R.layout.view_task;
import static lv.gdgriga.firebase.TaskContainer.tasks;
import static lv.gdgriga.firebase.board.ColumnFlip.NONE;

public class ColumnFragment extends Fragment {
    private static final String ARG_COLUMN = "column";
    private Column column;
    @BindView(column_label) TextView columnLabel;
    @BindView(task_list) ListView taskList;

    static ColumnFragment newInstance(Column column) {
        ColumnFragment fragment = new ColumnFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN, column.ordinal());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(fragment_board, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        column = Column.fromInt(getArguments().getInt(ARG_COLUMN));
        taskList.setAdapter(new TaskListAdapter(view.getContext(), view_task, thisColumnTasks()));
        columnLabel.setText(column.toString());
        view.setOnDragListener(this::onDrag);
    }

    private boolean onDrag(View view, DragEvent event) {
        switch (event.getAction()) {
            case ACTION_DRAG_LOCATION:
                switchColumnIfNeeded(view.getContext(), event.getX());
                break;
            case ACTION_DROP:
                switchTasksColumn(event.getClipData().getItemAt(0).getText());
                break;
        }
        return true;
    }

    private void switchTasksColumn(CharSequence draggedTaskId) {
        stream(tasks).filter(t -> t.id.equals(draggedTaskId)).findFirst().ifPresent(task -> {
            task.column = column;
        });
        ((BoardActivity) getActivity()).updateColumns();
    }

    private void switchColumnIfNeeded(Context context, float dragX) {
        int displayWidth = Display.getWidth(context);
        ColumnFlip columnFlip = ColumnFlip.fromRelativePosition(dragX / displayWidth);
        if (columnFlip != NONE) {
            ((BoardActivity) getActivity()).flipColumn(columnFlip);
        }
    }

    private List<Task> thisColumnTasks() {
        return stream(tasks).filter(task -> task.column == column)
                            .collect(toList());
    }
}

