package com.Testing;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.CustomClasses.FunktionForTesting;
import com.OriginalFiles.Move;

import custom.Exceptions.IllegalMove;
import custom.Exceptions.NoKingLeft;

public class AnalyzeTest extends FunktionForTesting {
    
    public AnalyzeTest() throws InterruptedException, IOException {
        super();
        // TODO Auto-generated constructor stub
    }

    @Test
    public final void testGameHasEnded() throws InterruptedException, IOException, NoKingLeft, IllegalMove {
        //test if the game is not ended with start positioning
        assertEquals(0, checkersBoard.analyzeFunctions.gameHasEnded(true));
        assertEquals(0, checkersBoard.analyzeFunctions.gameHasEnded(false));

        constructWinCase(true);
        //test if the game registers that the human have won independent of whos turn it is
        assertEquals(1, checkersBoard.analyzeFunctions.gameHasEnded(true));
        assertEquals(1, checkersBoard.analyzeFunctions.gameHasEnded(false));
        
        constructWinCase(false);
        //test if the game registers that the robot have won independent of whos turn it is
        assertEquals(2, checkersBoard.analyzeFunctions.gameHasEnded(true));
        assertEquals(2, checkersBoard.analyzeFunctions.gameHasEnded(false));
        
        emptyBoard();
        
        //test if the game ended when it is a draw when it is humans turn
        //test if the game is not ended when it is the robots turn
        checkersBoard.myBoard[4][5].setPieceOnField(producePiece(4, 5, 'b', true));
        checkersBoard.myBoard[6][7].setPieceOnField(producePiece(4, 5, 'g', true));
        
        assertEquals(3, checkersBoard.analyzeFunctions.gameHasEnded(true));
        assertNotEquals(3, checkersBoard.analyzeFunctions.gameHasEnded(false));
        
        emptyBoard();
        
        //test if the game ended when it is a draw when it is robots turn
        //test if the game is not ended when it is the human turn
        checkersBoard.myBoard[1][0].setPieceOnField(producePiece(4, 5, 'b',true));
        checkersBoard.myBoard[1][2].setPieceOnField(producePiece(4, 5, 'g',true));
        
        assertEquals(3, checkersBoard.analyzeFunctions.gameHasEnded(false));
        assertNotEquals(3, checkersBoard.analyzeFunctions.gameHasEnded(true));
        
        resetBoard();
    }

    

    

    @Test
    public final void testCheckForGameHasEnded() throws InterruptedException, IOException {
        
        //test if the games ends in the different scenarios
        assertFalse(checkersBoard.analyzeFunctions.checkForGameHasEnded(true));
        assertFalse(checkersBoard.analyzeFunctions.checkForGameHasEnded(false));
        
        constructWinCase(true);
        
        assertTrue(checkersBoard.analyzeFunctions.checkForGameHasEnded(true));
        assertTrue(checkersBoard.analyzeFunctions.checkForGameHasEnded(false));
        
        constructWinCase(false);
        
        assertTrue(checkersBoard.analyzeFunctions.checkForGameHasEnded(true));
        assertTrue(checkersBoard.analyzeFunctions.checkForGameHasEnded(false));
        
        emptyBoard();
        
        //draw case if humans turn
        checkersBoard.myBoard[4][5].setPieceOnField(producePiece(4, 5, 'b',true));
        checkersBoard.myBoard[6][7].setPieceOnField(producePiece(6, 7, 'g',true));
        
        assertTrue(checkersBoard.analyzeFunctions.checkForGameHasEnded(true));
        assertFalse(checkersBoard.analyzeFunctions.checkForGameHasEnded(false));
        
        emptyBoard();
        
        //draw case if robots turn
        checkersBoard.myBoard[1][0].setPieceOnField(producePiece(1, 0, 'b',true));
        checkersBoard.myBoard[1][2].setPieceOnField(producePiece(1, 2, 'g',true));
        
        assertTrue(checkersBoard.analyzeFunctions.checkForGameHasEnded(false));
        assertFalse(checkersBoard.analyzeFunctions.checkForGameHasEnded(true));
        
        resetBoard();
    }
    
