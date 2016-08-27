package lv.gdgriga.firebase.board;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import lv.gdgriga.firebase.MotherKaliStartedPartyDarklyException;
import lv.gdgriga.firebase.R;
import lv.gdgriga.firebase.User;
import lv.gdgriga.firebase.analytics.Analytics;
import lv.gdgriga.firebase.database.FirebaseDb;
import lv.gdgriga.firebase.invites.InvitationsActivity;
import lv.gdgriga.firebase.remote_config.RemoteConfig;
import lv.gdgriga.firebase.user_management.GoogleUser;
import lv.gdgriga.firebase.util.AsyncBitmapLoader;

import static android.graphics.BitmapFactory.decodeResource;
import static android.graphics.Color.parseColor;
import static java.lang.System.currentTimeMillis;
import static lv.gdgriga.firebase.R.id.*;
import static lv.gdgriga.firebase.R.layout.activity_board;
import static lv.gdgriga.firebase.R.menu.menu_board;
import static lv.gdgriga.firebase.board.ColumnFlip.RIGHT;

public class BoardActivity extends AppCompatActivity {
    public static final int flipDelay = 500;
    @BindView(container) ViewPager mViewPager;
    @BindView(toolbar_widget) Toolbar toolbar;
    @BindView(create_new_task_button) FloatingActionButton createNewTaskButton;
    @BindView(avatar) ImageButton avatarImage;
    private long lastFlip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_board);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ColumnPagerAdapter columnPager = new ColumnPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(columnPager);
        mViewPager.setOffscreenPageLimit(3);

        createNewTaskButton.setOnClickListener(view -> {
            Intent createTask = new Intent(this, CreateTaskActivity.class);
            createTask.putExtra("taskColumn", mViewPager.getCurrentItem());
            startActivity(createTask);
        });

        avatarImage.setOnClickListener(this::onAvatarClick);
        updateAvatar();
        Analytics.userOpenedApp(this);
    }

    private void onAvatarClick(View view) {
        PopupMenu popup = new PopupMenu(this, avatarImage);
        popup.getMenuInflater().inflate(menu_board, popup.getMenu());
        popup.setOnMenuItemClickListener(this::onMenuItemClicked);
        popup.show();
    }

    private boolean onMenuItemClicked(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case sign_out_menu:
                setResult(RESULT_OK);
                finish();
                break;
            case fetch_config:
                RemoteConfig.fetchConfig(config ->
                    toolbar.setBackgroundColor(parseColor(config.getString("toolbar_color"))));
                break;
            case invite_menu:
                startActivity(new Intent(this, InvitationsActivity.class));
                break;
            case crash_menu:
                throw new MotherKaliStartedPartyDarklyException("That's all, folks!");
        }
        return true;
    }

    public void updateAvatar() {
        FirebaseDb.getUserByKey(GoogleUser.getUserId(), snapshot -> {
            User user = snapshot.getValue(User.class);
            if (user.getKarma() == -1) avatarImage.setImageBitmap(decodeResource(getResources(), R.drawable.shame));
            else if (user.getKarma() == 10) avatarImage.setImageBitmap(decodeResource(getResources(), R.drawable.glory));
            else AsyncBitmapLoader.loadFromUrl(GoogleUser.getSignedIn().avatar).ifPresent(avatarImage::setImageBitmap);
        });
    }

    void flipColumn(ColumnFlip columnFlip) {
        if (currentTimeMillis() - lastFlip < flipDelay) return;
        int nextColumn = mViewPager.getCurrentItem() + (columnFlip == RIGHT ? 1 : -1);
        mViewPager.setCurrentItem(nextColumn);
        lastFlip = currentTimeMillis();
    }
}
