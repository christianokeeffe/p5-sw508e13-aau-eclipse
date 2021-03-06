package com.OriginalFiles;
import com.CustomClasses.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import custom.Exceptions.IllegalMove;
import custom.Exceptions.NoKingLeft;

public class Analyze {
    private Board checkersBoard;

    private Field fieldToCheck;
    private int analyzeBoardRepeatNumber = 0;
    private int totalAnalyzeRuns = 0;
    private final int analyzeRunsBeforeReset = 10;
    private RemoteNXTFunctions remoteFunctions;
    private boolean pieceFound;
    private boolean mustJump;

    public Analyze(Board input, RemoteNXTFunctions remoteInput) {
        checkersBoard = input;
        remoteFunctions = remoteInput;
    }

    //Analyzes the current board setup
    //latex start analyzeBoard
    public final boolean analyzeBoard() throws InterruptedException,
    IOException, NoKingLeft, IllegalMove {
        if (!checkForGameHasEnded(true)) {
            checkMotorCalibration();

            List<Field> moveableList = new ArrayList<Field>();
            moveableList = makeListOfMoveables(moveableList);
            pieceFound = checkMovement(moveableList);
            countDownToPanic();

            //Find the pieces that are currently movable
            checkersBoard.updateMoveables();
            checkRobotPieceReplaced();
            if (!checkForGameHasEnded(false)) {
                return true;
            }
        }
        return false;
    }
    //latex end

    private void checkMotorCalibration() {
        totalAnalyzeRuns += 1;

        if (totalAnalyzeRuns > analyzeRunsBeforeReset) {
            totalAnalyzeRuns = 0;
            remoteFunctions.resetMotors();
        }
    }

    private List<Field> makeListOfMoveables(List<Field> moveableList) {
        //Find the pieces that are currently movable
        checkersBoard.updateMoveables();
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
        pieceFound = false;
        mustJump = false;
        if (moveableList.size() != 0) {
            mustJump = moveableList.get(0).getPieceOnField().canJump;
        }
        return moveableList;
    }
    //latex start checkMovement
    private boolean checkMovement(List<Field> moveableList)
            throws IllegalMove, InterruptedException, IOException, NoKingLeft {
        for (Field field : moveableList) {
            if (checkersBoard.isFieldEmptyOnBoard(field.x, field.y)) {
                if (mustJump && !field.getPieceOnField().canJump) {
                    throw new custom.Exceptions.IllegalMove();
                }
                if (this.trackMovement(field)) {
                    return true;
                }
            }
        }
        return false;
    }
    //latex end
    //latex start countDownToPanic
    private void countDownToPanic()
            throws InterruptedException, IOException, NoKingLeft, IllegalMove {
        if (!pieceFound) {
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
    }
    //latex end

    //Try to find the piece that have been moved
    private boolean trackMovement(Field field) throws IllegalMove,
    InterruptedException, IOException, NoKingLeft {
        boolean foundPiece = false;
        if (field.getPieceOnField().canJump) {
            if (findJumpPiece(field)) {
                foundPiece = true;
            }
            if (!foundPiece) {
                throw new custom.Exceptions.IllegalMove();
            }
        } else if (checkMove(field, -1)) {
            foundPiece = true;
        }

        return foundPiece;
    }

    //Determine simple move
    private boolean checkMove(Field field, int directY) throws
    InterruptedException, IOException, NoKingLeft {
            //Check the first direction
            if (checkersBoard.checkMoveDirection(field, 1, directY)) {
                if (validateMove(checkersBoard.
                        myBoard[field.x + 1][field.y + directY], field)) {
                    checkersBoard.movePieceInRepresentation(field,
                            field.x + 1, field.y + directY, false);
                    return true;
                } else {
                    return false;
                }
                //Second direction
            } else if (checkersBoard.checkMoveDirection(field, -1, directY)) {
                if (validateMove(checkersBoard.
                        myBoard[field.x - 1][field.y + directY], field)) {
                    checkersBoard.movePieceInRepresentation(field,
                            field.x - 1, field.y + directY, false);
                    return true;
                } else {
                    return false;
                }
                //If king, also check backwards
            } else if (field.getPieceOnField().isCrowned) {
                if (checkersBoard.checkMoveDirection(field, 1, -directY)) {
                    if (validateMove(checkersBoard.
                            myBoard[field.x + 1][field.y - directY], field)) {
                        checkersBoard.movePieceInRepresentation(field,
                                field.x + 1, field.y - directY, false);
                        return true;
                    } else {
                        return false;
                    }
                } else if (checkersBoard.checkMoveDirection(
                        field, -1, -directY)) {
                    if (validateMove(checkersBoard.
                            myBoard[field.x - 1][field.y - directY], field)) {
                        checkersBoard.movePieceInRepresentation(field,
                                field.x - 1, field.y - directY, false);
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        return false;
    }

    //Checks if it could have been another piece that moved
    private boolean validateMove(Field field, Field fromField) throws
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
                returnValue = checkersBoard.verifyCorrectMove(
                        checkArray.get(i));
            }
            if (!returnValue) {
                return returnValue;
            }
        }
        return returnValue;
    }

    public final List<List<Field>> jumpSequence(Field input,
            boolean checkForOpponent, boolean isCrowned) throws
            InterruptedException, IOException, NoKingLeft {
        input.visited = true;
        List<List<Field>> returnList = new ArrayList<List<Field>>();
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
            returnList.add(new ArrayList<Field>());
        }

        for (int i = 0; i < returnList.size(); i++) {
            returnList.get(i).add(0, input);
        }
        return returnList;
    }

