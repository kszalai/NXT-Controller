package roy.NXT_Control;

import android.bluetooth.BluetoothDevice;
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
import android.widget.ToggleButton;

import roy.NXT_Control.BTConnectFragment.BTDialog;

public class ConnectionFragment extends Fragment{

    private ImageView bluetoothIcon;
    private TextView status;
    private ToggleButton connectButton;

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

        connectButton = (ToggleButton) v.findViewById(R.id.tbtn_connect);
        connectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Toggles the Button through on and off
                if (connectButton.isChecked()) {
                    BTDialog btDialog = new BTDialog();
                    btDialog.show(getFragmentManager(),"Select a Device");
                    btDialog.setCancelable(true);
                    status.setText("Connected");
                    connectButton.setChecked(true);
                    bluetoothIcon.setImageResource(R.mipmap.bt_icon_blue);
                } else {
                    status.setText("Disconnected");
                    connectButton.setChecked(false);
                    bluetoothIcon.setImageResource(R.mipmap.bt_icon_black);
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
}