package com.Testing;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.OriginalFiles.Field;
import com.OriginalFiles.Piece;
import com.OriginalFiles.RemoteNXTFunctions;
import com.OriginalFiles.MI;
import com.OriginalFiles.Move;

import custom.Exceptions.NoKingLeft;

public class MiTest {

    @Test
    public void testMI() throws InterruptedException, IOException {
        RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
        MI tMI = new MI(checkTopFunc);
        assertNotNull(tMI);
    }

    @Test
    public void testLookForBestMove() throws InterruptedException, IOException, NoKingLeft {
        RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
        MI tMI = new MI(checkTopFunc);
        
        Move tMove = tMI.lookForBestMove();
        assertNotNull(tMove);
        assertEquals(tMove.moves.size(), 2);
        
        //Construct case with one possible move
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[5][6], 2, 3, false);
        checkTopFunc.checkersBoard.myBoard[3][2].emptyThisField();
        
        tMove = tMI.lookForBestMove();
        assertEquals(tMove.isJump(), true);
    }
    
    @Test
    public void testLookForBestMoveWinGame() throws InterruptedException, IOException, NoKingLeft {
        RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
        MI tMI = new MI(checkTopFunc);
        
        Piece oPiece = checkTopFunc.checkersBoard.myBoard[1][6].getPieceOnField();
        Piece mPiece = checkTopFunc.checkersBoard.myBoard[0][1].getPieceOnField();
        Piece mPiece2 = checkTopFunc.checkersBoard.myBoard[2][1].getPieceOnField();
        
        //Empty the board 
        for (Field[] aF : checkTopFunc.checkersBoard.myBoard) {
            for (Field f : aF) {
                f.emptyThisField();
            }
        }
        
        assertTrue(oPiece != null);
        assertTrue(mPiece != null);
        
        checkTopFunc.checkersBoard.myBoard[3][4].setPieceOnField(oPiece);
        checkTopFunc.checkersBoard.myBoard[2][3].setPieceOnField(mPiece);
        checkTopFunc.checkersBoard.myBoard[4][3].setPieceOnField(mPiece2);
        tMI.lookForBestMove();
    }
    
    @Test
    public void testLookForBestMoveLoseGame() throws InterruptedException, IOException, NoKingLeft {
        RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
        MI tMI = new MI(checkTopFunc);
        
        Piece oPiece = checkTopFunc.checkersBoard.myBoard[1][6].getPieceOnField();
        Piece mPiece = checkTopFunc.checkersBoard.myBoard[0][1].getPieceOnField();
        
        //Empty the board 
        for (Field[] aF : checkTopFunc.checkersBoard.myBoard) {
            for (Field f : aF) {
                f.emptyThisField();
            }
        }
        
        assertTrue(oPiece != null);
        assertTrue(mPiece != null);
        
        checkTopFunc.checkersBoard.myBoard[2][7].setPieceOnField(oPiece);
        checkTopFunc.checkersBoard.myBoard[2][5].setPieceOnField(mPiece);

        tMI.lookForBestMove();
    }
    
    @Test
    public void testLookForBestMoveDrawGame() throws InterruptedException, IOException, NoKingLeft {
        RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
        MI tMI = new MI(checkTopFunc);
        
        Piece oPiece = checkTopFunc.checkersBoard.myBoard[1][6].getPieceOnField();
        Piece mPiece = checkTopFunc.checkersBoard.myBoard[0][1].getPieceOnField();
        //Piece mPiece2 = checkTopFunc.checkersBoard.myBoard[2][1].getPieceOnField();
        
        //Empty the board 
        for (Field[] aF : checkTopFunc.checkersBoard.myBoard) {
            for (Field f : aF) {
                f.emptyThisField();
            }
        }
        
        assertTrue(oPiece != null);
        assertTrue(mPiece != null);
        
        oPiece.isCrowned = true;
        mPiece.isCrowned = true;
        
        checkTopFunc.checkersBoard.myBoard[0][1].setPieceOnField(oPiece);
        checkTopFunc.checkersBoard.myBoard[1][2].setPieceOnField(mPiece);
        //checkTopFunc.checkersBoard.myBoard[2][1].setPieceOnField(mPiece2);

        tMI.lookForBestMove();
    }
    
    @Test
    public void testLookForBestMoveEndGame() throws InterruptedException, IOException, NoKingLeft {
        RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
        MI tMI = new MI(checkTopFunc);
        
        Field emptyField = new Field(-10,-10);
        
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[1][2], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[3][2], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[5][2], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[7][2], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[0][1], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[2][1], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[4][1], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[6][1], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[0][5], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[2][5], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[4][5], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[6][5], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[1][6], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[3][6], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[5][6], emptyField, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[7][6], emptyField, false);
        

        tMI.lookForBestMove();
    }

    @Test
    public void testLookForBestMoveSimulateKing() throws InterruptedException, IOException, NoKingLeft {
        RemoteNXTFunctions checkTopFunc = new RemoteNXTFunctions();
        MI tMI = new MI(checkTopFunc);
        
        Piece oPiece = checkTopFunc.checkersBoard.myBoard[1][6].getPieceOnField();
        Piece mPiece = checkTopFunc.checkersBoard.myBoard[0][1].getPieceOnField();
        
        //Empty the board 
        for (Field[] aF : checkTopFunc.checkersBoard.myBoard) {
            for (Field f : aF) {
                f.emptyThisField();
            }
        }
        
        assertTrue(oPiece != null);
        assertTrue(mPiece != null);
        
        checkTopFunc.checkersBoard.myBoard[2][1].setPieceOnField(oPiece);
        checkTopFunc.checkersBoard.myBoard[5][6].setPieceOnField(mPiece);

        tMI.lookForBestMove();
    }
}