    @Test
    public final void testAnalyzeBoard() throws InterruptedException, IOException, NoKingLeft, IllegalMove, custom.Exceptions.NoKingLeft, custom.Exceptions.IllegalMove{
        boolean testException = false;
        
        //test if analyzeboard works when moving a piece
        assertTrue(checkersBoard.analyzeFunctions.analyzeBoard());
        
        //production of an illegal move
        checkersBoard.myBoard[3][4].setPieceOnField(producePiece(4, 5, 'r',false));
        checkersBoard.myBoard[2][5].setPieceOnField(producePiece(4, 5, 'w',true));
        
        //test that analyzeBoard finds and reports the illegal move
        try {
            checkersBoard.analyzeFunctions.analyzeBoard();
        }
        catch (IllegalMove e) {
            testException = true;
        }
        
        assertTrue(testException);
        
        resetBoard();
        
        constructWinCase(true);
        // test if the game registers that it do not need to analyze the board if the game has ended if it humans turn
        assertEquals(false, checkersBoard.analyzeFunctions.analyzeBoard());
        
        constructWinCase(false);
        // test if the game registers that it do not need to analyze the board if the game has ended if it robots turn
        assertEquals(false, checkersBoard.analyzeFunctions.analyzeBoard());
        resetBoard();
    }
    
    
    //Test not that valid for the real GetColor function
    @Test
    public final void testGetColor() throws IOException, InterruptedException {
        assertEquals(checkersBoard.myBoard[1][0].getPieceOnField().color, checkersBoard.analyzeFunctions.getColor(1, 0));
        assertEquals(' ', checkersBoard.analyzeFunctions.getColor(0, 3));
        assertEquals('w', checkersBoard.analyzeFunctions.getColor(1,4));
        
        checkersBoard.myBoard[4][3].setPieceOnField(producePiece(4, 3, 'g',true));
        checkersBoard.myBoard[5][4].setPieceOnField(producePiece(5, 4, 'b',true));
        
        assertEquals('g', checkersBoard.analyzeFunctions.getColor(4, 3));
        assertEquals('b', checkersBoard.analyzeFunctions.getColor(5,4));
        resetBoard();
    }
    
    // test for to see if checkMotorCalibration will work ??? 
    @Test
    public final void testCheckMotorCalibration() throws InterruptedException, IOException, NoKingLeft, IllegalMove {
        resetTotalAnalyzeRuns();
        emptyBoard();
        checkersBoard.myBoard[3][2].setPieceOnField(producePiece(2, 3, 'g',true));
        checkersBoard.myBoard[5][4].setPieceOnField(producePiece(5, 4, 'b',true));
        checkersBoard.myBoard[3][2].getPieceOnField().canJump = false;
        checkersBoard.myBoard[5][4].getPieceOnField().canJump = false;
        boolean change = true;
        // need to run 11 times 10 times for getting totalAnalyzeRuns to 10 and one more for calling motorCalibration
        for(int i = 0 ; i <= 10; i++) {
            if(change) {
                remote.analyzeTestVariable = 10;
                change = false;
            }
            else  {
                remote.analyzeTestVariable = 11;
                change = true;
            }
            checkersBoard.analyzeFunctions.analyzeBoard();
        }
        // see if a Variable in checkMotorCalibration is set to true
        assertTrue(remote.analyzeresetMotorsTestVariable);
        remote.analyzeTestVariable = 0;
        resetBoard();
        // -----------------------------------------------------------------------------------------
        // now to see if it will call motorCalibration if there only is called 9 times
        resetTotalAnalyzeRuns();
        emptyBoard();
        checkersBoard.myBoard[3][2].setPieceOnField(producePiece(2, 3, 'g',true));
        checkersBoard.myBoard[5][4].setPieceOnField(producePiece(5, 4, 'b',true));
        checkersBoard.myBoard[3][2].getPieceOnField().canJump = false;
        checkersBoard.myBoard[5][4].getPieceOnField().canJump = false;
        change = true;
        // need to run 10 times 9 times for getting totalAnalyzeRuns to 9 and one more for calling motorCalibration
        for(int i = 0 ; i <= 9; i++) {
            if(change) {
                remote.analyzeTestVariable = 100;
                change = false;
            }
            else  {
                remote.analyzeTestVariable = 101;
                change = true;
            }
            checkersBoard.analyzeFunctions.analyzeBoard();
        }
        // see if a Variable in checkMotorCalibration is set to true
        assertTrue(!remote.analyzeresetMotorsTestVariable);
        remote.analyzeTestVariable = 0;
        resetBoard();
    }
    
    // test for to see if countDownToPanic will work ??? 
    @Test
    public final void testCountDownToPanic() throws InterruptedException, IOException, NoKingLeft, IllegalMove {
        resetTotalAnalyzeRuns();
        emptyBoard();
        checkersBoard.myBoard[2][5].setPieceOnField(producePiece(2, 5, 'w',false));
        checkersBoard.myBoard[4][3].setPieceOnField(producePiece(4, 3, 'r',false));
        
        remote.analyzeTestVariable = 3;
        checkersBoard.analyzeFunctions.analyzeBoard();
        remote.analyzeTestVariable = 0;
        // see if the variable in resetMotors is set to true
        assertTrue(remote.analyzeresetMotorsTestVariable);
        resetBoard();
    }
    
