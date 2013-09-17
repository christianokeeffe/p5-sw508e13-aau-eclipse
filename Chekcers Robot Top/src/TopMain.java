
import java.io.*;
import lejos.nxt.Motor;
import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.robotics.navigation.*;

public class TopMain {


    private static final int yFactor = 180;
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
        
			Motor.A.rotate(dis.readInt());
			dos.writeBoolean(true);
			dos.flush();
        }
	}

}
