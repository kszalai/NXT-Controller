package roy.NXT_Control;

import android.widget.Button;
import android.widget.ToggleButton;

/**
 * Created by Brandon on 11/20/2015.
 */
public class SensorListIntentData {
        private String label;

        public SensorListIntentData(){
        }

        public SensorListIntentData(String label){
            this.label = label;
        }

        public String getLabel(){
            return this.label;
        }


        public void setLabel(String label){
            this.label = label;
        }

}
