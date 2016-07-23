package lv.gdgriga.firebase;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;

import static lv.gdgriga.firebase.R.id.create_task_button;
import static lv.gdgriga.firebase.R.id.task_name;
import static lv.gdgriga.firebase.R.id.task_summary;
import static lv.gdgriga.firebase.R.layout.dialog_create_task;
import static lv.gdgriga.firebase.R.string.create_task;
import static lv.gdgriga.firebase.TaskContainer.tasks;

class CreateTaskDialog extends Dialog {
    @BindView(task_name) EditText taskName;
    @BindView(task_summary) EditText taskSummary;
    @BindView(create_task_button) Button createTask;
    private final ColumnFragment fragment;

    CreateTaskDialog(Context context, ColumnFragment fragment) {
        super(context);
        this.fragment = fragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(dialog_create_task);
        ButterKnife.bind(this);
        setTitle(create_task);
        createTask.setOnClickListener((view) -> {
            tasks.add(new Task(taskName.getText().toString(), taskSummary.getText().toString(), fragment.getColumn()));
            fragment.refresh();
            dismiss();
        });
    }
}
