package roy.NXT_Control;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import roy.NXT_Control.BTConnection.BTDialog;

public class ConnectionFragment extends Fragment{

    private ImageView bluetoothIcon;
    private TextView status;
    private TextView device;
    private ToggleButton connectButton;
    private BluetoothDevice robot;
    private BluetoothSocket socket;
    static final int PICK_BLUETOOTH_DEVICE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_connection,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();

        bluetoothIcon = (ImageView) v.findViewById(R.id.bt_icon);

        status = (TextView) v.findViewById(R.id.connection_status);
        device = (TextView) v.findViewById(R.id.tv_deviceConnected);

        connectButton = (ToggleButton) v.findViewById(R.id.tbtn_connect);
        connectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toggles the Button through on and off
                if (connectButton.isChecked()) {
                    Intent lv_deviceConnect = new Intent(getContext(),BTDialog.class);
                    startActivityForResult(lv_deviceConnect, PICK_BLUETOOTH_DEVICE);
                }
                else {
                    try{
                        socket.close();
                        status.setText("Disconnected");
                        device.setText("No Device");
                        connectButton.setChecked(false);
                        bluetoothIcon.setImageResource(R.mipmap.bt_icon_black);
                    }
                    catch(Exception e){
                        Toast.makeText(getContext(),"Error when Disconnecting - " + e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                //Picked a device to connect to
                robot = data.getExtras().getParcelable("device");
                try {
                    socket = robot.createRfcommSocketToServiceRecord(java.util.UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    socket.connect();
                    //Successful Connection
                    bluetoothIcon.setImageResource(R.mipmap.bt_icon_blue);
                    device.setText(robot.getName());
                    status.setText("Connected");

                } catch (Exception e) {
                    //Connection failed
                    Toast.makeText(getContext(), "Error: Failed to connect to " + robot.getName(), Toast.LENGTH_SHORT).show();
                    connectButton.setChecked(false); //Reset button to connect
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //User decided to cancel
                connectButton.setChecked(false);
            }
        }
    }
}