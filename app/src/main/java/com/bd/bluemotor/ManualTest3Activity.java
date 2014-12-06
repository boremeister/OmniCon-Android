package com.bd.bluemotor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class ManualTest3Activity extends ActionBarActivity {

    Handler bluetoothIn;
    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();
    private ConnectedThread mConnectedThread;

    TextView tvRespond, tvRespondLength;
    EditText etMessage;

    BoreToolbox bt;

    private static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static Set<BluetoothDevice> pairedDevices = null;
    private static BluetoothAdapter btAdapter = null;
    String deviceName = "HC-05";
    private static BluetoothDevice targetDevice = null;
    private static BluetoothSocket deviceSocket = null;
    private static String LOG_TAG = "com.bd.bluemotor.ManualTest3Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_test3);

        tvRespond = (TextView) findViewById(R.id.textViewRespond);
        tvRespondLength = (TextView) findViewById(R.id.textViewRespondLength);
        etMessage = (EditText) findViewById(R.id.editTextData);

        // handler for printing data sent from bluetooth module
/*
        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {

                tvRespondLength.setText("Test receive ...");

                if (msg.what == handlerState) {                                     //if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("#");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        tvRespond.setText("Data Received = " + dataInPrint);
                        int dataLength = dataInPrint.length();                          //get length of data received
                        tvRespondLength.setText("String Length = " + String.valueOf(dataLength));

                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        dataInPrint = " ";
                    }
                }

            }
        };
*/
        bt = new BoreToolbox(getApplicationContext());

        btAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Override
    public void onResume(){

        super.onResume();

        // check bluetooth status
        if(checkBluetooth()){

            pairedDevices = btAdapter.getBondedDevices();

            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {

                    // check if deviceName already paired
                    if(deviceName.equals(device.getName())){
                        targetDevice = device;
                    }
                }
            }

            try{
                deviceSocket = targetDevice.createInsecureRfcommSocketToServiceRecord(DEVICE_UUID);
                Log.i(LOG_TAG, "Device socket created OK");
            } catch (IOException e) {
                Log.i(LOG_TAG, "Cannot create socket: " + e.toString());
            }

            try {
                deviceSocket.connect();

                bt.print("Socket OK!");

                //mConnectedThread = new ConnectedThread(deviceSocket);
                //mConnectedThread.start();

                //mConnectedThread.write("y");

            } catch (IOException e) {

                bt.print("Socket not connected!");

                Log.i(LOG_TAG, "Cannot open socket connection: " + e.toString());
                try {
                    deviceSocket.close();
                } catch (Exception e2) {

                    Log.i(LOG_TAG, "Cannot close socket connection: " + e.toString());
                }
            }

        }else{
            bt.print("Bluetooth no ON/supported!");
        }

    }

    @Override
    public void onPause(){
        super.onPause();

        try{
            deviceSocket.close();
        }catch (IOException e){
            // empty
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_manual_test3, menu);
        return true;
    }

    public boolean checkBluetooth(){

        // check if bt adapter supported
        if(btAdapter!=null){
            // check if bt adapter enabled
            if(btAdapter.isEnabled()){
                return true;
            }
        }

        return false;
    }

    public void sendToTarget(View view){

        String msg = etMessage.getText().toString();

        //mConnectedThread.write(msg);

        if(deviceSocket != null){
            byte[] msgBuffer = msg.getBytes();
            OutputStream outStream = null;

            try {
                outStream = deviceSocket.getOutputStream();
                outStream.write(msgBuffer);
                //deviceSocket.close();
            } catch (IOException e) {
                Log.i(LOG_TAG, "Sending message to device failed: " + e.toString());
            }
        }


    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
                Log.i(LOG_TAG, "Both sockets created OK!");

            } catch (IOException e) {
                Log.i(LOG_TAG, "Both sockets not created OK: " + e.toString());
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        /*
        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    //bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        */

        //write method
        public void write(String input) {

            bt.print("Sending " + input);
            byte[] msgBuffer = input.getBytes();

            try {
                mmOutStream.write(msgBuffer);
                //deviceSocket.close();
                mmOutStream.flush();
            } catch (IOException e) {
                Log.i(LOG_TAG, "Sending message to device failed: " + e.toString());
                finish();
            }
        }
    }
}
