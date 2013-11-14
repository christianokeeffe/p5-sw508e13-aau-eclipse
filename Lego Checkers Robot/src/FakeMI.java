import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import custom.Exceptions.NoKingLeft;

//This is a test class to emulate a MI, not suppose to be part of the release
public class FakeMI {
    Random numberGen;
    int globalY;
    boolean vHUMAN;
    RemoteNXTFunctions nxt;

    FakeMI(RemoteNXTFunctions inputNXT, boolean versusHuman) {
        numberGen = new Random();

        nxt = inputNXT;

        if (versusHuman) {
            globalY = 1;
            vHUMAN = true;
        } else {
            globalY = -1;
            vHUMAN = false;
        }
    }

   /* public final void updateList() {
        moveList.clear();
        jumpList.clear();
        for (Field[] arrayField : nxt.checkersBoard.myBoard) {
            for (Field field : arrayField) {
                if (field.getPieceOnField() != null) {
                    if (vHUMAN) {
                        if (nxt.checkersBoard.checkAllegiance(field, false)) {
                            Piece temp = field.getPieceOnField();
                            if (temp.isMoveable) {
                                moveList.add(field);
                            }
                            if (temp.canJump) {
                                jumpList.add(field);
                            }
                        }
                    } else {
                        if (nxt.checkersBoard.checkAllegiance(field, true)) {
                            Piece temp = field.getPieceOnField();
                            if (temp.isMoveable) {
                                moveList.add(field);
                            }
                            if (temp.canJump) {
                                jumpList.add(field);
                            }
                        }
                    }
                }
            }
        }
    }*/
    
  //-1 = human, 1 = robot
    private List<Move> possibleMoves(int moveForSide) throws
                    InterruptedException, IOException, NoKingLeft {
        List<Move> jumpMovements = new ArrayList<Move>();
        List<Move> movements = new ArrayList<Move>();
        nxt.checkersBoard.updateMoveables();
        for (Field[] f : nxt.checkersBoard.myBoard) {
            for (Field field : f) {
                if (!field.isEmpty()) {
                    if (field.getPieceOnField().isMoveable
                            && nxt.checkersBoard.
                            checkAllegiance(field, moveForSide == -1)) {
                        //Jumps
                        if (field.getPieceOnField().canJump) {
                            List<List<Field>> listOfMoves =
                                    nxt.checkersBoard.
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
                                    nxt.checkersBoard.
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
        
        if (jumpMovements.size() != 0) {
            return jumpMovements;
        } else {
            return movements;
        }
    }

    public final boolean decideMovement()
            throws IOException, NoKingLeft, InterruptedException {
        List<Move> moves = possibleMoves(globalY);
        
        if (!nxt.checkersBoard.analyzeFunctions.checkForGameHasEnded(!vHUMAN)) {
            if (!moves.isEmpty()) {
                nxt.doMove(moves.get(numberGen.nextInt(moves.size())));
                nxt.checkersBoard.updateMoveables();
            } else {
                nxt.checkersBoard.informer.nothingPossible();
            }
            return true;
        }
        return false;
    }

   /* private void callMove(Field from, Field to) {
        try {
            nxt.doMove(new Move(from, to, from.getPieceOnField().isCrowned));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoKingLeft e) {
            e.printStackTrace();
        }
    }*/

   /* private void move(Field f) {
        if (!f.getPieceOnField().isCrowned) {
            if (!nxt.checkersBoard.fieldOccupied(f.x + 1, f.y + globalY)
               && !nxt.checkersBoard.fieldOccupied(f.x - 1, f.y + globalY)) {
                int random = numberGen.nextInt(2);
                if (random == 0) {
                    callMove(f, nxt.checkersBoard.myBoard
                            [f.x + 1][f.y + globalY]);
                } else if (random == 1) {
                    callMove(f, nxt.checkersBoard.myBoard
                            [f.x - 1][f.y + globalY]);
                }
            } else
              if (!nxt.checkersBoard.fieldOccupied(f.x + 1, f.y + globalY)) {
              callMove(f, nxt.checkersBoard.myBoard[f.x + 1][f.y + globalY]);
            } else
              if (!nxt.checkersBoard.fieldOccupied(f.x - 1, f.y + globalY)) {
                callMove(f, nxt.checkersBoard.myBoard[f.x - 1][f.y + globalY]);
            }
        } else if (f.getPieceOnField().isCrowned) {
            if (!nxt.checkersBoard.fieldOccupied(f.x + 1, f.y + globalY)
                && !nxt.checkersBoard.fieldOccupied(f.x - 1, f.y + globalY)
                && !nxt.checkersBoard.fieldOccupied(f.x + 1, f.y - globalY)
                && !nxt.checkersBoard.fieldOccupied(f.x - 1, f.y - globalY)) {

                int random = numberGen.nextInt(4);
                if (random == 0) {
                    callMove(f, nxt.checkersBoard.myBoard
                            [f.x + 1][f.y + globalY]);
                } else if (random == 1) {
                    callMove(f, nxt.checkersBoard.myBoard
                            [f.x - 1][f.y + globalY]);
                } else if (random == 3) {
                    callMove(f, nxt.checkersBoard.myBoard
                            [f.x + 1][f.y - globalY]);
                } else if (random == 4) {
                    callMove(f, nxt.checkersBoard.myBoard
                            [f.x - 1][f.y - globalY]);
                }
            } else
                if (!nxt.checkersBoard.fieldOccupied(f.x + 1, f.y + globalY)) {
                callMove(f, nxt.checkersBoard.myBoard
                        [f.x + 1][f.y + globalY]);
            } else
                if (!nxt.checkersBoard.fieldOccupied(f.x - 1, f.y + globalY)) {
                callMove(f, nxt.checkersBoard.myBoard
                        [f.x - 1][f.y + globalY]);
            } else
                if (!nxt.checkersBoard.fieldOccupied(f.x + 1, f.y - globalY)) {
                callMove(f, nxt.checkersBoard.myBoard
                        [f.x + 1][f.y - globalY]);
            } else
                if (!nxt.checkersBoard.fieldOccupied(f.x - 1, f.y - globalY)) {
                callMove(f, nxt.checkersBoard.myBoard[f.x - 1][f.y - globalY]);
            }
        }
    }*/

    /*private void calculateJump(Field f)
            throws IOException, NoKingLeft, InterruptedException {
        List<List<Field>> jumpPath = new ArrayList<List<Field>>();
        if (f.getPieceOnField() != null) {
            jumpPath = nxt.checkersBoard.analyzeFunctions.
                    jumpSequence(f, vHUMAN, f.getPieceOnField().isCrowned);
            nxt.checkersBoard.resetVisited();
            if (jumpPath.size() == 1) {
                nxt.doMove(new Move(jumpPath.get(0),
                        f.getPieceOnField().isCrowned));
            } else if (jumpPath.size() > 1) {
                nxt.doMove(new Move(jumpPath.
                        get(numberGen.nextInt(jumpPath.size() - 1)),
                        f.getPieceOnField().isCrowned));
            } else {
                LCD.clear();
                LCD.drawString("stack er tom", 0, 0);
                LCD.refresh();
                Button.ENTER.waitForPress();
            }
        }
    }*/
}
