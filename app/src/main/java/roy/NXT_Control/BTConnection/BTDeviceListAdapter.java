package roy.NXT_Control.BTConnection;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
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
    private ArrayList<BluetoothDevice> c2v_listItems;

    public BTDeviceListAdapter(Context context, ArrayList<BluetoothDevice> listItems){
        this.c2v_context = context;
        this.c2v_listItems = listItems;
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

    public void setData(ArrayList<BluetoothDevice> discoveredDevices){
        c2v_listItems = discoveredDevices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    c2v_context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.devicelistcell, null);
        }

        TextView lv_deviceName = (TextView) convertView.findViewById(R.id.tv_deviceName);
        lv_deviceName.setText(c2v_listItems.get(position).getName());
        lv_deviceName.setTextColor(convertView.getResources().getColor(R.color.black));

        TextView lv_macAddress = (TextView) convertView.findViewById(R.id.tv_macAddress);
        lv_macAddress.setText(c2v_listItems.get(position).getAddress());
        lv_macAddress.setTextColor(convertView.getResources().getColor(R.color.black));

        return convertView;
    }
}
