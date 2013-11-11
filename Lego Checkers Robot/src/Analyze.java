import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import lejos.nxt.LCD;
import lejos.robotics.Color;
import customExceptions.IllegalMove;
import customExceptions.NoKingLeft;


public class Analyze {
    private Board checkersBoard;

    private Field fieldToCheck;
    private int analyzeBoardRepeatNumber = 0;
    private int totalAnalyzeRuns = 0;
    private final int analyzeRunsBeforeReset = 10;
    private RemoteNXTFunctions remoteFunctions;

    public Analyze(Board input, RemoteNXTFunctions remoteInput) {
        checkersBoard = input;
        remoteFunctions = remoteInput;
        }
    //Analyzes the current board setup
    public final boolean analyzeBoard() throws InterruptedException,
    IOException, NoKingLeft, IllegalMove {
        if (!checkForGameHasEnded(true)) {
            totalAnalyzeRuns += 1;

            if (totalAnalyzeRuns > analyzeRunsBeforeReset) {
                totalAnalyzeRuns = 0;
                remoteFunctions.resetMotors();
                }

            //Find the pieces that are currently moveable
            checkersBoard.updateMoveables();

            List<Field> moveableList = new ArrayList<Field>();

            for (Field[] f : checkersBoard.myBoard) {
                for (Field field : f) {
                    if (!field.isEmpty()) {
                        if (field.getPieceOnField().isMoveable
                            && checkersBoard.checkAllegiance(field, true)) {
                            moveableList.add(field);
                        }
                    }
                }
            }

            checkersBoard.sortListOfFields(moveableList);
            boolean foundOne = false;
            boolean mustJump = false;
            if (moveableList.size() != 0) {
                mustJump = moveableList.get(0).getPieceOnField().canJump;
                }

            OUTERMOST: for (Field field : moveableList) {
                if (checkersBoard.isFieldEmptyOnBoard(field.x, field.y)) {
                    if (mustJump && !field.getPieceOnField().canJump) {
                        throw new customExceptions.IllegalMove();
                    }

                    if (this.trackMovement(field)) {
                        foundOne = true;
                        //Break the loop
                        break OUTERMOST;
                    }

                }
            }

            if (!foundOne) {
                if (analyzeBoardRepeatNumber < 3) {
                    analyzeBoardRepeatNumber++;
                    analyzeBoard();
                    analyzeBoardRepeatNumber = 0;
                } else if (analyzeBoardRepeatNumber < 6) {
                    remoteFunctions.resetMotors();
                    analyzeBoardRepeatNumber++;
                    analyzeBoard();
                    analyzeBoardRepeatNumber = 0;
                } else {
                    checkersBoard.findMissingPiece();
                }
            }

            //Find the pieces that are currently moveable
            checkersBoard.updateMoveables();
            checkRobotPieceReplaced();
            if (!checkForGameHasEnded(false)) {
                return true;
            }
        }
        return false;
    }

    //Try to find the piece which has been moved
    private boolean trackMovement(Field field) throws IllegalMove,
    InterruptedException, IOException, NoKingLeft {
        boolean pieceFound = false;
        if (field.getPieceOnField().canJump) {
            if (findJumpPiece(field)) {
                pieceFound = true;
            }
            if (!pieceFound) {
                throw new customExceptions.IllegalMove();
            }
        } else if (checkMove(field, -1)) {
            pieceFound = true;
        }

        return pieceFound;
    }

