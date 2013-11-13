import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import custom.Exceptions.NoKingLeft;

public class Board {

    public Field[][] myBoard = new Field[8][8];
    public Field[] kingPlace = new Field[8];
    public Field[][] trashPlace = new Field[8][2];
    public Analyze analyzeFunctions;

    public Communication informer = new Communication();

    char myPeasentColor, myKingColor, opponentPeasentColor, opponentKingColor;

    public Board(RemoteNXTFunctions remoteFunc)
            throws InterruptedException, IOException {
        analyzeFunctions = new Analyze(this, remoteFunc);

        analyzeFunctions.findMyColors();
        findOpponentColors();

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
                        pieceOnBoard = new Piece();
                        pieceOnBoard.color = myPeasentColor;

                        //Every piece on the front line of each player
                        //is moveable from the start
                        if (y == 2) {
                            pieceOnBoard.isMoveable = true;
                        }
                    }
                    //latex end

                    if (y > 4) {
                        pieceOnBoard = new Piece();
                        pieceOnBoard.color = opponentPeasentColor;
                        if (y == 5) {
                            pieceOnBoard.isMoveable = true;
                        }
                    }
                    temp.setPieceOnField(pieceOnBoard);
                } else {
                    temp.allowedField = false;
                }
                myBoard[x][y] = temp;
            }
        }

        //Set the location of the human players king pieces
        //And Trashfield
        //latex start ConstructorKing
        for (int i = 0; i < 8; i++) {
            Field temp = new Field();
            temp.x = i;
            temp.y = -2;
            Piece tempPiece = new Piece();
            tempPiece.color = opponentKingColor;
            tempPiece.isCrowned = true;
            temp.setPieceOnField(tempPiece);
            kingPlace[i] = temp;
        }
        //latex end
        for (int j = 0; j < 2; j++) {
        for (int i = 0; i < 8; i++) {
            Field temp = new Field();
            temp.x = i;
            temp.y = -3 - j;
            trashPlace[i][j] = temp;
        }
        }
    }

    public final void resetVisited() {
        for (Field[] f : myBoard) {
            for (Field field : f) {
                field.visited = false;
            }
        }
    }

    //Sorts the list of fields to search to minimize robot movement
    public final void sortListOfFields(List<Field> listOfFields) {
        int x = 0;
        int y = 0;
        int sizeOflistOfFields = listOfFields.size();
        for (int i = 0; i < sizeOflistOfFields; i++) {
            for (int n = i + 1; n < sizeOflistOfFields; n++) {
                if (!isGreater(listOfFields.get(i),
                        listOfFields.get(n), x, y)) {
                    Field temp1 = listOfFields.get(i);
                    Field temp2 = listOfFields.get(n);
                    listOfFields.remove(n);
                    listOfFields.remove(i);
                    listOfFields.add(i, temp2);
                    listOfFields.add(n, temp1);
                }
            }
            x = listOfFields.get(i).x;
            y = listOfFields.get(i).y;
        }
    }

    //Sorts the list of fields to search to minimize robot movement
        public final void sortListOfMoves(List<Move> listOfMoves) {
            int x = 0;
            int y = 0;
            for (int i = 0; i < listOfMoves.size(); i++) {
                for (int n = i + 1; n < listOfMoves.size(); n++) {
                    if (!isGreater(listOfMoves.get(i).moves.get(
                        listOfMoves.get(i).moves.size() - 1),
                            listOfMoves.get(n).moves.get(
                                listOfMoves.get(n).moves.size() - 1), x, y)) {
                        Move temp1 = listOfMoves.get(i);
                        Move temp2 = listOfMoves.get(n);
                        listOfMoves.remove(n);
                        listOfMoves.remove(i);
                        listOfMoves.add(i, temp2);
                        listOfMoves.add(n, temp1);
                    }
                }
                x = listOfMoves.get(i).moves.get(
                        listOfMoves.get(i).moves.size() - 1).x;
                y = listOfMoves.get(i).moves.get(
                        listOfMoves.get(i).moves.size() - 1).y;
            }
        }
    //Method to used in sorting the list of places to move the robot
    public final boolean isGreater(Field inputField,
            Field fieldToCompare, int compX, int compY) {
        //Check jumps to be crowned first
        if (inputField.getPieceOnField().canJump
                && !inputField.getPieceOnField().isCrowned
                && inputField.getPieceOnField().color == opponentPeasentColor
                && inputField.getPieceOnField().y == 2) {
            return true;
        } else if (inputField.getPieceOnField().canJump
                && !fieldToCompare.getPieceOnField().canJump) {
            return true;
        } else if (fieldToCompare.getPieceOnField().canJump
                && !inputField.getPieceOnField().canJump) {
            return false;
        } else {
            if (inputField.y == 1 && fieldToCompare.y != 1
                    && !inputField.getPieceOnField().isCrowned) {
                return true;
            } else if (fieldToCompare.y == 1 && inputField.y != 1
                    && !fieldToCompare.getPieceOnField().isCrowned) {
                return false;
            } else {
                if (weight(inputField, compX, compY)
                    > weight(fieldToCompare, compX, compY)) {
                    return false;
                } else if (weight(inputField, compX, compY)
                           < weight(fieldToCompare, compX, compY)) {
                    return true;
                } else {
                    return (inputField.y < fieldToCompare.y);
                }
            }
        }
    }

    private int weight(Field inputField, int x, int y) {
        return Math.abs(inputField.x - x) + Math.abs(inputField.y - y);
    }

    public final boolean verifyOpPieceIsOnField(Field field) throws
    InterruptedException, IOException {
        if (checkAllegiance(field, true)) {
            if (field.getPieceOnField().isMoveable) {
                return !(isFieldEmptyOnBoard(field.x, field.y));
            }
        }
        return true;
    }

    public final boolean peasentIsOnEndRow(Field field) {
        if (!field.isEmpty()) {
            int checkRow;
            if (checkAllegiance(field, true)) {
                checkRow = 0;
            } else {
                checkRow = 7;
            }

            if (field.y == checkRow && !field.getPieceOnField().isCrowned) {
                return true;
            }
        }
        return false;
    }

    //Moves a piece in the board representation
    public final void movePieceInRepresentation(Field fromField, int toFieldX,
            int toFieldY, boolean isSimulated) throws NoKingLeft, IOException {
        movePieceInRepresentation(fromField,
                myBoard[toFieldX][toFieldY], isSimulated);
    }
    //latex start movePieceInRepresentation
    public final void movePieceInRepresentation(Field fromField,
            Field toField, boolean isSimulated) throws NoKingLeft, IOException {
        toField.setPieceOnField(fromField.getPieceOnField());
        fromField.emptyThisField();
        if (checkBounds(toField.x, toField.y)) {
            analyzeFunctions.checkForUpgradeKing(
                    toField, isSimulated);
        }
        //latex end
    }

    //Check if field has been occupied
    public final boolean checkMoveDirection(Field field,
            int directX, int directY) throws InterruptedException, IOException {
        return (!fieldOccupied(field.x + directX, field.y + directY)
            && !this.isFieldEmptyOnBoard(field.x + directX, field.y + directY));
    }

    public final boolean isFieldEmptyOnBoard(int x, int y)
            throws InterruptedException, IOException {
        if (checkBounds(x, y)) {
            char color = analyzeFunctions.getColor(x, y);

            return color == ' ';
        } else {
            return false;
        }
    }

    public final boolean fieldOccupied(int x, int y) {
        if (checkBounds(x, y)) {
            return !myBoard[x][y].isEmpty();
        } else {
            return true;
        }
    }

    //Check if a piece can move and is jumpeable
    //latex start checkPiece
    private void checkPiece(Field field, int dify, boolean checkForOpponent) {
        field.getPieceOnField().canJump = checkJump(field,
                checkForOpponent, field.getPieceOnField().isCrowned);

        if (field.getPieceOnField().canJump) {
            field.getPieceOnField().isMoveable = true;
        } else {
            field.getPieceOnField().isMoveable =
                    checkMoveableBoolean(field, dify);
        }
    }
    //latex end

    private boolean checkMoveableBoolean(Field field, int dif) {
        return !checkMoveable(field, dif).isEmpty();
    }

    //Checks if a given field is movable
    public final List<Field> checkMoveable(Field field, int dif) {
        List<Field> possibleMoves = new ArrayList<Field>();

        //Check forward
        if (!this.fieldOccupied(field.x - 1, field.y + dif)) {
            possibleMoves.add(myBoard[field.x - 1][field.y + dif]);
        }
        if (!this.fieldOccupied(field.x + 1, field.y + dif)) {
            possibleMoves.add(myBoard[field.x + 1][field.y + dif]);
        }
        //if king, check backwards also
        if (field.getPieceOnField().isCrowned) {
            if (!this.fieldOccupied(field.x - 1, field.y - dif)) {
                possibleMoves.add(myBoard[field.x - 1][field.y - dif]);
            }
            if (!this.fieldOccupied(field.x + 1, field.y - dif)) {
                possibleMoves.add(myBoard[field.x + 1][field.y - dif]);
            }
        }
        return possibleMoves;
    }

    //Check if a given field can jump
    //latex start checkJump
    private boolean checkJump(Field field,
            boolean checkForOpponent, boolean isCrowned) {
        return (checkJumpDirectionBoolean(field, -1, -1,
                        checkForOpponent, isCrowned)
                || checkJumpDirectionBoolean(field, 1, -1,
                        checkForOpponent, isCrowned)
                || checkJumpDirectionBoolean(field, 1, 1,
                        checkForOpponent, isCrowned)
                || checkJumpDirectionBoolean(field, -1, 1,
                        checkForOpponent, isCrowned));
    }
    //latex end

    //Checks jumps
    //latex start jumpDirection
    public final Field checkJumpDirection(Field field, int difx, int dify,
            boolean checkForOpponent, boolean isCrowned) {
        if (((checkForOpponent && dify == -1)
                || (!checkForOpponent && dify == 1)) && !isCrowned) {
            return null;
        }

        if (checkBounds(field.x + 2 * difx, field.y + 2 * dify)) {
            if (!myBoard[field.x + 2 * difx][field.y + 2 * dify].visited) {
                if (checkAllegiance(
                      myBoard[field.x + difx][field.y + dify], checkForOpponent)
                      && !this.fieldOccupied(
                      field.x + 2 * difx, field.y + 2 * dify)) {
                    return myBoard[field.x + 2 * difx][field.y + 2 * dify];
                }
            }
        }
        return null;
    }
    //latex end

    private boolean checkJumpDirectionBoolean(Field field,
            int difx, int dify, boolean checkForOpponent, boolean isCrowned) {
        return !(checkJumpDirection(field, difx, dify,
                checkForOpponent, isCrowned) == null);
    }

    //Updates the moveable property on each piece
    public final void updateMoveables() {
        resetVisited();
        for (Field[] f : myBoard) {
            for (Field field : f) {
                if (field.allowedField) {
                    if (field.getPieceOnField() != null) {
                        //Check moveables for robot
                        if (checkAllegiance(field, false)) {
                            checkPiece(field, 1, true);

                        //Check moveable for human
                        } else if (checkAllegiance(field, true)) {
                            checkPiece(field, -1, false);
                        }
                    }
                }
            }
        }
        resetVisited();
    }

    //Sets the colors of the human players pieces
    private void findOpponentColors() {
        if (myPeasentColor == 'r') {
            opponentPeasentColor = 'w';
            opponentKingColor = 'g';
        } else {
            opponentPeasentColor = 'r';
            opponentKingColor = 'b';
        }
    }


    //if a piece is missing, scan whole board
    public final void findMissingPiece()
            throws InterruptedException, IOException {
        int i, j;
        boolean changer = true;

        for (i = 0; i < 8; i++) {
            //Change direction of the robot, to minimize robot movement
            if (changer) {
                for (j = 0; j < 8; j++) {
                    if ((i + j) % 2 == 1) {
                        myBoard[i][j].setPieceOnField(getPiece(i, j));
                    }
                }
                changer = false;
            } else {
                for (j = 7; j >= 0; j--) {
                    if ((i + j) % 2 == 1) {
                        myBoard[i][j].setPieceOnField(getPiece(i, j));
                    }
                }
                changer = true;
            }
        }

        this.updateMoveables();
    }

    //Get piece on given position
    private Piece getPiece(int x, int y) throws IOException {
        char color = analyzeFunctions.getColor(x, y);
        if (color == ' ') {
            return null;
        } else {
            Piece temp = new Piece();
            temp.color = color;
            temp.isCrowned = (color == 'g' || color == 'b');
            return temp;
        }
    }

    //Check if a given piece is the robots or the opponents
    /*public final boolean checkAllegiance(Field input, boolean checkForOpponent)
    {
        if (checkBounds(input.x, input.y)) {
                if ((input.isPieceOfColor(myPeasentColor)
                        || input.isPieceOfColor(myKingColor))&& !checkForOpponent) {
                     return true;
                 }
                 if ((input.isPieceOfColor(opponentPeasentColor)
                         || input.isPieceOfColor(opponentKingColor))
                         && checkForOpponent) {
                     return true;
                 }
                 return false;            }
        
        return false;
    }*/
    public final boolean checkAllegiance(Field input, boolean checkForOpponent)
    {
        if (checkBounds(input.x, input.y)) {
            if (!input.isEmpty())
            {
                return checkAllegiance(input.getPieceOnField(), checkForOpponent);
            }
        }
        return false;
    }

    public final boolean checkAllegiance(Piece input, boolean checkForOpponent)
    {
        if ((input.color == myPeasentColor
                || input.color == myKingColor) && !checkForOpponent) {
             return true;
         }
         if ((input.color == opponentPeasentColor
                 || input.color == opponentKingColor)
                 && checkForOpponent) {
             return true;
         }
         return false;
    }

    //Return true if the given position is inbounds
    public final boolean checkBounds(int x, int y) {
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }
}
