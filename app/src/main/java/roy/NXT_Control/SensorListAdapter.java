package roy.NXT_Control;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SensorListAdapter extends BaseAdapter{
    String [] result;
    Context context;
    int [] imageId;

    private static LayoutInflater inflater=null;
    public SensorListAdapter(Context context, String[] NumberList, int[] sensorImages) {
        result = NumberList;
        imageId = sensorImages;

        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    public void setImageAt(int position, int image){
        imageId[position] = image;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View v;
        v = inflater.inflate(R.layout.sensor_list_cell, null);

        holder.tv = (TextView) v.findViewById(R.id.xv_number);
        holder.img = (ImageView) v.findViewById(R.id.xv_sensorImage);

        holder.tv.setText(result[position]);
        holder.img.setImageResource(imageId[position]);

        return v;
    }
}