    private boolean findJumpPiece(Field field) throws
    InterruptedException, IOException, NoKingLeft {
        List<List<Field>> jumpList = new ArrayList<List<Field>>();
        jumpList = jumpSequence(field, false,
                field.getPieceOnField().isCrowned);
        checkersBoard.resetVisited();
        for (int i = 0; i < jumpList.size(); i++) {
            List<Field> tempList = jumpList.get(i);
            Field desField = tempList.get(tempList.size() - 1);
            if (!checkersBoard.isFieldEmptyOnBoard(desField.x, desField.y)) {
                checkersBoard.movePieceInRepresentation(field, desField, false);
                int stopj = tempList.size() - 1;
                for (int j = 0; j < stopj; j++) {
                    Field tempfield = tempList.get(j);
                    Field tempfield2 = tempList.get(j + 1);
                    Field takenField = checkersBoard.myBoard
                            [(tempfield.x + tempfield2.x) / 2]
                                    [(tempfield.y + tempfield2.y) / 2];
                    if (takenField.getPieceOnField().isCrowned) {
                        int k = 7;
                        while (!(k < 0)) {
                            if (checkersBoard.oppKingPlace[k].isEmpty()) {
                                //Insert king at location
                                checkersBoard.movePieceInRepresentation(
                                        takenField,
                                        checkersBoard.oppKingPlace[k], true);
                            }
                            k--;
                        }
                    } else {
                        int l = 0;
                        int h = 0;
                        OUTER: while (h <= checkersBoard.
                                oppTrashPlace[0].length - 1) {
                            while (l <= checkersBoard.
                                    oppTrashPlace.length - 1) {
                                if (checkersBoard.
                                        oppTrashPlace[l][h].isEmpty()) {
                                    break OUTER;
                                }
                                l++;
                            }
                            h++;
                            l = 0;
                        }
                        checkersBoard.movePieceInRepresentation(takenField,
                                checkersBoard.oppTrashPlace[l][h], false);
                    }
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
                if (isSimulated) {
                    field.getPieceOnField().color = checkersBoard.myKingColor;
                    field.getPieceOnField().isCrowned = true;
                } else {
                    replaceRobotKing(field);
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
                            throw new custom.Exceptions.NoKingLeft();
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

    private void replaceRobotKing(Field toKingField)
            throws NoKingLeft, IOException {
        int i = 0;
        int l = 0;
        int h = 0;

        OUTER: while (h <= checkersBoard.
                oppTrashPlace[0].length - 1) {

            while (l <= checkersBoard.
                    oppTrashPlace.length - 1) {

                if (checkersBoard.
                        oppTrashPlace[l][h].isEmpty()) {
                    break OUTER;
                }
                l++;
            }
            h++;
            l = 0;
        }
        checkersBoard.movePieceInRepresentation(toKingField,
                checkersBoard.oppTrashPlace[l][h], false);
        OUTER: while (!(i > 7)) {
            if (!checkersBoard.oppKingPlace[i].isEmpty()) {
                //Insert king at location
                checkersBoard.movePieceInRepresentation(
                        checkersBoard.oppKingPlace[i],
                        toKingField, true);
                break OUTER;
            } else {
                i++;
            }
        }

        if (i > 7) {
           throw new custom.Exceptions.NoKingLeft();
        }
    }

    //Function to check if user have replaced the
    //robots peasant piece with a king piece
    public final void checkRobotPieceReplaced() throws IOException, NoKingLeft {
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

        int red = colorResult.getRed();
        int green = colorResult.getGreen();
        int blue = colorResult.getBlue();
        if (red > 230 && red < 290 && green < 130 && green > 60
                && blue < 140 && blue > 60) {
            return 'r';
        } else if (red > 230 && red < 285 && green > 235 && green < 285
                && blue > 230 && blue < 280) {
            return 'w';
        } else if (red < 230 && red > 170 && green > 210 && green < 290
                && blue < 230 && blue > 170) {
            return 'g';
        } else if (red < 170 && red > 110 && green < 240 && green > 180
                && blue > 210 && blue < 270) {
            return 'b';
        } else {
            return ' ';
        }
    }

    public final boolean checkForGameHasEnded(boolean isHumansTurn)
            throws IOException, NoKingLeft {
        switch (gameHasEnded(isHumansTurn)) {
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
                boolean humanisInConer =
                        isOnDoubleCorners(humanPieceList.get(0));
                boolean robotisInConer =
                        isOnDoubleCorners(robotPieceList.get(0));
                if (humanisInConer || robotisInConer) {
                    if (humanTurn && !humanPieceList.get(0).canJump) {
                        return 3;
                    } else if (!humanTurn && !robotPieceList.get(0).canJump) {
                        return 3;
                    }
                }
            }
        }
        return 0;
    }

    public final boolean hasTheMove(boolean humansTurn,
            int ownPieces, int oppPieces) {
        int rowToCheck = 0;
        if (ownPieces - oppPieces == 0) {
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
        } else if (!humansTurn && ownPieces > oppPieces) {
            return true;
        } else if (humansTurn && ownPieces < oppPieces) {
            return true;
        }
        return false;
    }

    public final boolean isOnDoubleCorners(Piece piece) {
        if ((piece.getX() == 0 && piece.getY() == 1)
                || (piece.getX() == 1 && piece.getY() == 0)
                || (piece.getX() == 7 && piece.getY() == 6)
                || (piece.getX() == 6 && piece.getY() == 7)) {
            return true;
        }
        return false;
    }

    public final void cleanUp() throws IOException, NoKingLeft {
        for (Field[] fa : checkersBoard.myBoard) {
            for (Field f : fa) {
                if (!f.isEmpty()) {
                    placePiece(f);
                }
            }
        }

        for (Field[] fa : checkersBoard.trashPlace) {
            for (Field f : fa) {
                if (!f.isEmpty()) {
                    placePiece(f);
                }
            }
        }

        for (Field[] fa : checkersBoard.oppTrashPlace) {
            for (Field f : fa) {
                if (!f.isEmpty()) {
                    placePiece(f);
                }
            }
        }
    }

    private void placePiece(Field fieldToMove) throws IOException, NoKingLeft {
        if (fieldToMove.getPieceOnField().isCrowned) {
            moveToKingRow(fieldToMove);
        } else {
            if (isInCorrectEnd(fieldToMove.getPieceOnField())) {
                return;
            } else {
                movePeasent(fieldToMove);
            }
        }
    }

    private void movePeasent(Field fieldToMove) throws IOException, NoKingLeft {
        if (checkersBoard.checkAllegiance(
                fieldToMove.getPieceOnField(), true)) {
            OUTERMOST: for (int i = 0; i <= 7; i++) {
                for (int j = 5; j <= 7; j++) {
                    if (checkersBoard.myBoard[i][j].isEmpty()
                            && checkersBoard.myBoard[i][j].allowedField) {
                        remoteFunctions.doMove(new Move(fieldToMove,
                                checkersBoard.myBoard[i][j], false));
                        break OUTERMOST;
                    }
                }
            }
        } else if (checkersBoard.checkAllegiance(
                fieldToMove.getPieceOnField(), false)) {
            OUTERMOST: for (int i = 0; i <= 7; i++) {
                for (int j = 0; j <= 2; j++) {
                    if (checkersBoard.myBoard[i][j].isEmpty()
                            && checkersBoard.myBoard[i][j].allowedField) {
                        remoteFunctions.doMove(new Move(fieldToMove,
                                checkersBoard.myBoard[i][j], false));
                        break OUTERMOST;
                    }
                }
            }
        }
    }

    private void moveToKingRow(Field fieldToMove)
            throws IOException, NoKingLeft {
        if (checkersBoard.checkAllegiance(
                fieldToMove.getPieceOnField(), false)) {
            int i = 0;
            while (!(i > 7)) {
                if (checkersBoard.oppKingPlace[i].isEmpty()) {
                    //Insert king at location
                    remoteFunctions.doMove(new Move(fieldToMove,
                            checkersBoard.oppKingPlace[i], true));
                    return;
                }
                i++;
            }
        } else if (checkersBoard.checkAllegiance(
                fieldToMove.getPieceOnField(), true)) {
            int i = 0;
            while (!(i > 7)) {
                if (checkersBoard.kingPlace[i].isEmpty()) {
                    //Insert king at location
                    remoteFunctions.doMove(new Move(fieldToMove,
                            checkersBoard.kingPlace[i], true));
                    return;
                }
                i++;
            }
        }
    }

    private boolean isInCorrectEnd(Piece pieceToCheck) {
        return (checkersBoard.checkAllegiance(pieceToCheck, true)
                && (pieceToCheck.getY() >= 5 && pieceToCheck.getY() <= 7))
                || (checkersBoard.checkAllegiance(pieceToCheck, false)
                && (pieceToCheck.getY() >= 0 && pieceToCheck.getY() <= 2));
    }
}
