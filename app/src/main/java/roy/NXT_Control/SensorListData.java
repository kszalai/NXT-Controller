package roy.NXT_Control;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ToggleButton;

/**
 * Created by Brandon on 11/20/2015.
 */
public class SensorListData {
        private String ml_number;
        private Button mv_button;

        public SensorListData(){
        }

        public SensorListData(String number, ToggleButton button){
            this.ml_number = number;
            this.mv_button = button;
        }

        public String getNumber(){
            return this.ml_number;
        }

        public Button getButton(){
            return this.mv_button;
        }

        public void setNumber(String number){
            this.ml_number = number;
        }

        public void setButton(ToggleButton button){
            this.mv_button = button;
        }
}
