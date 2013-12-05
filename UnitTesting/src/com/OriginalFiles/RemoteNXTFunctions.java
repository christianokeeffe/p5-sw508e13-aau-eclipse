package com.OriginalFiles;

import com.CustomClasses.Color;

import custom.Exceptions.NoKingLeft;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoteNXTFunctions {
    public Board checkersBoard;
    public int analyzeTestVariable = 0;
    public boolean analyzeresetMotorsTestVariable = false;

    public RemoteNXTFunctions() throws InterruptedException, IOException {
        checkersBoard = new Board(this);
    }
    public void resetMotors() {
        analyzeresetMotorsTestVariable = true;
    }

    public void trashPieceOnField(Field field) {
        field.setPieceOnField(null);

    }

    public void doMove(Move move) throws IOException, NoKingLeft {
        List<Field> takenPieces = new ArrayList<Field>();

        int stop = move.moves.size() - 1;
        for (int i = 0; i < stop; i++) {
            Field jumpedField = movePieceOverField(move.moves.get(i),
                    move.moves.get(i + 1));
            if (jumpedField != null) {
                takenPieces.add(jumpedField);
            }
        }

        for (int i = 0; i < takenPieces.size(); i++) {
            trashPieceOnField(takenPieces.get(i));
        }

    }
    private Field movePieceOverField(Field fromField, Field toField)
            throws IOException, NoKingLeft {
        movePiece(fromField, toField);

        if (checkersBoard.checkBounds(fromField.x, fromField.y)
                && Math.abs(fromField.x - toField.x) == 2) {
            return checkersBoard.myBoard[(fromField.x + toField.x) / 2]
                    [(fromField.y + toField.y) / 2];
        } else {
            return null;
        }
    }
    private void movePiece(Field fromField, Field toField)
            throws IOException, NoKingLeft {
        checkersBoard.movePieceInRepresentation(fromField, toField, false);
    }

    public void waitForRedButton() {
        // TODO Auto-generated method stub

    }

    public void initColorSensor() {
        // TODO Auto-generated method stub

    }

    public final Color getColorOnField(int x, int y) {
        Color output = new Color();

        if(analyzeTestVariable == 0) {
            if(x == 0 && y == 1)
            {
                output.setColor('r');
            } else if (x == 2 && y == 5) {
                output.setColor(' ');
            } else if (x == 1 && y == 4) {
                output.setColor('w');
            } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
            } else {
                output.setColor(' ');
            }
        }
        else {
            // made to reset totalAnalyzeRuns
            if(analyzeTestVariable == 100) {
                if(x == 3 && y == 2)
                {
                    output.setColor(' ');
                }
                else if(x == 4 && y == 1)
                {
                    output.setColor('g');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            }
            // made to reset totalAnalyzeRuns
            if(analyzeTestVariable == 101) {
                if(x == 3 && y == 2)
                {
                    output.setColor('g');
                }
                else if(x == 4 && y == 1)
                {
                    output.setColor(' ');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            }

            if(analyzeTestVariable == 1) {
                if(x == 1 && y == 4)
                {
                    output.setColor('w');
                }
                else if(x == 0 && y == 5)
                {
                    output.setColor(' ');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            }
            else if (analyzeTestVariable == 2) {
                if(x == 2 && y == 5)
                {
                    output.setColor(' ');
                }
                else if(x == 3 && y == 4) {
                    output.setColor(' ');
                }
                else if(x == 4 && y == 3)
                {
                    output.setColor('w');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            }
            else if (analyzeTestVariable == 3) {
                if(x == 2 && y == 3) {
                    output.setColor(' ');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            }
            else if (analyzeTestVariable == 4) {
                if(x == 0 && y == 1) {
                    output.setColor('w');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            }
            else if (analyzeTestVariable == 5) {
                if(x == 5 && y == 4) {
                    output.setColor(' ');
                } else if(x == 4 && y == 3) {
                    output.setColor('g');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            }
            else if (analyzeTestVariable == 6) {
                if(x == 5 && y == 4) {
                    output.setColor(' ');
                } else if(x == 6 && y == 3) {
                    output.setColor('g');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            }
            else if (analyzeTestVariable == 7) {
                if(x == 5 && y == 4) {
                    output.setColor(' ');
                } else if(x == 4 && y == 5) {
                    output.setColor('g');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            }
            else if (analyzeTestVariable == 8) {
                if(x == 5 && y == 4) {
                    output.setColor(' ');
                } else if(x == 6 && y == 5) {
                    output.setColor('g');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            }
            else if (analyzeTestVariable == 9) {
                if(x == 5 && y == 4) {
                    output.setColor(' ');
                } else if(x == 4 && y == 3) {
                    output.setColor('g');
                } else if(x == 5 && y == 2) {
                    output.setColor(' ');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            } else if (analyzeTestVariable == 10) {
                if(x == 5 && y == 4) {
                    output.setColor(' ');
                } else if(x == 6 && y == 3) {
                    output.setColor('g');
                } else if(x == 5 && y == 2) {
                    output.setColor(' ');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            } else if (analyzeTestVariable == 11) {
                if(x == 5 && y == 4) {
                    output.setColor(' ');
                } else if(x == 4 && y == 5) {
                    output.setColor('g');
                } else if(x == 5 && y == 6) {
                    output.setColor(' ');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            } else if (analyzeTestVariable == 12) {
                if(x == 5 && y == 4) {
                    output.setColor(' ');
                } else if(x == 6 && y == 5) {
                    output.setColor('g');
                } else if(x == 5 && y == 6) {
                    output.setColor(' ');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            } else if (analyzeTestVariable == 13) {
                if(x == 4 && y == 1) {
                    output.setColor(' ');
                } else if(x == 3 && y == 0) {
                    output.setColor('w');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            } else if (analyzeTestVariable == 14) {
                if(x == 2 && y == 7) {
                    output.setColor('b');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            } else if (analyzeTestVariable == 15) {
                if(x == 5 && y == 4) {
                    output.setColor(' ');
                }
                if(x == 5 && y == 0) {
                    output.setColor('w');
                }
            } else if (analyzeTestVariable == 16) {
                if(x == 3 && y == 6) {
                    output.setColor(' ');
                } else if(x == 2 && y == 7) {
                    output.setColor('r');
                } else if (checkersBoard.myBoard[x][y].getPieceOnField() != null) {
                    output.setColor(checkersBoard.myBoard[x][y].getPieceOnField().color);
                } else {
                    output.setColor(' ');
                }
            }
        }

        return output;
    }

}
