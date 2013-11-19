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
        checkersBoard = remote.checkersBoard;
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
        emptyBoard();
        remote.analyzeTestVariable = 0;
        remote.analyzeresetMotorsTestVariable = false;
        int x, y;
        
        //Create the 8 times 8 board
        for (x = 0; x < 8; x++) {
            for (y = 0; y < 8; y++) {
                Field temp = new Field();
                temp.x = x;
                temp.y = y;

                //Every second field is an allowed field
                //latex start ConstructorLoop
                if ((x + y) % 2 == 1) {
                    temp.allowedField = true;
                    Piece pieceOnBoard = null;
                    if (y < 3) {
                        pieceOnBoard = new Piece(checkersBoard);
                        pieceOnBoard.color = checkersBoard.analyzeFunctions.getColor(0, 1);

                        //Every piece on the front line of each player
                        //is moveable from the start
                        if (y == 2) {
                            pieceOnBoard.isMoveable = true;
                        }
                    }
                    //latex end

                    if (y > 4) {
                        pieceOnBoard = new Piece(checkersBoard);
                        if(checkersBoard.analyzeFunctions.getColor(0, 1) == 'r') {
                            pieceOnBoard.color = 'w';
                        } else {
                            pieceOnBoard.color = 'r';
                        }
                        if (y == 5) {
                            pieceOnBoard.isMoveable = true;
                        }
                    }
                    temp.setPieceOnField(pieceOnBoard);
                } else {
                    temp.allowedField = false;
                }
                checkersBoard.myBoard[x][y] = temp;
            }
        }

        //Set the location of the human players king pieces
        //And Trashfield
        //latex start ConstructorKing
        for (int i = 0; i < 8; i++) {
            Field temp = new Field();
            temp.x = i;
            temp.y = -2;
            Piece tempPiece = new Piece(checkersBoard);
            if(checkersBoard.analyzeFunctions.getColor(0, 1) == 'r') {
                tempPiece.color = 'g';
            } else {
                tempPiece.color = 'b';
            }
            tempPiece.isCrowned = true;
            temp.setPieceOnField(tempPiece);
            checkersBoard.kingPlace[i] = temp;
        }
        //latex end
        for (int j = 0; j < 2; j++) {
        for (int i = 0; i < 8; i++) {
            Field temp = new Field();
            temp.x = i;
            temp.y = -3 - j;
            checkersBoard.trashPlace[i][j] = temp;
        }
        }
    }

}
