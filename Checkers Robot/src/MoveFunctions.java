
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
    private static final int xFactor = 135;
    private BTConnection connection;

	DataOutputStream output;
    DataInputStream Input;
    
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
        
        output=connection.openDataOutputStream();
        Input=connection.openDataInputStream();
    }

	public void MoveSensorTo(int x, int y) throws IOException
	{
		MoveTopMotor(y);
		Motor.A.rotate(x*xFactor, true);
		Motor.B.rotate(x*xFactor,true);
		Motor.A.waitComplete();
		Motor.B.waitComplete();	
		WaitForTopmoterStop();
	}
	
	private void WaitForTopmoterStop () throws IOException{
		Input.readBoolean();
	}
	
	private void MoveTopMotor(int y) throws IOException{
		if (connection == null){
			MakeConnection();
		}
		

	    output.writeUTF("move");
	    output.flush();
		output.writeInt(y);
		output.flush();
        
	}
}
