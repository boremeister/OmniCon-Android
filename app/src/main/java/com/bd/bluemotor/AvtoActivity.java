package com.bd.bluemotor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class AvtoActivity extends BaseActivity {

    private static final String DEBUG_TAG = "OmniCon.AvtoActivity";
    private BoreToolbox bt;
    private BluetoothHandler bth;
    private CommandHandler comHan;

    private Handler responseHandler;
    private ConnectedThread mConnectedThread;

    private StringBuilder recDataString = new StringBuilder();

    private static UUID DEVICE_UUID;
    private String deviceName, uuid, responseStartChar, responseEndChar, selectedFromList, servo1orientation, servo2orientation;
    TextView tvResponse;
    Button btnTurnLeft, btnTurnRight;
    ImageButton imgBtnStop;

    /* SEEK BAR TEST */
    private SeekBar sbLeds;
    private TextView tvProgress, tvAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_avto);
		// Show the Up button in the action bar.
		setupActionBar();

        // initialize
        bt = new BoreToolbox(this);
        comHan = new CommandHandler(this);

        // read values from preferences
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        deviceName = prefs.getString("device_name", bt.getStringResourceByName("value_bt_default_device_name"));
        uuid = prefs.getString("device_uuid", bt.getStringResourceByName("value_default_device_uuid"));
        responseStartChar = prefs.getString("command_start_char", bt.getStringResourceByName("value_default_command_start_char"));
        responseEndChar = prefs.getString("command_end_char", bt.getStringResourceByName("value_default_command_end_char"));
        servo1orientation = prefs.getString("servo_orientation_1", bt.getStringResourceByName("value_default_servo_1_orientation"));
        servo2orientation = prefs.getString("servo_orientation_2", bt.getStringResourceByName("value_default_servo_2_orientation"));
        DEVICE_UUID = UUID.fromString(uuid);

        Log.i(DEBUG_TAG, "Shared preferences read.");

        bth = new BluetoothHandler(BluetoothAdapter.getDefaultAdapter(), DEVICE_UUID);

        // UI fields
        tvResponse = (TextView) findViewById(R.id.textViewCarResponse);
        btnTurnLeft = (Button) findViewById(R.id.buttonTurnLeft);
        btnTurnRight = (Button) findViewById(R.id.buttonTurnRight);
        imgBtnStop = (ImageButton) findViewById(R.id.imageButtonStop);

        /*
        * handler for receiving data from BT module
        */
        responseHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                Bundle bundle = msg.getData();
                String myMsg = bundle.getString("bt_response");

                recDataString.append(myMsg);                                        //keep appending to string until end char
                int endOfLineIndex = recDataString.indexOf(responseEndChar);                    // determine the end-of-line
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

        /* SEEK BAR TEST */
        sbLeds = (SeekBar) findViewById(R.id.seekBarLed);
        tvProgress = (TextView) findViewById(R.id.textViewProgress);
        tvAction = (TextView) findViewById(R.id.textViewAction);

        sbLeds.setOnSeekBarChangeListener(SeekBarLedsTest);

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

    public void carMiddle(View view){

        // send command to stop the car
        String msg = "010900";
        msg = comHan.turnServoOneMiddle();

        mConnectedThread.write(msg);
    }

    public void carRight(View view){

        // send command to turn the car right
        String msg = "011800";
        msg = comHan.turnServoOneRight(150);

        mConnectedThread.write(msg);

    }

    public void carLeft(View view){

        // send command to turn the car left
        String msg = "010000";
        msg = comHan.turnServoOneLeft(30);

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

    /**
     * seek bar leds test
     */
    SeekBar.OnSeekBarChangeListener SeekBarLedsTest = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            tvProgress.setText("Value: " +  progress);

            // LED test
            String msg = "000000#";

            // prepare test command
            if (progress == 2) {
                msg = "100000#";
                Log.i(DEBUG_TAG, "Sending command 100000");
            }
            if (progress == 3) {
                msg = "010000#";
                Log.i(DEBUG_TAG, "Sending command 010000");
            }
            if (progress == 4) {
                msg = "001000#";
                Log.i(DEBUG_TAG, "Sending command 001000");
            }
            if (progress == 5) {
                msg = "000100#";
                Log.i(DEBUG_TAG, "Sending command 000100");
            }

            // send test command
            mConnectedThread.write(msg);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            tvAction.setText("sliding");
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            tvAction.setText("stop");

        }
    };
}

