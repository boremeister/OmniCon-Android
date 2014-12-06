package com.bd.bluemotor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class DriveScreenActivity extends Activity {

	public final static String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_drive_screen);
		// Show the Up button in the action bar.
		setupActionBar();
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
		getMenuInflater().inflate(R.menu.drive_screen, menu);
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
	
    public void sendMessage(View view) {
    	
    	Intent intent = new Intent(this, DisplayMessageActivity.class);
    	EditText editText = (EditText) findViewById(R.id.edit_message);
    	String message = editText.getText().toString();
    	intent.putExtra(EXTRA_MESSAGE, message);
    	startActivity(intent);

    }
    
    public void sendToBluetooth(View view){
    	
    	String deviceName = "HC-05";
    	BluetoothDevice target = null;
    	
    	Toast.makeText(getApplicationContext(), "Bluetooth testing ...", Toast.LENGTH_SHORT).show();
    	
		// bluetooth test
    	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		// check if BT is supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(getApplicationContext(), "Device does not support Bluetooth!", Toast.LENGTH_SHORT).show();
		} else {
			// check if BT is enabled
			if (!mBluetoothAdapter.isEnabled()) {
			    //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			    //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
				Toast.makeText(getApplicationContext(), "Bluetooth NOT enabled!", Toast.LENGTH_SHORT).show();
			
			} else {
				
				// check for paired devices
				Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
				ArrayList pairedList = new ArrayList();
				
				// If there are paired devices
				if (pairedDevices.size() > 0) {
				    // Loop through paired devices
				    for (BluetoothDevice device : pairedDevices) {
				        // Add the name and address to an array adapter to show in a ListView
				        //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
				    	pairedList.add(device.getName());
				    	
				    	// check if HC-05 already paired and save it into 'target'
				    	if(deviceName.equals(device.getName())){
				    		target = device;
				    	}
				    }
				    Toast.makeText(getApplicationContext(), "Showing paired devices!", Toast.LENGTH_SHORT).show();
				    
				    final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, pairedList);
				    
				    final ListView lv;
				    lv = (ListView) findViewById(R.id.listViewPairedDevices);
				    lv.setAdapter(adapter);
			
				} else {
					Toast.makeText(getApplicationContext(), "No paired devices!", Toast.LENGTH_SHORT).show();
				}
				
				// SEND DATA TO BT MODULE
				if(target!=null){
					String address = target.getAddress();
					Toast.makeText(getApplicationContext(), "HC-05 is already paired (" + address + ")!", Toast.LENGTH_SHORT).show();
					
					BluetoothSocket mySocket = null;
					UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
					try {
						mySocket = target.createInsecureRfcommSocketToServiceRecord(uuid);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						Toast.makeText(getApplicationContext(), "Can not create socket!", Toast.LENGTH_SHORT).show();
					}
					
					try {
						mySocket.connect();
					} catch (IOException e){
						try {
			                mySocket.close();
			            } catch (IOException e1){
			            	// empty
			            }
					}
					 
					//if(target.ACTION_ACL_CONNECTED.equals())
					//Toast.makeText(getApplicationContext(), "Socket"  + mySocket, Toast.LENGTH_SHORT).show();
					
					if (mySocket!=null) {
						String message="y";
						byte[] msgBuffer = message.getBytes();
						
						OutputStream outStream = null;
						
						try {
							outStream = mySocket.getOutputStream();
					        outStream.write(msgBuffer);
					        mySocket.close();
					      } catch (IOException e) {
					    	  // empty
					      }
					 }
					
				}
				
				
			}
		}
    }

}
