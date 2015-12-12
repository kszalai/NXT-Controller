package roy.NXT_Control;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

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
                Intent intent = new Intent(this,EditPreferences.class);
                startActivity(intent);
                break;
            case R.id.action_about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("NXT Control v1.0");
                builder.setMessage(
                        "Lego Mindstorm NXT Controller for Android\n" +
                                "\n" +
                                "Created by Kyle Szalai, Brandon Beals, & Brianna Wurtsmith\n" +
                                "for COSC 426 at Eastern Michigan University\n" +
                                "\n" +
                                "© Kyle Szalai, Brandon Beals, Brianna Wurtsmith 2015"
                );
                builder.create();
                builder.show();
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