    //Determine simple move
    private boolean checkMove(Field field, int directY) throws
    InterruptedException, IOException, NoKingLeft {
        //Verify that the given field is inbound
        if (checkersBoard.checkBounds(field.x, field.y)) {
            //Check the first direction
            if (checkersBoard.checkMoveDirection(field, 1, directY)) {
                if (checkIfOthersHasMove(checkersBoard.myBoard
                                       [field.x + 1][field.y + directY], field))
                {
                    checkersBoard.movePieceInRepresentation(field,
                            field.x + 1, field.y + directY, false);
                    return true;
                } else {
                    return false;
                }
            //Second direction
            } else if (checkersBoard.checkMoveDirection(field, -1, directY)) {
                if (checkIfOthersHasMove(checkersBoard.myBoard[field.x - 1]
                        [field.y + directY], field)) {
                    checkersBoard.movePieceInRepresentation(field,
                            field.x - 1, field.y + directY, false);
                    return true;
                } else {
                    return false;
                }
            //If king, also check backwards
            } else if (field.getPieceOnField().isCrowned) {
                if (checkersBoard.checkMoveDirection(field, 1, -directY)) {
                    if (checkIfOthersHasMove(checkersBoard.myBoard[field.x + 1]
                            [field.y - directY], field)) {
                        checkersBoard.movePieceInRepresentation(field,
                                field.x + 1, field.y - directY, false);
                        return true;
                    } else {
                        return false;
                    }
                } else if (checkersBoard.checkMoveDirection(
                        field, -1, -directY)) {
                    if (checkIfOthersHasMove(checkersBoard.myBoard[field.x - 1]
                            [field.y - directY], field)) {
                        checkersBoard.movePieceInRepresentation(field,
                                field.x - 1, field.y - directY, false);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private boolean checkIfOthersHasMove(Field field, Field fromField) throws
    InterruptedException, IOException {

        List<Field> checkArray = new ArrayList<Field>();
        if (checkersBoard.checkBounds(field.x + 1, field.y + 1)) {
            checkArray.add(checkersBoard.myBoard[field.x + 1][field.y + 1]);
        }
        if (checkersBoard.checkBounds(field.x + 1, field.y - 1)) {
            checkArray.add(checkersBoard.myBoard[field.x + 1][field.y - 1]);
        }
        if (checkersBoard.checkBounds(field.x - 1, field.y - 1)) {
            checkArray.add(checkersBoard.myBoard[field.x - 1][field.y - 1]);
        }
        if (checkersBoard.checkBounds(field.x - 1, field.y + 1)) {
            checkArray.add(checkersBoard.myBoard[field.x - 1][field.y + 1]);
        }
        boolean returnValue = true;
        for (int i = 0; i < checkArray.size(); i++) {
            if (!(checkArray.get(i).x == fromField.x
                  && checkArray.get(i).y == fromField.y)) {
                returnValue = checkersBoard.verifyOpPieceIsOnField(
                        checkArray.get(i));
            }
            if (!returnValue) {
                return returnValue;
            }
        }
        return returnValue;
    }

    public final List<Stack<Field>> jumpSequence(Field input,
            boolean checkForOpponent, boolean isCrowned) throws
            InterruptedException, IOException, NoKingLeft {
        input.visited = true;
        List<Stack<Field>> returnList = new ArrayList<Stack<Field>>();
        Field tempField = checkersBoard.checkJumpDirection(input,
                -1, 1, checkForOpponent, isCrowned);
        if (tempField != null) {
            returnList.addAll(jumpSequence(tempField, checkForOpponent,
                    isCrowned));
        }
        tempField = checkersBoard.checkJumpDirection(input,
                -1, -1, checkForOpponent, isCrowned);
        if (tempField != null) {
            returnList.addAll(jumpSequence(tempField, checkForOpponent,
                    isCrowned));
        }
        tempField = checkersBoard.checkJumpDirection(input,
                1, -1, checkForOpponent, isCrowned);
        if (tempField != null) {
            returnList.addAll(jumpSequence(tempField, checkForOpponent,
                    isCrowned));
        }
        tempField = checkersBoard.checkJumpDirection(input, 1, 1,
                checkForOpponent, isCrowned);
        if (tempField != null) {
            returnList.addAll(jumpSequence(tempField, checkForOpponent,
                    isCrowned));
        }

        if (returnList.size() == 0) {
            returnList.add(new Stack<Field>());
        }

        for (int i = 0; i < returnList.size(); i++) {
            returnList.get(i).push(input);
        }
        return returnList;
    }

    private boolean findJumpPiece(Field field) throws
        InterruptedException, IOException, NoKingLeft {
        List<Stack<Field>> jumpList = new ArrayList<Stack<Field>>();
        jumpList = jumpSequence(field, false,
                field.getPieceOnField().isCrowned);
        checkersBoard.resetVisited();
        for (int i = 0; i < jumpList.size(); i++) {
            Stack<Field> tempList = new Stack<Field>();
            int stop = jumpList.get(i).size();
            for (int j = 0; j < stop; j++) {
                tempList.push(jumpList.get(i).pop());
            }
            Field desField = tempList.peek();
            if (!checkersBoard.isFieldEmptyOnBoard(desField.x, desField.y)) {
                checkersBoard.movePieceInRepresentation(field, desField, false);
                int stopj = tempList.size() - 1;
                for (int j = 0; j < stopj; j++) {
                    Field tempfield = tempList.pop();
                    Field tempfield2 = tempList.peek();
                    Field takenField = checkersBoard.myBoard
                            [(tempfield.x + tempfield2.x) / 2]
                            [(tempfield.y + tempfield2.y) / 2];
                    checkersBoard.movePieceInRepresentation(takenField,
                            remoteFunctions.trashField, false);
                }
                return true;
            }
        }
        return false;
    }

    public final void checkForUpgradeKing(Field field, boolean isSimulated)
            throws NoKingLeft, IOException {
        if (checkersBoard.peasentIsOnEndRow(field)) {
            if (checkersBoard.checkAllegiance(field, false)) {
                field.getPieceOnField().color = checkersBoard.myKingColor;
                field.getPieceOnField().isCrowned = true;
                if (!isSimulated) {
                    fieldToCheck = field;
                }
            } else {
                if (isSimulated) {
                    field.getPieceOnField().color =
                            checkersBoard.opponentKingColor;
                    field.getPieceOnField().isCrowned = true;
                } else {
                    boolean foundOne = false;
                    int i = 0;
                    while (!foundOne) {
                        if (i > 7) {
                            throw new customExceptions.NoKingLeft();
                        }
                        if (!checkersBoard.kingPlace[i].isEmpty()) {
                            //Move old piece to trash
                            remoteFunctions.trashPieceOnField(field);
                            //Insert king at location
                            remoteFunctions.doMove(new Move(
                                    checkersBoard.kingPlace[i], field, true));
                            foundOne = true;
                        }
                        i++;
                    }
                }
            }
        }
    }

    //Function to check if user have replaced the
    //robots peasent piece with a king piece
    public final void checkRobotPieceReplaced() throws IOException {
        if (fieldToCheck != null) {
            boolean checkCondition = true;
            while (checkCondition) {
                if (getColor(fieldToCheck.x, fieldToCheck.y)
                        == checkersBoard.myKingColor) {
                    checkCondition = false;
                    fieldToCheck = null;
                } else {
                    checkersBoard.informer.myKingNotPlaced();
                    remoteFunctions.waitForRedButton();
                }
            }
        }
    }

    //Sets the colors of the pieces of the robot
    public final void findMyColors() throws InterruptedException, IOException {
        checkersBoard.myPeasentColor = getColor(0, 1);
        if (checkersBoard.myPeasentColor == 'r') {
            checkersBoard.myKingColor = 'b';
        } else if (checkersBoard.myPeasentColor == 'w') {
            checkersBoard.myKingColor = 'g';
        } else {
            remoteFunctions.resetMotors();
            remoteFunctions.initColorSensor();
            findMyColors();
        }
    }

    //Returns the color on the given position
    public final char getColor(int x, int y) throws IOException {
        Color colorResult = remoteFunctions.getColorOnField(x, y);

        LCD.clear();
        int red = colorResult.getRed();
        int green = colorResult.getGreen();
        int blue = colorResult.getBlue();
        LCD.drawInt(red, 0, 1);
        LCD.drawInt(green, 0, 2);
        LCD.drawInt(blue, 0, 3);
        if (red > 230  && green < 130 && blue < 130) {
            LCD.drawChar('r', 0, 0); LCD.refresh();
            return 'r';
        } else if (red > 250 && green > 250 && blue > 250) {
            LCD.drawChar('w', 0, 0); LCD.refresh();
            return 'w';
        } else if (red < 210 && green > 210  && blue < 210) {
            LCD.drawChar('g', 0, 0); LCD.refresh();
            return 'g';
        } else if (red < 160 && green < 220 && blue > 210) {
            LCD.drawChar('b', 0, 0); LCD.refresh();
            return 'b';
        } else {
            LCD.drawChar('e', 0, 0); LCD.refresh();
            return ' ';
        }
    }

    public final boolean checkForGameHasEnded(boolean isHumansTurn) {
        switch (gameHasEnded(isHumansTurn)) {
        case 0:
            return false;
        case 1:
            checkersBoard.informer.humanWon();
            return true;
        case 2:
            checkersBoard.informer.robotWon();
            return true;
        case 3:
            checkersBoard.informer.draw();
            return true;
        default:
            return false;
        }
    }

    /*returns 0 if game has not ended, 1 if the human won,
     * 2 if the robot won and 3 if it was a draw*/
    public final int gameHasEnded(boolean humanTurn) {
        checkersBoard.updateMoveables();

        List<Piece> humanPieceList = new ArrayList<Piece>();
        List<Piece> robotPieceList = new ArrayList<Piece>();
        boolean robotHasMovable = false;
        boolean humanHasMovable = false;

        for (Field[] f : checkersBoard.myBoard) {
            for (Field field : f) {
                if (!field.isEmpty()) {
                    if (checkersBoard.checkAllegiance(field, true)) {
                        humanPieceList.add(field.getPieceOnField());
                        if (field.getPieceOnField().isMoveable) {
                            humanHasMovable = true;
                        }
                    }
                    if (checkersBoard.checkAllegiance(field, false)) {
                        robotPieceList.add(field.getPieceOnField());
                        if (field.getPieceOnField().isMoveable) {
                            robotHasMovable = true;
                        }
                    }
                }
            }
        }

        if (robotPieceList.size() == 0 || (!humanTurn && !robotHasMovable)) {
            return 1;
        }
        if (humanPieceList.size() == 0 || (humanTurn && !humanHasMovable)) {
            return 2;
        }
        if (robotPieceList.size() == 1 && humanPieceList.size() == 1) {
            if (robotPieceList.get(0).isCrowned
                    && humanPieceList.get(0).isCrowned) {
                if (humanTurn) {
                    if (isOnDoubleCorners(humanPieceList.get(0))
                            && isNearDoubleCorners(robotPieceList.get(0))
                            && !hasTheMove(true)) {
                        return 3;
                    }
                } else {
                    if (isOnDoubleCorners(robotPieceList.get(0))
                            && isNearDoubleCorners(humanPieceList.get(0))
                            && !hasTheMove(false)) {
                        return 3;
                    }
                }
            }
        }
        return 0;
    }

    private boolean hasTheMove(boolean humansTurn) {
        int rowToCheck = 0;

        if (!humansTurn) {
            rowToCheck = 1;
        }

        int pieceCount = 0;

        for (int i = rowToCheck; i < 8; i += 2) {
            for (int j = 0; j < 8; j++) {
                if (!checkersBoard.myBoard[i][j].isEmpty()) {
                    pieceCount++;
                }
            }
        }

        if (pieceCount % 2 == 1) {
            return true;
        }
        return false;
    }

    private boolean isOnDoubleCorners(Piece piece) {
        if ((piece.x == 0 && piece.y == 1) || (piece.x == 1 && piece.y == 0)
                || (piece.x == 7 && piece.y == 6)
                || (piece.x == 6 && piece.y == 7)) {
            return true;
        }
        return false;
    }

    private boolean isNearDoubleCorners(Piece piece) {
        if ((piece.x == 1 && piece.y == 2) || (piece.x == 2 && piece.y == 1)
                || (piece.x == 6 && piece.y == 5)
                || (piece.x == 5 && piece.y == 6)
                || (piece.x == 2 && piece.y == 3)
                || (piece.x == 3 && piece.y == 2)
                || (piece.x == 4 && piece.y == 5)
                || (piece.x == 5 && piece.y == 4)) {
            return true;
        }
        return false;
    }

}
