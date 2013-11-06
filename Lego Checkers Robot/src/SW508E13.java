

import java.io.IOException;

import customExceptions.IllegalMove;
import customExceptions.NoKingLeft;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.TouchSensor;
import lejos.nxt.remote.RemoteNXT;
import lejos.util.Delay;



public class SW508E13 {

	public static void main(String[] args) throws IOException, NoKingLeft, InterruptedException {
		RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
		MI brain = new MI(checkTopFunc);

		Move bestMove;
		if(checkTopFunc.checkersBoard.myPeasentColor == 'r')
		{
			bestMove = brain.lookForBestMove();
			checkTopFunc.doMove(bestMove);
			checkTopFunc.getColorOnField(4, -2);
		}
		while(!Button.ESCAPE.isDown())
		{
			checkTopFunc.waitForRedButton();
			try {
				if(brain.nXTF.checkersBoard.analyzeBoard())
				{
					bestMove = brain.lookForBestMove();

					if(bestMove != null)
						brain.nXTF.doMove(bestMove);

					checkTopFunc.resetAfterMove();

					if(!brain.nXTF.checkersBoard.checkForGameHasEnded(true))
						brain.nXTF.checkersBoard.informer.playYourTurn();
				}

			} catch (IllegalMove e) {
				brain.nXTF.checkersBoard.informer.illeagalMove();
			}
		}

		/*
		  //FakeMI test = new FakeMI();
		  if(test.NXT.checkersBoard.myPeasentColor == 'r')
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
							brain.nXTF.checkersBoard.informer.playYourTurn();
					}

				} catch (IllegalMove e) {
					brain.nXTF.checkersBoard.informer.illeagalMove();
				}
			}
		}*/
	}
}
