package lv.gdgriga.firebase.board;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import java8.util.Optional;
import java8.util.function.Consumer;
import lv.gdgriga.firebase.Column;
import lv.gdgriga.firebase.Task;
import lv.gdgriga.firebase.User;
import lv.gdgriga.firebase.database.FirebaseDb;
import lv.gdgriga.firebase.storage.Storage;
import lv.gdgriga.firebase.util.AsyncBitmapLoader;
import lv.gdgriga.firebase.util.Execute;

import static android.content.ClipData.newIntent;
import static android.graphics.BitmapFactory.decodeStream;
import static android.view.View.INVISIBLE;

class TaskViewAdapter /* TODO: Extends FirebaseRecyclerAdapter<Task, TaskViewHolder> */ {
    TaskViewAdapter(Column column) {
        // TODO: Provide arguments to super constructor
    }

    //TODO: @Override
    protected void populateViewHolder(TaskViewHolder viewHolder, Task model, int position) {
        viewHolder.taskTitle.setText(model.title);
        Optional.ofNullable(model.assignee).ifPresent(setAssigneeAvatar(viewHolder));
        Optional.ofNullable(viewHolder.taskView.get()).ifPresent(taskView -> {
            if (model.attachment != null) {
                Storage.getAttachmentStream(model.attachment, stream -> {
                    Bitmap bitmap = decodeStream(stream);
                    Execute.using(taskView.getContext())
                           .onUiThread(() -> viewHolder.taskAttachment.setImageBitmap(bitmap));
                });
            }
            String taskKey =  /* TODO: Get task key*/ null;
            taskView.setOnLongClickListener((view) -> {
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                Intent switchColumn = new Intent();
                switchColumn.putExtra("taskKey", taskKey);
                switchColumn.putExtra("prevColumn", model.collection);
                switchColumn.putExtra("viewId", view.getId());
                view.startDrag(newIntent("switchColumn", switchColumn), shadowBuilder, view, 0);
                view.setVisibility(INVISIBLE);
                return true;
            });
            taskView.setOnClickListener((event) -> {
                Intent editTask = new Intent(taskView.getContext(), EditTaskActivity.class);
                editTask.putExtra("taskKey", taskKey);
                taskView.getContext().startActivity(editTask);
            });
        });
    }

    private Consumer<String> setAssigneeAvatar(TaskViewHolder viewHolder) {
        return key -> FirebaseDb.getUserByKey(key, snapshot -> {
            // TODO: Return if the snapshot doesn't exist
            User user = /*TODO: Convert snapshot to a User*/null;
            viewHolder.assignee.setText(user.toString());
            if (user.avatar != null) {
                AsyncBitmapLoader.loadFromUrl(user.avatar).ifPresent(bitmap ->
                    viewHolder.assigneeAvatar.setImageBitmap(bitmap));
            }
        });
    }
}
