package roy.NXT_Control;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class DriveFragment extends Fragment implements View.OnTouchListener{

    ImageButton upButton;
    ImageButton downButton;
    ImageButton leftButton;
    ImageButton rightButton;
    SeekBar powerControl;
    TextView powerAmount;

    public DriveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drive, container, false);

        //Declare buttons
        upButton = (ImageButton) view.findViewById(R.id.btn_upDrive);
        upButton.setOnTouchListener(this);
        downButton = (ImageButton) view.findViewById(R.id.btn_downDrive);
        downButton.setOnTouchListener(this);
        leftButton = (ImageButton) view.findViewById(R.id.btn_leftDrive);
        leftButton.setOnTouchListener(this);
        rightButton = (ImageButton) view.findViewById(R.id.btn_rightDrive);
        rightButton.setOnTouchListener(this);
        powerControl = (SeekBar) view.findViewById(R.id.sb_powerLevel);
        powerAmount = (TextView) view.findViewById(R.id.tv_poweramount);

        //Set Power Level Change Listener
        powerControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                powerAmount.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //Required but unused
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //Required but unused
            }
        });
        return view;
    }

    //Method is to contain bluetooth movement methods
    @Override
    public boolean onTouch(View button, MotionEvent theMotion){
        int power = Integer.parseInt(String.valueOf(powerAmount.getText()));

        switch(button.getId())
        {
            case R.id.btn_upDrive:
                if(theMotion.getAction() == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(getView().getContext(), "Up Drive Down " + power, Toast.LENGTH_SHORT).show();
                    cfp_moveMotor(0, power, 0x20);
                    cfp_moveMotor(1, power, 0x20);
                }
                else {
                    Toast.makeText(getView().getContext(), "Up Drive Up", Toast.LENGTH_SHORT).show();
                    cfp_moveMotor(0, power, 0x00);
                    cfp_moveMotor(1, power, 0x00);
                }
                break;
            case R.id.btn_downDrive:
                if(theMotion.getAction() == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(getView().getContext(), "Down Drive Down", Toast.LENGTH_SHORT).show();
                    cfp_moveMotor(0, -power, 0x20);
                    cfp_moveMotor(1, -power, 0x20);
                }
                else {
                    Toast.makeText(getView().getContext(), "Down Drive Up", Toast.LENGTH_SHORT).show();
                    cfp_moveMotor(0, -power, 0x00);
                    cfp_moveMotor(1, -power, 0x00);
                }
                break;
            case R.id.btn_leftDrive:
                if(theMotion.getAction() == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(getView().getContext(), "Left Drive Down", Toast.LENGTH_SHORT).show();
                    cfp_moveMotor(0, power, 0x20);
                    //cfp_moveMotor(1, power, 0x20);
                    //Need to debug to determine left/right motor
                }
                else {
                    Toast.makeText(getView().getContext(), "Left Drive Up", Toast.LENGTH_SHORT).show();
                    cfp_moveMotor(0, power, 0x00);
                    //cfp_moveMotor(1, power, 0x00);
                }
                break;
            case R.id.btn_rightDrive:
                if(theMotion.getAction() == MotionEvent.ACTION_DOWN) {
                    Toast.makeText(getView().getContext(), "Right Drive Down", Toast.LENGTH_SHORT).show();
                    //cfp_moveMotor(0, power, 0x20);
                    cfp_moveMotor(1, power, 0x20);
                }
                else {
                    Toast.makeText(getView().getContext(), "Right Drive Up", Toast.LENGTH_SHORT).show();
                    //cfp_moveMotor(0, power, 0x20);
                    cfp_moveMotor(1, power, 0x00);
                }
                break;
        }

        return true;
    }

    //Move motor method
    //Sends bluetooth package to move the motors
    private void cfp_moveMotor(int motor,int speed, int state) {
        try {
            byte[] buffer = new byte[15];

            buffer[0] = (byte) (15-2);			//length lsb
            buffer[1] = 0;						// length msb
            buffer[2] =  0;						// direct command (with response)
            buffer[3] = 0x04;					// set output state
            buffer[4] = (byte) motor;			// output 1 (motor B)
            buffer[5] = (byte) speed;			// power
            buffer[6] = 1 + 2;					// motor on + brake between PWM
            buffer[7] = 0;						// regulation
            buffer[8] = 0;						// turn ration??
            buffer[9] = (byte) state; //0x20;					// run state
            buffer[10] = 0;
            buffer[11] = 0;
            buffer[12] = 0;
            buffer[13] = 0;
            buffer[14] = 0;

            //os.write(buffer);
            //os.flush();
        }
        catch (Exception e) {
            //cv_label.setText("Error in MoveForward(" + e.getMessage() + ")");
        }
    }


}
