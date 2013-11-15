package com.Testing;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import customExceptions.IllegalMove;
import customExceptions.NoKingLeft;

public class AnalyzeTest {
    @Test
    public void testGameIsEnded() throws InterruptedException, IOException {
        //test if the game is not ended with start positioning
        assertEquals(0, test.gameIsEnded(true));
        assertEquals(0, test.gameIsEnded(false));

        constructWinCase(true);
        //test if the game registers that the human have won independent of whos turn it is
        assertEquals(1, test.gameIsEnded(true));
        assertEquals(1, test.gameIsEnded(false));
        
        constructWinCase(false);
        //test if the game registers that the robot have won independent of whos turn it is
        assertEquals(2, test.gameIsEnded(true));
        assertEquals(2, test.gameIsEnded(false));
        
        emptyBoard();
        
        //test if the game ended when it is a draw when it is humans turn
        //test if the game is not ended when it is the robots turn
        test.myBoard[4][5].setPieceOnField(producePiece('b',true));
        test.myBoard[6][7].setPieceOnField(producePiece('g',true));
        
        assertEquals(3, test.gameIsEnded(true));
        assertNotEquals(3, test.gameIsEnded(false));
        
        emptyBoard();
        
        //test if the game ended when it is a draw when it is robots turn
        //test if the game is not ended when it is the human turn
        test.myBoard[1][0].setPieceOnField(producePiece('b',true));
        test.myBoard[1][2].setPieceOnField(producePiece('g',true));
        
        assertEquals(3, test.gameIsEnded(false));
        assertNotEquals(3, test.gameIsEnded(true));
        
        resetBoard();
    }

    @Test
    public void testCheckForGameHasEnded() throws InterruptedException, IOException {
        
        //test if the games ends in the different scenarios
        assertFalse(test.checkForGameHasEnded(true));
        assertFalse(test.checkForGameHasEnded(false));
        
        constructWinCase(true);
        
        assertTrue(test.checkForGameHasEnded(true));
        assertTrue(test.checkForGameHasEnded(false));
        
        constructWinCase(false);
        
        assertTrue(test.checkForGameHasEnded(true));
        assertTrue(test.checkForGameHasEnded(false));
        
        emptyBoard();
        
        //draw case if humans turn
        test.myBoard[4][5].setPieceOnField(producePiece('b',true));
        test.myBoard[6][7].setPieceOnField(producePiece('g',true));
        
        assertTrue(test.checkForGameHasEnded(true));
        assertFalse(test.checkForGameHasEnded(false));
        
        emptyBoard();
        
        //draw case if robots turn
        test.myBoard[1][0].setPieceOnField(producePiece('b',true));
        test.myBoard[1][2].setPieceOnField(producePiece('g',true));
        
        assertTrue(test.checkForGameHasEnded(false));
        assertFalse(test.checkForGameHasEnded(true));
        
        resetBoard();
    }
    
    @Test
    public void testAnalyzeBoard() throws InterruptedException, IOException, NoKingLeft, IllegalMove{
        boolean testException = false;
        
        //test if analyzeboard works when moving a piece
        assertTrue(test.analyzeBoard());
        
        //production of an illegal move
        test.myBoard[3][4].setPieceOnField(producePiece('r',false));
        test.myBoard[2][5].setPieceOnField(producePiece('w',true));
        
        //test that analyzeBoard finds and reports the illegal move
        try {
            test.analyzeBoard();
        }
        catch (IllegalMove e) {
            testException = true;
        }
        
        assertTrue(testException);
        
        resetBoard();
    }
    
    @Test
    //Test not that valid for the real GetColor function
    public void testGetColor() throws IOException {
        assertEquals(test.myBoard[1][0].getPieceOnField().color, getColor(1, 0));
        assertEquals(' ',getColor(0, 3));
        assertEquals('w',getColor(1,4));
    }
}
