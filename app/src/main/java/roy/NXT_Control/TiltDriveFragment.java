package roy.NXT_Control;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

    private SensorManager mSensorManager;
    private Button swapToDirectional;
    private RelativeLayout tiltFrag;
    private byte leftMotor;
    private byte rightMotor;

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

        tiltFrag = (RelativeLayout)v.findViewById(R.id.tiltFrag);

        //Swap to Directional
        swapToDirectional = (Button)v.findViewById(R.id.btn_toDirectional);
        swapToDirectional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BTChatService.motors(leftMotor,rightMotor,(byte) 0, (byte) 0, false, false);
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

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        String defaultMotors = pref.getString("servoPortSetting", "A and B");
        switch(defaultMotors){
            case "1": //A and B
                leftMotor = 0x00;
                rightMotor = 0x01;
                break;
            case "2": //B and C
                leftMotor = 0x01;
                rightMotor = 0x02;
                break;
            case "3": //A and C
                leftMotor = 0x00;
                rightMotor = 0x02;
                break;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this){
            //event.values[0] - x
            //event.values[1] - y

            float x = event.values[0];
            float y = event.values[1];

            if(Math.abs(x) > 0.25 && Math.abs(y) > 0.25) {
                float sqrt22 = 0.707106781f;
                float nx = x * sqrt22 + y * sqrt22;
                float ny = -x * sqrt22 + y * sqrt22;
                float power = (float) Math.sqrt(nx * nx + ny * ny);
                if (power > 1.0f) {
                    nx /= power;
                    ny /= power;
                    power = 1.0f;
                }
                float angle = (float) Math.atan2(y, x);
                float l, r;
                if (angle > 0f && angle <= Math.PI / 2f) {
                    l = 1.0f;
                    r = (float) (2.0f * angle / Math.PI);
                } else if (angle > Math.PI / 2f && angle <= Math.PI) {
                    l = (float) (2.0f * (Math.PI - angle) / Math.PI);
                    r = 1.0f;
                } else if (angle < 0f && angle >= -Math.PI / 2f) {
                    l = -1.0f;
                    r = (float) (2.0f * angle / Math.PI);
                } else if (angle < -Math.PI / 2f && angle > -Math.PI) {
                    l = (float) (-2.0f * (angle + Math.PI) / Math.PI);
                    r = -1.0f;
                } else {
                    l = r = 0f;
                }
                l *= power;
                r *= power;
                BTChatService.motors(leftMotor,rightMotor,(byte) (100 * l), (byte) (100 * r), false, false);
            }
            else{
                BTChatService.motors(leftMotor,rightMotor,(byte) 0, (byte) 0, false, false);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Do nothing
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), mSensorManager.SENSOR_DELAY_GAME);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        String defaultMotors = pref.getString("servoPortSetting", "A and B");
        switch(defaultMotors){
            case "1": //A and B
                leftMotor = 0x00;
                rightMotor = 0x01;
                break;
            case "2": //B and C
                leftMotor = 0x01;
                rightMotor = 0x02;
                break;
            case "3": //A and C
                leftMotor = 0x00;
                rightMotor = 0x02;
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this,mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    public void receiveBTchat(BluetoothChatService chatService){
        BTChatService = chatService;
    }
}