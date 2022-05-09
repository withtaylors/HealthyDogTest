import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.myapplication.Guide1Fragment;
import com.example.myapplication.Guide2Fragment;

public class TabPagerAdapter extends FragmentStatePagerAdapter {

    // Count number of tabs
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0:
                Guide1Fragment tabFragment1 = new Guide1Fragment();
                return tabFragment1;
            case 1:
                Guide2Fragment tabFragment2 = new Guide2Fragment();
                return tabFragment2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}