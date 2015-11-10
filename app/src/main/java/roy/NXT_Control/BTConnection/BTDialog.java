package roy.NXT_Control.BTConnection;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import roy.NXT_Control.MainActivity;
import roy.NXT_Control.R;

public class BTDialog extends AppCompatActivity{

    private BluetoothAdapter btAdapter;
    ListView lv_deviceList;
    ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
    BTDeviceListAdapter btDeviceListAdapter;
    BluetoothSocket socket;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_device_connect);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!btAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            startActivityForResult(intent, 1000);
        }

        Set<BluetoothDevice> btDevices = btAdapter.getBondedDevices();

        deviceList.addAll(btDevices);
        btDeviceListAdapter = new BTDeviceListAdapter(this,deviceList);

        lv_deviceList = (ListView)findViewById(R.id.lv_devices);
        lv_deviceList.setAdapter(btDeviceListAdapter);

        //Tap - Connect to a device
        lv_deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Connect to device
                if (btAdapter.isDiscovering())
                    btAdapter.cancelDiscovery();
                final BluetoothDevice deviceSelected = (BluetoothDevice) btDeviceListAdapter.getItem(position);

                Intent returnIntent = new Intent();
                returnIntent.putExtra("device", deviceSelected);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        //Scan Button
        Button scanButton = (Button)findViewById(R.id.btn_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceList.clear();
                btDeviceListAdapter.notifyDataSetChanged();
                btAdapter.startDiscovery();
            }
        });
    }

    public void onBackPressed(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("device",0);
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
    }
}
