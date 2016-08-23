package lv.gdgriga.firebase.board;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import lv.gdgriga.firebase.Column;
import lv.gdgriga.firebase.database.FirebaseDb;
import lv.gdgriga.firebase.user_management.GoogleUser;
import lv.gdgriga.firebase.util.Display;

import static android.view.DragEvent.ACTION_DRAG_LOCATION;
import static android.view.DragEvent.ACTION_DROP;
import static android.view.View.VISIBLE;
import static lv.gdgriga.firebase.R.id.column_label;
import static lv.gdgriga.firebase.R.id.task_list;
import static lv.gdgriga.firebase.R.layout.fragment_board;
import static lv.gdgriga.firebase.board.ColumnFlip.NONE;

public class ColumnFragment extends Fragment {
    private static final String ARG_COLUMN = "column";
    private Column column;
    @BindView(column_label) TextView columnLabel;
    @BindView(task_list) RecyclerView taskList;

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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        column = Column.fromInt(getArguments().getInt(ARG_COLUMN));
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        taskList.setLayoutManager(layoutManager);
        TaskViewAdapter adapter = new TaskViewAdapter(column);
        RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                taskList.scrollToPosition(positionStart);
            }
        };
        // TODO: Register the observer in the adapter
        // TODO: Set the adapter for the taskList
        columnLabel.setText(column.toString());
        view.setOnDragListener(this::onDrag);
    }

    private boolean onDrag(View view, DragEvent event) {
        switch (event.getAction()) {
            case ACTION_DRAG_LOCATION:
                switchColumnIfNeeded(view.getContext(), event.getX());
                break;
            case ACTION_DROP:
                Intent intent = event.getClipData().getItemAt(0).getIntent();
                switchTasksColumn(intent);
                updateKarma(intent);
                break;
        }
        return true;
    }

    private void switchColumnIfNeeded(Context context, float dragX) {
        int displayWidth = Display.getWidth(context);
        ColumnFlip columnFlip = ColumnFlip.fromRelativePosition(dragX / displayWidth);
        if (columnFlip != NONE) {
            ((BoardActivity) getActivity()).flipColumn(columnFlip);
        }
    }

    private void switchTasksColumn(Intent intent) {
        if (!column.name().equals(intent.getStringExtra("prevColumn"))) {
            FirebaseDb.changeTaskColumn(intent.getStringExtra("taskKey"), column.name());
        }
        getActivity().findViewById(intent.getIntExtra("viewId", -1)).setVisibility(VISIBLE);
    }

    private void updateKarma(Intent intent) {
        int diff = column.ordinal() - Column.valueOf(intent.getStringExtra("prevColumn")).ordinal();
        KarmaManager.updateUserKarma(GoogleUser.getUserId(), diff);
        ((BoardActivity) getActivity()).updateAvatar();
    }
}

