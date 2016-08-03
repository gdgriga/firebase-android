package lv.gdgriga.firebase.board;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import lv.gdgriga.firebase.Task;

import static android.content.ClipData.newPlainText;
import static lv.gdgriga.firebase.R.id.task_attachment_image;
import static lv.gdgriga.firebase.R.id.task_title;
import static lv.gdgriga.firebase.R.layout.view_task;

class TaskViewBuilder {
    private final LayoutInflater layout;
    private final ViewGroup parent;
    private final Task task;
    @BindView(task_title) TextView taskTitle;
    @BindView(task_attachment_image) ImageView taskAttachment;

    TaskViewBuilder(Context context, ViewGroup parent, Task task) {
        layout = ((Activity) context).getLayoutInflater();
        this.parent = parent;
        this.task = task;
    }

    View buildView() {
        View taskView = layout.inflate(view_task, parent, false);
        ButterKnife.bind(this, taskView);
        taskTitle.setText(task.title);
        if (task.attachment != null) {
            new AttachmentBitmapSetter(taskView.getResources(), taskAttachment).execute(task.attachment);
        }
        taskView.setOnLongClickListener(this::onLongClick);
        return taskView;
    }

    private boolean onLongClick(View view) {
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
        view.startDrag(newPlainText("taskId", task.id), shadowBuilder, view, 0);
        view.setVisibility(View.INVISIBLE);
        return true;
    }
}
