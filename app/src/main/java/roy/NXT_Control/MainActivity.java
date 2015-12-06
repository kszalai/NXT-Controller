package roy.NXT_Control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;

import roy.NXT_Control.BTConnection.BluetoothChatService;

public class  MainActivity extends AppCompatActivity implements FragCommunicator {
    ViewPager pager;
    TabLayout tabLayout;
    FragmentManager manager;
    PagerAdapter adapter;
    BluetoothChatService BTChatService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pager = (ViewPager) findViewById(R.id.view_pager);
        tabLayout= (TabLayout) findViewById(R.id.tab_layout);

        manager = getSupportFragmentManager();
        adapter = new PagerAdapter(manager);
        pager.setAdapter(adapter);

        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        pager.setOffscreenPageLimit(3);

        tabLayout.setTabsFromPagerAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.action_preferences:
                break;
            case R.id.action_about:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendBTChatService(BluetoothChatService chatService){
        //Intercept BluetoothChatService
        BTChatService = chatService;

        //Pass Bluetooth Stuff to DirectionalDriveFragment
        DirectionalDriveFragment driveFrag = (DirectionalDriveFragment)manager.getFragments().get(1);
        driveFrag.receiveBTchat(chatService);
    }

    public void onBackPressed(){
        try {
            if (BTChatService != null) {
                if (BTChatService.getState() == BluetoothChatService.STATE_CONNECTED
                        || BTChatService.getState() == BluetoothChatService.STATE_CONNECTING) {
                    BTChatService.stop();
                }
            }
        }
        catch(Exception e){
            Toast.makeText(this,"Error: Failed to close Bluetooth Communications",Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
