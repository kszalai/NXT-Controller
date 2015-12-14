package roy.NXT_Control;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class SensorDialog extends AppCompatActivity {

    //Return Intent Extra
    public static String EXTRA_SENSOR = "sensor";

    public static int[] sensorImages = {
            R.drawable.nxt_light_120,
            R.drawable.nxt_sound_120,
            R.drawable.nxt_touch_120,
            R.drawable.nxt_distance_120};

    private int itemClicked;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor_dialog);

        if (getIntent() != null) {
            itemClicked = getIntent().getIntExtra("clicked",-1);
        }

        String[] cv_labelArray = {"Light Sensor", "Sound Sensor", "Touch Sensor", "Distance Sensor"};

        ArrayList<SensorListIntentData> lv_listItems = new ArrayList<SensorListIntentData>();
        for (int i = 0; i < cv_labelArray.length; i++) {
            lv_listItems.add(new SensorListIntentData(cv_labelArray[i]));
        }

        setResult(RESULT_CANCELED);

        SensorListIntentAdapter lv_adapter = new SensorListIntentAdapter(this, cv_labelArray, sensorImages);

        ListView listView = (ListView) findViewById(R.id.lv_sensors);
        listView.setAdapter(lv_adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra(EXTRA_SENSOR, position);
                intent.putExtra("clicked",itemClicked);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
