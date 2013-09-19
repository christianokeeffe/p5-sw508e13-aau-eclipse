import java.io.IOException;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorConstants;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.remote.RemoteNXT;
import lejos.nxt.remote.RemoteSensorPort;
import lejos.util.Delay;


public class RemoteNXTFunctions {
	RemoteNXT CheckTop = null;
	private static final int xFactor = 130;
	private static final int yFactor = -300;
	private static final int displacementFactor = 4;
    private int PresentX = 0;
    private int PresentY = 0;
    TouchSensor TouchOnY;
    TouchSensor TouchOnZ;
	
	public RemoteNXTFunctions() throws InterruptedException{
		connect();
	    Motor.A.setSpeed(400);
	    Motor.B.setSpeed(400);
	    CheckTop.A.setSpeed(100);
	    Motor.A.setAcceleration(3000);
	    Motor.B.setAcceleration(3000);
	    TouchOnY = new TouchSensor(SensorPort.S1);
	    TouchOnZ = new TouchSensor(CheckTop.S2);
	    Reset();
	}
	
	public int GetColorOnField (int x, int y) throws IOException{
		MoveSensorTo(x, y, false);
		return 0;
	}
	
	private void MoveSensorTo(int x, int y, boolean GoToMagnet) throws IOException
	{
		MoveTopTo(y);
		MoveButtomTo(x,GoToMagnet);

		Motor.A.waitComplete();
		Motor.B.waitComplete();
		CheckTop.A.waitComplete();
	}
	
	private void MoveTopTo(int y) throws IOException{
		CheckTop.A.rotate(y*yFactor-PresentY, true);
		PresentY = y*yFactor;
	}
	
	private void MoveButtomTo(int x, boolean GoToMagnet)
	{
		int displacement = 0;
		if(GoToMagnet == true){
			displacement =  (int) (xFactor*displacementFactor);
		}
		LCD.clear();
		LCD.drawInt(x, 0, 0);
		LCD.drawInt(displacement, 0, 1);
		LCD.drawInt(PresentX, 0, 3);
		LCD.drawInt(x*xFactor-PresentX+displacement, 0, 2);
		LCD.refresh();
		MoveBothAAndBMotor(x*xFactor-PresentX+displacement);
		
		PresentX = x*xFactor+displacement;
	}
	
	private void MoveBothAAndBMotor(int angle){
		Motor.A.rotate(angle, true);
		Motor.B.rotate(angle,true);
	}
	
	private void Reset(){
		CheckTop.B.setSpeed(100);
		CheckTop.A.backward();
		CheckTop.B.forward();

		while(!TouchOnY.isPressed() || !TouchOnZ.isPressed())
		{
			if(TouchOnY.isPressed()){
				CheckTop.B.stop();
			}
			if(TouchOnZ.isPressed()){
				CheckTop.A.stop();
			}
		}
		CheckTop.B.stop();
		CheckTop.A.stop();
		CheckTop.B.setSpeed(1000);
	}
	
	private void connect() throws InterruptedException{
		// Now connect
	    try {
	        LCD.clear();
	        LCD.drawString("Connecting...",0,0);
	    	CheckTop = new RemoteNXT("CheckTop", Bluetooth.getConnector());
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
