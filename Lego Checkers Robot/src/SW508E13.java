import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.util.Delay;



public class SW508E13 {

	public static void main(String[] args) throws InterruptedException, IOException {
		RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
		checkTopFunc.GetColorOnField(7, 7);
		
		Board test = new Board(checkTopFunc);
		test.analyzeBoard();
		LCD.drawString("Donedonedone", 0, 0);
		LCD.refresh();
		Button.ENTER.waitForPress();
		/*ColorSensor.Color Test = checkTopFunc.GetColorOnField(7, 7);
		LCD.drawString("R" + Test.getRed() + "G" + Test.getGreen() + "B" + Test.getBlue(), 0, 0);
		LCD.refresh();
		Delay.msDelay(10000);
		checkTopFunc.GetColorOnField(2, 2);*/
	}

}
