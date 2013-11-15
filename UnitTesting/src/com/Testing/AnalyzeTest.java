package com.Testing;

import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.Test;
import com.CustomClasses.FunktionForTesting;
import custom.Exceptions.IllegalMove;
import custom.Exceptions.NoKingLeft;

public class AnalyzeTest extends FunktionForTesting {
    com.OriginalFiles.Analyze analyzeForTest;
    public AnalyzeTest() throws InterruptedException, IOException {
        super();
        analyzeForTest = checkersBoard.analyzeFunctions;
        // TODO Auto-generated constructor stub
    }

    @Test
    public final void testGameHasEnded() throws InterruptedException, IOException {
        //test if the game is not ended with start positioning
        assertEquals(0, analyzeForTest.gameHasEnded(true));
        assertEquals(0, analyzeForTest.gameHasEnded(false));

        constructWinCase(true);
        //test if the game registers that the human have won independent of whos turn it is
        assertEquals(1, analyzeForTest.gameHasEnded(true));
        assertEquals(1, analyzeForTest.gameHasEnded(false));
        
        constructWinCase(false);
        //test if the game registers that the robot have won independent of whos turn it is
        assertEquals(2, analyzeForTest.gameHasEnded(true));
        assertEquals(2, analyzeForTest.gameHasEnded(false));
        
        emptyBoard();
        
        //test if the game ended when it is a draw when it is humans turn
        //test if the game is not ended when it is the robots turn
        checkersBoard.myBoard[4][5].setPieceOnField(producePiece(4, 5, 'b', true));
        checkersBoard.myBoard[6][7].setPieceOnField(producePiece(4, 5, 'g', true));
        
        assertEquals(3, analyzeForTest.gameHasEnded(true));
        assertNotEquals(3, analyzeForTest.gameHasEnded(false));
        
        emptyBoard();
        
        //test if the game ended when it is a draw when it is robots turn
        //test if the game is not ended when it is the human turn
        checkersBoard.myBoard[1][0].setPieceOnField(producePiece(4, 5, 'b',true));
        checkersBoard.myBoard[1][2].setPieceOnField(producePiece(4, 5, 'g',true));
        
        assertEquals(3, analyzeForTest.gameHasEnded(false));
        assertNotEquals(3, analyzeForTest.gameHasEnded(true));
        
        resetBoard();
    }

    

    private void constructWinCase(boolean b) {
        // TODO Auto-generated method stub
        
    }

    @Test
    public final void testCheckForGameHasEnded() throws InterruptedException, IOException {
        
        //test if the games ends in the different scenarios
        assertFalse(analyzeForTest.checkForGameHasEnded(true));
        assertFalse(analyzeForTest.checkForGameHasEnded(false));
        
        constructWinCase(true);
        
        assertTrue(analyzeForTest.checkForGameHasEnded(true));
        assertTrue(analyzeForTest.checkForGameHasEnded(false));
        
        constructWinCase(false);
        
        assertTrue(analyzeForTest.checkForGameHasEnded(true));
        assertTrue(analyzeForTest.checkForGameHasEnded(false));
        
        emptyBoard();
        
        //draw case if humans turn
        checkersBoard.myBoard[4][5].setPieceOnField(producePiece(4, 5, 'b',true));
        checkersBoard.myBoard[6][7].setPieceOnField(producePiece(6, 7, 'g',true));
        
        assertTrue(analyzeForTest.checkForGameHasEnded(true));
        assertFalse(analyzeForTest.checkForGameHasEnded(false));
        
        emptyBoard();
        
        //draw case if robots turn
        checkersBoard.myBoard[1][0].setPieceOnField(producePiece(1, 0, 'b',true));
        checkersBoard.myBoard[1][2].setPieceOnField(producePiece(1, 2, 'g',true));
        
        assertTrue(analyzeForTest.checkForGameHasEnded(false));
        assertFalse(analyzeForTest.checkForGameHasEnded(true));
        
        resetBoard();
    }
    
    @Test
    public final void testAnalyzeBoard() throws InterruptedException, IOException, NoKingLeft, IllegalMove, custom.Exceptions.NoKingLeft, custom.Exceptions.IllegalMove{
        boolean testException = false;
        
        //test if analyzeboard works when moving a piece
        assertTrue(analyzeForTest.analyzeBoard());
        
        //production of an illegal move
        checkersBoard.myBoard[3][4].setPieceOnField(producePiece(4, 5, 'r',false));
        checkersBoard.myBoard[2][5].setPieceOnField(producePiece(4, 5, 'w',true));
        
        //test that analyzeBoard finds and reports the illegal move
        try {
            analyzeForTest.analyzeBoard();
        }
        catch (IllegalMove e) {
            testException = true;
        }
        
        assertTrue(testException);
        
        resetBoard();
    }
    
    
    //Test not that valid for the real GetColor function
    @Test
    public final void testGetColor() throws IOException {
        assertEquals(checkersBoard.myBoard[1][0].getPieceOnField().color, analyzeForTest.getColor(1, 0));
        assertEquals(' ', analyzeForTest.getColor(0, 3));
        assertEquals('w', analyzeForTest.getColor(1,4));
    }
}
