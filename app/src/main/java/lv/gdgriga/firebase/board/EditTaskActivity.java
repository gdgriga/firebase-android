package lv.gdgriga.firebase.board;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.*;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import java8.util.Optional;
import lv.gdgriga.firebase.Task;
import lv.gdgriga.firebase.User;
import lv.gdgriga.firebase.database.FirebaseDb;
import lv.gdgriga.firebase.storage.Storage;
import lv.gdgriga.firebase.util.Execute;
import lv.gdgriga.firebase.util.PathFromUriResolver;

import static android.content.Intent.ACTION_PICK;
import static android.graphics.BitmapFactory.decodeStream;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static lv.gdgriga.firebase.R.id.*;
import static lv.gdgriga.firebase.R.layout.activity_edit_task;
import static lv.gdgriga.firebase.R.layout.spinner_item;
import static lv.gdgriga.firebase.database.Snapshot.toUsers;

public class EditTaskActivity extends Activity {
    private static final int PICK_ATTACHMENT = 1488;
    private String taskKey;
    private Task task;
    @BindView(task_title) TextView taskTitle;
    @BindView(assignee_spinner) Spinner assignees;
    @BindView(task_attachment) ImageView taskAttachment;
    @BindView(choose_attachment_button) ImageButton chooseAttachment;
    @BindView(delete_attachment_button) Button deleteAttachmentButton;
    @BindView(delete_task_button) Button deleteTask;
    @BindView(apply_changes_button) Button applyChanges;
    private volatile boolean deleteAttachment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_edit_task);
        taskKey = getIntent().getStringExtra("taskKey");
        ButterKnife.bind(this);
        FirebaseDb.getTaskByKey(taskKey, snapshot -> {
            task = /*TODO: Convert snapshot to task*/null;
            taskTitle.setText(task.title);
            setupUserSpinner(task.assignee);
            if (task.attachment != null) {
                showAttachment(task.attachment);
            }
            if (task.attachment == null) chooseAttachment.setVisibility(VISIBLE);
            else deleteAttachmentButton.setVisibility(VISIBLE);
            chooseAttachment.setOnClickListener(view -> {
                Intent intent = new Intent(ACTION_PICK, EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_ATTACHMENT);
            });
            deleteAttachmentButton.setOnClickListener(view -> {
                deleteAttachment = true;
                taskAttachment.setImageBitmap(null);
                deleteAttachmentButton.setVisibility(GONE);
                chooseAttachment.setVisibility(VISIBLE);
            });
            deleteTask.setOnClickListener(view -> {
                if (task.attachment != null) Storage.deleteAttachment(task.attachment);
                FirebaseDb.deleteTask(taskKey);
                finish();
            });
            applyChanges.setOnClickListener(view -> {
                task.title = taskTitle.getText().toString();
                task.assignee = ((User) assignees.getSelectedItem()).getKey();
                if (deleteAttachment) {
                    Storage.deleteAttachment(task.attachment);
                    task.attachment = null;
                }
                FirebaseDb.updateTask(taskKey, task);
                finish();
            });
        });
    }

    private void showAttachment(String attachment) {
        Storage.getAttachmentStream(attachment, stream -> {
            Bitmap bitmap = decodeStream(stream);
            Execute.using(EditTaskActivity.this)
                   .onUiThread(() -> taskAttachment.setImageBitmap(bitmap));
        });
    }

    private void setupUserSpinner(String assignee) {
        FirebaseDb.getAllUsers(snapshot -> {
            List<User> users = toUsers(snapshot);
            ArrayAdapter<User> dataAdapter = new ArrayAdapter<>(EditTaskActivity.this, spinner_item, users);
            dataAdapter.setDropDownViewResource(spinner_item);
            assignees.setAdapter(dataAdapter);
            Optional.ofNullable(assignee).ifPresent(asgn -> {
                for (int i = 0; i < users.size(); i++) {
                    if (asgn.equals(users.get(i).getKey())) {
                        assignees.setSelection(i);
                        break;
                    }
                }
            });
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || requestCode != PICK_ATTACHMENT) return;
        String path = PathFromUriResolver.fromContext(getBaseContext()).resolve(data.getData());
        Storage.uploadAttachment(path, this::onAttachmentUploaded);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void onAttachmentUploaded(String uri) {
        if (deleteAttachment) {
            Storage.deleteAttachment(task.attachment);
            deleteAttachment = false;
        }
        task.attachment = uri;
        showAttachment(uri);
        chooseAttachment.setVisibility(GONE);
        deleteAttachmentButton.setVisibility(VISIBLE);
    }
}
