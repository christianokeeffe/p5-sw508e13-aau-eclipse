
import java.io.*;

import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.robotics.navigation.*;

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
			if(inputline=="move" || inputline!="move"){
				Motor.A.rotate(dis.readInt()*yFactor);
			}
			dos.writeBoolean(true);
			dos.flush();
        }
	}

}
