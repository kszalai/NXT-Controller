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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

import roy.NXT_Control.R;

public class DeviceListActivity extends Activity{

    //Tag for Log
    private static final String TAG = "DeviceListActivity";

    //Return Intent Extra
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothAdapter btAdapter;
    private BTDeviceListAdapter newDevicesAdapter;

    ProgressDialog progDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Setup the window
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device_list);

        //Set result to CANCELLED if user backs out
        setResult(RESULT_CANCELED);

        //Scan Button
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        //For Scan Button
        progDialog = new ProgressDialog(this);
        progDialog.setMessage("Scanning...");
        progDialog.setCancelable(false);
        progDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                btAdapter.cancelDiscovery();
            }
        });

        //Initialize Device Adapters. One is for paired devices, one is for discovered devices
        BTDeviceListAdapter pairedDevicesAdapter = new BTDeviceListAdapter(this,R.layout.cell_device_list);
        newDevicesAdapter = new BTDeviceListAdapter(this,R.layout.cell_device_list);

        //Find and setup ListView for paired devices
        ListView pairedListView = (ListView)findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesAdapter);
        pairedListView.setOnItemClickListener(DeviceClickListener);

        //Find and setup ListView for newly discovered devices
        ListView newDevicesListView = (ListView)findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(newDevicesAdapter);
        newDevicesListView.setOnItemClickListener(DeviceClickListener);

        //Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(receiver, filter);

        //Get local BluetoothAdapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        //Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        //If there are paired devices, add each one to the ArrayAdapter
        if(pairedDevices.size() > 0){
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for(BluetoothDevice device : pairedDevices){
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
        else{
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesAdapter.add(noDevices);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        //Make sure not discovering
        if(btAdapter!=null){
            btAdapter.cancelDiscovery();
        }

        //Unregister broadcast listeners
        this.unregisterReceiver(receiver);
    }

    //Start device discover with BluetoothAdapter
    private void doDiscovery() {
        Log.d(TAG, "doDiscovery()");

        //Indicate scanning on title
        setProgressBarIndeterminate(true);
        setTitle(R.string.scanning);

        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        //If we're already scanning, stop it
        if (btAdapter.isDiscovering())
            btAdapter.cancelDiscovery();

        btAdapter.startDiscovery();
    }

    private AdapterView.OnItemClickListener DeviceClickListener = new AdapterView.OnItemClickListener(){
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Cancel discovery because it's costly and we're about to connect
            if(btAdapter.isDiscovering()) {
                btAdapter.cancelDiscovery();
            }

            // Get the device MAC address, which is the last 17 chars in the View
            String info = parent.getItemAtPosition(position).toString();
            String address = info.substring(info.length() - 17);

            // Create the result Intent and include the MAC address
            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    //The BroadcastReceiver that listens for discovered devices and changes the title when discovery is finished
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    newDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }
            //Show ProgressDialog when discovery begins
            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                progDialog.show();
            }
            // When discovery is finished, change the Activity title
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                progDialog.dismiss();
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (newDevicesAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    newDevicesAdapter.add(noDevices);
                }
            }
        }
    };
}
