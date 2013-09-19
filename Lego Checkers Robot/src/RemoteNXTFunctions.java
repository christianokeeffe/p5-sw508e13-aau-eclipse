import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.ColorSensor.Color;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.remote.RemoteNXT;


public class RemoteNXTFunctions {
	RemoteNXT BottomNXT = null;
	private static final int xFactor = -130;
	private static final int yFactor = -310;
	private static final int displacementFactor = 4;
    private int PresentX = 0;
    private int PresentY = 0;
    private TouchSensor TouchOnY;
    private TouchSensor TouchOnZ;
    private ColorSensor ColorSensorOnBoard;
	
	public RemoteNXTFunctions() throws InterruptedException{
		connect();
		BottomNXT.A.setSpeed(400);
		BottomNXT.B.setSpeed(400);
	    Motor.A.setSpeed(100);
	    Motor.B.setSpeed(1000);
	    BottomNXT.A.setAcceleration(3000);
	    BottomNXT.B.setAcceleration(3000);
	    Motor.A.setAcceleration(3000);
	    Motor.B.setAcceleration(3000);
	    TouchOnY = new TouchSensor(BottomNXT.S1);
	    TouchOnZ = new TouchSensor(SensorPort.S2);
	    ColorSensorOnBoard = new ColorSensor(SensorPort.S1);
	    Reset();
	}
	
	public ColorSensor.Color GetColorOnField (int x, int y) throws IOException{
		MoveSensorTo(x, y, false);
		
		return ColorSensorOnBoard.getColor();
	}
	
	private void MoveSensorTo(int x, int y, boolean GoToMagnet) throws IOException
	{
		MoveTopTo(y);
		MoveButtomTo(x,GoToMagnet);

		BottomNXT.A.waitComplete();
		BottomNXT.B.waitComplete();
		Motor.B.waitComplete();
	}
	
	private void MoveTopTo(int y) throws IOException{
		Motor.B.rotate(y*yFactor-PresentY, true);
		PresentY = y*yFactor;
	}
	
	private void MoveButtomTo(int x, boolean GoToMagnet)
	{
		int displacement = 0;
		if(GoToMagnet == true){
			displacement =  (int) (xFactor*displacementFactor);
		}
		MoveBothAAndBMotor(x*xFactor-PresentX+displacement);
		
		PresentX = x*xFactor+displacement;
	}
	
	private void MoveBothAAndBMotor(int angle){
		BottomNXT.A.rotate(angle, true);
		BottomNXT.B.rotate(angle,true);
	}
	
	private void Reset(){
		Motor.B.setSpeed(200);
		Motor.A.backward();
		Motor.B.forward();

		while(!TouchOnY.isPressed() || !TouchOnZ.isPressed())
		{
			if(TouchOnY.isPressed()){
				Motor.B.stop();
			}
			if(TouchOnZ.isPressed()){
				Motor.A.stop();
			}
		}
		Motor.B.stop();
		Motor.A.stop();
		Motor.B.setSpeed(1000);
	}
	
	private void connect() throws InterruptedException{
		// Now connect
	    try {
	        LCD.clear();
	        LCD.drawString("Connecting...",0,0);
	    	BottomNXT = new RemoteNXT("CheckBottom", Bluetooth.getConnector());
	    	LCD.clear();
	        LCD.drawString("Connected",0,1);
	        Thread.sleep(2000);
	    } catch (IOException ioe) {
	    	LCD.clear();
	        LCD.drawString("Conn Failed",0,0);
	        Thread.sleep(2000);
	        System.exit(1);
	    }
	}
}
