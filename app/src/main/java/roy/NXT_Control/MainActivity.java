package roy.NXT_Control;

import android.app.FragmentTransaction;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Don't use this class, it sets up the tabs and is not used for any coding that I am aware of
 */

public class MainActivity extends AppCompatActivity implements FragCommunicator {
    ViewPager pager;
    TabLayout tabLayout;
    FragmentManager manager;
    PagerAdapter adapter;
    ConnectionFragment connectFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout= (TabLayout) findViewById(R.id.tab_layout);

        manager = getSupportFragmentManager();
        adapter = new PagerAdapter(manager);
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setTabsFromPagerAdapter(adapter);
    }

    public void moveMotor(byte[] leftMotor, byte[] rightMotor){
        connectFrag.moveMotor(leftMotor,rightMotor);
    }
}
