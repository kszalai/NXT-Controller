package roy.NXT_Control;

/**
 * Created by Brandon on 10/31/2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ConnectionFragment extends Fragment {

    private ImageView bluetoothIconBlue, bluetoothIconBlack;
    private TextView status;
    private Button clickButton;
    private int click = 0;

    public ConnectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_connection, container, false);

        bluetoothIconBlue = (ImageView) v.findViewById(R.id.bt_icon_blue);
        bluetoothIconBlack = (ImageView) v.findViewById(R.id.bt_icon_black);
        bluetoothIconBlue.setVisibility(View.INVISIBLE);
        InitializeButton(v);

        return v;
    }

    public void InitializeButton(View v){
        status = (TextView) v.findViewById(R.id.connection_status);
        clickButton = (Button) v.findViewById(R.id.connect_button);
        clickButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                click++;
                if(click % 2 == 1) {
                    status.setText("Connected");
                    clickButton.setText("Disconnect");
                    bluetoothIconBlue.setVisibility(View.VISIBLE);
                }
                else {
                    status.setText("Disconnected");
                    clickButton.setText("Connect");
                    bluetoothIconBlue.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}