package lv.gdgriga.firebase.board;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import butterknife.BindView;
import butterknife.ButterKnife;
import lv.gdgriga.firebase.Column;
import lv.gdgriga.firebase.Task;
import lv.gdgriga.firebase.User;
import lv.gdgriga.firebase.database.FirebaseDb;
import lv.gdgriga.firebase.storage.Storage;
import lv.gdgriga.firebase.util.Execute;
import lv.gdgriga.firebase.util.PathFromUriResolver;

import static android.content.Intent.ACTION_PICK;
import static android.graphics.BitmapFactory.decodeStream;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static lv.gdgriga.firebase.R.id.*;
import static lv.gdgriga.firebase.R.layout.activity_create_task;
import static lv.gdgriga.firebase.R.layout.spinner_item;
import static lv.gdgriga.firebase.database.Snapshot.toUsers;

public class CreateTaskActivity extends Activity {
    private static final int PICK_ATTACHMENT = 1337;
    private final Task task = new Task();
    private Column column;
    @BindView(task_title) EditText taskName;
    @BindView(assignee_spinner) Spinner assignee;
    @BindView(choose_attachment_button) ImageButton chooseAttachment;
    @BindView(create_task_button) Button createTask;
    @BindView(attachment_preview) ImageView attachmentPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_create_task);
        column = Column.fromInt(getIntent().getIntExtra("taskColumn", 0));
        ButterKnife.bind(this);
        setupAssigneeSpinner();
        chooseAttachment.setOnClickListener(this::onChooseAttachmentButtonClick);
        createTask.setOnClickListener(this::createTask);
    }

    private void createTask(View view) {
        task.title = taskName.getText().toString();
        task.assignee = ((User) assignee.getSelectedItem()).getKey();
        task.collection = column.name();
        FirebaseDb.createTask(task);
        finish();
    }

    private void onChooseAttachmentButtonClick(View view) {
        Intent intent = new Intent(ACTION_PICK, EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_ATTACHMENT);
        createTask.setEnabled(false);
    }

    private void setupAssigneeSpinner() {
        FirebaseDb.getAllUsers(snapshot -> {
            ArrayAdapter<User> dataAdapter = new ArrayAdapter<>(CreateTaskActivity.this, spinner_item, toUsers(snapshot));
            dataAdapter.setDropDownViewResource(spinner_item);
            assignee.setAdapter(dataAdapter);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK || requestCode != PICK_ATTACHMENT) return;
        String taskAttachment = PathFromUriResolver.fromContext(getBaseContext()).resolve(data.getData());
        Storage.uploadAttachment(taskAttachment, uri -> {
            task.attachment = uri;
            Storage.getAttachmentStream(uri, stream -> {
                Bitmap bitmap = decodeStream(stream);
                Execute.using(CreateTaskActivity.this)
                       .onUiThread(() -> {
                           attachmentPreview.setImageBitmap(bitmap);
                           createTask.setEnabled(true);
                       });
            });
        });
        super.onActivityResult(requestCode, resultCode, data);
    }
}
