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
    private Field presentField = null;

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
            if (pastFields.size() > numberOfPastFieldsToCheck
                    && pastFields.size() != 0) {
                pastFields.remove(0);
            }
            if (presentField != null) {
                pastFields.add(presentField);
            }

            presentField = checkersBoard.myBoard[x][y];
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

    public final double priceForPiece(int gameState, int ownPieceCount,
            int oppPieceCount, int turn, boolean hasMove) {
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

    private void updatePrice(int gameState, int ownPieceCount,
            int oppPieceCount, int turn, boolean hasMove) {
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
                if (hasMove) {
                    returnValue += blockValue();
                    returnValue += closeBonus - closestPiece();
                }
                if (!this.isCrowned) {
                    if (checkersBoard.checkAllegiance(this, true)) {
                        returnValue += 7 - y;
                    } else {
                        returnValue += y;
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
                returnValue += backlineBonus / 2;
                if(gameState == isMidGame) {
                    returnValue += backlineBonus / 2; 
                }
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

    private int blockValue() {
        int returnValue = 0;
        int diff = 1;
        if (checkersBoard.checkAllegiance(this, true)) {
            diff = -1;
        }
        returnValue += blockFields(this.x - 1, this.y + diff);
        returnValue += blockFields(this.x + 1, this.y + diff);
        if (this.isCrowned) {
            returnValue += blockFields(this.x - 1, this.y - diff);
            returnValue += blockFields(this.x + 1, this.y - diff);
        }
        return (returnValue / 100) * blockBonus;
    }

    private int blockFields(int xToCheck, int yToCheck) {
        if (checkersBoard.checkBounds(xToCheck, yToCheck)) {
            if (checkersBoard.myBoard[xToCheck][yToCheck].isEmpty()) {
                int returnValue = 0;
                Field thisField = checkersBoard.myBoard[xToCheck][yToCheck];
                returnValue += checkBlockade(xToCheck - 1,
                        yToCheck - 1, thisField);
                returnValue += checkBlockade(xToCheck - 1,
                        yToCheck + 1, thisField);
                returnValue += checkBlockade(xToCheck + 1,
                        yToCheck - 1, thisField);
                returnValue += checkBlockade(xToCheck + 1,
                        yToCheck + 1, thisField);
                return returnValue;
            }
        }

        return 0;
    }

    private int checkBlockade(int xToCheck, int yToCheck, Field blockField) {
        boolean checkForOpponent = !checkersBoard.checkAllegiance(this, true);
        if (checkersBoard.checkBounds(xToCheck, yToCheck)) {
            if (checkersBoard.checkAllegiance(checkersBoard.
                    myBoard[xToCheck][yToCheck], checkForOpponent)) {
                int diff = 1;
                if (checkForOpponent) {
                    diff = -1;
                }
                int procentBlocked = 0;
                if (checkersBoard.fieldOccupied(blockField.x, blockField.y)) {
                    return 0;
                } else {
                    procentBlocked += 25;
                }
                if (checkersBoard.fieldOccupied(xToCheck - 1,
                        yToCheck + diff)) {
                    procentBlocked += 25;
                }
                if (checkersBoard.fieldOccupied(xToCheck + 1,
                        yToCheck + diff)) {
                    procentBlocked += 25;
                }

                if (checkersBoard.myBoard[xToCheck][yToCheck].
                        getPieceOnField().isCrowned) {
                    if (checkersBoard.fieldOccupied(xToCheck - 1,
                            yToCheck - diff)) {
                        procentBlocked += 25;
                    }
                    if (checkersBoard.fieldOccupied(xToCheck + 1,
                            yToCheck - diff)) {
                        procentBlocked += 25;
                    }
                } else {
                    procentBlocked += 50;
                }
                return procentBlocked;
            }
        }
        return 0;
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
                                myBoard[x + i][y + j],
                                !checkersBoard.checkAllegiance(this, true))) {
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
