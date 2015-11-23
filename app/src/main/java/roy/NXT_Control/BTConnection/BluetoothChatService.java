package roy.NXT_Control.BTConnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

import roy.NXT_Control.ConnectionFragment;
import roy.NXT_Control.Constants;

public class BluetoothChatService {
    //Debugging
    private static final String TAG = "BluetoothChatService";

    //UUID for Application
    private static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //Member fields
    private int state;
    private Handler handler;
    private BluetoothAdapter btAdapter;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device

    //Constructor that prepare for a BluetoothChat session.
    public BluetoothChatService(Context context, Handler handler){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_NONE;
        this.handler = handler;
    }

    //Set current state of chat connection
    private synchronized void setState(int state){
        Log.d(TAG,"setState() " + this.state + "->" + state);
        this.state = state;

        //Give new state to handler so the UI activity can update
        handler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    //Return the connection state
    public synchronized int getState(){
        return state;
    }

    public synchronized void connect(BluetoothDevice device){
        Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        // Cancel any thread currently running a connection
        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectThread = new ConnectThread(device);
        connectThread.start();
        setState(STATE_CONNECTING);

    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device){
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }

        connectedThread = new ConnectedThread(socket);
        connectedThread.start();
        setState(STATE_CONNECTED);
    }

    public synchronized void stop(){
        if (connectThread != null) {
            connectThread.cancel();
            connectThread = null;
        }

        if (connectedThread != null) {
            connectedThread.cancel();
            connectedThread = null;
        }
        setState(STATE_NONE);
    }

    public void connectionFailed(){
        setState(STATE_NONE);
    }

    public void connectionLost(){
        setState(STATE_NONE);
    }

    public void motors(byte l, byte r, boolean speedReg, boolean motorSync) {
        byte[] data = { 0x0c, 0x00, (byte) 0x80, 0x04, 0x02, 0x32, 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00,
                0x0c, 0x00, (byte) 0x80, 0x04, 0x01, 0x32, 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00 };

        //Log.i(TAG, "motors: " + Byte.toString(l) + ", " + Byte.toString(r));

        data[5] = l;
        data[19] = r;
        if (speedReg) {
            data[7] |= 0x01;
            data[21] |= 0x01;
        }
        if (motorSync) {
            data[7] |= 0x02;
            data[21] |= 0x02;
        }
        write(data);
    }

    public void motor(int motor, byte power, boolean speedReg, boolean motorSync) {
        byte[] data = { 0x0c, 0x00, (byte) 0x80, 0x04, 0x02, 0x32, 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00 };

        //Log.i(TAG, "motor: " + Integer.toString(motor) + ", " + Byte.toString(power));

        if (motor == 0) {
            data[4] = 0x02;
        } else {
            data[4] = 0x01;
        }
        data[5] = power;
        if (speedReg) {
            data[7] |= 0x01;
        }
        if (motorSync) {
            data[7] |= 0x02;
        }
        write(data);
    }

    public void motors3(byte l, byte r, byte action, boolean speedReg, boolean motorSync) {
        byte[] data = { 0x0c, 0x00, (byte) 0x80, 0x04, 0x02, 0x32, 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00,
                0x0c, 0x00, (byte) 0x80, 0x04, 0x01, 0x32, 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00,
                0x0c, 0x00, (byte) 0x80, 0x04, 0x00, 0x32, 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00 };

        //Log.i(TAG, "motors3: " + Byte.toString(l) + ", " + Byte.toString(r) + ", " + Byte.toString(action));

        data[5] = l;
        data[19] = r;
        data[33] = action;
        if (speedReg) {
            data[7] |= 0x01;
            data[21] |= 0x01;
        }
        if (motorSync) {
            data[7] |= 0x02;
            data[21] |= 0x02;
        }
        write(data);
    }

    //Gets battery level from robot
    public void getBatteryLevel(){
        byte [] data = new byte[4];

        data[0] = (byte) (4-2);  //length lsb
        data[1] = 0;              //length msb
        data[2] = 0;              //direct command (with response)
        data[3] = 0x0B;           //gets battery level

        write(data);
    }

    private void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (state != STATE_CONNECTED) {
                return;
            }
            r = connectedThread;
        }
        r.write(out);
    }

    private class ConnectThread extends Thread{
        private BluetoothSocket btSocket;
        private final BluetoothDevice robot;

        public ConnectThread(BluetoothDevice device){
            robot = device;
        }

        public void run(){
            try {
                btSocket = robot.createRfcommSocketToServiceRecord(myUUID);
                btSocket.connect();
            }
            catch(IOException e){
                e.printStackTrace();
                try {
                    // This is a workaround that reportedly helps on some older devices where using
                    // the standard createRfcommSocketToServiceRecord() method always causes connect() to fail.
                    Method method = robot.getClass().getMethod("createRfcommSocket", new Class[] { int.class });
                    btSocket = (BluetoothSocket) method.invoke(robot, Integer.valueOf(1));
                    btSocket.connect();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    connectionFailed();
                    try {
                        btSocket.close();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                    return;
                }
            }

            synchronized (BluetoothChatService.this){
                connectThread = null;
            }

            connected(btSocket,robot);
        }

        public void cancel() {
            try {
                if (btSocket != null) {
                    btSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }//End ConnectThread Class

    private class ConnectedThread extends Thread{
        private final BluetoothSocket btSocket;
        private final InputStream is;
        private final OutputStream os;

        public ConnectedThread(BluetoothSocket socket){
            btSocket = socket;
            InputStream tmpIs = null;
            OutputStream tmpOs = null;

            try{
                tmpIs = socket.getInputStream();
                tmpOs = socket.getOutputStream();
            }
            catch(IOException e){
                e.printStackTrace();
            }

            is = tmpIs;
            os = tmpOs;
        }

        public void run(){
            Log.i(TAG,"BEGIN connectedThread");
            byte[] buffer = new byte[1024];
            int bytes;

            //Keep listening to the InputStream while connected
            while (true) {
                try {
                    //Read from InputStream
                    bytes = is.read(buffer);

                    // Send the obtained bytes to the UI Activity
                    if(buffer[3]==(byte)11)//for Battery Level
                        handler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    connectionLost();
                    break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                os.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                // XXX?
            }
        }

        public void cancel() {
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }//End ConnectedThread Class
}
