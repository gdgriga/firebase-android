package lv.gdgriga.firebase;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.util.Arrays.asList;
import static lv.gdgriga.firebase.Column.Done;
import static lv.gdgriga.firebase.Column.InProgress;
import static lv.gdgriga.firebase.Column.ToDo;
import static lv.gdgriga.firebase.TaskContainer.tasks;

public class BoardActivity extends AppCompatActivity {
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.container) ViewPager mViewPager;

    static {
        tasks.addAll(asList(new Task("A Task", "Do A", ToDo), new Task("B Task", "Do B", InProgress), new Task("C Task", "Do C", ToDo), new Task("Z Task", "Do Z", Done)));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        ColumnPagerAdapter columnPager = new ColumnPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(columnPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> new CreateTaskDialog(
            view.getContext(), (ColumnFragment) columnPager.getItem(mViewPager.getCurrentItem())
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
}
