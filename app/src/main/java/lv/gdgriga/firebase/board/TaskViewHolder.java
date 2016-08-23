package lv.gdgriga.firebase.board;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

import static lv.gdgriga.firebase.R.id.*;

class TaskViewHolder extends RecyclerView.ViewHolder {
    @BindView(task_title) TextView taskTitle;
    @BindView(task_attachment_image) ImageView taskAttachment;
    @BindView(task_assignee) TextView assignee;
    @BindView(assignee_avatar) ImageView assigneeAvatar;
    WeakReference<View> taskView;

    TaskViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
        view.setId((int) Math.floor(0xffff + Math.random() * 0xfff));
        taskView = new WeakReference<>(view);
    }
}
