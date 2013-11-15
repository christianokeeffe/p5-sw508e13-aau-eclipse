package com.OriginalFiles;


import java.awt.Color;
import java.io.IOException;

public class RemoteNXTFunctions {
    public Board checkersBoard;

    public RemoteNXTFunctions() throws InterruptedException, IOException {
    checkersBoard = new Board(this);
    }
    public void resetMotors() {
        // TODO Auto-generated method stub
        
    }

    public void trashPieceOnField(Field field) {
        // TODO Auto-generated method stub
        
    }

    public void doMove(Move move) {
        // TODO Auto-generated method stub
        
    }

    public void waitForRedButton() {
        // TODO Auto-generated method stub
        
    }

    public void initColorSensor() {
        // TODO Auto-generated method stub
        
    }

    public final char getColorOnField(int x, int y) {
        if (x == 2 && y == 5) {
            return ' ';
        }
        if (x == 1 && y == 4) {
            return 'w';
        }
        if (x == 4 && y == 3) {
            return ' ';
        }

        if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
            return checkersBoard.myBoard[x][y].getPieceOnField().color;
        } else {
            return ' ';
        }
    }

}
