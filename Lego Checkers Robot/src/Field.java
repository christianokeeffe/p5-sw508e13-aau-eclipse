
public class Field {
    //allowedField determines if is an allowed field to play on (a black field)
    public boolean allowedField;
    public boolean visited = false;
    public int x;
    public int y;
    private Piece pieceOnField = null;
    public Field() { };
    public Field(final int inputx, final int inputy) {
        x = inputx;
        y = inputy;
    }

    //Get and Set to access to private variable pieceOnField. 
    //Updates the coordinates of an piece when assigned to a field. 
    public final void setPieceOnField(Piece inputPiece) {
        pieceOnField = inputPiece;
        if (!isEmpty()) {
            pieceOnField.x = x;
            pieceOnField.y = y;
        }
    }

    public final Piece getPieceOnField() {
        return pieceOnField;
    }

    //A field is empty, if no piece is assigned to it.
    public final boolean isEmpty() {
        return !(pieceOnField != null);
    }

    //Returns true if there is a piece on the field, and the piece is of the given color.
    public boolean isPieceOfColor(char input){
        if(isEmpty()){
            return false;
        }
        else{
            if(pieceOnField.color == input){
                return true;
            }else{
                return false;
            }
        }
    }

    //Empty the field, by deassigning the piece of the field.
    public final void emptyThisField() {
        pieceOnField = null;
    }
}
