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
	RemoteNXT TopNXT = null;
	private static final int xFactor = -130;
	private static final int yFactor = -310;
	private static final int displacementFactor = 4;
    private int PresentX = 0;
    private int PresentY = 0;
    TouchSensor TouchOnY;
    TouchSensor TouchOnZ;
	
	public RemoteNXTFunctions() throws InterruptedException{
		connect();
	    Motor.A.setSpeed(400);
	    Motor.B.setSpeed(400);
	    TopNXT.A.setSpeed(100);
	    TopNXT.B.setSpeed(200);
	    Motor.A.setAcceleration(3000);
	    Motor.B.setAcceleration(3000);
	    TopNXT.A.setAcceleration(3000);
	    TopNXT.B.setAcceleration(3000);
	    TouchOnY = new TouchSensor(SensorPort.S1);
	    TouchOnZ = new TouchSensor(TopNXT.S2);
	    Reset();
	}
	
	public ColorSensor.Color GetColorOnField (int x, int y) throws IOException{
		MoveSensorTo(x, y, false);
		return null;
	}
	
	private void MoveSensorTo(int x, int y, boolean GoToMagnet) throws IOException
	{
		MoveTopTo(y);
		MoveButtomTo(x,GoToMagnet);

		Motor.A.waitComplete();
		Motor.B.waitComplete();
		TopNXT.B.waitComplete();
	}
	
	private void MoveTopTo(int y) throws IOException{
		TopNXT.B.rotate(y*yFactor-PresentY, true);
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
		Motor.A.rotate(angle, true);
		Motor.B.rotate(angle,true);
	}
	
	private void Reset(){
		TopNXT.A.backward();
		TopNXT.B.forward();

		while(!TouchOnY.isPressed() || !TouchOnZ.isPressed())
		{
			if(TouchOnY.isPressed()){
				TopNXT.B.stop();
			}
			if(TouchOnZ.isPressed()){
				TopNXT.A.stop();
			}
		}
		TopNXT.B.stop();
		TopNXT.A.stop();
	}
	
	private void connect() throws InterruptedException{
		// Now connect
	    try {
	        LCD.clear();
	        LCD.drawString("Connecting...",0,0);
	    	TopNXT = new RemoteNXT("CheckTop", Bluetooth.getConnector());
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
