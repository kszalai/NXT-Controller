package roy.NXT_Control;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import roy.NXT_Control.BTConnection.BluetoothChatService;

public class TiltDriveFragment extends Fragment implements SensorEventListener {

    private TextView powerLevel;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private Button swapToDirectional;
    private RelativeLayout tiltFrag;

    //Bluetooth Stuff needed
    BluetoothChatService BTChatService;

    static TiltDriveFragment newInstance(int num) {
        TiltDriveFragment f = new TiltDriveFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_tilt_drive,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();

        //Find powerLevel TextView
        powerLevel = (TextView)v.findViewById(R.id.tv_tiltPowerLevel);

        tiltFrag = (RelativeLayout)v.findViewById(R.id.tiltFrag);

        //Swap to Directional
        swapToDirectional = (Button)v.findViewById(R.id.btn_toDirectional);
        swapToDirectional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                DirectionalDriveFragment directionalDrive = new DirectionalDriveFragment();
                directionalDrive.receiveBTchat(BTChatService);
                tiltFrag.setVisibility(View.INVISIBLE);
                ft.replace(R.id.root_frame,directionalDrive);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.addToBackStack(null);
                ft.commit();
            }
        });

        //Setup SensorManager and Gyroscope Sensor
        mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //if sensor is unreliable, return void
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            return;
        }

        //else it will output the Roll, Pitch and Yawn values
        powerLevel.setText("Orientation X (Roll) :"+ Float.toString(event.values[2]) +"\n"+
                "Orientation Y (Pitch) :"+ Float.toString(event.values[1]) +"\n"+
                "Orientation Z (Yaw) :"+ Float.toString(event.values[0]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Do nothing
    }

    public void receiveBTchat(BluetoothChatService chatService){
        BTChatService = chatService;
    }
}