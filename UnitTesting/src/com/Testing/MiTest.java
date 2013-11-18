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
        
        checkTopFunc.checkersBoard.myBoard[4][3].emptyThisField();
        
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[3][4], 2, 7, false);
        checkTopFunc.checkersBoard.movePieceInRepresentation(checkTopFunc.checkersBoard.myBoard[2][3], 2, 5, false);
        tMI.lookForBestMove();
    }

}
