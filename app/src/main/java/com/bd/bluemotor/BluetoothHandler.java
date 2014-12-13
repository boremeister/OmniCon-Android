package com.bd.bluemotor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import android.widget.Toast;

public class BluetoothHandler {

	private static String LOG_TAG = "com.bd.bluemotor.BluetoothHandler";
	private static final UUID DEVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	//private static String deviceName = "";
	private static BluetoothAdapter btAdapter = null;
	private static ArrayList<String> pairedDevicesList = null;
	private static Set<BluetoothDevice> pairedDevices = null;
	private static BluetoothDevice targetDevice = null;
	private static BluetoothSocket deviceSocket = null;

    public BluetoothHandler(BluetoothAdapter ba){
        btAdapter = ba;
    }

	public static void init() {
		if(btAdapter==null)
			btAdapter = BluetoothAdapter.getDefaultAdapter();
	}
	
	public static UUID getUUID(){
		return BluetoothHandler.DEVICE_UUID;
	}

	/*
	public static String getDeviceName() {
		return deviceName;
	}

	public static void setDeviceName(String deviceName) {
		BluetoothHandler.deviceName = deviceName;
	}
	*/
	
	public static String getDeviceAddress(){
		return targetDevice!=null ? targetDevice.getAddress() : "";
	}

    public static BluetoothDevice getTargetDevice(){
        return targetDevice;
    }

	/**
	 * checks if Bluetooth adapter is supported
	 * 
	 * @return true/false
	 */
 	public static boolean isBluetoothSupported(){
		
		// check if BT supported
		if(btAdapter!=null){
			return true;
		} else {
			return false;
		}
	}

    /**
     * checks if Bluetooth adapter is supported
     *
     * @return true/false
     */
    public boolean isBluetoothSupported1(){

        // check if BT supported
        if(btAdapter!=null){
            return true;
        } else {
            return false;
        }
    }

    /**
     * checks if Bluetooth adapter is enabled
     *
     * @return true/false
     */
    public static boolean isBluetoothEnabled(){

        // check if BT enabled
        if(isBluetoothSupported() && btAdapter.isEnabled()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * checks if Bluetooth adapter is enabled
     *
     * @return true/false
     */
    public boolean isBluetoothEnabled1(){

        // check if BT enabled
        if(isBluetoothSupported() && btAdapter.isEnabled()){
            return true;
        }else{
            return false;
        }
    }

    /**
     * checks if Bluetooth adapter is ready to be used
     *
     * @return true/false
     */
    public boolean isBluetoothReady(){

        // check if BT enabled
        if(isBluetoothSupported() && btAdapter.isEnabled()){
            return true;
        }else{
            return false;
        }
    }
	
	/**
	 * gets paired devices
	 * 
	 * @return ArrayList of paired devices
	 */
	public static ArrayList<String> getPairedDevices(){
		
		if(isBluetoothSupported() && isBluetoothEnabled()){
			pairedDevices = btAdapter.getBondedDevices();
			pairedDevicesList = new ArrayList<String>();
			
			// If there are paired devices
			if (pairedDevices.size() > 0) {
			    // Loop through paired devices
			    for (BluetoothDevice device : pairedDevices) {
			        // Add the name and address to an array adapter
			    	pairedDevicesList.add(device.getName() + " - " + device.getAddress() + "\n");
			    }
			}
		}
		
		return pairedDevicesList;
	}

    /**
     * check if any paired device
     *
     * @return true/false
     */
    public boolean doPairedDevicesExist(){

        if(btAdapter != null){
            pairedDevices = btAdapter.getBondedDevices();

            // If there are paired devices
            if (pairedDevices.size() > 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * gets paired devices
     *
     * @return ArrayList of paired devices
     */
    public ArrayList<String> getPairedDevices1(){

        if(btAdapter != null){
            pairedDevices = btAdapter.getBondedDevices();
            pairedDevicesList = new ArrayList<String>();

            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    // Add the name and address to an array adapter
                    pairedDevicesList.add(device.getName() + " - " + device.getAddress() + "\n");
                }
            }
        }

        return pairedDevicesList;
    }

    /**
	 * checks if 'deviceName' is already paired
	 * 
	 * @param deviceName
	 * @return true/false
	 */
	public static boolean isTargetDevicePaired(String deviceName){

		if(isBluetoothSupported() && isBluetoothEnabled()){
			pairedDevices = btAdapter.getBondedDevices();
			
			// If there are paired devices
			if (pairedDevices.size() > 0) {
			    // Loop through paired devices
			    for (BluetoothDevice device : pairedDevices) {
			        
			    	// check if deviceName already paired
			    	if(deviceName.equals(device.getName())){
			    		targetDevice = device;
			    		return true;
			    	}
			    }
			}
		}
		
		return false;
	}

    /**
     * checks if 'deviceName' is already paired
     *
     * @param deviceName
     * @return true/false
     */
    public boolean isTargetDevicePaired1(String deviceName){

        if(btAdapter != null){
            pairedDevices = btAdapter.getBondedDevices();

            // If there are paired devices
            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {

                    // check if deviceName already paired
                    if(deviceName.equals(device.getName())){
                        targetDevice = device;
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * checks if 'deviceName' is already paired
     *
     * @return true/false
     */
    public boolean establishConnection(){

        try{
            // create socket connection
            deviceSocket = targetDevice.createInsecureRfcommSocketToServiceRecord(DEVICE_UUID);
            Log.i(LOG_TAG, "Device socket created OK");
        } catch (IOException e) {
            Log.i(LOG_TAG, "Cannot create socket: " + e.toString());
        }

        try {

            // connect
            deviceSocket.connect();

            // connection established OK
            return true;

        } catch (IOException e) {

            Log.i(LOG_TAG, "Cannot open socket connection: " + e.toString());
            try {
                deviceSocket.close();
            } catch (Exception e2) {
                Log.i(LOG_TAG, "Cannot close socket connection: " + e.toString());
            }
        }

        return false;
    }

    public BluetoothSocket getSocket(){
        return deviceSocket;
    }

	public static boolean sendMsgToDevice(String message){
		
		try{
			deviceSocket = targetDevice.createInsecureRfcommSocketToServiceRecord(getUUID());
		} catch (IOException e) {
			Log.i(LOG_TAG, "Cannot create socket: " + e.toString());
			return false;
		}
		
		try {
			deviceSocket.connect();
		} catch (IOException e) {
			Log.i(LOG_TAG, "Cannot open socket connection: " + e.toString());
			try {
				deviceSocket.close();
			} catch (Exception e2) {
				Log.i(LOG_TAG, "Cannot close socket connection: " + e.toString());
				return false;
			}
			return false;
		}
		
		// apparently connection established OK
		// send message
		if(deviceSocket != null){
			byte[] msgBuffer = message.getBytes();			
			OutputStream outStream = null;

			try {
				outStream = deviceSocket.getOutputStream();
		        outStream.write(msgBuffer);
		        deviceSocket.close();
		        return true;
			} catch (IOException e) {
				Log.i(LOG_TAG, "Sending message to device failed: " + e.toString());
				return false;
			}
		}
		
		return false;
	}
}