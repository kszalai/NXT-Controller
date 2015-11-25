package roy.NXT_Control;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

import roy.NXT_Control.BTConnection.BluetoothChatService;
import roy.NXT_Control.BTConnection.DeviceListActivity;

public class ConnectionFragment extends Fragment{

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_CONNECT_DEVICE = 2;

    public static final int MESSAGE_BATTERY = 1;

    public static final String BATTERY = "battery";

    private BluetoothAdapter btAdapter;
    private BluetoothChatService BTChatService;
    private String deviceAddress;
    private ImageView bluetoothIcon;
    private TextView status;
    private TextView device;
    private SeekBar batteryLevel;
    private TextView batteryAmount;
    private ToggleButton connectButton;
    private Timer batteryTimer;

    FragCommunicator fc;

    static ConnectionFragment newInstance(int num) {
        ConnectionFragment f = new ConnectionFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_connection,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();

        fc = (FragCommunicator)getActivity();

        BTChatService = new BluetoothChatService(getContext(),mHandler);

        bluetoothIcon = (ImageView) v.findViewById(R.id.bt_icon);

        status = (TextView) v.findViewById(R.id.connection_status);
        device = (TextView) v.findViewById(R.id.tv_deviceConnected);

        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!btAdapter.isEnabled()){
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }

        //Connect Device Button
        connectButton = (ToggleButton) v.findViewById(R.id.tbtn_connect);
        connectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toggles the Button through on and off
                if (connectButton.isChecked()) { //Button says Connect
                    findDevice();
                }
                else { //Button says Disconnect
                    disconnectDevice();
                }
            }
        });

        //Battery Level
        batteryLevel = (SeekBar)v.findViewById(R.id.sb_batteryLevel);
        batteryAmount = (TextView)v.findViewById(R.id.tv_batteryAmount);
        batteryLevel.setEnabled(false);

        batteryTimer = new Timer();
    }

    private void disconnectDevice() {
        if(batteryTimer!=null) {
            batteryTimer.cancel();
            batteryTimer = new Timer();
        }
        BTChatService.stop();
        status.setText("Disconnected");
        status.setTextColor(getResources().getColor(R.color.black));
        device.setText("No Device");
        device.setTextColor(getResources().getColor(R.color.black));
        batteryAmount.setText("0%");
        batteryLevel.setProgress(0);
        connectButton.setChecked(false);
        bluetoothIcon.setImageResource(R.mipmap.bt_icon_black);
    }

    private void findDevice() {
        Intent lv_deviceConnect = new Intent(getContext(),DeviceListActivity.class);
        startActivityForResult(lv_deviceConnect, REQUEST_CONNECT_DEVICE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    connectButton.setChecked(true);
                    findDevice();
                } else {
                    Toast.makeText(getContext(), "Bluetooth is not available on this device, exiting.", Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }
                break;
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = btAdapter.getRemoteDevice(address);
                    deviceAddress = address;
                    BTChatService.connect(device);
                    fc.sendBTChatService(BTChatService);

                    //Device is connected
                    //Wait for ConnectedThread to start
                    Runnable checkConnected = new Runnable() {
                        @Override
                        public void run() {
                            isConnectedRunning();
                        }
                    };
                    Thread checkConnectedThread = new Thread(checkConnected);
                    checkConnectedThread.start();
                    //One ConnectedThread is started and information is obtained, display connected
                    bluetoothIcon.setImageResource(R.mipmap.bt_icon_blue);
                    this.device.setText(device.getName());
                }
                else{//User didn't connect a device
                    connectButton.setChecked(false);
                    disconnectDevice();
                }
                break;
        }
    }

    private void isConnectedRunning(){
        while(BTChatService.getState()!=BTChatService.STATE_CONNECTED){
            //Waiting for thread to start, do nothing
        }
        TimerTask getBatteryLevel = new TimerTask() {
            @Override
            public void run() {
                BTChatService.getBatteryLevel();
            }
        };
        batteryTimer.schedule(getBatteryLevel,0,10000*60);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
                case Constants.MESSAGE_READ:
                    byte[] buffer = (byte[])msg.obj;
                    if(buffer[3]==(byte)11) {
                        int value = (buffer[6] * 256) + (buffer[5]);
                        double batPercent = (((double) (value)) / 9000) * 100;
                        Log.i("Battery Level", Double.toString(batPercent));
                        batteryLevel.setProgress((int) batPercent);
                        batteryAmount.setText(Integer.toString((int) batPercent) + "%");
                    }
                    break;
                case Constants.MESSAGE_STATE_CHANGE:
                    if(BTChatService.getState()==BTChatService.STATE_NONE){
                        Toast.makeText(getContext(), device.getText() + " has been disconnected!", Toast.LENGTH_SHORT).show();
                        status.setText("Disconnected");
                        status.setTextColor(getResources().getColor(R.color.black));
                        device.setText("No Device");
                        device.setTextColor(getResources().getColor(R.color.black));
                        batteryAmount.setText("0%");
                        batteryLevel.setProgress(0);
                        connectButton.setChecked(false);
                        bluetoothIcon.setImageResource(R.mipmap.bt_icon_black);
                    }
                    if(BTChatService.getState()==BTChatService.STATE_CONNECTING){
                        status.setText("Connecting...");
                    }
                    else if(BTChatService.getState()==BTChatService.STATE_CONNECTED){
                        status.setText("Connected");
                        status.setTextColor(getResources().getColor(R.color.colorAccent));
                        device.setTextColor(getResources().getColor(R.color.colorAccent));
                    }
                    break;
            }
        }
    };
}