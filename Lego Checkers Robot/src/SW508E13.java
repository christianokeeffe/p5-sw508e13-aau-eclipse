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
        checkTopFunc.emptyBoard();
        
        checkTopFunc.checkersBoard.myBoard[7][0].setPieceOnField(checkTopFunc.producePiece('w', false));
        checkTopFunc.checkersBoard.myBoard[5][0].setPieceOnField(checkTopFunc.producePiece('w', false));
        checkTopFunc.checkersBoard.myBoard[1][0].setPieceOnField(checkTopFunc.producePiece('w', false));
        checkTopFunc.checkersBoard.myBoard[2][3].setPieceOnField(checkTopFunc.producePiece('g', true));
        checkTopFunc.checkersBoard.myBoard[2][5].setPieceOnField(checkTopFunc.producePiece('b', true));
        checkTopFunc.checkersBoard.myBoard[0][7].setPieceOnField(checkTopFunc.producePiece('r', false));
        checkTopFunc.checkersBoard.myBoard[2][7].setPieceOnField(checkTopFunc.producePiece('r', false));
        checkTopFunc.checkersBoard.myBoard[6][3].setPieceOnField(checkTopFunc.producePiece('r', false));
        checkTopFunc.checkersBoard.myBoard[5][4].setPieceOnField(checkTopFunc.producePiece('r', false));
        checkTopFunc.checkersBoard.kingPlace[0].emptyThisField();
        
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
        }*//*
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
        }*/

        checkTopFunc.resetAfterMove();
    }
}
