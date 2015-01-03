package com.bd.bluemotor;

import android.content.Context;
import android.content.ContextWrapper;

public class CommandHandler extends ContextWrapper {

    private static int motor_servo_1 = 0;
    private static int motor_servo_2 = 0;
    private static int motor_dc_1 = 0;
    private static int motor_dc_2 = 0;
    private static int servo_min_angle = 0;
    private static int servo_default_angle = 0;
    private static int servo_max_angle = 0;
    private static int dc_min_speed = 0;
    private static int dc_max_speed = 0;
    private static int direction_forward = 0;
    private static int direction_backward = 0;
    private static String command_ending_char = "";
    private static String command = "";

    // constructor
    public CommandHandler(Context base){

        super(base);

        motor_servo_1 = getResources().getInteger(R.integer.motor_servo_1);
        motor_servo_2 = getResources().getInteger(R.integer.motor_servo_2);
        motor_dc_1 = getResources().getInteger(R.integer.motor_dc_1);
        motor_dc_2 = getResources().getInteger(R.integer.motor_dc_2);
        servo_min_angle = getResources().getInteger(R.integer.servo_min_angle);
        servo_default_angle = getResources().getInteger(R.integer.servo_default_angle);
        servo_max_angle = getResources().getInteger(R.integer.servo_max_angle);
        dc_min_speed = getResources().getInteger(R.integer.dc_min_speed);
        dc_max_speed = getResources().getInteger(R.integer.dc_max_speed);
        direction_forward = getResources().getInteger(R.integer.direction_forward);
        direction_backward = getResources().getInteger(R.integer.direction_backward);
        command_ending_char = getResources().getString(R.string.value_default_command_end_char);
    }

    /*
    * getters
    * */

    public static int getMotor_servo_1() {
        return motor_servo_1;
    }

    public static int getMotor_servo_2() {
        return motor_servo_2;
    }

    public static int getMotor_dc_1() {
        return motor_dc_1;
    }

    public static int getMotor_dc_2() {
        return motor_dc_2;
    }

    public static int getServo_min_angle() {
        return servo_min_angle;
    }

    public static int getServo_max_angle() {
        return servo_max_angle;
    }

    public static int getServo_default_angle() {
        return servo_default_angle;
    }

    public static int getDc_min_speed() {
        return dc_min_speed;
    }

    public static int getDc_max_speed() {
        return dc_max_speed;
    }

    public static int getDirection_forward() {
        return direction_forward;
    }

    public static int getDirection_backward() {
        return direction_backward;
    }

    public static String getCommand_ending_char() {
        return command_ending_char;
    }

    /*
    *
    * command methods
    *

	command list

	servo 1 - 0 1(smer vrtenja) XYZ (kot ~ 0-180)
	servo 2 - 1 1(smer vrtenja) XYZ (kot ~ 0-180)
	DC 1 - 2 1/0 (smer vrtenja ~ 1 - naprej, 0 - nazaj) XYZ (hitrost ~ 0-100)
	DC 2 - 3 1/0 (smer vrtenja ~ 1 - naprej, 0 - nazaj) XYZ (hitrost ~ 0-100)

	* */

    public static String turnServoOneLeft(int val){

        // check that angle is insider borders
        if(val < getServo_min_angle() || val > getServo_default_angle())
            val = getServo_default_angle();

        // prepare string command
        command = Integer.toString(getMotor_servo_1()) + Integer.toString(getDirection_forward()) + Integer.toString(val);

        checkCommandEnding();

        return command;
    }

    public static String turnServoOneMiddle(){

        // prepare string command
        command = Integer.toString(getMotor_servo_1()) + Integer.toString(getDirection_forward()) + Integer.toString(getServo_default_angle());

        checkCommandEnding();

        return command;
    }

    public static String turnServoOneRight(int val){

        // check that angle is insider borders
        if(val < getServo_default_angle() || val > getServo_max_angle())
            val = getServo_default_angle();

        // prepare string command
        command = Integer.toString(getMotor_servo_1()) + Integer.toString(getDirection_forward()) + Integer.toString(val);

        checkCommandEnding();

        return command;
    }

    public static String turnServoTwoLeft(int val){

        // check that angle is insider borders
        if(val < getServo_min_angle() || val > getServo_default_angle())
            val = getServo_default_angle();

        // prepare string command
        command = Integer.toString(getMotor_servo_2()) + Integer.toString(getDirection_forward()) + Integer.toString(val);

        checkCommandEnding();

        return command;
    }

    public static String turnServoTwoMiddle(){

        // prepare string command
        command = Integer.toString(getMotor_servo_2()) + Integer.toString(getDirection_forward()) + Integer.toString(getServo_default_angle());

        checkCommandEnding();

        return command;
    }

    public static String turnServoTwoRight(int val){

        // check that angle is insider borders
        if(val < getServo_default_angle() || val > getServo_max_angle())
            val = getServo_default_angle();

        // prepare string command
        command = Integer.toString(getMotor_servo_2()) + Integer.toString(getDirection_forward()) + Integer.toString(val);

        checkCommandEnding();

        return command;
    }

    public static String moveDcOneForward(int val){

        // check that speed is insider borders
        if(val < getDc_min_speed() || val > getDc_max_speed())
            val = 0;

        // prepare string command
        command = Integer.toString(getMotor_dc_1()) + Integer.toString(getDirection_forward()) + Integer.toString(val);

        checkCommandEnding();

        return command;
    }

    public static String moveDcOneBackward(int val){

        // check that speed is insider borders
        if(val < getDc_min_speed() || val > getDc_max_speed())
            val = 0;

        // prepare string command
        command = Integer.toString(getMotor_dc_1()) + Integer.toString(getDirection_backward()) + Integer.toString(val);

        checkCommandEnding();

        return command;
    }

    public static String moveDcTwoForward(int val){

        // check that speed is insider borders
        if(val < getDc_min_speed() || val > getDc_max_speed())
            val = 0;

        // prepare string command
        command = Integer.toString(getMotor_dc_2()) + Integer.toString(getDirection_forward()) + Integer.toString(val);

        checkCommandEnding();

        return command;
    }

    public static String moveDcTwoBackward(int val){

        // check that speed is insider borders
        if(val < getDc_min_speed() || val > getDc_max_speed())
            val = 0;

        // prepare string command
        command = Integer.toString(getMotor_dc_2()) + Integer.toString(getDirection_backward()) + Integer.toString(val);

        checkCommandEnding();

        return command;
    }

    // checks that command ending with correct char
    private static void checkCommandEnding(){

        char ch = command.charAt(command.length() - 1);
        if(ch!=getCommand_ending_char().charAt(0))
            command = command + getCommand_ending_char();

    }
}
