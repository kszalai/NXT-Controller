package roy.NXT_Control;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter  extends FragmentStatePagerAdapter{

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = ConnectionFragment.newInstance(position);
                break;
            case 1:
                fragment = DirectionalDriveFragment.newInstance(position);
                break;
            case 2:
                fragment = SensorFragment.newInstance(position);
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String sectionTitle = " ";
        switch (position){
            case 0:
                sectionTitle = "Connection";
                break;
            case 1:
                sectionTitle = "Drive";
                break;
            case 2:
                sectionTitle ="Sensors";
                break;
        }
        return sectionTitle;
    }
}
