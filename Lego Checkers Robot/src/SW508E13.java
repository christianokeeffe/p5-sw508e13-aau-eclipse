import java.io.IOException;

//import custom.Exceptions.IllegalMove;
import custom.Exceptions.NoKingLeft;
import lejos.nxt.Button;
import lejos.nxt.LCD;
//import lejos.util.Stopwatch;

public final class SW508E13 {

    private SW508E13() {
        throw new AssertionError();
    }

    public static void main(String[] args)
            throws IOException, NoKingLeft, InterruptedException {
        RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
        //Stopwatch sW = new Stopwatch();
        MI mi = new MI(checkTopFunc, true, 4);

        Move bestMove;
        /*if (checkTopFunc.checkersBoard.myPeasentColor == 'r') {
            bestMove = mi.lookForBestMove();

            checkTopFunc.doMove(bestMove);
            checkTopFunc.resetAfterMove();
            mi.remoteNXT.checkersBoard.informer.playYourTurn();
        }
        while (!Button.ESCAPE.isDown()) {
            checkTopFunc.waitForRedButton();
            mi.remoteNXT.checkersBoard.informer.robotTurn();
            try {
                if (mi.remoteNXT.checkersBoard.analyzeFunctions.analyzeBoard())
                {
                    sW.reset();
                    bestMove = mi.lookForBestMove();
                    //LCD.clear();
                    //LCD.drawString("Total T: " + sW.elapsed(), 0, 0);
                    //LCD.refresh();
                    //Button.waitForAnyPress();
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
        }*/
        /*FakeMI fm2 = new FakeMI(checkTopFunc, false);
        if (checkTopFunc.checkersBoard.myPeasentColor == 'r') {
            bestMove = mi.lookForBestMove();
            checkTopFunc.doMove(bestMove);
        }
        boolean endGame = true;
        while (!Button.ESCAPE.isDown() && endGame) {
            LCD.clear();
            LCD.drawString("human", 0, 0);
            LCD.refresh();
            endGame = fm2.decideMovement();
            checkTopFunc.checkersBoard.updateMoveables();
            LCD.clear();
            LCD.drawString("robot", 0, 0);
            LCD.refresh();

            bestMove = mi.lookForBestMove();

            if (bestMove != null) {
                checkTopFunc.doMove(bestMove);
                checkTopFunc.checkersBoard.updateMoveables();
            }
        }*/
        MI mi2 = new MI(checkTopFunc, false, 2);
        if (checkTopFunc.checkersBoard.myPeasentColor == 'r') {
            bestMove = mi.lookForBestMove();
            checkTopFunc.doMove(bestMove);
        }
        boolean endGame = true;
        while (!Button.ESCAPE.isDown() && endGame) {
            LCD.clear();
            LCD.drawString("human", 0, 0);
            LCD.refresh();
            bestMove = mi2.lookForBestMove();

            if (bestMove != null) {
                checkTopFunc.doMove(bestMove);
                checkTopFunc.checkersBoard.updateMoveables();
            }
            checkTopFunc.checkersBoard.updateMoveables();
            LCD.clear();
            LCD.drawString("robot", 0, 0);
            LCD.refresh();

            bestMove = mi.lookForBestMove();

            if (bestMove != null) {
                checkTopFunc.doMove(bestMove);
                checkTopFunc.checkersBoard.updateMoveables();
            }
        }
    }
}
