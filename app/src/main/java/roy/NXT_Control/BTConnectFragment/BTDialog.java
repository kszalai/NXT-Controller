package roy.NXT_Control.BTConnectFragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

import roy.NXT_Control.R;

public class BTDialog extends DialogFragment {

    private BluetoothAdapter btAdapter;
    ListView lv_deviceList;
    ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
    BTDeviceListAdapter btDeviceListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_device_connect, container,
                false);
        getDialog().setTitle("Select a Device");

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> btDevices = btAdapter.getBondedDevices();

        deviceList.addAll(btDevices);
        btDeviceListAdapter = new BTDeviceListAdapter(getContext(),deviceList);

        lv_deviceList = (ListView)rootView.findViewById(R.id.lv_devices);
        lv_deviceList.setAdapter(btDeviceListAdapter);

        //Tap - Connect to a device
        lv_deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Connect to device
                if(btAdapter.isDiscovering())
                    btAdapter.cancelDiscovery();
                final BluetoothDevice deviceSelected = (BluetoothDevice)btDeviceListAdapter.getItem(position);
                Toast.makeText(getContext(),deviceSelected.getName(),Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        });

        //Scan Button
        Button scanButton = (Button)rootView.findViewById(R.id.btn_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deviceList.clear();
                btDeviceListAdapter.notifyDataSetChanged();
                btAdapter.startDiscovery();
            }
        });

        return rootView;
    }
}
