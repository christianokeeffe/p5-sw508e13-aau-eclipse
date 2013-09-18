
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
    private static final int xFactor = 130;
    private BTConnection connection;
    private int PresentX = 0;
    private int PresentY = 0;

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
    
    public void reset() throws IOException{
    	WriteCommandToTop("reset", null);
    	
    }

	public void MoveSensorTo(int x, int y) throws IOException
	{
		MoveTopTo(y);
		MoveButtomTo(x);
		WaitForTopmoterStop();
	}
	
	public void MoveTopTo(int y) throws IOException{
		MoveTopMotor(y-PresentY);
		PresentY = y;
			
	}
	
	public void MoveButtomTo(int x)
	{
		Motor.A.rotate((x-PresentX)*xFactor, true);
		Motor.B.rotate((x-PresentX)*xFactor,true);
		PresentX = x;
		Motor.A.waitComplete();
		Motor.B.waitComplete();
	}
	
	private void WaitForTopmoterStop () throws IOException{
		Input.readBoolean();
	}
	
	private <T> void WriteCommandToTop(String Mode, T ContentToWrite) throws IOException{
		if (connection == null){
			MakeConnection();
		}
		
	    output.writeUTF(Mode);
	    output.flush();
	    if(ContentToWrite != null){
	    	output.writeUTF(ContentToWrite.toString());
	    	output.flush();
		}else{
			output.writeUTF("");
	    	output.flush();
		}
	}
	
	private void MoveTopMotor(int y) throws IOException{
		WriteCommandToTop("move", y);
	}
}
