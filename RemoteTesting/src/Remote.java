import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.remote.RemoteMotor;
import lejos.nxt.remote.RemoteNXT;
import lejos.util.TextMenu;

public class Remote {
	public static void main(String[] args) throws Exception {
		RemoteNXT CheckTop = null;	
		int power = 0;
		int mode = 1;
		int motor = 0;
		String motorString = "Motor:";
		String modeString = "Mode:";
		String powerString = "Power:";
		String batteryString = "Battery:";
		String lightString = "Light:";
		String tachoString = "Tacho:";

        // Get the type of communications to be used
        String[] connectionStrings = new String[]{"Bluetooth"};
        TextMenu connectionMenu = new TextMenu(connectionStrings, 1, "Connection");
        NXTCommConnector[] connectors = {Bluetooth.getConnector()};

        int connectionType = connectionMenu.select();

        // Now connect
        try {
            LCD.clear();
            LCD.drawString("Connecting...",0,0);
        	CheckTop = new RemoteNXT("CheckTop", connectors[connectionType]);
        	LCD.clear();
            LCD.drawString("Type: " + connectionStrings[connectionType], 0, 0);
            LCD.drawString("Connected",0,1);
            Thread.sleep(2000);
        } catch (IOException ioe) {
        	LCD.clear();
            LCD.drawString("Conn Failed",0,0);
            Thread.sleep(2000);
            System.exit(1);
        }

        LCD.clear();
		RemoteMotor[] motors = {CheckTop.A, CheckTop.B, CheckTop.C};
		LightSensor light = new LightSensor(CheckTop.S2);
		while (true) {
			// Get data from the remote NXT and display it
			LCD.drawString(motorString,0,0);
			LCD.drawInt(motor, 3, 10, 0);
			LCD.drawString(powerString,0,1);
			LCD.drawInt(power, 3, 10, 1);
			LCD.drawString(modeString,0,2);
			LCD.drawInt(mode, 3, 10, 2);
			LCD.drawString(tachoString,0,3);
			LCD.drawInt(motors[motor].getTachoCount(), 6,  7, 3);
			LCD.drawString(batteryString,0,4);
			LCD.drawInt(CheckTop.Battery.getVoltageMilliVolt(), 6,  7, 4);
			LCD.drawString(lightString,0,5);
			LCD.drawInt(light.readValue(), 6,  7, 5);
			LCD.drawString(CheckTop.getBrickName(), 0, 6);
			LCD.drawString(CheckTop.getFirmwareVersion(), 0, 7);
			LCD.drawString(CheckTop.getProtocolVersion(), 4, 7);
			LCD.drawInt(CheckTop.getFlashMemory(), 6, 8, 7);

            // Do we have a button press?
			int key = Button.readButtons();
			if (key != 0)
            {
                // New command, work out what to do.
                if (key == 1) { // ENTER
                    power += 20;
                    if (power > 100) power = 0;
                } else if (key == 2) { // LEFT
                    mode++;
                    if (mode > 4) mode = 1;
                } else if (key == 4) { // RIGHT
                    motor++;
                    if (motor > 2) motor = 0;
                } else if (key == 8) { // ESCAPE
                    LCD.clear();
                    LCD.drawString("Closing...", 0, 0);
                    for(int i = 0; i < motors.length; i++)
                        motors[i].flt();
                    CheckTop.close();
                    Thread.sleep(2000);
                    System.exit(0);
                }

                LCD.clear();
                LCD.drawString("Setting power",0,0);
                motors[motor].setPower(power);
                LCD.drawString("Moving motor",0,1);
                if (mode == 1) motors[motor].forward();
                else if (mode == 2) motors[motor].backward();
                else if (mode == 3) motors[motor].flt();
                else if (mode == 4) motors[motor].stop();
                // Wait for the button to be released...
                while (Button.readButtons() != 0)
                    Thread.yield();
                LCD.clear();
            }
		}
	}
}