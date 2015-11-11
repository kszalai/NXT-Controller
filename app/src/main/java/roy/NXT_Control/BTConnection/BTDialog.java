package roy.NXT_Control.BTConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import roy.NXT_Control.R;

public class BTDialog extends AppCompatActivity{

    private BluetoothAdapter btAdapter;
    ListView lv_deviceList;
    ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
    BTDeviceListAdapter btDeviceListAdapter;
    ProgressDialog progDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_device_connect);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        progDialog = new ProgressDialog(this);
        progDialog.setMessage("Scanning...");
        progDialog.setCancelable(false);
        progDialog.setButton(DialogInterface.BUTTON_NEGATIVE,"Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                btAdapter.cancelDiscovery();
            }
        });

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
                if(btAdapter.isDiscovering())
                    btAdapter.cancelDiscovery();
                IntentFilter filter = new IntentFilter();
                filter.addAction(BluetoothDevice.ACTION_FOUND);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                registerReceiver(receiver,filter);
                btAdapter.startDiscovery();
            }
        });
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                deviceList = new ArrayList<BluetoothDevice>();
                progDialog.show();
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                btDeviceListAdapter.setData(deviceList);
                btDeviceListAdapter.notifyDataSetChanged();
                progDialog.dismiss();
            }
            else if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice deviceFound = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(deviceFound);
                Toast.makeText(getApplicationContext(),"Found " + deviceFound.getName(),Toast.LENGTH_SHORT).show();
            }
        }
    };

    public void onBackPressed(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("device", 0);
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
    }

    @Override
    public void onDestroy(){
        unregisterReceiver(receiver);
        super.onDestroy();
    }
}
