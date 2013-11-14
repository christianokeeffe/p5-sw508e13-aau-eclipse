
public class Piece {
    private int x = -1, y = -1;
    public char color;
    public boolean isMoveable = false;
    public boolean canJump = false;
    public boolean isCrowned = false;
    private double currentValue = 0;
    private int calculatedGameState = 0;
    private final int isMidgame = 1;
    private final int isEndgame = 2;
    private Board checkersBoard;

    private final int valueOfPiece = 10;
    private final int middleBonus = 3;
    private final int closeBonus = 4;
    private final int backlineBonus = 7;
    private final int kingBonus = 15;

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

    public final double priceForPiece(int gameState) {
        if (calculatedGameState != gameState || gameState == isEndgame) {
                updatePrice(gameState);
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
        updatePrice(isMidgame);
    }

    private void updatePrice(int gameState) {
        if (isOnBoard()) {
            calculatedGameState = gameState;
            int returnValue = valueOfPiece;
            if (gameState == isMidgame) {
                returnValue += middleBonus
                        - min(Math.abs(3 - x), Math.abs(4 - x));
            }
            if (gameState == isEndgame) {
                returnValue += closeBonus - closestPiece();
            }

            if (!isCrowned
                    && ((checkersBoard.checkAllegiance(this, true)
                            && y == 7)
                    || (checkersBoard.checkAllegiance(this, false)
                            && y == 0))) {
                if (gameState == isMidgame) {
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
