package roy.NXT_Control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;

import roy.NXT_Control.BTConnection.BluetoothChatService;

public class  MainActivity extends AppCompatActivity implements FragCommunicator {
    ViewPager pager;
    TabLayout tabLayout;
    FragmentManager manager;
    PagerAdapter adapter;
    BluetoothAdapter btAdapter;
    BluetoothSocket socket;
    InputStream is;
    OutputStream os;

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


    public void sendBTChatService(BluetoothChatService chatService){
        //Intercept BluetoothChatService

        //Pass Bluetooth Stuff to DriveFragment
        DriveFragment driveFrag = (DriveFragment)manager.getFragments().get(1);
        driveFrag.receiveBTchat(chatService);
    }

    public void onBackPressed(){
        try {
            if (socket != null) {
                if (socket.isConnected()) {
                    socket.close();
                    is.close();
                    os.close();
                    btAdapter.disable();
                }
            }
            else{
                btAdapter = BluetoothAdapter.getDefaultAdapter();
                btAdapter.disable();
            }
            Toast.makeText(this,"Turning Off Bluetooth...",Toast.LENGTH_SHORT).show();
        }
        catch(Exception e){
            Toast.makeText(this,"Error: Failed to close Bluetooth",Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