    // test for to see if 
    @Test
    public final void testCheckMovement() throws InterruptedException, IOException, NoKingLeft, IllegalMove {
        emptyBoard();
        boolean testException = false;
        checkersBoard.myBoard[2][5].setPieceOnField(producePiece(2, 5, 'w',true));
        checkersBoard.myBoard[0][5].setPieceOnField(producePiece(0, 5, 'w',false));
        checkersBoard.myBoard[3][4].setPieceOnField(producePiece(3, 4, 'r',true));

        try {
            remote.analyzeTestVariable = 1;
            checkersBoard.analyzeFunctions.analyzeBoard();
        }
        catch (IllegalMove e) {
            testException = true;
        }
        remote.analyzeTestVariable = 0;
        assertTrue(testException);
        testException = false;
        
        try {
            remote.analyzeTestVariable = 2;
            checkersBoard.analyzeFunctions.analyzeBoard();
        }
        catch (IllegalMove e) {
            testException = true;
        }
        remote.analyzeTestVariable = 0;
        assertTrue(!testException);
        resetBoard();
    }
    
    //test for checkMove
    @Test
    public final void testCheckMove() throws InterruptedException, IOException, NoKingLeft, IllegalMove {
        
        // test the first direction
        assertTrue(helpToTestCheckMove(5));
        assertTrue(!helpToTestCheckMove(9));
        
        // test the Second direction
        assertTrue(helpToTestCheckMove(6));
        assertTrue(!helpToTestCheckMove(10));
        
        //If king, also check backwards
        assertTrue(helpToTestCheckMove(7));
        assertTrue(!helpToTestCheckMove(11));
        
        //If king, also check backwards 2.
        assertTrue(helpToTestCheckMove(8));
        assertTrue(!helpToTestCheckMove(12));
        
        resetBoard();
    }
    public boolean helpToTestCheckMove(int changeVariable) throws InterruptedException, IOException, NoKingLeft, IllegalMove
    {
        resetTotalAnalyzeRuns();
        emptyBoard();
        checkersBoard.myBoard[1][0].setPieceOnField(producePiece(1, 0, 'b',true));
        checkersBoard.myBoard[5][4].setPieceOnField(producePiece(5, 4, 'g',true));
        checkersBoard.myBoard[5][6].setPieceOnField(producePiece(5, 6, 'g',true));
        checkersBoard.myBoard[5][2].setPieceOnField(producePiece(5, 2, 'g',true));
        
        checkersBoard.myBoard[1][0].getPieceOnField().canJump = false;
        checkersBoard.myBoard[5][4].getPieceOnField().canJump = false;
        checkersBoard.myBoard[5][6].getPieceOnField().canJump = false;
        checkersBoard.myBoard[5][2].getPieceOnField().canJump = false;

        remote.analyzeTestVariable = changeVariable;
        checkersBoard.analyzeFunctions.analyzeBoard();
        remote.analyzeTestVariable = 0;
        
        return !remote.analyzeresetMotorsTestVariable;
    }
    
    // test for checkForUpgradeKing
    @Test
    public final void testCheckForUpgradeKing() throws InterruptedException, IOException, NoKingLeft, IllegalMove {
        // test if the robot can upgrade a peasant to a kings
        resetTotalAnalyzeRuns();
        emptyBoard();
        checkersBoard.myBoard[4][1].setPieceOnField(producePiece(4, 1, 'w',false));
        checkersBoard.myBoard[3][6].setPieceOnField(producePiece(3, 6, 'r',false));
        
        remote.analyzeTestVariable = 13;
        checkersBoard.analyzeFunctions.analyzeBoard();
        
        remote.analyzeTestVariable = 0;
        assertTrue(checkersBoard.myBoard[3][0].getPieceOnField().color == 'g');
        
        // test if the robot can see if it owns kings
        
        resetTotalAnalyzeRuns();
        emptyBoard();
        checkersBoard.myBoard[4][1].setPieceOnField(producePiece(4, 1, 'w',false));
        checkersBoard.myBoard[3][6].setPieceOnField(producePiece(3, 6, 'r',false));
        
        Move tempMove = new Move(checkersBoard.myBoard[3][6], checkersBoard.myBoard[2][7], false);
        
        remote.doMove(tempMove);
        checkersBoard.analyzeFunctions.analyzeBoard();
        
        assertTrue(checkersBoard.myBoard[2][7].getPieceOnField().color == 'b'); 
 
    }

    
    // test for checkForGameIsDraw
    @Test
    public final void testCheckForGameIsDraw() throws InterruptedException, IOException, NoKingLeft, IllegalMove {
        emptyBoard();
        
        // side 1 where green is in (1,0)
        checkersBoard.myBoard[1][0].setPieceOnField(producePiece(1, 0, 'g',true));
        checkersBoard.myBoard[1][2].setPieceOnField(producePiece(1, 2, 'b',true));
        
        checkersBoard.myBoard[1][0].getPieceOnField().canJump = false;
        checkersBoard.myBoard[1][2].getPieceOnField().canJump = false;
        
        //assertTrue(checkersBoard.analyzeFunctions.checkForGameHasEnded(true));
        // ------------------------------------------------------------------------
        
    }
}
