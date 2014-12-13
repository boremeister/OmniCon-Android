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
    private static BluetoothAdapter btAdapter = null;
    private static UUID deviceUuid = null;

	private static BluetoothDevice targetDevice = null;
	private static BluetoothSocket deviceSocket = null;

    // constructor
    public BluetoothHandler(BluetoothAdapter ba, UUID du){
        btAdapter = ba;
        deviceUuid = du;
    }

    public BluetoothSocket getSocket(){
        return deviceSocket;
    }

    /**
     * checks if Bluetooth adapter is supported
     *
     * @return true/false
     */
    public boolean isBluetoothSupported(){

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
    public boolean isBluetoothEnabled(){

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
    public ArrayList<String> getPairedDevices(){

        ArrayList<String> pairedDevicesList = new ArrayList<String>();

        if(btAdapter != null){
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

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
    public boolean isTargetDevicePaired(String deviceName){

        if(btAdapter != null){

            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

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
            deviceSocket = targetDevice.createInsecureRfcommSocketToServiceRecord(deviceUuid);
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


}