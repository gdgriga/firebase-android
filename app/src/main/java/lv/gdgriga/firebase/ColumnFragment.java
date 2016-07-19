package lv.gdgriga.firebase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java8.util.stream.Collectors.toList;
import static java8.util.stream.StreamSupport.stream;
import static lv.gdgriga.firebase.R.id.section_label;
import static lv.gdgriga.firebase.R.id.task_list;
import static lv.gdgriga.firebase.R.layout.fragment_board;
import static lv.gdgriga.firebase.R.layout.fragment_task;
import static lv.gdgriga.firebase.R.string.section_format;

/**
 * A placeholder fragment containing a simple view.
 */
public class ColumnFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    @BindView(section_label) TextView sectionLabel;
    @BindView(task_list) ListView taskList;
    private List<Task> tasks = Arrays.asList(new Task("A Task", 1), new Task("B Task", 2), new Task("C Task", 1), new Task("Z Task", 3));

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    static ColumnFragment newInstance(int sectionNumber) {
        ColumnFragment fragment = new ColumnFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(fragment_board, container, false);
        ButterKnife.bind(this, rootView);
        int columnNumber = getArguments().getInt(ARG_SECTION_NUMBER);
        sectionLabel.setText(getString(section_format, columnNumber));
        taskList.setAdapter(new TaskListAdapter(taskList.getContext(), fragment_task, stream(tasks).filter(task -> task.column == columnNumber)
                                                                                                   .collect(toList())));
        return rootView;
    }
}

