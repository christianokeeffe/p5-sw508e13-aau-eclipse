package com.OriginalFiles;

import com.CustomClasses.Color;
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

    public final Color getColorOnField(int x, int y) {
        Color output = new Color();
        if(checkersBoard == null)
        {
            output.setColor('w');
        } else if (x == 2 && y == 5) {
            output.setColor('r');
        } else if (x == 1 && y == 4) {
            output.setColor('w');
        } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
           output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
        } else {
            output.setColor(' ');
        }

        return output;
    }

}
