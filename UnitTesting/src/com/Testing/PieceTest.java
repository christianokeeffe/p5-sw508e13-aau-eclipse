package com.Testing;

import java.io.IOException;

import static org.junit.Assert.*;

import org.junit.Test;

import com.CustomClasses.FunktionForTesting;
import com.OriginalFiles.Piece;

import custom.Exceptions.NoKingLeft;

public class PieceTest extends FunktionForTesting{
    Piece testPiece;

    private final int isMidgame = 1;
    private final int isEndgame = 2;
    public PieceTest() throws InterruptedException, IOException {
        super();
        testPiece = new Piece(checkersBoard);
        // TODO Auto-generated constructor stub
    }

    //latex start unittestxy
    @Test
    public final void testGetSetXY() {
        testPiece.setXY(4, 5);
        assertEquals(4, testPiece.getX());
        assertEquals(5, testPiece.getY());
    }
    //latex end
    
    @Test
    public final void testIsOnBoard() {
        testPiece.setXY(4, 5);
        assertTrue(0 < testPiece.priceForPiece(isMidgame, 1, 1, 1, true));
        testPiece.setXY(-3, 5);
        assertTrue(0 == testPiece.priceForPiece(isMidgame, 1, 1, 1, true));
    }
    
    @Test
    public final void testPriceForPiece() {
        emptyBoard();
        testPiece = new Piece(checkersBoard);
        testPiece.setXY(4, 3);
        double initialValue = testPiece.priceForPiece(isMidgame, 1, 1, 1, true);
        double nextValue = testPiece.priceForPiece(isMidgame, 1, 1, 1, true);
        assertTrue(initialValue == nextValue);
        testPiece.setXY(6, 3);
        nextValue = testPiece.priceForPiece(isMidgame, 1, 1, 1, true);
        assertFalse(initialValue == nextValue);
        testPiece.setXY(4, 3);
        nextValue = testPiece.priceForPiece(isMidgame, 1, 1, 1, true);
        assertTrue(initialValue == nextValue);
        nextValue = testPiece.priceForPiece(isEndgame, 1, 1, 1, true);
        assertFalse(initialValue == nextValue);
    }
    
