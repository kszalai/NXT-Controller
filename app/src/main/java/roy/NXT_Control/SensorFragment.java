package roy.NXT_Control;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by Brandon on 11/20/2015.
 */
public class SensorFragment extends Fragment {
    FragCommunicator fc;

    public static int[] sensorImages = {
            R.drawable.nxt_distance_120,
            R.drawable.nxt_light_120,
            R.drawable.nxt_touch_120,
            R.drawable.nxt_sound_120,
            R.drawable.nxt_servo_120,
            R.drawable.nxt_servo_120,
            R.drawable.nxt_servo_120
    };

    static SensorFragment newInstance(int num) {
        SensorFragment f = new SensorFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sensor, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        View v = getView();
        fc = (FragCommunicator) getActivity();

        String[] cv_numberArray = {"1", "2", "3", "4", "A", "B", "C"};
        ImageView[] cv_imageArray = {null, null, null, null, null, null, null};
        ToggleButton[] cv_buttonArray = {null, null, null, null, null, null, null};

        ArrayList<SensorListData> lv_listItems = new ArrayList<SensorListData>();
        for (int i = 0; i < cv_numberArray.length; i++) {
            lv_listItems.add(new SensorListData(cv_numberArray[i], cv_buttonArray[i]));
        }

        SensorListAdapter lv_adapter = new SensorListAdapter(getContext(), cv_numberArray, sensorImages);

        ListView listView = (ListView) v.findViewById(R.id.xv1_sensorList);
        listView.setAdapter(lv_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent sensorIntent;
                switch (position) {
                    case 0:
                        sensorIntent = new Intent(getContext(), SensorDialog.class);
                        startActivity(sensorIntent);
                        break;
                    case 1:
                        sensorIntent = new Intent(getContext(), SensorDialog.class);
                        startActivity(sensorIntent);
                        break;
                    case 2:
                        sensorIntent = new Intent(getContext(), SensorDialog.class);
                        startActivity(sensorIntent);
                        break;
                    case 3:
                        sensorIntent = new Intent(getContext(), SensorDialog.class);
                        startActivity(sensorIntent);
                        break;
                    default:
                        break;
                }
                Toast.makeText(getContext(), "Clicked item at position " + position, Toast.LENGTH_LONG).show();
            }
        });
    }
}
