package com.bd.bluemotor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class AvtoActivity extends Activity {

    private BoreToolbox bt;
    private BluetoothHandler bth;
    private Handler responseHandler;
    private ConnectedThread mConnectedThread;

    private StringBuilder recDataString = new StringBuilder();

    private static UUID DEVICE_UUID;
    String deviceName, responseStartChar, responseEndChar;
    TextView tvResponse;
    Button btnTurnLeft, btnTurnRight;
    ImageButton imgBtnStop;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_avto);
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
        tvResponse = (TextView) findViewById(R.id.textViewCarResponse);
        btnTurnLeft = (Button) findViewById(R.id.buttonTurnLeft);
        btnTurnRight = (Button) findViewById(R.id.buttonTurnRight);
        imgBtnStop = (ImageButton) findViewById(R.id.imageButtonStop);

        bt = new BoreToolbox(getApplicationContext());
        // initialize BluetoothHandler
        bth = new BluetoothHandler(BluetoothAdapter.getDefaultAdapter(), DEVICE_UUID);

        /*
        * handler for receiving data from BT module
        */
        responseHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Bundle bundle = msg.getData();
                String myMsg = bundle.getString("bt_response");

                recDataString.append(myMsg);                                        //keep appending to string until end char
                int endOfLineIndex = recDataString.indexOf("#");                    // determine the end-of-line
                if (endOfLineIndex > 0) {                                           // make sure there data before ~
                    String dataInPrint = recDataString.substring(1, endOfLineIndex);    // extract string (without start char)
                    tvResponse.setText(dataInPrint);
                    //int dataLength = dataInPrint.length();                          //get length of data received
                    //tvRespondLength.setText("[" + String.valueOf(dataLength) + "]");

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

        if(bth.isBluetoothReady()){

            if(bth.isTargetDevicePaired(deviceName)){

                // try to establish connection
                if(bth.establishConnection()){

                    mConnectedThread = new ConnectedThread(bth.getSocket());
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
            bth.getSocket().close();
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
		getMenuInflater().inflate(R.menu.avto, menu);
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

    public void carStop(View view){

        // TODO - prepare command list/xml/table - add instruction translation into new 'values' (../res/values/instructions.xml) folder

        // send command to stop the car

        String msg = "010900";

        // if command not ended with endChar, append endChar
        char ch = msg.charAt(msg.length() - 1);
        if(ch!=responseEndChar.charAt(0))
            msg = msg + responseEndChar;

        mConnectedThread.write(msg);
    }

    public void carRight(View view){

        // send command to turn the car right

        String msg = "011800";

        // if command not ended with endChar, append endChar
        char ch = msg.charAt(msg.length() - 1);
        if(ch!=responseEndChar.charAt(0))
            msg = msg + responseEndChar;

        mConnectedThread.write(msg);

    }

    public void carLeft(View view){

        // send command to turn the car left

        String msg = "010000";

        // if command not ended with endChar, append endChar
        char ch = msg.charAt(msg.length() - 1);
        if(ch!=responseEndChar.charAt(0))
            msg = msg + responseEndChar;

        mConnectedThread.write(msg);

    }

    public void checkBluetooth(){

        // check if BT supported and enabled
        if(bth.isBluetoothSupported()){
            if(bth.isBluetoothEnabled()){
                // BT supported and enabled
                btnTurnLeft.setEnabled(true);
                btnTurnRight.setEnabled(true);
                imgBtnStop.setEnabled(true);
            } else {
                // Bluetooth NOT enabled
                btnTurnLeft.setEnabled(false);
                btnTurnRight.setEnabled(false);
                imgBtnStop.setEnabled(false);
            }
        } else {
            bt.print("Device does NOT support Bluetooth!");
            btnTurnLeft.setEnabled(false);
            btnTurnRight.setEnabled(false);
            imgBtnStop.setEnabled(false);
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

        /*
        * listening to BT module
        * */
        public void run() {

            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);

                    // Send the obtained bytes to the activity via handler
                    Message msg = responseHandler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("bt_response", readMessage);
                    msg.setData(bundle);
                    responseHandler.sendMessage(msg);

                } catch (IOException e) {
                    //break;
                }
            }
        }

        /*
        * send command to BT module
        * */
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
