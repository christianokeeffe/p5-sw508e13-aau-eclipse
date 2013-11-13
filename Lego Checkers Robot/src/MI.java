import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import custom.Exceptions.NoKingLeft;
import lejos.util.Stopwatch;


public class MI {
    public RemoteNXTFunctions remoteNXT;
    private final double inf = 100000.0;
    public Stopwatch sW = new Stopwatch();
    private List<Move> simulatedMoves = new ArrayList<Move>();
    MI(RemoteNXTFunctions inputRemoteNXT) {
        remoteNXT = inputRemoteNXT;
    }
    public int totalTimeForPossibleMoves = 0;
    public int numberOftimesforPossibleMoves = 0;
    public int totalTimeForEvaluation = 0;
    public int numberOftimesforEvaluation = 0;

    /* ------------------------------------------------------------------  *
    /* MI brain starts */

    ///Test method
    public final void scanPieces(int side) throws IOException {
        for (Field[] f:remoteNXT.checkersBoard.myBoard) {
            for (Field q: f) {
                if (!q.isEmpty()) {
                    if (side == 1) {
                        if (remoteNXT.checkersBoard.checkAllegiance(q, false)
                                && q.getPieceOnField().isCrowned) {
                            remoteNXT.getColorOnField(q.x, q.y);

                            if (!q.isEmpty() && q.getPieceOnField().isCrowned) {
                                Sound.twoBeeps();
                            }
                        }
                    } else {
                        if (remoteNXT.checkersBoard.checkAllegiance(q, true)
                                && q.getPieceOnField().isCrowned) {
                            remoteNXT.getColorOnField(q.x, q.y);

                            if (!q.isEmpty() && q.getPieceOnField().isCrowned) {
                                Sound.twoBeeps();
                            }
                        }
                    }
                }
            }
        }
    }


