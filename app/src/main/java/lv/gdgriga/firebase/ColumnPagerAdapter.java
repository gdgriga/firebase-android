package lv.gdgriga.firebase;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import static java8.util.J8Arrays.stream;
import static java8.util.stream.Collectors.toList;

class ColumnPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragments = stream(Column.values()).map(ColumnFragment::newInstance).collect(toList());

    ColumnPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return Column.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Column.fromInt(position).toString();
    }
}