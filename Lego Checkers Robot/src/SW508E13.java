

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.TouchSensor;
import lejos.util.Delay;



public class SW508E13 {

	public static void main(String[] args) throws Exception {
		//RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
		FakeMI test = new FakeMI();
		TouchSensor bigRedButton = new TouchSensor(test.NXT.bottomNXT.S3);
		communication Com = new communication();
		/*
		List<Field> FlytteListe = new ArrayList<Field>();
		FlytteListe.add(new Field(3,4));
		FlytteListe.add(new Field(5,2));
		FlytteListe.add(new Field(3,0));
		checkTopFunc.takePiece(new Field(1,6), FlytteListe);
		 */
		//Delay.msDelay(10000);
		/*Field outside = new Field();
		Field inside = new Field();
		inside.x = 5;
		inside.y = 5;
		outside.x = 7;
		outside.y = -2;

		checkTopFunc.movePiece(outside,inside, false);*/
		
		if(test.NXT.checkersBoard.myPeasentColor == 'r')
		{
			test.decideMovement();
			test.NXT.getColorOnField(4, -2);
		}
		while(!Button.ESCAPE.isDown())
		{
			if(bigRedButton.isPressed()){
				if(test.NXT.checkersBoard.analyzeBoard())
				{
					if(!test.NXT.checkersBoard.checkForGameHasEnded(false))
					{
						test.decideMovement();
						test.NXT.getColorOnField(4, -2);
						if(!test.NXT.checkersBoard.checkForGameHasEnded(true))
							Com.playYourTurn();
					}
				}
				else
				{
					Com.illeagalMove();
				}
			}
		}

		/*//code for printing a piece color
		while(!Button.ESCAPE.isDown())
		{
			test.NXT.checkersBoard.getColor(3, 4);
			
			Button.ENTER.waitForPress();
			
		}*/
		/*ColorSensor.Color Test = checkTopFunc.GetColorOnField(7, 7);
		LCD.drawString("R" + Test.getRed() + "G" + Test.getGreen() + "B" + Test.getBlue(), 0, 0);
		LCD.refresh();
		Delay.msDelay(10000);*/
	}
}
