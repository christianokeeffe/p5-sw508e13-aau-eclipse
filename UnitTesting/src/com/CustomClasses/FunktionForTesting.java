package com.CustomClasses;

import com.OriginalFiles.Field;
import com.OriginalFiles.Piece;

public class FunktionForTesting {
    //function for creating a piece
    protected Piece producePiece(int x, int y, char color, boolean upgrade){
        Piece temp = new Piece();
        temp.color = color;
        temp.x = x;
        temp.y = y;
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
}
