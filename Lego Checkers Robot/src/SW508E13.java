

import java.io.IOException;

import customExceptions.IllegalMove;
import customExceptions.NoKingLeft;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.TouchSensor;
import lejos.util.Delay;



public class SW508E13 {

	public static void main(String[] args) throws IOException, NoKingLeft, InterruptedException {
		RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
		//FakeMI test = new FakeMI();
		MI brain = new MI(checkTopFunc);
		TouchSensor bigRedButton = new TouchSensor(checkTopFunc.bottomNXT.S3);
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

		Move bestMove;
		if(checkTopFunc.checkersBoard.myPeasentColor == 'r')
		{
			
			bestMove = brain.lookForBestMove();
			checkTopFunc.doMove(bestMove);
			checkTopFunc.getColorOnField(4, -2);
			
		}
		while(!Button.ESCAPE.isDown())
		{
			if(bigRedButton.isPressed()){
				try {
					
					//checkTopFunc.checkersBoard.analyzeBoard();
					brain.nXTF.checkersBoard.analyzeBoard();
					
					if(!brain.nXTF.checkersBoard.checkForGameHasEnded(false))
					{
						
						bestMove = brain.lookForBestMove();
						
						
						
						//brain.nXTF.doMove(bestMove);
						//brain.nXTF.getColorOnField(4, -2);
						
						
						for(Field[] f : brain.nXTF.checkersBoard.myBoard)
						{
							for(Field field : f)
							{
								if(field.getPieceOnField() != null)
								{
									brain.nXTF.checkersBoard.getColor(field.getPieceOnField().x, field.getPieceOnField().y);
									Delay.msDelay(1000);
								}
							}
						}
						
						
						if(!brain.nXTF.checkersBoard.checkForGameHasEnded(true))
							Com.playYourTurn();
					
					}

				} catch (IllegalMove e) {
					Com.illeagalMove();
				}
			}
		}
		
		
		
		
		
		
		/*if(test.NXT.checkersBoard.myPeasentColor == 'r')
		{
			test.decideMovement();
			test.NXT.getColorOnField(4, -2);
		}
		while(!Button.ESCAPE.isDown())
		{
			if(bigRedButton.isPressed()){
				try {
					test.NXT.checkersBoard.analyzeBoard();
					if(!test.NXT.checkersBoard.checkForGameHasEnded(false))
					{
						test.decideMovement();
						test.NXT.getColorOnField(4, -2);
						if(!test.NXT.checkersBoard.checkForGameHasEnded(true))
							Com.playYourTurn();
					}

				} catch (IllegalMove e) {
					Com.illeagalMove();
				}
			}
		}*/

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
