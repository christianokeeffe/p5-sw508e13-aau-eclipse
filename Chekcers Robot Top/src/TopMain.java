
import java.io.*;

import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.robotics.*;
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
			ColorSensor cs = new ColorSensor(SensorPort.S1);
			LCD.drawString("Test 1", 0, 0);
			LCD.refresh();
			Delay.msDelay(3000);
			while(true){
			
        
			Sound.beepSequenceUp();
			LCD.drawString("Test 2", 0, 0);
			LCD.refresh();
			Delay.msDelay(3000);
			String inputMode = dis.readUTF();
			LCD.drawString("Test 3", 0, 0);
			LCD.refresh();
			Delay.msDelay(3000);
			String inputContent = dis.readUTF();
			LCD.drawString("Test 4", 0, 0);
			LCD.refresh();
			Delay.msDelay(3000);
			switch(inputMode){
			case "ColorSensor":
				ColorSensor.Color ColorTesting = cs.getColor();
				int Red = ColorTesting.getRed();
				int Green = ColorTesting.getGreen();
				int Blue = ColorTesting.getBlue();
				int Background = ColorTesting.getBackground();
				int ID = cs.getColorID();
				
				dos.write(Red);
				dos.flush();
				dos.write(Green);
				dos.flush();
				dos.write(Blue);
				dos.flush();
				dos.write(Background);
				dos.write(ID);
				break;
			}
			dos.writeBoolean(true);
			dos.flush();
        }
	}

}
