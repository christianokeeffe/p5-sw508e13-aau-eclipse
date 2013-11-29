import java.util.ArrayList;
import java.util.List;


public class Piece {
    //latex start Piececode
    private int x = -1, y = -1;
    public char color;
    public boolean isMoveable = false;
    public boolean canJump = false;
    public boolean isCrowned = false;
    private double currentValue = 0;
    private int calculatedGameState = 0;
    private final int isMidGame = 1;
    private final int isEndGame = 2;
    private Board checkersBoard;
    private final int numberOfPastFieldsToCheck = 1;
    
    private final int hasBeenOnField = 5;
    private final int valueOfPiece = 100;
    private final int middleBonus = 3;
    private final int closeBonus = 4;
    private final int backlineBonus = 7;
    private final int kingBonus = 150;
    private final int crownAble = 80;
    private final int nearDoubleBonus = 5;
    private final int doubleCornerBonus = 8;
    private final int blockBonus = 10;
    //latex end
    private List<Field> pastFields = new ArrayList<Field>();

    public Piece(Board input) {
        checkersBoard = input;
    }

    public final int getY() {
        return y;
    }

    public final int getX() {
        return x;
    }

    public final void setXY(int inputX, int inputY) {
        x = inputX;
        y = inputY;
        if (checkersBoard.checkBounds(x, y) && isCrowned) {
            if (pastFields.size() > numberOfPastFieldsToCheck) {
                pastFields.remove(0);
            }
            pastFields.add(checkersBoard.myBoard[x][y]);
        } else {
            pastFields.clear();
        }
        updatePriceMidgame();
    }

    private boolean isOnBoard() {
        if (checkersBoard.checkBounds(x, y)) {
            return true;
        } else {
            currentValue = 0;
            return false;
        }
    }

    public final double priceForPiece(int gameState, int ownPieceCount, int oppPieceCount, int turn, boolean hasMove) {
        if (calculatedGameState != gameState || gameState == isEndGame) {
            updatePrice(gameState, ownPieceCount, oppPieceCount, turn, hasMove);
        }
        return currentValue;
    }

    private int min(int a, int b) {
        if (a < b) {
            return a;
        }
        return b;
    }

    private void updatePriceMidgame() {
        updatePrice(isMidGame, 100, 100, 0, false);
    }

    private void updatePrice(int gameState, int ownPieceCount, int oppPieceCount, int turn, boolean hasMove) {
        if (isOnBoard()) {
            calculatedGameState = gameState;
            int returnValue = valueOfPiece;
            if (pastFields.contains(checkersBoard.myBoard[x][y])) {
                returnValue -= hasBeenOnField;
            }
            if (gameState == isMidGame) {
                returnValue += middleBonus
                        - min(Math.abs(3 - x), Math.abs(4 - x));
            }
            if (gameState == isEndGame) {
                if (ownPieceCount - oppPieceCount > 0) {
                    if (blocksAPiece()) {
                        returnValue += blockBonus;
                    }
                    returnValue += closeBonus - closestPiece();
                } else if (ownPieceCount == oppPieceCount && hasMove) {
                    if (blocksAPiece()) {
                        returnValue += blockBonus;
                    }
                    returnValue += closeBonus - closestPiece();
                } else if (ownPieceCount - oppPieceCount < 0) {
                    if (!this.isCrowned) {
                        if (checkersBoard.checkAllegiance(this, true)) {
                            returnValue += 7 - y;
                        } else {
                            returnValue += y;
                        }
                    }
                }
                if (ownPieceCount == 1) {
                    if (isNearDoubleCorners()) {
                        returnValue += nearDoubleBonus;
                    }
                    if (isNearDoubleCorners() && this.isCrowned) {
                        returnValue += nearDoubleBonus;
                    }

                    if (checkersBoard.analyzeFunctions.
                            isOnDoubleCorners(this) && this.isCrowned) {
                        returnValue += doubleCornerBonus;
                    }
                }

            }
            if (!isCrowned
                    && ((checkersBoard.checkAllegiance(this, true)
                            && y == 1)
                            || (checkersBoard.checkAllegiance(this, false)
                                    && y == 6))) {
                returnValue += crownAble;
            }

            if (!isCrowned
                    && ((checkersBoard.checkAllegiance(this, true)
                            && y == 7)
                            || (checkersBoard.checkAllegiance(this, false)
                                    && y == 0))) {
                if (gameState == isMidGame) {
                    returnValue += backlineBonus / 2;
                }
                returnValue += backlineBonus / 2;
            }
            if (isCrowned) {
                returnValue += kingBonus;
            }
            currentValue = returnValue;
        }
    }

    private boolean isNearDoubleCorners() {
        if ((this.getX() == 1 && this.getY() == 2)
                || (this.getX() == 2 && this.getY() == 1)
                || (this.getX() == 6 && this.getY() == 5)
                || (this.getX() == 5 && this.getY() == 6)
                || (this.getX() == 2 && this.getY() == 3)
                || (this.getX() == 3 && this.getY() == 2)
                || (this.getX() == 4 && this.getY() == 5)
                || (this.getX() == 5 && this.getY() == 4)) {
            return true;
        }
        return false;
    }

    private boolean checkBlocksBackline(int inputX, int inputY) {
        if(checkersBoard.checkAllegiance(checkersBoard.myBoard[inputX][inputY], true))
        {
            if(checkersBoard.checkBounds(inputX+2, inputY))
            {
                if(checkersBoard.myBoard[inputX+2][inputY].isEmpty())
                {
                    return true;
                }
            }

            if(checkersBoard.checkBounds(inputX-2, inputY))
            {
                if(checkersBoard.myBoard[inputX-2][inputY].isEmpty())
                {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean blocksAPiece() {
        int direction = -2;
        boolean checkForOpponent = false;

        if (checkersBoard.checkAllegiance(this, !checkForOpponent)) {
            direction = 2;
            checkForOpponent = true;
        }
        if (checkersBoard.checkBounds(this.x, this.y + direction)) {
            if (checkersBoard.checkAllegiance(checkersBoard.
                    myBoard[this.x][this.y + direction], checkForOpponent)) {
                return true;
            }
        }

        if (this.isCrowned) {
            if (checkersBoard.checkBounds(this.x, this.y - direction)) {
                if (checkersBoard.checkAllegiance(checkersBoard.
                        myBoard[this.x][this.y - direction],
                        checkForOpponent)) {
                    if (checkersBoard.myBoard[this.x][this.y - direction].
                            getPieceOnField().isCrowned) {
                        return true;
                    }
                }
            }

            if (checkersBoard.checkBounds(this.x - direction, this.y)) {
                if (checkersBoard.checkAllegiance(checkersBoard.
                        myBoard[this.x - direction][this.y], checkForOpponent)) {
                    return true;
                }
            }

            if (checkersBoard.checkBounds(this.x + direction, this.y)) {
                if (checkersBoard.checkAllegiance(checkersBoard.
                        myBoard[this.x + direction][this.y], checkForOpponent)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int closestPiece() {
        boolean found = false;
        int distance = 0;
        while (!found && distance < 8) {
            distance += 1;
            OUTERMOST: for (int i = -distance; i < 1 + distance; i++) {
                for (int j = -distance; j < 1 + distance; j++) {
                    if (checkersBoard.
                            checkBounds(x + i, y + j)) {
                        if (checkersBoard.checkAllegiance(
                                checkersBoard.
                                myBoard[x + i][y + j], true)) {
                            found = true;
                            break OUTERMOST;
                        }
                    }
                }
            }
        }
        return distance;
    }
}
