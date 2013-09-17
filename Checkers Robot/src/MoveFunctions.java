
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.Button;
import lejos.nxt.Sound;
import lejos.util.Delay;

import java.io.*;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.nxt.remote.RemoteNXT;
import lejos.robotics.navigation.*;

public class MoveFunctions {
    private static final int xFactor = 140;
    private static final int yFactor = 100;
    private BTConnection connection;
    
    public void MoveFunction() throws IOException{
    Motor.A.setSpeed(400);
	Motor.B.setSpeed(400);
	Motor.A.setAcceleration(2);
	Motor.B.setAcceleration(2);
	
	
    }
    
    private void MakeConnection() throws IOException{
    	String name = "CheckTop";
        
        Sound.twoBeeps();
        RemoteDevice receiver = Bluetooth.getKnownDevice(name);
        
        if (receiver == null)
    		throw new IOException("no such device");

        connection = Bluetooth.connect(receiver);
        if (connection == null)
    		throw new IOException("Connect fail");
    }

	public void MoveSensorTo(int x, int y) throws IOException
	{
		
		while(true)
		{
		Motor.A.rotate(x*xFactor, true);
		Motor.B.rotate(x*xFactor,true);
		Motor.A.waitComplete();
		Motor.B.waitComplete();	
		MoveTopMotor(100);
		Button.waitForAnyPress();}
	}
	
	private void MoveTopMotor(int z) throws IOException{
		if (connection == null){
			MakeConnection();
		}
		
		DataOutputStream output = connection.openDataOutputStream();
	    DataInputStream Input = connection.openDataInputStream();


		output.writeInt(z);
		output.flush();
        Input.readBoolean();
        Sound.beepSequenceUp();
	}
}
