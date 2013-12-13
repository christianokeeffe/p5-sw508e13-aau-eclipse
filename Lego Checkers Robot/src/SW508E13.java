import java.io.IOException;

import custom.Exceptions.IllegalMove;
import custom.Exceptions.NoKingLeft;
import lejos.nxt.Button;
import lejos.util.Delay;

public final class SW508E13 {

    private SW508E13() {
        throw new AssertionError();
    }

    public static void main(String[] args)
            throws IOException, NoKingLeft, InterruptedException {
        RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
        MI mi = new MI(checkTopFunc, true,
                checkTopFunc.checkersBoard.informer.getDifficulty());
        Delay.msDelay(500);
        boolean gameEnd = false;

        Move bestMove;
        if (checkTopFunc.checkersBoard.myPeasentColor == 'r') {
            bestMove = mi.lookForBestMove();

            checkTopFunc.doMove(bestMove);
            checkTopFunc.resetAfterMove();
            mi.remoteNXT.checkersBoard.informer.playYourTurn();
        } else {
            mi.remoteNXT.checkersBoard.informer.playYourTurn();
        }
        while (!Button.ESCAPE.isDown() && !gameEnd) {
            checkTopFunc.waitForRedButton();
            mi.remoteNXT.checkersBoard.informer.robotTurn();
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
                    } else {
                        gameEnd = true;
                    }
                } else {
                    gameEnd = true;
                }

            } catch (IllegalMove e) {
                mi.remoteNXT.checkersBoard.informer.illeagalMove();
            }
        }

        checkTopFunc.resetAfterMove();
    }
}
