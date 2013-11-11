import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import lejos.nxt.Button;
import lejos.nxt.Sound;
import customExceptions.NoKingLeft;


public class MI {
    public RemoteNXTFunctions remoteNXT;
    private final double inf = 100000.0;

    private Stack<Move> simulatedMoves = new Stack<Move>();
    MI(RemoteNXTFunctions inputRemoteNXT) {
        remoteNXT = inputRemoteNXT;
    }

    /* ------------------------------------------------------------------  *
    /* MI brain starts */

    ///Test method
    public final void scanPieces(int side) throws IOException {
        for (Field[] f:remoteNXT.checkersBoard.myBoard) {
            for (Field q: f) {
                if (!q.isEmpty()) {
                    if (side == 1) {
                        if (remoteNXT.checkersBoard.checkAllegiance(q, false)&& q.getPieceOnField().isCrowned) {
                            remoteNXT.getColorOnField(q.x, q.y);
                            
                            if(!q.isEmpty() && q.getPieceOnField().isCrowned)
                            {
                            	Sound.twoBeeps();
                            }
                        }
                    } else {
                        if (remoteNXT.checkersBoard.checkAllegiance(q, true)&& q.getPieceOnField().isCrowned) {
                            remoteNXT.getColorOnField(q.x, q.y);
                            
                            if(!q.isEmpty() && q.getPieceOnField().isCrowned)
                            {
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

        Move bestMove = new Move();
        double price = -inf, tempPrice;
        for (Move move : posMoves) {
            revertAllMoves();
            simulateMove(move);

            tempPrice =  -negaMax(numberofmovelook, -1, -inf, -price);
            revertMove();
            /*LCD.clear();
            LCD.drawString("P:  "+ price, 0, 0);
            LCD.drawString("TP: "+ tempPrice, 0, 1);
            LCD.drawString("From X: " + move.moves.peek().x, 0, 3);
            LCD.drawString("From Y: " + move.moves.peek().y, 0, 4);
            LCD.refresh();
            Button.ENTER.waitForAnyPress();*/
            if (tempPrice > price) {
                price = tempPrice;
                bestMove = move;
            }
        }
        return bestMove;
    }


    public final double negaMax(int depth, int turn, double alpha, double beta)
            throws NoKingLeft, IOException, InterruptedException {
        if (depth == 0) {
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
    private double gameIsWon = inf;
    private final int gameIsDraw = 20;

    private final int valueOfPiece = 10;
    private final int middleBonus = 3;
    private final int backlineBonus = 5;
    private final int pieceDifferenceFactor = 4;
    private final int kingBonus = 8;

    private double evaluation(int turn) {
        int oppPieces = 0;
        int ownPieces = 0;
        double valueOfBoard = 0;
        remoteNXT.checkersBoard.updateMoveables();
        for (Field[] f: remoteNXT.checkersBoard.myBoard) {
            for (Field q: f) {
                if (!q.isEmpty()) {
                    if (remoteNXT.checkersBoard.checkAllegiance(q, false)) {
                        ownPieces++;
                        valueOfBoard += priceForField(q);
                    } else {
                        oppPieces++;
                        valueOfBoard -= priceForField(q);
                    }
                }
            }
        }
        boolean isHuman = (turn == -1);

        switch (remoteNXT.checkersBoard.analyzeFunctions.gameHasEnded(isHuman)) {
        case 1:
            valueOfBoard -= gameIsWon;
            break;
        case 2:
            valueOfBoard += gameIsWon;
            break;
        case 3:
            if (ownPieces - oppPieces > 0) {
                valueOfBoard -= gameIsDraw;
            } else if (ownPieces - oppPieces < 0) {
                valueOfBoard += gameIsDraw;
            }
            break;
        default:
            break;
        }

        valueOfBoard +=  pieceDifferenceFactor * ((ownPieces / oppPieces) - 1);

        return valueOfBoard;
    }

    private double priceForField(Field field) {
        int returnValue = 0;

        returnValue += valueOfPiece + middleBonus
                       - min(Math.abs(3 - field.x), Math.abs(4 - field.x));
        if (!field.getPieceOnField().isCrowned
                && ((remoteNXT.checkersBoard.checkAllegiance(field, true)
                        && field.y == 7)
                || (remoteNXT.checkersBoard.checkAllegiance(field, false)
                        && field.y == 0))) {
            returnValue += backlineBonus;
        }
        if (field.getPieceOnField().isCrowned) {
            returnValue += kingBonus;
        }
        return returnValue;
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

            Stack<Field> tempStack = new Stack<Field>();
            for (int i = 0; i < stop; i++) {
                Field from = move.moves.pop();
                Field to = move.moves.peek();
                if (Math.abs(from.x - to.x) == 2) {
                    move.takenPieces.push(remoteNXT.checkersBoard.myBoard
                            [(from.x + to.x) / 2]
                            [(from.y + to.y) / 2].getPieceOnField());
                    remoteNXT.checkersBoard.myBoard
                            [(from.x + to.x) / 2]
                            [(from.y + to.y) / 2].setPieceOnField(null);
                }
                remoteNXT.checkersBoard.movePieceInRepresentation(from, to, true);
                tempStack.push(from);
            }
            for (int i = 0; i < stop; i++) {
                move.moves.push(tempStack.pop());
            }
            
            simulatedMoves.push(move);
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
            Move temp = simulatedMoves.pop();
            int stop = temp.moves.size() - 1;
            Stack<Field> tempMoves = new Stack<Field>();
            Field tempMove = null;
            temp.moves = flipStack(temp.moves);

            for (int j = 0; j < stop; j++) {
                tempMove = temp.moves.pop();
                
                if (!tempMove.isEmpty()) {
                    if (tempMove.getPieceOnField().isCrowned && !temp.wasKingBefore) {
                        if (remoteNXT.checkersBoard.checkAllegiance(tempMove, true)) {
                            tempMove.getPieceOnField().color = remoteNXT.checkersBoard.opponentPeasentColor;
                        } else if (remoteNXT.checkersBoard.checkAllegiance(tempMove, false)) {
                            tempMove.getPieceOnField().color = remoteNXT.checkersBoard.myPeasentColor;
                        }
                        tempMove.getPieceOnField().isCrowned = false;
                    }
                }
                remoteNXT.checkersBoard.movePieceInRepresentation(
                        tempMove, temp.moves.peek(), true);
                tempMoves.push(tempMove);
            }
            
            

            stop = tempMoves.size();
            for (int j = 0; j < stop; j++) {
                temp.moves.push(tempMoves.pop());
            }
            temp.moves = flipStack(temp.moves);
            stop = temp.takenPieces.size();

            for (int i = 0; i < stop; i++) {
                Piece tempPiece = temp.takenPieces.pop();
                remoteNXT.checkersBoard.myBoard[tempPiece.x][tempPiece.y].setPieceOnField(tempPiece);
            }
        }
    }

    //-1 = human, 1 = robot
    private List<Move> possibleMoves(int moveForSide) throws
                    InterruptedException, IOException, NoKingLeft {
        List<Move> movements = new ArrayList<Move>();
        remoteNXT.checkersBoard.updateMoveables();
        for (Field[] f : remoteNXT.checkersBoard.myBoard) {
            for (Field field : f) {
                if (!field.isEmpty()) {
                    if (field.getPieceOnField().isMoveable
                            && remoteNXT.checkersBoard.checkAllegiance(field, moveForSide == -1)) {
                        //Jumps
                        List<Stack<Field>> listOfMoves =
                                remoteNXT.checkersBoard.analyzeFunctions.jumpSequence(
                                        field, moveForSide == 1,
                                        field.getPieceOnField().isCrowned);

                        for (Stack<Field> stackOfFields : listOfMoves) {
                            if (stackOfFields.size() >= 2) {
                                movements.add(new Move(stackOfFields,
                                        field.getPieceOnField().isCrowned));
                            }
                        }

                        if (!field.getPieceOnField().canJump) {
                            List<Field> possibleMoves =
                                    remoteNXT.checkersBoard.checkMoveable(field, moveForSide);
                            //Simple moves
                            if (!possibleMoves.isEmpty()) {
                                for (Field posField : possibleMoves) {
                                    Move movement = new Move(field,
                                            posField, field.getPieceOnField().isCrowned);
                                    movements.add(movement);
                                }
                            }
                        }
                    }
                }
            }
        }



        remoteNXT.checkersBoard.sortListOfMoves(movements);
        boolean mustJump = false;
        if (movements.size() != 0) {
            mustJump = movements.get(0).isJump();

            for (int i = 0; movements.size() > i; i++) {
                if (mustJump && !movements.get(i).isJump()) {
                    movements.remove(i);
                    i--;
                }
            }
        }
        return movements;
    }
}