    @Test
    public final void testPriceForPieceValue() throws InterruptedException, IOException {
        emptyBoard();
        checkersBoard.myBoard[3][4].setPieceOnField(producePiece(3, 4, 'r', false));
        checkersBoard.myBoard[7][0].setPieceOnField(producePiece(7, 0, 'w', false));
        double firstPrice = checkersBoard.myBoard[3][4].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[4][3].setPieceOnField(checkersBoard.myBoard[7][0].getPieceOnField());
        checkersBoard.myBoard[7][0].setPieceOnField(null);
        double secondPrice = checkersBoard.myBoard[3][4].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        assertTrue(firstPrice < secondPrice);
        emptyBoard();
        checkersBoard.myBoard[3][4].setPieceOnField(producePiece(3, 4, 'w', false));
        firstPrice = checkersBoard.myBoard[3][4].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[3][4].getPieceOnField().isCrowned = true;
        secondPrice = checkersBoard.myBoard[3][4].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        assertTrue(firstPrice < secondPrice);
        secondPrice = checkersBoard.myBoard[3][4].getPieceOnField().priceForPiece(isMidgame, 1, 1, 1, true);
        checkersBoard.myBoard[3][7].setPieceOnField(checkersBoard.myBoard[3][4].getPieceOnField());
        checkersBoard.myBoard[3][4].setPieceOnField(null);
        firstPrice = checkersBoard.myBoard[3][7].getPieceOnField().priceForPiece(isMidgame, 1, 1, 1, true);
        assertTrue(firstPrice == secondPrice);//No king backline bonus
        emptyBoard();
        checkersBoard.myBoard[3][4].setPieceOnField(producePiece(3, 4, 'w', false));
        firstPrice = checkersBoard.myBoard[3][4].getPieceOnField().priceForPiece(isMidgame, 1, 1, 1, true);
        checkersBoard.myBoard[0][7].setPieceOnField(checkersBoard.myBoard[3][4].getPieceOnField());
        checkersBoard.myBoard[3][4].setPieceOnField(null);
        secondPrice = checkersBoard.myBoard[0][7].getPieceOnField().priceForPiece(isMidgame, 1, 1, 1, true);
        assertTrue(firstPrice < secondPrice);//Backline bonus
        emptyBoard();
        checkersBoard.myBoard[3][4].setPieceOnField(producePiece(3, 4, 'r', false));
        firstPrice = checkersBoard.myBoard[3][4].getPieceOnField().priceForPiece(isMidgame, 1, 1, 1, true);
        checkersBoard.myBoard[1][0].setPieceOnField(checkersBoard.myBoard[3][4].getPieceOnField());
        checkersBoard.myBoard[3][4].setPieceOnField(null);
        secondPrice = checkersBoard.myBoard[1][0].getPieceOnField().priceForPiece(isMidgame, 1, 1, 1, true);
        assertTrue(firstPrice < secondPrice);//Backline bonus
        
        emptyBoard();
        checkersBoard.myBoard[1][0].setPieceOnField(producePiece(1, 0, 'r', false));
        firstPrice = checkersBoard.myBoard[1][0].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[2][1].setPieceOnField(checkersBoard.myBoard[1][0].getPieceOnField());
        checkersBoard.myBoard[1][0].setPieceOnField(null);
        secondPrice = checkersBoard.myBoard[2][1].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        assertTrue(firstPrice < secondPrice);//Backline penalty for end game
        
        emptyBoard();
        checkersBoard.myBoard[6][7].setPieceOnField(producePiece(1, 0, '2', false));
        firstPrice = checkersBoard.myBoard[6][7].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[5][6].setPieceOnField(checkersBoard.myBoard[6][7].getPieceOnField());
        checkersBoard.myBoard[6][7].setPieceOnField(null);
        secondPrice = checkersBoard.myBoard[5][6].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        assertTrue(firstPrice < secondPrice);//Backline penalty for end game
    }
    @Test
    public final void testBlock() throws NoKingLeft, IOException {
        double firstPrice =0, secondPrice =0;
        
        emptyBoard();
        checkersBoard.myBoard[5][0].setPieceOnField(producePiece(5, 0, 'b', true));
        checkersBoard.myBoard[5][0].getPieceOnField().canJump = false;
        checkersBoard.myBoard[5][4].setPieceOnField(producePiece(5, 4, 'b', true));
        checkersBoard.myBoard[5][4].getPieceOnField().canJump = false;
        checkersBoard.myBoard[5][2].setPieceOnField(producePiece(5, 2, 'g', true));
        checkersBoard.myBoard[5][2].getPieceOnField().canJump = false;
        firstPrice = checkersBoard.myBoard[5][2].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[5][0].emptyThisField();
        checkersBoard.myBoard[5][4].emptyThisField();
        secondPrice = checkersBoard.myBoard[5][2].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        assertTrue(firstPrice > secondPrice);
        
        emptyBoard();
        checkersBoard.myBoard[3][2].setPieceOnField(producePiece(5, 0, 'b', true));
        checkersBoard.myBoard[3][2].getPieceOnField().canJump = false;
        checkersBoard.myBoard[7][2].setPieceOnField(producePiece(5, 4, 'b', true));
        checkersBoard.myBoard[7][2].getPieceOnField().canJump = false;
        checkersBoard.myBoard[5][2].setPieceOnField(producePiece(5, 2, 'g', true));
        checkersBoard.myBoard[5][2].getPieceOnField().canJump = false;
        firstPrice = checkersBoard.myBoard[5][2].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[3][2].emptyThisField();
        checkersBoard.myBoard[7][2].emptyThisField();
        secondPrice = checkersBoard.myBoard[5][2].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        assertTrue(firstPrice > secondPrice);
        
        emptyBoard();
        checkersBoard.myBoard[3][2].setPieceOnField(producePiece(3, 2, 'b', true));
        checkersBoard.myBoard[3][2].getPieceOnField().canJump = false;
        checkersBoard.myBoard[7][2].setPieceOnField(producePiece(7, 2, 'b', true));
        checkersBoard.myBoard[7][2].getPieceOnField().canJump = false;
        checkersBoard.myBoard[5][2].setPieceOnField(producePiece(5, 2, 'g', true));
        checkersBoard.myBoard[5][2].getPieceOnField().canJump = false;
        firstPrice = checkersBoard.myBoard[5][2].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[3][2].emptyThisField();
        checkersBoard.myBoard[7][2].emptyThisField();
        secondPrice = checkersBoard.myBoard[5][2].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        assertTrue(firstPrice > secondPrice);
        
        
        emptyBoard();
        checkersBoard.myBoard[3][0].setPieceOnField(producePiece(5, 0, 'b', true));
        checkersBoard.myBoard[3][0].getPieceOnField().canJump = false;
        checkersBoard.myBoard[3][4].setPieceOnField(producePiece(3, 4, 'b', true));
        checkersBoard.myBoard[3][4].getPieceOnField().canJump = false;
        checkersBoard.myBoard[7][0].setPieceOnField(producePiece(7, 0, 'b', true));
        checkersBoard.myBoard[7][0].getPieceOnField().canJump = false;
        checkersBoard.myBoard[7][4].setPieceOnField(producePiece(7, 4, 'b', true));
        checkersBoard.myBoard[7][4].getPieceOnField().canJump = false;
        checkersBoard.myBoard[5][2].setPieceOnField(producePiece(5, 2, 'g', true));
        checkersBoard.myBoard[5][2].getPieceOnField().canJump = false;
        firstPrice = checkersBoard.myBoard[5][2].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[3][0].emptyThisField();
        checkersBoard.myBoard[3][4].emptyThisField();
        checkersBoard.myBoard[7][0].emptyThisField();
        checkersBoard.myBoard[7][4].emptyThisField();
        secondPrice = checkersBoard.myBoard[5][2].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        assertTrue(firstPrice > secondPrice);
        
        emptyBoard();
        checkersBoard.myBoard[3][4].setPieceOnField(producePiece(5, 4, 'w', false));
        checkersBoard.myBoard[7][4].setPieceOnField(producePiece(7, 4, 'w', false));
        checkersBoard.myBoard[5][2].setPieceOnField(producePiece(5, 2, 'r', false));
        firstPrice = checkersBoard.myBoard[5][2].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[5][2].setPieceOnField(producePiece(5, 2, 'b', true));
        checkersBoard.myBoard[5][2].getPieceOnField().canJump = false;
        secondPrice = checkersBoard.myBoard[5][2].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        secondPrice -= 150;
        assertTrue(firstPrice > secondPrice);
        
        emptyBoard();
        checkersBoard.myBoard[7][0].setPieceOnField(producePiece(7, 0, 'b', true));
        checkersBoard.myBoard[7][0].getPieceOnField().canJump = false;
        checkersBoard.myBoard[6][1].setPieceOnField(producePiece(6, 1, 'b', true));
        checkersBoard.myBoard[6][1].getPieceOnField().canJump = false;
        checkersBoard.myBoard[5][2].setPieceOnField(producePiece(5, 2, 'g', true));
        checkersBoard.myBoard[5][2].getPieceOnField().canJump = false;
        firstPrice = checkersBoard.myBoard[5][2].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[6][1].emptyThisField();
        secondPrice = checkersBoard.myBoard[5][2].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        assertTrue(firstPrice < secondPrice);
        
        emptyBoard(); // test for past field
        checkersBoard.myBoard[7][0].setPieceOnField(producePiece(7, 0, 'b', true));
        checkersBoard.myBoard[7][0].getPieceOnField().canJump = false;
        firstPrice = checkersBoard.myBoard[7][0].getPieceOnField().priceForPiece(isMidgame, 1, 1, 1, true);
        checkersBoard.movePieceInRepresentation(checkersBoard.myBoard[7][0], checkersBoard.myBoard[4][1], true);
        checkersBoard.movePieceInRepresentation(checkersBoard.myBoard[4][1], checkersBoard.myBoard[7][0], true);
        secondPrice = checkersBoard.myBoard[7][0].getPieceOnField().priceForPiece(isMidgame, 1, 1, 1, true);
        assertTrue(firstPrice > secondPrice);
        checkersBoard.movePieceInRepresentation(checkersBoard.myBoard[7][0], checkersBoard.myBoard[6][7], true);
        firstPrice = checkersBoard.myBoard[6][7].getPieceOnField().priceForPiece(isMidgame, 1, 1, 1, true);
        assertTrue(firstPrice > secondPrice);
        
        
        //test for isNearDoubleCorners bonus
        emptyBoard(); // test for past field
        checkersBoard.myBoard[3][4].setPieceOnField(producePiece(7, 0, 'b', true));
        checkersBoard.myBoard[3][4].getPieceOnField().canJump = false;
        firstPrice = checkersBoard.myBoard[3][4].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        
        checkersBoard.myBoard[1][2].setPieceOnField(producePiece(7, 0, 'b', true));
        checkersBoard.myBoard[1][2].getPieceOnField().canJump = false;
        secondPrice = checkersBoard.myBoard[1][2].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[1][2].emptyThisField();
        assertTrue(firstPrice < secondPrice);
        
        checkersBoard.myBoard[2][1].setPieceOnField(producePiece(7, 0, 'b', true));
        checkersBoard.myBoard[2][1].getPieceOnField().canJump = false;
        secondPrice = checkersBoard.myBoard[2][1].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[2][1].emptyThisField();
        assertTrue(firstPrice < secondPrice);
        
        checkersBoard.myBoard[6][5].setPieceOnField(producePiece(7, 0, 'b', true));
        checkersBoard.myBoard[6][5].getPieceOnField().canJump = false;
        secondPrice = checkersBoard.myBoard[6][5].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[6][5].emptyThisField();
        assertTrue(firstPrice < secondPrice);
        
        checkersBoard.myBoard[5][6].setPieceOnField(producePiece(7, 0, 'b', true));
        checkersBoard.myBoard[5][6].getPieceOnField().canJump = false;
        secondPrice = checkersBoard.myBoard[5][6].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[5][6].emptyThisField();
        assertTrue(firstPrice < secondPrice);
        
        checkersBoard.myBoard[2][3].setPieceOnField(producePiece(7, 0, 'b', true));
        checkersBoard.myBoard[2][3].getPieceOnField().canJump = false;
        secondPrice = checkersBoard.myBoard[2][3].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[2][3].emptyThisField();
        assertTrue(firstPrice < secondPrice);
        
        checkersBoard.myBoard[3][2].setPieceOnField(producePiece(7, 0, 'b', true));
        checkersBoard.myBoard[3][2].getPieceOnField().canJump = false;
        secondPrice = checkersBoard.myBoard[3][2].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[3][2].emptyThisField();
        assertTrue(firstPrice < secondPrice);
        
        checkersBoard.myBoard[4][5].setPieceOnField(producePiece(7, 0, 'b', true));
        checkersBoard.myBoard[4][5].getPieceOnField().canJump = false;
        secondPrice = checkersBoard.myBoard[4][5].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[4][5].emptyThisField();
        assertTrue(firstPrice < secondPrice);
        
        checkersBoard.myBoard[5][4].setPieceOnField(producePiece(7, 0, 'b', true));
        checkersBoard.myBoard[5][4].getPieceOnField().canJump = false;
        secondPrice = checkersBoard.myBoard[5][4].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        checkersBoard.myBoard[5][4].emptyThisField();
        assertTrue(firstPrice < secondPrice);
        
        emptyBoard();
        checkersBoard.myBoard[2][1].setPieceOnField(producePiece(7, 0, 'b', true));
        checkersBoard.myBoard[2][1].getPieceOnField().canJump = false;
        checkersBoard.myBoard[2][6].setPieceOnField(producePiece(7, 0, 'b', true));
        checkersBoard.myBoard[2][6].getPieceOnField().canJump = false;
        firstPrice = checkersBoard.myBoard[2][1].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        secondPrice = checkersBoard.myBoard[2][6].getPieceOnField().priceForPiece(isEndgame, 1, 1, 1, true);
        assertTrue(firstPrice > secondPrice);
        
    }
}
