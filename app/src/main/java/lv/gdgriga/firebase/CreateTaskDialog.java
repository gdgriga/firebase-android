package lv.gdgriga.firebase;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.*;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.R.layout.simple_spinner_item;
import static lv.gdgriga.firebase.R.id.assignee_spinner;
import static lv.gdgriga.firebase.R.id.create_task_button;
import static lv.gdgriga.firebase.R.id.task_name;
import static lv.gdgriga.firebase.R.layout.dialog_create_task;
import static lv.gdgriga.firebase.R.string.create_task;
import static lv.gdgriga.firebase.TaskContainer.tasks;

class CreateTaskDialog extends Dialog {
    @BindView(task_name) EditText taskName;
    @BindView(assignee_spinner) Spinner assignee;
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
        setupAssigneeSpinner();
        createTask.setOnClickListener((view) -> {
            tasks.add(new Task(
                taskName.getText().toString(),
                (User) assignee.getSelectedItem(),
                "",
                fragment.getColumn()));
            fragment.refresh();
            dismiss();
        });
    }

    private void setupAssigneeSpinner() {
        ArrayAdapter<User> dataAdapter = new ArrayAdapter<>(getContext(), simple_spinner_item, UserContainer.users);
        dataAdapter.setDropDownViewResource(simple_spinner_dropdown_item);
        assignee.setAdapter(dataAdapter);
    }
}
