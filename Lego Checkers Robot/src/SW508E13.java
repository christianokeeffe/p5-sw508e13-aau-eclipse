

import java.io.IOException;

import customExceptions.IllegalMove;
import customExceptions.NoKingLeft;
import lejos.nxt.Button;

public class SW508E13 {

	public static void main(String[] args) throws IOException, NoKingLeft, InterruptedException {
		RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
		MI mi = new MI(checkTopFunc);

		Move bestMove;
		if(checkTopFunc.checkersBoard.myPeasentColor == 'r')
		{
			bestMove = mi.lookForBestMove();
			checkTopFunc.doMove(bestMove);
			checkTopFunc.resetAfterMove();
		}
		while(!Button.ESCAPE.isDown())
		{
			checkTopFunc.waitForRedButton();
			try {
				if(mi.nXTF.checkersBoard.analyzeFunctions.analyzeBoard())
				{
					bestMove = mi.lookForBestMove();

					if(bestMove != null)
						mi.nXTF.doMove(bestMove);

					checkTopFunc.resetAfterMove();

					if(!mi.nXTF.checkersBoard.analyzeFunctions.checkForGameHasEnded(true))
						mi.nXTF.checkersBoard.informer.playYourTurn();
				}

			} catch (IllegalMove e) {
				mi.nXTF.checkersBoard.informer.illeagalMove();
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
