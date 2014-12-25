package com.bd.bluemotor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class ManualTestActivity extends BaseActivity {

    private BoreToolbox bt;
    private BluetoothHandler bth;
    private ConnectedThread mConnectedThread;

	Button btnPairedDevices, btnSend;
	EditText etTargetDeviceName, etCommand;
    TextView tvRespond, tvRespondLength, tvBluetoothConnectionOn, tvBluetoothConnectionOff;
    ListView lvPairedDevices;
    Handler responseHandler;
    private StringBuilder recDataString = new StringBuilder();
    private ArrayAdapter<String> listAdapter;

    private static UUID DEVICE_UUID;
    String deviceName, responseStartChar, responseEndChar, selectedFromList;

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
        tvBluetoothConnectionOn = (TextView) findViewById(R.id.textViewBluetoothConnectionOn);
        tvBluetoothConnectionOff = (TextView) findViewById(R.id.textViewBluetoothConnectionOff);
        tvRespond = (TextView) findViewById(R.id.textViewRespond);
        tvRespondLength = (TextView) findViewById(R.id.textViewRespondLength);
        lvPairedDevices = (ListView) findViewById(R.id.listViewPairedDevices);
        etCommand = (EditText) findViewById(R.id.editTextCommand);

        bt = new BoreToolbox(getApplicationContext());

        // initialize BluetoothHandler
        bth = new BluetoothHandler(BluetoothAdapter.getDefaultAdapter(), DEVICE_UUID);

        /*
        * listener for ListView selection
         */
        lvPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                selectedFromList = (String) (lvPairedDevices.getItemAtPosition(myItemInt));

                // get device's name only
                selectedFromList = selectedFromList.split(" - ")[0];

                etTargetDeviceName.setText(selectedFromList);
                bt.print("Device " + selectedFromList + " chosen.");
            }
        });

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

        if(bth.getSocket()!=null){

            try{
                bth.getSocket().close();
            }catch (IOException e){
                // empty
            }
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
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem mi = menu.findItem(R.id.menu_manual);
        if(mi != null){
            mi.setEnabled(false);
        }

        return true;

    }

    public void	getPairedDevices (View view){

        // get paired devices
        ArrayList<String> pairedDevicesList = bth.getPairedDevices();

		if(pairedDevicesList.isEmpty()){
            pairedDevicesList.add("No paired devices found!");
        }

        listAdapter = new ArrayAdapter<String>(this, R.layout.list_row, pairedDevicesList);

        lvPairedDevices.setAdapter(listAdapter);

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
		if(bth.isBluetoothSupported()){
			if(bth.isBluetoothEnabled()){
				// BT supported and enabled
                tvBluetoothConnectionOn.setTextSize(36);
                tvBluetoothConnectionOn.setTextColor(Color.GREEN);
                tvBluetoothConnectionOff.setTextSize(30);
                tvBluetoothConnectionOff.setTextColor(Color.GRAY);

				btnPairedDevices.setEnabled(true);
				btnSend.setEnabled(true);
			} else {
                // Bluetooth NOT enabled
                tvBluetoothConnectionOn.setTextSize(30);
                tvBluetoothConnectionOn.setTextColor(Color.GRAY);
                tvBluetoothConnectionOff.setTextSize(36);
                tvBluetoothConnectionOff.setTextColor(Color.RED);

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
