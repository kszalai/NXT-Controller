package roy.NXT_Control;

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
