import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.util.Delay;



public class SW508E13 {

	public static void main(String[] args) throws InterruptedException, IOException {
		RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
		
		List<Field> FlytteListe = new ArrayList<Field>();
		FlytteListe.add(new Field(4,5));
		//FlytteListe.add(new Field(6,3));
		checkTopFunc.MoveAndTakePiece(new Field(2,3), FlytteListe);
		FlytteListe.clear();
		/*
		FlytteListe.add(new Field(3,0));
		checkTopFunc.MoveAndTakePiece(new Field(2,1), FlytteListe);
		Delay.msDelay(10000);
		*/
		checkTopFunc.CheckersBoard.analyzeBoard();
		//LCD.drawString("Donedonedone", 0, 0);
		//LCD.refresh();
		Button.ENTER.waitForPress();
		/*ColorSensor.Color Test = checkTopFunc.GetColorOnField(7, 7);
		LCD.drawString("R" + Test.getRed() + "G" + Test.getGreen() + "B" + Test.getBlue(), 0, 0);
		LCD.refresh();
		Delay.msDelay(10000);
		checkTopFunc.GetColorOnField(2, 2);*/
	}

}
