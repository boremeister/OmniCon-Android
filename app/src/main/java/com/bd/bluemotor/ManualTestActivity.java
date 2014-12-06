package com.bd.bluemotor;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ManualTestActivity extends Activity {

	BoreToolbox bt;
	Button btnPairedDevices, btnSend;
	EditText etTargetDeviceName, etMessage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manual_test);
		// Show the Up button in the action bar.
		setupActionBar();
		
		bt = new BoreToolbox(getApplicationContext());
		btnPairedDevices = (Button) findViewById(R.id.buttonPairedDevices);
		btnSend = (Button) findViewById(R.id.buttonSend);
		etTargetDeviceName = (EditText) findViewById(R.id.editTextDeviceName);
		etMessage = (EditText) findViewById(R.id.editTextData);
		etTargetDeviceName.setText("HC-05");
		
		// check bluetooth status
		checkBluetooth();
		
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
		
		if(!BluetoothHandler.getPairedDevices().isEmpty()){
			// header text
			text = "Name - Address\n";
			// devices
			for (String device : BluetoothHandler.getPairedDevices()) {
				text += device.toString(); 
			}
		} else {
			text = "No paired devices!";
		}
		
		tv.setText(text);
	}
	
	public void sendToTarget(View view){
		
		// check if device is paired
		if(BluetoothHandler.isTargetDevicePaired(etTargetDeviceName.getText().toString())){
			
			// send data to it
			//Log.i("test.com.bt", "Logcat test - SendTOTarget called!");
			
			String msg = etMessage.getText().toString();
			
			// send message to device
			if(BluetoothHandler.sendMsgToDevice(msg)){
				bt.print("Message (" + msg + ") successfuly sent!");
			} else {
				bt.print("Message (" + msg + ") NOT sent!");
			}
		}
		
	}
	
	public void checkBluetooth(View view){

		// check bluetooth status
		checkBluetooth();
	}
	
	public void checkBluetooth(){
		
		// initialize BluetoothHandler
		BluetoothHandler.init();
		
		// check if BT supported and enabled
		if(BluetoothHandler.isBluetoothSupported()){
			if(BluetoothHandler.isBluetoothEnabled()){
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
}
