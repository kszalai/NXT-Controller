package roy.NXT_Control;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;

public class SensorFragment extends Fragment {

    private static final int REQUEST_NEW_SENSOR = 1;

    ArrayList<SensorListData> lv_listItems;
    SensorListAdapter lv_adapter;
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

        lv_listItems = new ArrayList<SensorListData>();
        for (int i = 0; i < cv_numberArray.length; i++) {
            lv_listItems.add(new SensorListData(cv_numberArray[i], cv_buttonArray[i]));
        }

        lv_adapter = new SensorListAdapter(getContext(), cv_numberArray, sensorImages);

        ListView listView = (ListView) v.findViewById(R.id.xv1_sensorList);
        listView.setAdapter(lv_adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent sensorIntent = null;
                switch (position) {
                    case 0:
                        sensorIntent = new Intent(getContext(), SensorDialog.class);
                        sensorIntent.putExtra("clicked",position);
                        startActivityForResult(sensorIntent, REQUEST_NEW_SENSOR);
                        break;
                    case 1:
                        sensorIntent = new Intent(getContext(), SensorDialog.class);
                        sensorIntent.putExtra("clicked",position);
                        startActivityForResult(sensorIntent, REQUEST_NEW_SENSOR);
                        break;
                    case 2:
                        sensorIntent = new Intent(getContext(), SensorDialog.class);
                        sensorIntent.putExtra("clicked",position);
                        startActivityForResult(sensorIntent, REQUEST_NEW_SENSOR);
                        break;
                    case 3:
                        sensorIntent = new Intent(getContext(), SensorDialog.class);
                        sensorIntent.putExtra("clicked",position);
                        startActivityForResult(sensorIntent, REQUEST_NEW_SENSOR);
                        break;
                    default:
                        break;
                }
                //Toast.makeText(getContext(), "Clicked item at position " + position, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_NEW_SENSOR:
                if(resultCode == Activity.RESULT_OK){
                    //To swap when implemented
                    int sensor = data.getExtras().getInt(SensorDialog.EXTRA_SENSOR);
                    int itemClicked = data.getExtras().getInt("clicked");
                    int newImage = 0;
                    switch(sensor){
                        case 0:
                            newImage = R.drawable.nxt_light_120;
                            break;
                        case 1:
                            newImage = R.drawable.nxt_sound_120;
                            break;
                        case 2:
                            newImage = R.drawable.nxt_touch_120;
                            break;
                        case 3:
                            newImage = R.drawable.nxt_distance_120;
                            break;
                    }
                    lv_adapter.setImageAt(itemClicked,newImage);
                    lv_adapter.notifyDataSetChanged();
                    //Toast.makeText(getContext(), sensor, Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getContext(), "Back was pressed", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
