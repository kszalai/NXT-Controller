package roy.NXT_Control;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Brandon on 11/28/2015.
 */
public class SensorDialog extends AppCompatActivity {

    public static int[] sensorImages = {
            R.drawable.nxt_light_120,
            R.drawable.nxt_sound_120,
            R.drawable.nxt_touch_120,
            R.drawable.nxt_distance_120};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_dialog);

        String[] cv_labelArray = {"Light Sensor", "Sound Sensor", "Touch Sensor", "Distance Sensor"};

        ArrayList<SensorListIntentData> lv_listItems = new ArrayList<SensorListIntentData>();
        for (int i = 0; i < cv_labelArray.length; i++) {
            lv_listItems.add(new SensorListIntentData(cv_labelArray[i]));
        }

        SensorListIntentAdapter lv_adapter = new SensorListIntentAdapter(this, cv_labelArray, sensorImages);

        ListView listView = (ListView) findViewById(R.id.lv_sensors);
        listView.setAdapter(lv_adapter);
    }
}
