
import java.io.*;

import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.robotics.navigation.*;
import lejos.util.Delay;

public class TopMain {


    private static final int yFactor = -260;
	public static void main(String[] args) throws IOException {
		DataInputStream dis = null;
			DataOutputStream dos = null;
	   	        
			Sound.twoBeeps();
	    
			BTConnection btc = Bluetooth.waitForConnection();
			if (btc == null)
				throw new IOException("Connect fail");
				
			TouchSensor zTouch = new TouchSensor(SensorPort.S2);
			dis = btc.openDataInputStream();
			dos = btc.openDataOutputStream();
			
			Motor.A.setSpeed(1000);
			Motor.B.setSpeed(200);
			
			while(true){
			
        
			Sound.beepSequenceUp();
			String inputMode = dis.readUTF();
			String inputContent = dis.readUTF();
			switch(inputMode){
			case "move":
				Motor.A.rotate((Integer.parseInt(inputContent))*yFactor);
				break;
			case "reset":
				Motor.B.backward();
				LCD.drawString("Running", 0, 0);
				LCD.refresh();
				while (!zTouch.isPressed()) {
			    	// try again
			    }
				LCD.drawString("Done", 0, 0);
				LCD.refresh();
				Motor.B.stop();
				dos.writeBoolean(true);
				dos.flush();
				break;
			}
			dos.writeBoolean(true);
			dos.flush();
        }
	}

}
