package com.CustomClasses;

import java.io.IOException;

import com.OriginalFiles.Board;
import com.OriginalFiles.Field;
import com.OriginalFiles.Piece;
import com.OriginalFiles.RemoteNXTFunctions;

public class FunktionForTesting {
    protected RemoteNXTFunctions remote;
    protected Board checkersBoard;
    public FunktionForTesting() throws InterruptedException, IOException {
        remote = new RemoteNXTFunctions();
        checkersBoard = new Board(remote);
    }
    //function for creating a piece
    protected Piece producePiece(int x, int y, char color, boolean upgrade){
        Piece temp = new Piece(checkersBoard);
        temp.color = color;
        temp.setXY(x, y);
        if (upgrade) {
            temp.isMoveable = true;
            temp.isCrowned = true;
            temp.canJump = true;
        }

        return temp;
    }
    //Used to empty the entire board
    public final void emptyBoard() {
        for (Field[] aF : checkersBoard.myBoard) {
            for (Field f : aF) {
                f.emptyThisField();
            }
        }

    }
    //Method used to construct a win scenario for either of the players
    protected void constructWinCase(boolean b) {
        for (Field[] aF : checkersBoard.myBoard) {
            for (Field f : aF) {
                if (f.getPieceOnField() != null) {
                    if (b) {
                        if (f.getPieceOnField().color == 'r') {
                            checkersBoard.myBoard[f.x][f.y].getPieceOnField().color = 'w';
                        }
                    } else {
                        if (f.getPieceOnField().color == 'w') {
                            checkersBoard.myBoard[f.x][f.y].getPieceOnField().color = 'r';
                        }
                    }
                }
            }
        }
    }

    //produces a field
    protected Field produceField(int x, int y){
        Field temp = new Field();
        temp.x = x;
        temp.y = y;
        temp.allowedField = true;
        
        return temp;
    }
    
    protected void resetBoard() throws InterruptedException, IOException{
        checkersBoard = new Board(remote);
    }

}
