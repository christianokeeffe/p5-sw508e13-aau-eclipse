
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

			dis = btc.openDataInputStream();
			dos = btc.openDataOutputStream();
			while(true){
			
        
			Sound.beepSequenceUp();
			String inputline = dis.readUTF();
			
			switch(inputline){
			case "move":
				Motor.A.rotate(dis.readInt()*yFactor);
				break;
			case "reset":
				LCD.drawString("reset", 0, 0);
				break;
			}
			dos.writeBoolean(true);
			dos.flush();
        }
	}

}
