package com.bd.bluemotor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ManualTestActivity extends Activity {

	BoreToolbox bt;
    BluetoothHandler bh;
    private ConnectedThread mConnectedThread;

	Button btnPairedDevices, btnSend;
	EditText etTargetDeviceName, etCommand;
    TextView tvRespond, tvRespondLength;
    Handler respondHandler;
    private StringBuilder recDataString = new StringBuilder();

    private static UUID DEVICE_UUID;
    private static Set<BluetoothDevice> pairedDevices = null;
    private static BluetoothAdapter btAdapter = null;
    String deviceName, responseStartChar, responseEndChar;

    //private static String LOG_TAG = "com.bd.bluemotor.ManualTestActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manual_test);
		// Show the Up button in the action bar.
		setupActionBar();

        // read values from settings file
        SharedPreferences settings = getSharedPreferences("OMNICON_PREF", 0);
        String uuid = settings.getString("UUID", null);
        DEVICE_UUID = UUID.fromString(uuid);
        deviceName = settings.getString("DEVICE_NAME", null);
        responseStartChar = settings.getString("RESPONSE_STARTCHAR", null);
        responseEndChar = settings.getString("RESPONSE_ENDCHAR", null);

        // UI fields
        btnPairedDevices = (Button) findViewById(R.id.buttonPairedDevices);
        btnSend = (Button) findViewById(R.id.buttonSend);
        etTargetDeviceName = (EditText) findViewById(R.id.editTextDeviceName);
        etTargetDeviceName.setText(deviceName);
        tvRespond = (TextView) findViewById(R.id.textViewRespond);
        tvRespondLength = (TextView) findViewById(R.id.textViewRespondLength);
        etCommand = (EditText) findViewById(R.id.editTextCommand);

        bt = new BoreToolbox(getApplicationContext());

        // initialize BluetoothHandler
        bh = new BluetoothHandler(BluetoothAdapter.getDefaultAdapter());

        /*
        * handler for receiving data from BT module
        */
        respondHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Bundle bundle = msg.getData();
                String myMsg = bundle.getString("bt_response");

                recDataString.append(myMsg);                                        //keep appending to string until end char
                int endOfLineIndex = recDataString.indexOf("#");                    // determine the end-of-line
                if (endOfLineIndex > 0) {                                           // make sure there data before ~
                    String dataInPrint = recDataString.substring(1, endOfLineIndex);    // extract string (without start char)
                    tvRespond.setText(dataInPrint);
                    int dataLength = dataInPrint.length();                          //get length of data received
                    tvRespondLength.setText("[" + String.valueOf(dataLength) + "]");

                    recDataString.delete(0, recDataString.length());                    //clear all string data
                    dataInPrint = " ";
                }
            }
        };

        // check bluetooth status
		checkBluetooth();
		
	}

    @Override
    public void onResume() {

        super.onResume();

        if(bh.isBluetoothReady()){

            if(bh.isTargetDevicePaired1(deviceName)){

                // try to establish connection
                if(bh.establishConnection()){

                    mConnectedThread = new ConnectedThread(bh.getSocket());
                    mConnectedThread.start();

                    bt.print("Connection established OK!");

                }else{
                    bt.print("Connection could not be established!");
                }
            }else{
                bt.print("Target device " + deviceName + " not paired!");
            }
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        try{
            bh.getSocket().close();
        }catch (IOException e){
            // empty
        }
    }

        /**
         * Set up the {@link android.app.ActionBar}, if the API is available.
         */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.manual_test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void	getPairedDevices (View view){
		
		TextView tv;
		tv = (TextView) findViewById(R.id.textViewPairedDevices);
		String text = "";

        // get paired devices
        ArrayList<String> pairedDevicesList = bh.getPairedDevices1();

		if(!pairedDevicesList.isEmpty()){
			// header text
			text = "Name - Address\n";
			// devices
			for (String device : pairedDevicesList) {
				text += device.toString();
			}
		} else {
			text = "No paired devices!";
		}
		
		tv.setText(text);
	}
	
	public void sendToTarget(View view){

        String msg = etCommand.getText().toString();

        // if command not ended with endChar, append endChar
        char ch = msg.charAt(msg.length() - 1);
        if(ch!=responseEndChar.charAt(0))
            msg = msg + responseEndChar;

        mConnectedThread.write(msg);

    }
	
	public void checkBluetooth(View view){

		// check bluetooth status
		checkBluetooth();
	}
	
	public void checkBluetooth(){

		// check if BT supported and enabled
		if(bh.isBluetoothSupported1()){
			if(bh.isBluetoothEnabled1()){
				// everything OK
				bt.print("BT supported and enabled!");
				btnPairedDevices.setEnabled(true);
				btnSend.setEnabled(true);
			} else {
				bt.print("Bluetooth NOT enabled!");
				btnPairedDevices.setEnabled(false);
				btnSend.setEnabled(false);
			}
		} else {
			bt.print("Device does NOT support Bluetooth!");
			btnPairedDevices.setEnabled(false);
			btnSend.setEnabled(false);
		}
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
                    Message msg = respondHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("bt_response", readMessage);
                    msg.setData(bundle);
                    respondHandler.sendMessage(msg);

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
