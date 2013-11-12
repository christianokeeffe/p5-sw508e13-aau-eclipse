import java.io.IOException;

import custom.Exceptions.IllegalMove;
import custom.Exceptions.NoKingLeft;
import lejos.nxt.Button;

public final class SW508E13 {

    private SW508E13() {
        throw new AssertionError();
    }

    public static void main(String[] args)
            throws IOException, NoKingLeft, InterruptedException {
        RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
        MI mi = new MI(checkTopFunc);

        Piece temp = new Piece();
        temp.isCrowned = true;
        temp.canJump = true;
        temp.color = 'g';
        checkTopFunc.checkersBoard.myBoard[4][7].setPieceOnField(temp);
        Move bestMove;
        if (checkTopFunc.checkersBoard.myPeasentColor == 'r') {
            bestMove = mi.lookForBestMove();
            checkTopFunc.doMove(bestMove);
            checkTopFunc.resetAfterMove();
        }
        while (!Button.ESCAPE.isDown()) {
            checkTopFunc.waitForRedButton();
            try {
                if (mi.remoteNXT.checkersBoard.analyzeFunctions.analyzeBoard())
                {
                    bestMove = mi.lookForBestMove();

                    if (bestMove != null) {
                        mi.remoteNXT.doMove(bestMove);
                    }
                    checkTopFunc.resetAfterMove();

                    if (!mi.remoteNXT.checkersBoard.
                            analyzeFunctions.checkForGameHasEnded(true)) {
                        mi.remoteNXT.checkersBoard.informer.playYourTurn();
                    }
                }

            } catch (IllegalMove e) {
                mi.remoteNXT.checkersBoard.informer.illeagalMove();
            }
        }
        /*FakeMI fm1 = new FakeMI(checkTopFunc,true);
        FakeMI fm2 = new FakeMI(checkTopFunc,false);
        if(checkTopFunc.checkersBoard.myPeasentColor == 'r')
        {
            fm1.decideMovement();
            checkTopFunc.getColorOnField(4, -2);
        }
        boolean endGame = true;
        while(!Button.ESCAPE.isDown() && endGame)
        {
            LCD.clear();
            LCD.drawString("human", 0, 0);
            LCD.refresh();
            endGame = fm2.decideMovement();
            LCD.clear();
            LCD.drawString("robot", 0, 0);
            LCD.refresh();
            endGame = fm1.decideMovement();
        }*/
    }
}
