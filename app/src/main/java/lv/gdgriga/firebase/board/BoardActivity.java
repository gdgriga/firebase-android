package lv.gdgriga.firebase.board;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lv.gdgriga.firebase.Column;
import lv.gdgriga.firebase.R;
import lv.gdgriga.firebase.util.PathFromUriResolver;

import static java8.util.stream.StreamSupport.stream;
import static lv.gdgriga.firebase.R.id.container;
import static lv.gdgriga.firebase.R.id.create_new_task_button;
import static lv.gdgriga.firebase.R.layout.activity_board;
import static lv.gdgriga.firebase.board.CreateTaskDialog.PICK_ATTACHMENT;

public class BoardActivity extends AppCompatActivity {
    interface AttachmentSelectedListener {
        void attachmentSelected(String attachment);
    }

    @BindView(container) ViewPager mViewPager;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(create_new_task_button) FloatingActionButton createNewTaskButton;
    private PathFromUriResolver resolver;
    private List<AttachmentSelectedListener> attachmentSelectedListeners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_board);
        ButterKnife.bind(this);

        resolver = PathFromUriResolver.fromContext(getBaseContext());
        setSupportActionBar(toolbar);

        ColumnPagerAdapter columnPager = new ColumnPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(columnPager);

        createNewTaskButton.setOnClickListener(view -> new CreateTaskDialog(
            this, Column.fromInt(mViewPager.getCurrentItem()), columnPager
        ).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_board, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != PICK_ATTACHMENT || resultCode != RESULT_OK) return;
        Uri selectedImageUri = data.getData();
        stream(attachmentSelectedListeners).forEach(listener -> listener.attachmentSelected(resolver.resolve(selectedImageUri)));
        super.onActivityResult(requestCode, resultCode, data);
    }

    void subscribeForAttachmentSelected(AttachmentSelectedListener listener) {
        attachmentSelectedListeners.add(listener);
    }

    void unsubscribeFromAttachmentSelected(AttachmentSelectedListener listener) {
        attachmentSelectedListeners.remove(listener);
    }
}
