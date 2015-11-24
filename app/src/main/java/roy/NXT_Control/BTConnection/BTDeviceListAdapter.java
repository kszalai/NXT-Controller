package roy.NXT_Control.BTConnection;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import roy.NXT_Control.R;

public class BTDeviceListAdapter extends BaseAdapter{
    private Context c2v_context;
    private int layoutResourceId;
    private ArrayList<String> c2v_listItems;

    public BTDeviceListAdapter(Context context, int resource){
        c2v_context = context;
        layoutResourceId = resource;
        c2v_listItems = new ArrayList<String>();
    }

    @Override
    public int getCount() {
        return c2v_listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return c2v_listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void add(String device){
        c2v_listItems.add(device);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    c2v_context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(layoutResourceId, null);
        }

        TextView lv_deviceName = (TextView) convertView.findViewById(R.id.tv_deviceName);
        lv_deviceName.setText(c2v_listItems.get(position));
        lv_deviceName.setTextColor(convertView.getResources().getColor(R.color.black));

        return convertView;
    }
}
