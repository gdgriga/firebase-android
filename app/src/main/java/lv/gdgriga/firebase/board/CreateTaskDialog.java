package lv.gdgriga.firebase.board;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lv.gdgriga.firebase.*;
import lv.gdgriga.firebase.board.BoardActivity.AttachmentSelectedListener;

import static android.R.layout.simple_spinner_dropdown_item;
import static android.R.layout.simple_spinner_item;
import static android.content.Intent.ACTION_PICK;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static android.support.v4.app.ActivityCompat.startActivityForResult;
import static lv.gdgriga.firebase.R.id.*;
import static lv.gdgriga.firebase.R.layout.dialog_create_task;
import static lv.gdgriga.firebase.R.string.create_task;
import static lv.gdgriga.firebase.TaskContainer.tasks;

class CreateTaskDialog extends Dialog implements AttachmentSelectedListener {
    static final int PICK_ATTACHMENT = 1337;
    private final BoardActivity parent;
    private final Column column;
    private final ColumnPagerAdapter columnPager;
    private final Task task = new Task();
    @BindView(task_name) EditText taskName;
    @BindView(assignee_spinner) Spinner assignee;
    @BindView(create_task_button) Button createTask;

    CreateTaskDialog(BoardActivity parent, Column column, ColumnPagerAdapter columnPager) {
        super(parent);
        this.parent = parent;
        this.column = column;
        this.columnPager = columnPager;
        parent.subscribeForAttachmentSelected(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(dialog_create_task);
        ButterKnife.bind(this);
        setTitle(create_task);
        setupAssigneeSpinner();
    }

    @OnClick(create_task_button)
    public void onCreateTaskClick() {
        task.title = taskName.getText().toString();
        task.assignee = (User) assignee.getSelectedItem();
        task.column = column;
        tasks.add(task);
        columnPager.notifyDataSetChanged();
        parent.unsubscribeFromAttachmentSelected(this);
        dismiss();
    }

    @OnClick(choose_attachment_button)
    public void onChooseAttachmentButtonClick() {
        startActivityForResult(parent, new Intent(ACTION_PICK, EXTERNAL_CONTENT_URI), PICK_ATTACHMENT, new Bundle());
    }

    private void setupAssigneeSpinner() {
        ArrayAdapter<User> dataAdapter = new ArrayAdapter<>(getContext(), simple_spinner_item, UserContainer.users);
        dataAdapter.setDropDownViewResource(simple_spinner_dropdown_item);
        assignee.setAdapter(dataAdapter);
    }

    @Override
    public void attachmentSelected(String attachment) {
        task.attachment = attachment;
    }
}