    public final Move lookForBestMove() throws NoKingLeft, IOException,
                                         InterruptedException {
        List<Move> posMoves = possibleMovesForRobot();
        List<Move> bestMoves = new ArrayList<Move>();
        double price = -inf;
        double tempPrice;

        if (posMoves.size() == 1) {
            return posMoves.get(0);
        }

        for (Move move : posMoves) {
            revertAllMoves();
            simulateMove(move);

            tempPrice =  -negaMax(numberofmovelook, -1, -inf, -price);
            revertMove();

            if (tempPrice > price) {
                price = tempPrice;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (tempPrice == price) {
                bestMoves.add(move);
            }
        }
        LCD.clear();
        LCD.drawString("E TT: " + totalTimeForEvaluation, 0, 0);
        LCD.drawString("E N: " + numberOftimesforEvaluation, 0, 1);
        LCD.refresh();
        Button.waitForAnyPress();
        LCD.clear();
        LCD.drawString("P TT: " + totalTimeForPossibleMoves, 0, 0);
        LCD.drawString("P N: " + numberOftimesforPossibleMoves, 0, 1);
        LCD.refresh();
        Button.waitForAnyPress();

        return bestMoves.get((int) (Math.random() * (bestMoves.size() - 1)));
    }


    public final double negaMax(int depth, int turn, double alpha, double beta)
            throws NoKingLeft, IOException, InterruptedException {
        if (depth == 0
                || remoteNXT.checkersBoard.analyzeFunctions.
                gameHasEnded(-1 == turn) != 0) {
            return turn * evaluation(turn);
        }
        List<Move> moves;
        if (turn == 1) {
            moves = possibleMovesForRobot();
        } else {
            moves = possibleMovesForHuman();
        }

        double bestValue = -inf;

        OUTERMOST: for (Move move : moves) {
            simulateMove(move);
            double newScore = -negaMax(depth - 1, -turn, -beta, -alpha);
            revertMove();

            bestValue = max(bestValue, newScore);
            alpha = max(alpha, newScore);
            if (alpha >= beta) {
                break OUTERMOST;
            }
        }
        return bestValue;
    }
    /* how much the AI/MI looks forward */
    private int numberofmovelook        = 2;

    /* how glad the MI/AI are for the result of the game */
    private double gameIsWon = inf / 2;
    private final int gameIsDraw = 20;

    private final int valueOfPiece = 10;
    private final int middleBonus = 3;
    private final int closeBonus = 4;
    private final int backlineBonus = 7;
    private final int pieceDifferenceFactor = 4;
    private final int kingBonus = 8;

    private final int isMidgame = 1;
    private final int isEndgame = 2;
    private final int midgameEnd = 7;

    private double evaluation(int turn) {
        sW.reset();
        List<Field> ownPieces = new ArrayList<Field>();
        List<Field> oppPieces = new ArrayList<Field>();
        double valueOfBoard = 0;
        remoteNXT.checkersBoard.updateMoveables();
        for (Field[] f: remoteNXT.checkersBoard.myBoard) {
            for (Field q: f) {
                if (!q.isEmpty()) {
                    if (remoteNXT.checkersBoard.checkAllegiance(q, false)) {
                        ownPieces.add(q);
                    } else {
                        oppPieces.add(q);
                    }
                }
            }
        }

        int state = gameState(ownPieces.size(), oppPieces.size());

        for (int i = 0; i < ownPieces.size(); i++) {
            valueOfBoard += priceForField(ownPieces.get(i), state);
        }
        for (int i = 0; i < oppPieces.size(); i++) {
            valueOfBoard -= priceForField(oppPieces.get(i), state);
        }

        boolean isHuman = (turn == -1);

        switch (remoteNXT.checkersBoard.
                analyzeFunctions.gameHasEnded(isHuman)) {
        case 1:
            valueOfBoard -= gameIsWon;
            break;
        case 2:
            valueOfBoard += gameIsWon;
            break;
        case 3:
            if (ownPieces.size() - oppPieces.size() > 0) {
                valueOfBoard -= gameIsDraw;
            } else if (ownPieces.size() - oppPieces.size() < 0) {
                valueOfBoard += gameIsDraw;
            }
            break;
        default:
            break;
        }

        valueOfBoard +=  pieceDifferenceFactor
                * ((ownPieces.size() / oppPieces.size()) - 1);
        totalTimeForEvaluation += sW.elapsed();
        numberOftimesforEvaluation++;
        return valueOfBoard;
    }

    private int gameState(int ownPieces, int oppPieces) {
        if (max(ownPieces, oppPieces) >= midgameEnd) {
            return isMidgame;
        }
        return isEndgame;
    }

    private double priceForField(Field field, int gameState) {
        int returnValue = 0;

        if (gameState == isMidgame) {
            returnValue += valueOfPiece + middleBonus
                       - min(Math.abs(3 - field.x), Math.abs(4 - field.x));
        }
        if (gameState == isEndgame) {
            returnValue += closeBonus - closestPiece(field);
        }

        if (!field.getPieceOnField().isCrowned
                && ((remoteNXT.checkersBoard.checkAllegiance(field, true)
                        && field.y == 7)
                || (remoteNXT.checkersBoard.checkAllegiance(field, false)
                        && field.y == 0))) {
            if (gameState == isMidgame) {
                returnValue += backlineBonus / 2;
            }
            returnValue += backlineBonus / 2;
        }
        if (field.getPieceOnField().isCrowned) {
            returnValue += kingBonus;
        }
        return returnValue;
    }

    private int closestPiece(Field field) {
        boolean found = false;
        int distance = 0;
        while (!found && distance < 8) {
            distance += 1;
            for (int i = -distance; i < 1 + distance; i++) {
                for (int j = -distance; j < 1 + distance; j++) {
                    if (remoteNXT.checkersBoard.
                            checkBounds(field.x + i, field.y + j)) {
                        if (remoteNXT.checkersBoard.checkAllegiance(
                                remoteNXT.checkersBoard.
                                myBoard[field.x + i][field.y + j], true)) {
                            found = true;
                            i = (int) inf;
                            j = (int) inf;
                        }
                    }
                }
            }
        }
        return distance;
    }

    private int min(int x, int y) {
        if (x < y) {
            return x;
        }
        return y;
    }

    private double max(double x, double y) {
        if (x < y) {
            return y;
        }
        return x;
    }


    /* MI brain stops */
    /* ---------------------------------------------------------------------  */
    private List<Move> possibleMovesForHuman() throws InterruptedException,
                                                      IOException, NoKingLeft {
        return possibleMoves(-1);
    }

    private List<Move> possibleMovesForRobot() throws InterruptedException,
                                                      IOException, NoKingLeft {
        return possibleMoves(1);
    }

    public final void simulateMove(Move move) throws NoKingLeft, IOException {
        if (move.moves.size() >= 2) {
            int stop = move.moves.size() - 1;

           // Stack<Field> tempStack = new Stack<Field>();                                                    //HERE CHANGE
            for (int i = 0; i < stop; i++) {
                //Field from = move.moves.pop();                                                                        //HERE CHANGE
               // Field to = move.moves.peek();                                                                 //HERE CHANGE
                
                Field from = move.moves.get(i);                                     //HERE CHANGE
                Field to = move.moves.get(i+1);                                       //HERE CHANGE
                
                if (Math.abs(from.x - to.x) == 2) {
                    move.takenPieces.add(remoteNXT.checkersBoard.myBoard                ////HERE CHANGE
                            [(from.x + to.x) / 2]
                            [(from.y + to.y) / 2].getPieceOnField());
                    remoteNXT.checkersBoard.myBoard
                            [(from.x + to.x) / 2]
                            [(from.y + to.y) / 2].setPieceOnField(null);
                }
                remoteNXT.checkersBoard.
                    movePieceInRepresentation(from, to, true);
                //tempStack.push(from);                                                         //HERE CHANGE
            }
            //for (int i = 0; i < stop; i++) {
            //    move.moves.push(tempStack.pop());             //HERE CHANGE
            //}

            simulatedMoves.add(move);
        }
    }

    private void revertAllMoves() throws NoKingLeft, IOException {
        int stop = simulatedMoves.size();
        for (int i = 0; i < stop; i++) {
            revertMove();
        }
    }

    private Stack<Field> flipStack(Stack<Field> inputMoves) {
        Stack<Field> tmpMove = new Stack<Field>();
        int stop = inputMoves.size();

        for (int i = 0; i < stop; i++) {
            tmpMove.push(inputMoves.pop());
        }

        return tmpMove;
    }

    public final void revertMove() throws NoKingLeft, IOException {
        
        if (simulatedMoves.size() != 0) {
            Move temp = simulatedMoves.get(simulatedMoves.size()-1);
            simulatedMoves.remove(simulatedMoves.size()-1);
            
            int stop = temp.moves.size() - 1;
            //Stack<Field> tempMoves = new Stack<Field>();
            Field tempMove = null;
           // temp.moves = flipStack(temp.moves);           HERE CHANGE

            for (int j = stop; j >= 1; j--) {                       //HERE CHANGE DIRECTION
                tempMove = temp.moves.get(j);
                if (!tempMove.isEmpty()) {
                    if (tempMove.getPieceOnField().isCrowned
                            && !temp.wasKingBefore) {
                        if (remoteNXT.checkersBoard.
                                checkAllegiance(tempMove, true)) {
                            tempMove.getPieceOnField().color =
                                   remoteNXT.checkersBoard.opponentPeasentColor;
                        } else if (remoteNXT.checkersBoard.
                                checkAllegiance(tempMove, false)) {
                            tempMove.getPieceOnField().color =
                                    remoteNXT.checkersBoard.myPeasentColor;
                        }
                        tempMove.getPieceOnField().isCrowned = false;
                    }
                }
                remoteNXT.checkersBoard.movePieceInRepresentation(
                        tempMove, temp.moves.get(j-1), true);       ///CHANGE HERE
               //tempMoves.push(tempMove);
            }

           // temp.moves = flipStack(temp.moves);  CHANGE SHOULD NOT BE NEEDED ANYMORE
            stop = temp.takenPieces.size();

            for (int i = 0; i < stop; i++) {
                Piece tempPiece = temp.takenPieces.get(i);
                remoteNXT.checkersBoard.myBoard
                    [tempPiece.x][tempPiece.y].setPieceOnField(tempPiece);
            }
        }
    }

    //-1 = human, 1 = robot
    private List<Move> possibleMoves(int moveForSide) throws
                    InterruptedException, IOException, NoKingLeft {
        sW.reset();
        List<Move> jumpMovements = new ArrayList<Move>();
        List<Move> movements = new ArrayList<Move>();
        remoteNXT.checkersBoard.updateMoveables();
        for (Field[] f : remoteNXT.checkersBoard.myBoard) {
            for (Field field : f) {
                if (!field.isEmpty()) {
                    if (field.getPieceOnField().isMoveable
                            && remoteNXT.checkersBoard.
                            checkAllegiance(field, moveForSide == -1)) {
                        //Jumps
                        if (field.getPieceOnField().canJump) {
                            List<List<Field>> listOfMoves =
                                    remoteNXT.checkersBoard.
                                    analyzeFunctions.jumpSequence(
                                            field, moveForSide == 1,
                                            field.getPieceOnField().isCrowned);

                            for (List<Field> stackOfFields : listOfMoves) {
                                if (stackOfFields.size() >= 2) {
                                    jumpMovements.add(new Move(stackOfFields,
                                            field.getPieceOnField().isCrowned));
                                }
                            }
                        } else {  // Moves
                            List<Field> possibleMoves =
                                    remoteNXT.checkersBoard.
                                    checkMoveable(field, moveForSide);
                            //Simple moves
                            if (!possibleMoves.isEmpty()) {
                                for (Field posField : possibleMoves) {
                                    Move movement = new Move(field, posField,
                                            field.getPieceOnField().isCrowned);
                                    movements.add(movement);
                                }
                            }
                        }
                    }
                }
            }
        }
        /*
        remoteNXT.checkersBoard.sortListOfMoves(movements);
        boolean mustJump = false;
        if (movements.size() != 0) {
            mustJump = movements.get(0).isJump();
            if (mustJump) {
                for (int i = 0; movements.size() > i; i++) {
                    if (!movements.get(i).isJump()) {
                        movements.remove(i);
                        i--;
                    }
                }
            }
        }
        return movements;
         */
        numberOftimesforPossibleMoves ++;
        totalTimeForPossibleMoves = sW.elapsed();
        if (jumpMovements.size() != 0) {
            return jumpMovements;
        } else {
            return movements;
        }
    }
}
