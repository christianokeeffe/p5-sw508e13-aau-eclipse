package com.OriginalFiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import custom.Exceptions.NoKingLeft;

public class MI {
    public RemoteNXTFunctions remoteNXT;
    private final double inf = 100000.0;
    private List<Move> simulatedMoves = new ArrayList<Move>();
    private int side;

    /* how much the AI/MI looks forward */
    private int numberofmovelook;

    /* how glad the MI/AI are for the result of the game */
    private double gameIsWon = inf / 2;
    private final int gameIsDraw = 200;

    private final int isMidGame = 1;
    private final int isEndgame = 2;
    private final int midGameEndMax = 7;
    private final int midGameEndMin = 4;

    public MI(RemoteNXTFunctions inputRemoteNXT,
            boolean isRobot, int hardness) {
        remoteNXT = inputRemoteNXT;
        numberofmovelook = hardness;
        updatePieceList();
        if (isRobot) {
            side = 1;
        } else {
            side = -1;
        }
    }

    List<Piece> ownPieces = new ArrayList<Piece>();
    List<Piece> oppPieces = new ArrayList<Piece>();

    private void updatePieceList() {
        ownPieces.clear();
        oppPieces.clear();
        for (Field[] f: remoteNXT.checkersBoard.myBoard) {
            for (Field q: f) {
                if (!q.isEmpty()) {
                    if (remoteNXT.checkersBoard.checkAllegiance(q, false)) {
                        ownPieces.add(q.getPieceOnField());
                    } else {
                        oppPieces.add(q.getPieceOnField());
                    }
                }
            }
        }
    }

    /* ------------------------------------------------------------------  *
    /* MI starts */

    public final Move lookForBestMove() throws NoKingLeft, IOException,
                                         InterruptedException {
        updatePieceList();
        List<Move> posMoves = possibleMoves(side);
        List<Move> bestMoves = new ArrayList<Move>();
        double price = -inf;
        double tempPrice;

        if (posMoves.size() == 1) {
            return posMoves.get(0);
        }

        for (Move move : posMoves) {
            simulateMove(move);

            tempPrice =  -negaMax(numberofmovelook, -side, -inf, -price);
            revertMove();

            if (tempPrice > price) {
                price = tempPrice;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (tempPrice == price) {
                bestMoves.add(move);
            }
        }
        return bestMoves.get((int) (Math.random() * (bestMoves.size() - 1)));
    }


    private double negaMax(int depth, int turn, double alpha, double beta)
            throws NoKingLeft, IOException, InterruptedException {
      //latex start negaMax
        if (depth == 0
                || remoteNXT.checkersBoard.analyzeFunctions.
                gameHasEnded(-1 == turn) != 0) {
            return turn * evaluation(turn);
        }
        List<Move> moves;
        moves = possibleMoves(turn);

        double bestValue = -inf;
        for (Move move : moves) {
            simulateMove(move);
            double newPrice = -negaMax(depth - 1, -turn, -beta, -alpha);
            revertMove();

            bestValue = max(bestValue, newPrice);
            alpha = max(alpha, newPrice);
            if (alpha >= beta) {
                break;
            }
        }
        //latex end
        return bestValue;
    }

    private double evaluation(int turn) {
        double valueOfBoard = 0;
        boolean robotHasTheMove = false;
        boolean humanTurn = false;

        if (turn == -1) {
            humanTurn = true;
        }
        if (!humanTurn) {
            robotHasTheMove = remoteNXT.checkersBoard.analyzeFunctions.
                    hasTheMove(humanTurn, ownPieces.size(), oppPieces.size());
        } else {
            robotHasTheMove = !remoteNXT.checkersBoard.analyzeFunctions.
                    hasTheMove(humanTurn, ownPieces.size(), oppPieces.size());
        }
        int state = gameState();

        for (int i = 0; i < ownPieces.size(); i++) {
            valueOfBoard += ownPieces.get(i).priceForPiece(state,
                    ownPieces.size(), oppPieces.size(), turn, robotHasTheMove);
        }

        for (int i = 0; i < oppPieces.size(); i++) {
            valueOfBoard -= oppPieces.get(i).priceForPiece(state,
                    ownPieces.size(), oppPieces.size(), turn, !robotHasTheMove);
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
                valueOfBoard += gameIsDraw;
            break;
        default:
            valueOfBoard +=  242 + (ownPieces.size() - oppPieces.size())
                          * ((24 - ownPieces.size() - oppPieces.size()) * 2);
            break;
        }

        return valueOfBoard;
    }

    private int gameState() {
        if (min(ownPieces.size(), oppPieces.size()) >= midGameEndMin
                && max(ownPieces.size(), oppPieces.size()) >= midGameEndMax) {
            return isMidGame;
        }
        return isEndgame;
    }

    private double max(double x, double y) {
        if (x < y) {
            return y;
        }
        return x;
    }
    private double min(double x, double y) {
        if (x < y) {
            return x;
        }
        return y;
    }


    /* MI stops */
    /* ---------------------------------------------------------------------  */

    private void simulateMove(Move move) throws NoKingLeft, IOException {
        if (move.moves.size() >= 2) {
            int stop = move.moves.size() - 1;
            for (int i = 0; i < stop; i++) {
                Field from = move.moves.get(i);
                Field to = move.moves.get(i + 1);

                if (Math.abs(from.x - to.x) == 2) {
                    Piece takenPiece = remoteNXT.checkersBoard.myBoard
                            [(from.x + to.x) / 2]
                            [(from.y + to.y) / 2].getPieceOnField();
                    if (remoteNXT.checkersBoard.
                            checkAllegiance(takenPiece, true)) {
                        oppPieces.remove(takenPiece);
                    } else {
                        ownPieces.remove(takenPiece);
                    }
                    move.takenPieces.add(takenPiece);
                    remoteNXT.checkersBoard.myBoard
                            [(from.x + to.x) / 2]
                            [(from.y + to.y) / 2].setPieceOnField(null);
                }
                remoteNXT.checkersBoard.
                    movePieceInRepresentation(from, to, true);
            }
            simulatedMoves.add(move);
        }
    }

    private void revertMove() throws NoKingLeft, IOException {

        if (simulatedMoves.size() != 0) {
            Move temp = simulatedMoves.get(simulatedMoves.size() - 1);
            simulatedMoves.remove(simulatedMoves.size() - 1);

            int stop = temp.moves.size() - 1;
            Field tempMove = null;

            for (int j = stop; j >= 1; j--) {
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
                        tempMove, temp.moves.get(j - 1), true);
            }

            stop = temp.takenPieces.size();

            for (int i = 0; i < stop; i++) {
                Piece tempPiece = temp.takenPieces.get(i);
                if (remoteNXT.checkersBoard.checkAllegiance(tempPiece, true)) {
                    oppPieces.add(tempPiece);
                } else {
                    ownPieces.add(tempPiece);
                }
                remoteNXT.checkersBoard.myBoard
                    [tempPiece.getX()][tempPiece.getY()].
                    setPieceOnField(tempPiece);
            }
        }
    }

    //-1 = human, 1 = robot
    private List<Move> possibleMoves(int moveForSide) throws
                    InterruptedException, IOException, NoKingLeft {
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

        if (jumpMovements.size() != 0) {
            return jumpMovements;
        } else {
            return movements;
        }
    }
}
