package com.bd.bluemotor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class ManualTest3Activity extends ActionBarActivity {

    private StringBuilder recDataString = new StringBuilder();
    private ConnectedThread mConnectedThread;
    BoreToolbox bt;
    Handler handler;
    TextView tvRespond, tvRespondLength;
    EditText etMessage;

    private static UUID DEVICE_UUID;
    private static Set<BluetoothDevice> pairedDevices = null;
    private static BluetoothAdapter btAdapter = null;
    //String deviceName = "HC-05";
    String deviceName, responseStartChar, responseEndChar;

    private static BluetoothDevice targetDevice = null;
    private static BluetoothSocket deviceSocket = null;

    private static String LOG_TAG = "com.bd.bluemotor.ManualTest3Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_test3);

        // UI fields
        tvRespond = (TextView) findViewById(R.id.textViewRespond);
        tvRespondLength = (TextView) findViewById(R.id.textViewRespondLength);
        etMessage = (EditText) findViewById(R.id.editTextData);

        // read values from settings file
        SharedPreferences settings = getSharedPreferences("OMNICON_PREF", 0);
        String uuid = settings.getString("UUID", null);
        DEVICE_UUID = UUID.fromString(uuid);
        deviceName = settings.getString("DEVICE_NAME", null);
        responseStartChar = settings.getString("RESPONSE_STARTCHAR", null);
        responseEndChar = settings.getString("RESPONSE_ENDCHAR", null);

        bt = new BoreToolbox(getApplicationContext());

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        /*
        * handler for receiving data from BT module
        */
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Bundle bundle = msg.getData();
                String myMsg = bundle.getString("bt_response");

                recDataString.append(myMsg);                                        //keep appending to string until end char
                int endOfLineIndex = recDataString.indexOf("#");                    // determine the end-of-line
                if (endOfLineIndex > 0) {                                           // make sure there data before ~
                    String dataInPrint = recDataString.substring(1, endOfLineIndex);    // extract string (without start char)
                    tvRespond.setText("Data Received = " + dataInPrint);
                    int dataLength = dataInPrint.length();                          //get length of data received
                    tvRespondLength.setText("String Length = " + String.valueOf(dataLength));

                    recDataString.delete(0, recDataString.length());                    //clear all string data
                    dataInPrint = " ";
                }
            }
        };

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

                mConnectedThread = new ConnectedThread(deviceSocket);
                mConnectedThread.start();

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
        mConnectedThread.write(msg);

    }

    /*
    * class for thread which is used for sending to and receiving from (listening) bluetooth module
    */
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
            } catch (IOException e) {
                // empty
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);

                    // Send the obtained bytes to the activity via handler
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("bt_response", readMessage);
                    msg.setData(bundle);
                    handler.sendMessage(msg);

                } catch (IOException e) {
                    //break;
                }
            }
        }

        // write method
        public void write(String input) {

            byte[] msgBuffer = input.getBytes();

            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                finish();
            }
        }
    }
}
