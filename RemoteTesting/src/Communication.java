import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.remote.RemoteNXT;
import lejos.nxt.remote.RemoteSensorPort;

public class Communication {
	RemoteNXT BottomNXT = null;
	
	private void connect() throws InterruptedException{
		// Now connect
	    try {
	        LCD.clear();
	        LCD.drawString("Connecting...",0,0);
	    	BottomNXT = new RemoteNXT("CheckBottom", Bluetooth.getConnector());
	    	LCD.clear();
	        LCD.drawString("Connected",0,1);
	        Thread.sleep(2000);
	        LCD.clear();
	    } catch (IOException ioe) {
	    	LCD.clear();
	        LCD.drawString("Conn Failed",0,0);
	        Thread.sleep(2000);
	        System.exit(1);
	    }
	}
	
	public Communication() throws InterruptedException {
		
		connect();
		while(true){
		byte[] message = BottomNXT.receiveMessage(1, 1, true);
		if(message != null){
		LCD.drawString(message.toString(), 0, 1);
		LCD.refresh();
		}
		}
		
	
		
	}

}
	
