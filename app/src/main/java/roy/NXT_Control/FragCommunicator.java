package roy.NXT_Control;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.InputStream;
import java.io.OutputStream;

public interface FragCommunicator {

    public void sendBTDeviceDetails(BluetoothAdapter btAdapter, BluetoothDevice robot,
                                    BluetoothSocket btSocket, InputStream is, OutputStream os);
}
