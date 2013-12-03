package com.OriginalFiles;


public class Field {
    //allowedField determines if is an allowed field to play on (a black field)
    public boolean allowedField;
    public boolean visited = false;
    public int x;
    public int y;
    private Piece pieceOnField = null;
    public Field() { };
    public Field(int inputx, int inputy) {
        x = inputx;
        y = inputy;
    }

    //Get and Set to access to private variable pieceOnField.
    //Updates the coordinates of an piece when assigned to a field.
    public final void setPieceOnField(Piece inputPiece) {
        pieceOnField = inputPiece;
        if (!isEmpty()) {
            pieceOnField.setXY(x, y);
        }
    }

    public final Piece getPieceOnField() {
        return pieceOnField;
    }

    //A field is empty, if no piece is assigned to it.
    public final boolean isEmpty() {
        return (pieceOnField == null);
    }

    //Returns true if there is a piece on the field,
    //and the piece is of the given color.
    public final boolean isPieceOfColor(char input) {
        if (isEmpty()) {
            return false;
        } else {
            return (pieceOnField.color == input);
        }
    }

    //Empty the field, by un-assigning the piece of the field.
    public final void emptyThisField() {
        pieceOnField = null;
    }
}
