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

import roy.NXT_Control.BTConnection.BluetoothChatService;

public class DirectionalDriveFragment extends Fragment{

    ImageButton upButton;
    ImageButton downButton;
    ImageButton leftButton;
    ImageButton rightButton;
    SeekBar powerControl;
    TextView powerAmount;

    //Bluetooth Stuff Needed
    BluetoothChatService BTChatService;

    static DirectionalDriveFragment newInstance(int num) {
        DirectionalDriveFragment f = new DirectionalDriveFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        return inflater.inflate(R.layout.fragment_directional_drive,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();

        //Declare buttons
        upButton = (ImageButton) v.findViewById(R.id.btn_upDrive);
        upButton.setOnTouchListener(new DirectionOnTouchListener(1,1));
        downButton = (ImageButton) v.findViewById(R.id.btn_downDrive);
        downButton.setOnTouchListener(new DirectionOnTouchListener(-1,-1));
        leftButton = (ImageButton) v.findViewById(R.id.btn_leftDrive);
        leftButton.setOnTouchListener(new DirectionOnTouchListener(-0.6,0.6));
        rightButton = (ImageButton) v.findViewById(R.id.btn_rightDrive);
        rightButton.setOnTouchListener(new DirectionOnTouchListener(0.6,-0.6));
        powerControl = (SeekBar) v.findViewById(R.id.sb_powerLevel);
        powerAmount = (TextView) v.findViewById(R.id.tv_poweramount);

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
    }

    //For Directional Drive Buttons
    private class DirectionOnTouchListener implements View.OnTouchListener {

        private double lmod;
        private double rmod;

        public DirectionOnTouchListener(double l, double r) {
            lmod = l;
            rmod = r;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //Log.i("NXT", "onTouch event: " + Integer.toString(event.getAction()));
            int action = event.getAction();
            //if ((action == MotionEvent.ACTION_DOWN) || (action == MotionEvent.ACTION_MOVE)) {
            if (action == MotionEvent.ACTION_DOWN) {
                byte power = (byte) powerControl.getProgress();
                byte l = (byte) (power*lmod);
                byte r = (byte) (power*rmod);
                BTChatService.motors(l, r, false, false);
            } else if ((action == MotionEvent.ACTION_UP) || (action == MotionEvent.ACTION_CANCEL)) {
                BTChatService.motors((byte) 0, (byte) 0, false, false);
            }
            return true;
        }
    }

    public void receiveBTchat(BluetoothChatService chatService){
        BTChatService = chatService;
    }

}
