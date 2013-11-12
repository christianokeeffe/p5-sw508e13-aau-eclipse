import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import custom.Exceptions.NoKingLeft;

//This is a test class to emulate a MI, not suppose to be part of the release
public class FakeMI {
    Random numberGen;
    int globalY;
    boolean vHUMAN;
    List<Field> jumpList;
    List<Field> moveList;
    RemoteNXTFunctions nxt;

    FakeMI(RemoteNXTFunctions inputNXT, boolean versusHuman) {
        numberGen = new Random();
        jumpList = new ArrayList<Field>();
        moveList = new ArrayList<Field>();

        nxt = inputNXT;

        if (versusHuman) {
            globalY = 1;
            vHUMAN = true;
        } else {
            globalY = -1;
            vHUMAN = false;
        }

        updateList();
    }

    public final void updateList() {
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
    }

    public final boolean decideMovement()
            throws IOException, NoKingLeft, InterruptedException {
        updateList();
        if (!nxt.checkersBoard.analyzeFunctions.checkForGameHasEnded(!vHUMAN)) {
            if (!jumpList.isEmpty()) {
                int jtemp = numberGen.nextInt(jumpList.size());
                calculateJump(jumpList.get(jtemp));
                nxt.checkersBoard.updateMoveables();
            } else {
                if (!moveList.isEmpty()) {
                    int mtemp = numberGen.nextInt(moveList.size());
                    move(moveList.get(mtemp));
                    nxt.checkersBoard.updateMoveables();
                } else {
                    nxt.checkersBoard.informer.nothingPossible();
                }
            }
            return true;
        }
        return false;
    }

    private void callMove(Field from, Field to) {
        try {
            nxt.doMove(new Move(from, to, from.getPieceOnField().isCrowned));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoKingLeft e) {
            e.printStackTrace();
        }
    }

    private void move(Field f) {
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
    }

    private void calculateJump(Field f)
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
    }
}
