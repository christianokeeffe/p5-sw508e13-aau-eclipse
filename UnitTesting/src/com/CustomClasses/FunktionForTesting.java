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
    
    //produces a field
    protected Field produceField(int x, int y){
        Field temp = new Field();
        temp.x = x;
        temp.y = y;
        temp.allowedField = true;
        
        return temp;
    }
    
    private void resetBoard() throws InterruptedException, IOException{
        checkersBoard = new Board(remote);
    }

}
