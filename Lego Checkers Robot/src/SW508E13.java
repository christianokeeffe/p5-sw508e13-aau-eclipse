import java.io.IOException;

import lejos.nxt.Button;
import lejos.util.Delay;



public class SW508E13 {

	public static void main(String[] args) throws InterruptedException, IOException {
		RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
		
		/*List<Field> FlytteListe = new ArrayList<Field>();
		FlytteListe.add(new Field(5,4));
		FlytteListe.add(new Field(3,6));
		checkTopFunc.takePiece(new Field(3,2), FlytteListe);*/
		
		//Delay.msDelay(10000);
		
		boolean changer = true;
		
		while(!Button.ESCAPE.isDown())
		{
			if (Button.ENTER.isDown()){
				if(changer)
				{
					checkTopFunc.checkersBoard.analyzeBoard();
					changer = false;
					checkTopFunc.getColorOnField(4, -2);
				}
				else
				{
					checkTopFunc.checkersBoard.findMissingPiece();
					changer = true;
					checkTopFunc.getColorOnField(4, -2);
				}
			}
			else if(Button.RIGHT.isDown()){
				checkTopFunc.checkersBoard.testRedPieces();
			}
			Delay.msDelay(1000);
		}
		//code for printing a piece color
		/*while(!Button.ESCAPE.isDown())
		{
			Color colorResult = checkTopFunc.getColorOnField(4, 5);
			int red = colorResult.getRed();
			int green = colorResult.getGreen();
			int blue = colorResult.getBlue();
			LCD.clear();
			LCD.drawString("red: " + red, 0, 0);
			LCD.drawString("green: " + green, 0, 1);
			LCD.drawString("blue: " + blue, 0, 2);
			LCD.refresh();
			if(red > 5 && green < 5 && blue < 5)
			{
				LCD.drawString("red",0,3);
				LCD.refresh();
			}
			else if(red > 0 && green > 0 && blue > 0)
			{
				LCD.drawString("white",0,3);
				LCD.refresh();
			}
			else if(green > 0 && red < 5 && blue < 5)
			{
				LCD.drawString("green",0,3);
				LCD.refresh();
			}
			else if(blue > 0 && red < 5 && green < 5)
			{
				LCD.drawString("blue",0,3);
				LCD.refresh();
			}
			else
			{
				LCD.drawString("empty", 0, 3);
				LCD.refresh();
			}
			//Button.ENTER.waitForPress();
			Delay.msDelay(500);
		}*/
		/*ColorSensor.Color Test = checkTopFunc.GetColorOnField(7, 7);
		LCD.drawString("R" + Test.getRed() + "G" + Test.getGreen() + "B" + Test.getBlue(), 0, 0);
		LCD.refresh();
		Delay.msDelay(10000);*/
	}
}
