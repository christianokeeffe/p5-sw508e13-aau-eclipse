import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lejos.nxt.Button;
import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.util.Delay;



public class SW508E13 {

	public static void main(String[] args) throws InterruptedException, IOException {
		RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
		
		List<Field> FlytteListe = new ArrayList<Field>();
		FlytteListe.add(new Field(5,4));
		FlytteListe.add(new Field(3,6));
		checkTopFunc.takePiece(new Field(3,2), FlytteListe);
		
		//Delay.msDelay(10000);
		//checkTopFunc.checkersBoard.analyzeBoard();
		LCD.drawString("Donedonedone", 0, 0);
		LCD.refresh();
		checkTopFunc.getColorOnField(7, 0);
		Button.ENTER.waitForPress();
		
		//code for printing a piece color
		/*while(!Button.ESCAPE.isDown()){
			
			ColorSensor.Color colorResult = checkTopFunc.getColorOnField(6, 7);
			int red = colorResult.getRed();
			int green = colorResult.getGreen();
			int blue = colorResult.getBlue();
			LCD.drawString("red: " + red, 0, 0);
			LCD.drawString("green: " + green, 0, 1);
			LCD.drawString("blue: " + blue, 0, 2);
			LCD.refresh();
			if((red >= 100 && red <= 160) && (green >= 10 && green <= 50) && (blue >= 5 && blue <= 60))
			{
				LCD.drawString("red",0,0);
				LCD.refresh();
			}
			else if((red >= 100 && red <= 175) && (green >= 80 && green <= 200) && (blue >= 80 && blue <= 200))
			{
				LCD.drawString("white",0,0);
				LCD.refresh();
			}
			else
			{
				LCD.drawString("empty", 0, 0);
				LCD.refresh();
			}
			Button.ENTER.waitForPress();
		}
		ColorSensor.Color Test = checkTopFunc.GetColorOnField(7, 7);
		LCD.drawString("R" + Test.getRed() + "G" + Test.getGreen() + "B" + Test.getBlue(), 0, 0);
		LCD.refresh();
		Delay.msDelay(10000);
		*/
	}

}
